package torenameEquens.utils.http;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;
import torenameEquens.bean.configuration.RequestConfiguration;
import torenameEquens.exception.InvalidDataException;
import torenameEquens.exception.PluginException;
import torenameEquens.utils.Constants;
import torenameEquens.utils.security.RSAHolder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.tomitribe.auth.signatures.Algorithm.RSA_SHA256;

/**
 * Generic HTTP client to contact Equens Worldline API.
 * It's based upon OAuthHttpClient as Equens API uses this authorization protocol.
 * This class must be extended because the construction of the headers for the authorization call needs an app name.
 * Each Equens API has its proper name and access tokens.
 */
abstract class EquensHttpClient extends OAuthHttpClient {

    /**
     * Holder containing the keystore data (keys or certificates).
     */
    private RSAHolder rsaHolder;

    public void init(PartnerConfiguration partnerConfiguration) {
        // TODO: remove / modify
        // --- Temporary code ---
        SSLContext sslContext;
        try {
            // Build RSA holder from PartnerConfiguration
            if( partnerConfiguration.getProperty( Constants.PartnerConfigurationKeys.CLIENT_CERTIFICATE ) == null ){
                throw new InvalidDataException("Missing client certificate chain from partner configuration (sentitive properties)");
            }
            if( partnerConfiguration.getProperty( Constants.PartnerConfigurationKeys.CLIENT_PRIVATE_KEY ) == null ){
                throw new InvalidDataException("Missing client private key from partner configuration (sentitive properties)");
            }

            this.rsaHolder = new RSAHolder.RSAHolderBuilder()
                    .parseChain( partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CLIENT_CERTIFICATE) )
                    .parsePrivateKey( partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CLIENT_PRIVATE_KEY) )
                    .build();

            // SSL context
            sslContext = SSLContexts.custom()
                    .loadKeyMaterial(this.rsaHolder.getKeyStore(), this.rsaHolder.getPrivateKeyPassword())
                    .build();
        } catch ( IOException | GeneralSecurityException e ){
            throw new PluginException( "A problem occurred initializing SSL context", FailureCause.INVALID_DATA, e );
        }

        // Build authorization endpoint
        String authorizationEndpoint = partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.API_BASE_URL) + "/authorize/token";

        super.init(authorizationEndpoint, sslContext);
        // --- Temporary code ---
    }

    @Override
    protected List<Header> authorizationHeaders( String uri, RequestConfiguration requestConfiguration ) {
        // Check required data
        ContractConfiguration contractConfiguration = requestConfiguration.getContractConfiguration();
        if( contractConfiguration.getProperty( Constants.ContractConfigurationKeys.CLIENT_NAME ) == null
                || contractConfiguration.getProperty( Constants.ContractConfigurationKeys.CLIENT_NAME ).getValue() == null ){
            throw new InvalidDataException("Missing client name in contract configuration");
        }
        if( contractConfiguration.getProperty( Constants.ContractConfigurationKeys.ONBOARDING_ID ) == null
                || contractConfiguration.getProperty( Constants.ContractConfigurationKeys.ONBOARDING_ID ).getValue() == null ){
            throw new InvalidDataException("Missing onboarding id in contract configuration");
        }

        // Build headers list
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("App", this.appName());
        headers.put("Client", contractConfiguration.getProperty( Constants.ContractConfigurationKeys.CLIENT_NAME ).getValue());
        headers.put("Date", new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US).format(new Date()));
        headers.put("Id", contractConfiguration.getProperty( Constants.ContractConfigurationKeys.ONBOARDING_ID ).getValue());

        // Generate the request signature
        Signature signature = this.generateSignature( uri, headers );
        String finalSignature = signature.toString().replace(RSA_SHA256.getPortableName(), RSA_SHA256.getJmvName());

        // Insert the signature into Authorization header
        headers.put(HttpHeaders.AUTHORIZATION, finalSignature);

        // Convert the headers map to a list of Header
        List<Header> headersList = new ArrayList<>();
        for (Map.Entry<String, String> h : headers.entrySet()) {
            headersList.add( new BasicHeader(h.getKey(), h.getValue()) );
        }
        return headersList;
    }

    protected abstract String appName();

    /**
     * Generate a signature, using the private key and client certificate returned by the {@link RSAHolder} instance.
     *
     * @param uri the request URI
     * @param headers the request headers
     * @return The signature
     */
    Signature generateSignature( String uri, Map<String, String> headers ){
        // Retrieve private key
        Key pk;
        try {
            pk = this.rsaHolder.getPrivateKey();
        } catch (GeneralSecurityException e) {
            throw new PluginException("plugin error: unable to get client private key", e);
        }

        // Generate keyId for the signature, which is the SHA1 hash of the client certificate without colons
        String sha1;
        try {
            sha1 = sha1( this.rsaHolder.getClientCertificate().getEncoded() );
        }
        catch (KeyStoreException e) {
            throw new PluginException("plugin error: unable to get client certificate", e);
        }
        catch (CertificateEncodingException e) {
            throw new PluginException("plugin error: unable to encode client certificate", e);
        }
        if( sha1 == null ){
            throw new PluginException("plugin error: unable to apply sha1 on client certificate");
        }
        String keyId = sha1.replace(":", "");

        // @see https://github.com/tomitribe/http-signatures-java
        // Create a signer
        Signature signature = new Signature(keyId, RSA_SHA256, null, "app", "client", "id", "date");
        Signer signer = new Signer(pk, signature);

        // Sign the HTTP message
        try {
            signature = signer.sign("POST", uri, headers);
        }
        catch (IOException e) {
            throw new PluginException("unexpected plugin error: while signing the request");
        }

        return signature;
    }

    private static String sha1( byte[] block ){
        try {
            return toHexString(MessageDigest.getInstance("SHA1").digest( block ));
        }
        catch (NoSuchAlgorithmException e) {
            // Should not happen as "SHA1" is a verified valid algorithm
            return null;
        }
    }

    private static String toHexString(byte[] block) {
        StringBuilder sb = new StringBuilder();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], sb);
            if (i < len - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    private static void byte2hex(byte b, StringBuilder sb) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        sb.append(hexChars[high]);
        sb.append(hexChars[low]);
    }

}
