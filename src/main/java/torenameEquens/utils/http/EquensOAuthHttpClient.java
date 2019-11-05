package torenameEquens.utils.http;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import torenameEquens.exception.InvalidDataException;
import torenameEquens.exception.PluginException;
import torenameEquens.utils.Constants;
import torenameEquens.utils.security.RSAHolder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic HTTP client for to contact Equens Worldline API.
 * It's based upon OAuthHttpClient as Equens API uses this authorization protocol.
 * This class must be extended because the construction of the headers for the authorization call needs an app name.
 * Each Equens API has its proper name and access tokens.
 */
abstract class EquensOAuthHttpClient extends OAuthHttpClient {

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
    protected Header[] authorizationHeaders() {
        // TODO: this is a mock ! Replace it by the real code.
        List<Header> headers = new ArrayList<>();

        headers.add( new BasicHeader("App", this.appName() ) );
        headers.add( new BasicHeader("Client", "MarketPay") );
        headers.add( new BasicHeader("Date", "Tue Sep 17 14:48:21 CEST 2019") );
        headers.add( new BasicHeader("Id", "000061") );
        headers.add( new BasicHeader(HttpHeaders.AUTHORIZATION, "Signature keyId=\"2B48EAB0F05CCEC06F8A4BC4FBD5EFB98B7F3B5E\",algorithm=\"SHA256withRSA\",headers=\"app client id date\",signature=\"cOi/iqzqKc18pohxhmFYjLJMXtp5e37hYCz+0nBwJeeJQF5VOTtedo60H4+3osFReB68OxmMwGxBqHSvloO/uhT01m/zyebmPGFdj9im6fc0i5FXc9EE9V5N6R4T2XDNLvCY1ImhZPHzB7H6bI9k6x1pL6kOh160UOO+GghoKUf6QsRRYmqF9x/tK1llD/XEZeg5Vz1qlGzjVkOyKZqXnqSFYZKNW3UDR19UKnrfiGLk5+dew+3WVbpqnaYoRYE/v4C+7edNDBIlT7A23Qp3GK6clWW34hxvuKgRNU51Nhm3+BYKofWkHGgb2e9xwKOZokslm+ae7BVRIlDaZ3ZfpQ==\"") );

        Header[] headersArray = new Header[headers.size()];
        return headers.toArray( headersArray );
    }

    protected abstract String appName();

}
