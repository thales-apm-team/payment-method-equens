package com.payline.payment.equens.utils.http;

import com.google.gson.JsonSyntaxException;
import com.payline.payment.equens.bean.business.payment.PaymentInitiationRequest;
import com.payline.payment.equens.bean.business.payment.PaymentInitiationResponse;
import com.payline.payment.equens.bean.business.payment.PaymentStatusResponse;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.InvalidDataException;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HTTP client in charge of requesting the PIS (Payment Initiation Service) API.
 */
public class PisHttpClient extends EquensHttpClient {

    private static final String API_PATH_ASPSPS = "api.pis.aspsps";
    private static final String API_PATH_PAYMENTS = "api.pis.payments";
    private static final String API_PATH_PAYMENTS_STATUS = "api.pis.payments.status";

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
    public GetAspspsResponse getAspsps(RequestConfiguration requestConfiguration) {
        // Service full URL
        String url = this.getBaseUrl(requestConfiguration.getPartnerConfiguration()) + this.getPath(API_PATH_ASPSPS);

        // Send
        StringResponse response = this.get(url, this.initHeaders(requestConfiguration));

        // Handle potential errors
        if (!response.isSuccess() || response.getContent() == null) {
            throw this.handleError(response);
        }
        try {

            return GetAspspsResponse.fromJson(response.getContent());
        } catch (JsonSyntaxException e) {
            LOGGER.error("getAspsps Response is not a JSON: {}", response.getContent(), e);
            throw new InvalidDataException(response.getContent());
        }
    }

    /**
     * Initialize a payment.
     *
     * @param paymentInitiationRequest the payment initiation request, containing the future payment's data
     * @param requestConfiguration     the request configuration
     * @return The payment initiation response, containing the client approval redirection URL
     */
    public PaymentInitiationResponse initPayment(PaymentInitiationRequest paymentInitiationRequest, RequestConfiguration requestConfiguration) {
        // Service full URL
        String url = this.getBaseUrl(requestConfiguration.getPartnerConfiguration()) + this.getPath(API_PATH_PAYMENTS);

        // Init. headers with Authorization (access token)
        List<Header> headers = this.initHeaders(requestConfiguration);

        // Body
        StringEntity body = new StringEntity(paymentInitiationRequest.toString(), StandardCharsets.UTF_8);
        headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

        // Send request
        StringResponse response = this.post(url, headers, body);

        // Handle potential errors
        if (!response.isSuccess() || response.getContent() == null) {
            throw this.handleError(response);
        }

        try {
            return PaymentInitiationResponse.fromJson(response.getContent());
        } catch (JsonSyntaxException e) {
            LOGGER.error("initPayment Response is not a JSON: {}", response.getContent(), e);
            throw new InvalidDataException(response.getContent());
        }
    }

    /**
     * Recover the payment status.
     *
     * @param paymentId            The payment ID
     * @param requestConfiguration The request configuration
     * @return The payment status
     */
    public PaymentStatusResponse paymentStatus(String paymentId, RequestConfiguration requestConfiguration, boolean autoConfirm) {
        // Service full URL
        String url = this.getBaseUrl(requestConfiguration.getPartnerConfiguration())
                + this.getPath(API_PATH_PAYMENTS_STATUS).replace("{paymentId}", paymentId);
        if (autoConfirm) {
            url += "?confirm=true";
        }

        // Send request
        StringResponse response = this.get(url, this.initHeaders(requestConfiguration));

        // Handle potential errors
        if (!response.isSuccess() || response.getContent() == null) {
            throw this.handleError(response);
        }

        try {
            return PaymentStatusResponse.fromJson(response.getContent());
        } catch (JsonSyntaxException e) {
            LOGGER.error("paymentStatus Response is not a JSON: {}", response.getContent(), e);
            throw new InvalidDataException(response.getContent());
        }
    }

}
