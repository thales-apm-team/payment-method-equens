package com.payline.payment.equens.utils.http;

import com.payline.payment.equens.bean.business.payment.PaymentInitiationRequest;
import com.payline.payment.equens.bean.business.payment.PaymentInitiationResponse;
import com.payline.payment.equens.bean.business.reachdirectory.Aspsp;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.Logger;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.InvalidDataException;
import com.payline.payment.equens.utils.Constants;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP client in charge of requesting the PIS (Payment Initiation Service) API.
 */
public class PisHttpClient extends EquensHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(PisHttpClient.class);

    private static final String API_PATH_ASPSPS = "/directory/v1/aspsps";
    private static final String API_PATH_PAYMENTS = "/pis/v1/payments";

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
    public List<Aspsp> getAspsps(RequestConfiguration requestConfiguration ){
        // Service full URL
        String url = this.getBaseUrl( requestConfiguration.getPartnerConfiguration() ) + API_PATH_ASPSPS;

        // Init. headers with Authorization (access token)
        List<Header> headers = new ArrayList<>();
        Authorization auth = this.authorize( requestConfiguration );
        headers.add( new BasicHeader( HttpHeaders.AUTHORIZATION, auth.getHeaderValue() ) );

        // Send
        StringResponse response = this.get( url, headers );

        // Handle potential errors
        if( !response.isSuccess() || response.getContent() == null ){
            throw this.handleError( response );
        }

        return GetAspspsResponse.fromJson( response.getContent() ).getAspsps();
    }

    /**
     * Initialize a payment.
     *
     * @param paymentInitiationRequest the payment initiation request, containing the future payment's data
     * @param requestConfiguration the request configuration
     * @return The payment initiataion response, containing the client approval redirection URL
     */
    public PaymentInitiationResponse initPayment( PaymentInitiationRequest paymentInitiationRequest, RequestConfiguration requestConfiguration ){
        // Service full URL
        String url = this.getBaseUrl( requestConfiguration.getPartnerConfiguration() ) + API_PATH_PAYMENTS;

        // Init. headers with Authorization (access token)
        List<Header> headers = new ArrayList<>();
        Authorization auth = this.authorize( requestConfiguration );
        headers.add( new BasicHeader( HttpHeaders.AUTHORIZATION, auth.getHeaderValue() ) );

        // Body
        StringEntity body = new StringEntity( paymentInitiationRequest.toString(), StandardCharsets.UTF_8 );
        headers.add( new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json") );

        // Send request
        StringResponse response = this.post( url, headers, body );

        // Handle potential errors
        if( !response.isSuccess() || response.getContent() == null ){
            throw this.handleError( response );
        }

        return PaymentInitiationResponse.fromJson( response.getContent() );
    }

}
