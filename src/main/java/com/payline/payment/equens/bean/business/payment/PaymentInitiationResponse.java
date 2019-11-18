package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.equens.bean.business.EquensApiMessage;

/**
 * Response obtained from the payment initiation request.
 */
public class PaymentInitiationResponse extends EquensApiMessage {

    /** Id generated by the TPP solution. This should be used to refer to this payment in subsequent api calls. */
    @SerializedName("PaymentId")
    private String paymentId;
    /**
     * Reference to the payment created by the Initiating Party.
     * This Id will not be visible to the Payment Service User.
     */
    @SerializedName("InitiatingPartyReferenceId")
    private String initiatingPartyReferenceId;
    /** Payment status */
    @SerializedName("PaymentStatus")
    private PaymentStatus paymentStatus;
    /** ? */
    @SerializedName("PsuMessage")
    private String psuMessage;
    /**
     * URL to be used by the Initiating Party to redirect the PSU towards the ASPSP.
     */
    @SerializedName("AspspRedirectUrl")
    private String aspspRedirectUrl;
    /** Strong Customer Authentication challenge */
    @SerializedName("ScaChallenge")
    private ScaChallenge scaChallenge;
    /** Information used for transporting transaction fees by the ASPSP. */
    @SerializedName("TransactionFees")
    private TransactionFees transactionFees;
    /** ? */
    @SerializedName("TransactionFeeIndicator")
    private Boolean transactionFeeIndicator;

    // _links field is not mapped because its structure is complex and it won't be used for now...

    /** Authorization Id to be used for further payment authorization. */
    @SerializedName("AuthorizationId")
    private String authorizationId;

    // ScaMethods field is not mapped because it won't be used for now...


    PaymentInitiationResponse( PaymentInitiationResponseBuilder builder ){
        super( builder );
        this.paymentId = builder.paymentId;
        this.initiatingPartyReferenceId = builder.initiatingPartyReferenceId;
        this.paymentStatus = builder.paymentStatus;
        this.psuMessage = builder.psuMessage;
        this.aspspRedirectUrl = builder.aspspRedirectUrl;
        this.scaChallenge = builder.scaChallenge;
        this.transactionFees = builder.transactionFees;
        this.transactionFeeIndicator = builder.transactionFeeIndicator;
        this.authorizationId = builder.authorizationId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getInitiatingPartyReferenceId() {
        return initiatingPartyReferenceId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getPsuMessage() {
        return psuMessage;
    }

    public String getAspspRedirectUrl() {
        return aspspRedirectUrl;
    }

    public ScaChallenge getScaChallenge() {
        return scaChallenge;
    }

    public TransactionFees getTransactionFees() {
        return transactionFees;
    }

    public Boolean getTransactionFeeIndicator() {
        return transactionFeeIndicator;
    }

    public String getAuthorizationId() {
        return authorizationId;
    }

    public static class PaymentInitiationResponseBuilder extends EquensApiMessageBuilder {

        private String paymentId;
        private String initiatingPartyReferenceId;
        private PaymentStatus paymentStatus;
        private String psuMessage;
        private String aspspRedirectUrl;
        private ScaChallenge scaChallenge;
        private TransactionFees transactionFees;
        private Boolean transactionFeeIndicator;
        private String authorizationId;

        public PaymentInitiationResponseBuilder withPaymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public PaymentInitiationResponseBuilder withInitiatingPartyReferenceId(String initiatingPartyReferenceId) {
            this.initiatingPartyReferenceId = initiatingPartyReferenceId;
            return this;
        }

        public PaymentInitiationResponseBuilder withPaymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public PaymentInitiationResponseBuilder withPsuMessage(String psuMessage) {
            this.psuMessage = psuMessage;
            return this;
        }

        public PaymentInitiationResponseBuilder withAspspRedirectUrl(String aspspRedirectUrl) {
            this.aspspRedirectUrl = aspspRedirectUrl;
            return this;
        }

        public PaymentInitiationResponseBuilder withScaChallenge(ScaChallenge scaChallenge) {
            this.scaChallenge = scaChallenge;
            return this;
        }

        public PaymentInitiationResponseBuilder withTransactionFees(TransactionFees transactionFees) {
            this.transactionFees = transactionFees;
            return this;
        }

        public PaymentInitiationResponseBuilder withTransactionFeeIndicator(boolean transactionFeeIndicator) {
            this.transactionFeeIndicator = transactionFeeIndicator;
            return this;
        }

        public PaymentInitiationResponseBuilder withAuthorizationId(String authorizationId) {
            this.authorizationId = authorizationId;
            return this;
        }

        public PaymentInitiationResponse build(){
            return new PaymentInitiationResponse( this );
        }

    }
}
