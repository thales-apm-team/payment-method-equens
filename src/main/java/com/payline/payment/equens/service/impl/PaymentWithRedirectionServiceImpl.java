package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.bean.business.payment.PaymentStatus;
import com.payline.payment.equens.bean.business.payment.PaymentStatusResponse;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.bean.pmapi.TransactionAdditionalData;
import com.payline.payment.equens.exception.InvalidDataException;
import com.payline.payment.equens.exception.PluginException;
import com.payline.payment.equens.utils.Constants;
import com.payline.payment.equens.utils.http.PisHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.BankAccount;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.BankTransfer;
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
            // Retrieve payment ID from request context
            if( redirectionPaymentRequest.getRequestContext() == null
                    || redirectionPaymentRequest.getRequestContext().getRequestData() == null
                    || redirectionPaymentRequest.getRequestContext().getRequestData().get(Constants.RequestContextKeys.PAYMENT_ID) == null ){
                throw new InvalidDataException("Missing payment ID from request context");
            }
            String paymentId = redirectionPaymentRequest.getRequestContext().getRequestData().get(Constants.RequestContextKeys.PAYMENT_ID);

            // check and update payment status
            paymentResponse = this.updatePaymentStatus( paymentId, RequestConfiguration.build( redirectionPaymentRequest ) );
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

            // Build transaction additional data
            TransactionAdditionalData transactionAdditionalData = new TransactionAdditionalData( paymentStatusResponse.getAspspPaymentId() );

            // Retrieve merchant IBAN
            String merchantIban = null;
            if( requestConfiguration.getContractConfiguration().getProperty( Constants.ContractConfigurationKeys.MERCHANT_IBAN ) != null ){
                merchantIban = requestConfiguration.getContractConfiguration().getProperty( Constants.ContractConfigurationKeys.MERCHANT_IBAN ).getValue();
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
                            .withTransactionDetails( new BankTransfer(
                                    this.getOwnerBankAccount( paymentStatusResponse ),
                                    this.getReceiverBankAccount( merchantIban )
                            ))
                            .withTransactionAdditionalData( transactionAdditionalData.toString() )
                            .build();
                    break;

                case CANCELLED:
                    paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withErrorCode("Payment not approved by PSU or insufficient funds")
                            .withFailureCause( FailureCause.CANCEL )
                            .withPartnerTransactionId( paymentId )
                            .withTransactionAdditionalData( transactionAdditionalData.toString() )
                            .build();
                    break;

                case EXPIRED:
                    paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withErrorCode("Consent approval has expired")
                            .withFailureCause( FailureCause.SESSION_EXPIRED )
                            .withPartnerTransactionId( paymentId )
                            .withTransactionAdditionalData( transactionAdditionalData.toString() )
                            .build();
                    break;

                case ERROR:
                default:
                    paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withErrorCode("Payment was rejected due to an error")
                            .withFailureCause( FailureCause.REFUSED )
                            .withPartnerTransactionId( paymentId )
                            .withTransactionAdditionalData( transactionAdditionalData.toString() )
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

    /**
     * Extract the owner bank account data from the given PaymentStatusResponse.
     *
     * @param paymentStatusResponse the payment status response
     * @return the owner bank account
     */
    BankAccount getOwnerBankAccount( PaymentStatusResponse paymentStatusResponse ){
        // pre-fill a builder with empty strings (null values not authorized)
        BankAccount.BankAccountBuilder ownerBuilder = BankAccount.BankAccountBuilder.aBankAccount()
                .withHolder("")
                .withAccountNumber("")
                .withIban("")
                .withBic("")
                .withCountryCode("")
                .withBankName("")
                .withBankCode("");

        // Fill available data
        if( paymentStatusResponse.getDebtorName() != null ){
            ownerBuilder.withHolder( paymentStatusResponse.getDebtorName() );
        }
        if( paymentStatusResponse.getDebtorAccount() != null ){
            ownerBuilder.withHolder( paymentStatusResponse.getDebtorAccount() );
        }
        if( paymentStatusResponse.getDebtorAgent() != null ){
            ownerBuilder.withBic( paymentStatusResponse.getDebtorAgent() );
        }

        return ownerBuilder.build();
    }

    /**
     * Build the receiver bank account with the given merchant IBAN.
     * Every other field is filled with an empty string.
     *
     * @param merchantIban the merchant IBAN
     * @return the receiver bank account
     */
    BankAccount getReceiverBankAccount( String merchantIban ){
        // pre-fill a builder fwith empty strings (null values not authorized)
        BankAccount.BankAccountBuilder receiverBuilder = BankAccount.BankAccountBuilder.aBankAccount()
                .withHolder("")
                .withAccountNumber("")
                .withIban( merchantIban )
                .withBic("")
                .withCountryCode("")
                .withBankName("")
                .withBankCode("");

        return receiverBuilder.build();
    }
}
