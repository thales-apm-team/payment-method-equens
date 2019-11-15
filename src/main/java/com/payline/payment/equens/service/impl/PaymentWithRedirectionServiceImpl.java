package com.payline.payment.equens.service.impl;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.apache.logging.log4j.Logger;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.PluginException;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentWithRedirectionServiceImpl.class);

    // TODO: declare needed HTTP client(s)

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        PaymentResponse paymentResponse = null; // TODO: remove useless initialization

        try {
            // Init HTTP client
            // TODO: is necessary

            // Build request configuration
            RequestConfiguration requestConfiguration = RequestConfiguration.build( redirectionPaymentRequest );

            // TODO: finalize redirection payment
        }
        catch( PluginException e ){
            paymentResponse = e.toPaymentResponseFailureBuilder().build();
        }
        catch( RuntimeException e ){
            LOGGER.error("Unexpected plugin error", e);
            paymentResponse = handleRuntimeException( e );
        }

        return paymentResponse;
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        PaymentResponse paymentResponse = null; // TODO: remove useless initialization

        try {
            // Init HTTP client
            // TODO: is necessary

            // Build request configuration
            RequestConfiguration requestConfiguration = RequestConfiguration.build( transactionStatusRequest );

            // TODO: check the status and confirm if necessary
        }
        catch( PluginException e ){
            paymentResponse = e.toPaymentResponseFailureBuilder().build();
        }
        catch( RuntimeException e ){
            LOGGER.error("Unexpected plugin error", e);
            paymentResponse = handleRuntimeException( e );
        }

        return paymentResponse;
    }

    private PaymentResponseFailure handleRuntimeException( RuntimeException e ){
        return PaymentResponseFailure.PaymentResponseFailureBuilder
                .aPaymentResponseFailure()
                .withErrorCode( PluginException.runtimeErrorCode( e ) )
                .withFailureCause( FailureCause.INTERNAL_ERROR )
                .build();
    }
}
