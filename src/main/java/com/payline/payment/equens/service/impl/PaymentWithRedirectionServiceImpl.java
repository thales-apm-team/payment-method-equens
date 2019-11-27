package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.bean.business.payment.PaymentStatus;
import com.payline.payment.equens.bean.business.payment.PaymentStatusResponse;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.PluginException;
import com.payline.payment.equens.utils.http.PisHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.apache.logging.log4j.Logger;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentWithRedirectionServiceImpl.class);

    private PisHttpClient pisHttpClient = PisHttpClient.getInstance();

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        PaymentResponse paymentResponse;

        try {
            // TODO: check if it's necessary to use RequestContext to get the partner transaction id
            paymentResponse = this.updatePaymentStatus( redirectionPaymentRequest.getTransactionId(), RequestConfiguration.build( redirectionPaymentRequest ) );
        }
        catch( PluginException e ){
            paymentResponse = e.toPaymentResponseFailureBuilder().build();
        }

        return paymentResponse;
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        PaymentResponse paymentResponse;

        try {
            paymentResponse = this.updatePaymentStatus( transactionStatusRequest.getTransactionId(), RequestConfiguration.build( transactionStatusRequest ) );
        }
        catch( PluginException e ){
            paymentResponse = e.toPaymentResponseFailureBuilder().build();
        }

        return paymentResponse;
    }

    /**
     * Convert a RuntimeException into a Payline <code>PaymentResponseFailure</code>.
     * @param e the exception
     * @return a <code>PaymentResponseFailure</code>
     */
    private PaymentResponseFailure handleRuntimeException( RuntimeException e ){
        return PaymentResponseFailure.PaymentResponseFailureBuilder
                .aPaymentResponseFailure()
                .withErrorCode( PluginException.runtimeErrorCode( e ) )
                .withFailureCause( FailureCause.INTERNAL_ERROR )
                .build();
    }

    /**
     * Request the partner API to get the payment status and return the appropriate <code>PaymentResponse</code>.
     *
     * @param paymentId The payment ID (on the partner side)
     * @param requestConfiguration the request configuration
     * @return a PaymentResponse
     */
    PaymentResponse updatePaymentStatus( String paymentId, RequestConfiguration requestConfiguration ){
        PaymentResponse paymentResponse;

        try {
            // Init HTTP client
            pisHttpClient.init( requestConfiguration.getPartnerConfiguration() );

            // Retrieve the payment status
            // TODO: check if auto-confirm is required (and functional)
            PaymentStatusResponse paymentStatusResponse = pisHttpClient.paymentStatus( paymentId, requestConfiguration, true );
            PaymentStatus status = paymentStatusResponse.getPaymentStatus();
            if( status == null ){
                throw new PluginException("Missing payment status in the partner response", FailureCause.PARTNER_UNKNOWN_ERROR);
            }

            // Build the appropriate response
            switch( status ){
                case OPEN:
                case AUTHORISED:
                case SETTLEMENT_IN_PROCESS:
                case PENDING:
                    paymentResponse = PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                            .withPartnerTransactionId( paymentId )
                            .withOnHoldCause( OnHoldCause.SCORING_ASYNC )
                            .withStatusCode( status.name() )
                            .build();
                    break;

                case SETTLEMENT_COMPLETED:
                    paymentResponse = PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                            .withPartnerTransactionId( paymentId )
                            .withStatusCode( status.name() )
                            .withTransactionDetails( new EmptyTransactionDetails() ) // TODO !!
                            .build();
                    break;

                case CANCELLED:
                    paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withErrorCode("Payment not approved by PSU or insufficient funds")
                            .withFailureCause( FailureCause.CANCEL )
                            .withPartnerTransactionId( paymentId )
                            .build();
                    break;

                case EXPIRED:
                    paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withErrorCode("Consent approval has expired")
                            .withFailureCause( FailureCause.SESSION_EXPIRED )
                            .withPartnerTransactionId( paymentId )
                            .build();
                    break;

                case ERROR:
                default:
                    paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withErrorCode("Payment was rejected due to an error")
                            .withFailureCause( FailureCause.REFUSED )
                            .withPartnerTransactionId( paymentId )
                            .build();
                    break;
            }
        }
        catch( PluginException e ){
            paymentResponse = e.toPaymentResponseFailureBuilder()
                    .withPartnerTransactionId( paymentId )
                    .build();
        }
        catch( RuntimeException e ){
            LOGGER.error("Unexpected plugin error", e);
            paymentResponse = handleRuntimeException( e );
        }

        return paymentResponse;
    }
}
