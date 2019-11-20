package com.payline.payment.equens.utils.http;

import com.payline.payment.equens.bean.business.payment.PaymentInitiationRequest;
import com.payline.payment.equens.bean.business.payment.PaymentInitiationResponse;
import com.payline.payment.equens.bean.business.payment.PaymentStatusResponse;
import com.payline.payment.equens.bean.business.reachdirectory.Aspsp;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HTTP client in charge of requesting the PIS (Payment Initiation Service) API.
 */
public class PisHttpClient extends EquensHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(PisHttpClient.class);

    private static final String API_PATH_ASPSPS = "/directory/v1/aspsps";
    private static final String API_PATH_PAYMENTS = "/pis/v1/payments";
    private static final String API_PATH_PAYMENTS_STATUS = "/pis/v1/payments/{paymentId}/status";

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

        // Send
        StringResponse response = this.get( url, this.initHeaders( requestConfiguration ) );

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
        List<Header> headers = this.initHeaders( requestConfiguration );

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

    /**
     * Recover the payment status.
     *
     * @param paymentId The payment ID
     * @param requestConfiguration The request configuration
     * @return The payment status
     */
    public PaymentStatusResponse paymentStatus( String paymentId, RequestConfiguration requestConfiguration, boolean autoConfirm ){
        // Service full URL
        String url = this.getBaseUrl( requestConfiguration.getPartnerConfiguration() )
                + API_PATH_PAYMENTS_STATUS.replace("{paymentId}", paymentId);
        // TODO: verify the usability, and remove if necessary
        if( autoConfirm ){
            url += "?confirm=true";
        }

        // Send request
        StringResponse response = this.get( url, this.initHeaders( requestConfiguration ) );

        // Handle potential errors
        if( !response.isSuccess() || response.getContent() == null ){
            throw this.handleError( response );
        }

        return PaymentStatusResponse.fromJson( response.getContent() );
    }

}
