package torenameEquens.utils.http;

import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.Logger;
import torenameEquens.bean.business.reachdirectory.GetAspspsResponse;
import torenameEquens.bean.configuration.RequestConfiguration;
import torenameEquens.exception.InvalidDataException;
import torenameEquens.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * HTTP client in charge of requesting the PIS (Payment Initiation Service) API.
 */
public class PisHttpClient extends EquensHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(PisHttpClient.class);

    private static final String API_PATH_ASPSPS = "/directory/v1/aspsps";

    // --- Singleton Holder pattern + initialization BEGIN
    PisHttpClient() {
    }
    private static class Holder {
        private static final PisHttpClient instance = new PisHttpClient();
    }
    public static PisHttpClient getInstance() {
        return Holder.instance;
    }
    // --- Singleton Holder pattern + initialization END

    @Override
    protected String appName() {
        return "PIS";
    }

    /**
     * Get the list of the ASPSPs available through Equens Worldline API.
     *
     * @param requestConfiguration the request configuration
     * @return The list of ASPSPs
     */
    public GetAspspsResponse getAspsps( RequestConfiguration requestConfiguration ){
        // Service full URL
        String url = this.getBaseUrl( requestConfiguration ) + API_PATH_ASPSPS;

        // Init. headers
        List<Header> headers = new ArrayList<>();

        // Authorization (access token)
        Authorization auth = this.authorize( requestConfiguration );
        headers.add( new BasicHeader( HttpHeaders.AUTHORIZATION, auth.getHeaderValue() ) );

        // Send
        StringResponse response = this.get( url, headers );

        // Handle potential errors
        if( !response.isSuccess() || response.getContent() == null ){
            // TODO: handle errors
        }

        return GetAspspsResponse.fromJson( response.getContent() );
    }

    /**
     * Retrieve the API base URL in PartnerConfiguration. Throws an exception if it's not present.
     *
     * @param requestConfiguration The request configuration
     * @return The API base URL
     */
    String getBaseUrl( RequestConfiguration requestConfiguration ){
        String baseUrl = requestConfiguration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.API_BASE_URL);
        if( baseUrl == null ){
            throw new InvalidDataException( "Missing API base url in PartnerConfiguration" );
        }
        return baseUrl;
    }

}
