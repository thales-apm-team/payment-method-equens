package torenameEquens.utils.http;

import com.google.gson.JsonSyntaxException;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.Logger;
import torenameEquens.bean.business.authorization.RFC6749AccessTokenErrorResponse;
import torenameEquens.bean.business.authorization.RFC6749AccessTokenSuccessResponse;
import torenameEquens.exception.InvalidDataException;
import torenameEquens.exception.PluginException;
import torenameEquens.utils.Constants;
import torenameEquens.utils.PluginUtils;
import torenameEquens.utils.properties.ConfigProperties;
import torenameEquens.utils.security.RSAHolder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Client dedicated to OAuth 2.0 authorization process : the recovering of a valid access token.
 * The class handles the storage and renewal of this token, and must be extended to use it.
 * It also provides a generic method to execute HTTP requests, with a retry system.
 */
abstract class OAuthHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(OAuthHttpClient.class);

    private ConfigProperties config = ConfigProperties.getInstance();

    /**
     * Support for the current authorization information.
     */
    private Authorization authorization;

    /**
     * Client used to contact APIs through HTTP.
     */
    protected CloseableHttpClient client;

    /**
     * Has this class been initialized with partner configuration ?
     */
    protected AtomicBoolean initialized = new AtomicBoolean();

    /**
     * The number of time the client must retry to send the request if it doesn't obtain a response.
     */
    private int retries;

    /**
     * Full URL (API domain + path) of the endpoint that delivers OAuth access token.
     * Since the domain and path change from one implementation to the other,
     * its value must be passed through the init method.
     */
    private String tokenEndpointUrl;

    /**
     * Initialize the instance.
     *
     * @param tokenEndpointUrl the full URL of the endpoint that delivers access tokens
     */
    protected void init( String tokenEndpointUrl, SSLContext sslContext ){
        if( this.initialized.compareAndSet(false, true) ){
            // Set the token endpoint URL
            this.tokenEndpointUrl = tokenEndpointUrl;

            try {
                // Get the number of retry attempts from plugin configuration
                this.retries = Integer.parseInt(config.get("http.retries"));
            }
            catch( NumberFormatException e ){
                throw new PluginException("plugin error: http.* properties must be integers", e);
            }

            // TODO: remove / modify
            // --- Temporary code ---
            // Instantiate Apache HTTP client
            this.client = HttpClientBuilder.create()
                    .useSystemProperties()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectionRequestTimeout(5000) // TODO: from config !
                            .setConnectTimeout(5000)
                            .setSocketTimeout(10000)
                            .build()
                    )
                    .setSSLContext( sslContext )
                    .build();
            // --- Temporary code ---
        }
    }

    /**
     * Return an access token in an {@link Authorization} object (contains the token type too).
     *
     * @return A valid authorization
     */
    // TODO: change back to protected !
    public Authorization authorize(){
        if( this.isAuthorized() ){
            LOGGER.info("Client already contains a valid authorization");
            return this.authorization;
        }

        // Init request
        URI uri;
        try {
            uri = new URI(this.tokenEndpointUrl);
        }
        catch (URISyntaxException e) {
            throw new InvalidDataException("Authorization API URL is invalid", e);
        }
        HttpPost httpPost = new HttpPost(uri);

        // Authorization header
        httpPost.setHeaders( this.authorizationHeaders() );

        // Content-Type
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        // www-form-urlencoded content
        StringEntity data = new StringEntity("grant_type=client_credentials", StandardCharsets.UTF_8);
        httpPost.setEntity(data);

        // Execute request
        StringResponse response = this.execute( httpPost );

        // Handle potential error
        if( !response.isSuccess() ){
            throw this.handleAuthorizationErrorResponse( response );
        }

        // Build authorization from response content
        Authorization.AuthorizationBuilder authBuilder = new Authorization.AuthorizationBuilder();
        try {
            // parse response content
            RFC6749AccessTokenSuccessResponse authResponse = RFC6749AccessTokenSuccessResponse.fromJson( response.getContent() );

            // retrieve access token & token type
            if( authResponse.getAccessToken() == null ){
                throw new PluginException("No access_token in the authorization response", FailureCause.COMMUNICATION_ERROR);
            }
            authBuilder.withAccessToken( authResponse.getAccessToken() )
                    .withTokenType( authResponse.getTokenType() == null ? "Bearer" : authResponse.getTokenType() );

            // expiration date
            long expiresIn = 60L * 5 * 1000; // 5 minutes default expiration time
            if( authResponse.getExpiresIn() != null ){
                expiresIn = 1000L * authResponse.getExpiresIn();
            }
            Date expiresAt = new Date( System.currentTimeMillis() + expiresIn );
            authBuilder.withExpiresAt( expiresAt );

            this.authorization = authBuilder.build();
            return this.authorization;
        }
        catch(JsonSyntaxException | IllegalStateException e){
            throw new PluginException("Failed to parse authorization response", FailureCause.COMMUNICATION_ERROR, e);
        }
    }

    /**
     * Build the request headers required to obtain an access token, which vary from one Instant Payment to another.
     * This method should be overridden by children classes, specific to each Instant Payment method.
     * @return the authorization request headers
     */
    protected abstract Header[] authorizationHeaders();

    /**
     * Check if there is a current valid authorization.
     * @return `true` if the current authorization is valid, `false` otherwise.
     */
    boolean isAuthorized(){
        return this.authorization != null
                && this.authorization.getExpiresAt().compareTo( new Date() ) > 0;
        /* Warning: the token can be valid at the time this method is called but not anymore 1 second later...
        Maybe add some time (5 minutes ?) to the current time, to be sure. */
    }

    /**
     * Send the request, with a retry system in case the client does not obtain a proper response from the server.
     *
     * @param httpRequest The request to send.
     * @return The response converted as a {@link StringResponse}.
     * @throws PluginException If an error repeatedly occurs and no proper response is obtained.
     */
    protected StringResponse execute( HttpRequestBase httpRequest ){
        StringResponse strResponse = null;
        int attempts = 1;

        while( strResponse == null && attempts <= this.retries ){
            if( LOGGER.isDebugEnabled() ){
                LOGGER.debug( "Start call to partner API (attempt {}) :" + System.lineSeparator() + PluginUtils.requestToString( httpRequest ), attempts );
            } else {
                LOGGER.info( "Start call to partner API [{} {}] (attempt {})", httpRequest.getMethod(), httpRequest.getURI(), attempts );
            }
            try( CloseableHttpResponse httpResponse = this.client.execute( httpRequest ) ){
                strResponse = StringResponse.fromHttpResponse( httpResponse );
            }
            catch (IOException e) {
                LOGGER.error("An error occurred during the HTTP call :", e);
                strResponse = null;
            }
            finally {
                attempts++;
            }
        }

        if( strResponse == null ){
            throw new PluginException( "Failed to contact the partner API", FailureCause.COMMUNICATION_ERROR );
        }
        LOGGER.info("Response obtained from partner API [{} {}]", strResponse.getStatusCode(), strResponse.getStatusMessage() );
        return strResponse;
    }

    /**
     * Handle error responses with RFC 6749 format.
     *
     * @param response The response received, converted as {@link StringResponse}.
     * @return The {@link PluginException} to throw
     */
    PluginException handleAuthorizationErrorResponse( StringResponse response ){
        RFC6749AccessTokenErrorResponse errorResponse;
        try {
            errorResponse = RFC6749AccessTokenErrorResponse.fromJson( response.getContent() );
        }
        catch( JsonSyntaxException e ){
            errorResponse = null;
        }

        if( errorResponse != null && errorResponse.getError() != null ){
            if( errorResponse.getErrorDescription() != null ){
                LOGGER.error( "Authorization error: {}", errorResponse.getErrorDescription() );
            }
            return new PluginException("authorization error: " + errorResponse.getError(), FailureCause.INVALID_DATA);
        }
        else {
            return new PluginException("unknown authorization error", FailureCause.PARTNER_UNKNOWN_ERROR);
        }
    }

}
