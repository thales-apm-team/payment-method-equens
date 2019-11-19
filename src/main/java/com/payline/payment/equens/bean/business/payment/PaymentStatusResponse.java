package com.payline.payment.equens.bean.business.payment;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.payline.payment.equens.bean.business.EquensApiMessage;

/**
 * Response obtained from the payment status request.
 */
public class PaymentStatusResponse extends EquensApiMessage {

    /** Id generated by the TPP solution. This should be used to refer to this payment in subsequent api calls. */
    @SerializedName("PaymentId")
    private String paymentId;
    /** Payment status */
    @SerializedName("PaymentStatus")
    private PaymentStatus paymentStatus;
    /** Id used by the ASPSP to refer to the payment. */
    @SerializedName("AspspPaymentId")
    private PaymentStatus aspspPaymentId;
    /**
     * Reference to the payment created by the Initiating Party.
     * This Id will not be visible to the Payment Service User.
     */
    @SerializedName("InitiatingPartyReferenceId")
    private String initiatingPartyReferenceId;
    /** ? */
    @SerializedName("DebtorName")
    private String debtorName;
    /** ? */
    @SerializedName("DebtorAgent")
    private String debtorAgent;
    /** ? */
    @SerializedName("DebtorAccount")
    private String debtorAccount;
    /** Strong Customer Authentication challenge */
    @SerializedName("ScaChallenge")
    private ScaChallenge scaChallenge;
    /** ? */
    @SerializedName("FundsAvailable")
    private Boolean fundsAvailable;
    /** ? */
    @SerializedName("ApprovalLink")
    private String approvalLink;
    /** ? */
    @SerializedName("MultiAuthorizationStatus")
    private String multiAuthorizationStatus;

    // ScaMethods field is not mapped because it won't be used for now...
    // _links field is not mapped because its structure is complex and it won't be used for now...


    PaymentStatusResponse( PaymentStatusResponseBuilder builder ){
        super(builder);
        this.paymentId = builder.paymentId;
        this.paymentStatus = builder.paymentStatus;
        this.aspspPaymentId = builder.aspspPaymentId;
        this.initiatingPartyReferenceId = builder.initiatingPartyReferenceId;
        this.debtorName = builder.debtorName;
        this.debtorAgent = builder.debtorAgent;
        this.debtorAccount = builder.debtorAccount;
        this.scaChallenge = builder.scaChallenge;
        this.fundsAvailable = builder.fundsAvailable;
        this.approvalLink = builder.approvalLink;
        this.multiAuthorizationStatus = builder.multiAuthorizationStatus;
    }

    public static class PaymentStatusResponseBuilder extends EquensApiMessageBuilder {

        private String paymentId;
        private PaymentStatus paymentStatus;
        private PaymentStatus aspspPaymentId;
        private String initiatingPartyReferenceId;
        private String debtorName;
        private String debtorAgent;
        private String debtorAccount;
        private ScaChallenge scaChallenge;
        private Boolean fundsAvailable;
        private String approvalLink;
        private String multiAuthorizationStatus;

        public PaymentStatusResponseBuilder withPaymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public PaymentStatusResponseBuilder withPaymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public PaymentStatusResponseBuilder withAspspPaymentId(PaymentStatus aspspPaymentId) {
            this.aspspPaymentId = aspspPaymentId;
            return this;
        }

        public PaymentStatusResponseBuilder withInitiatingPartyReferenceId(String initiatingPartyReferenceId) {
            this.initiatingPartyReferenceId = initiatingPartyReferenceId;
            return this;
        }

        public PaymentStatusResponseBuilder withDebtorName(String debtorName) {
            this.debtorName = debtorName;
            return this;
        }

        public PaymentStatusResponseBuilder withDebtorAgent(String debtorAgent) {
            this.debtorAgent = debtorAgent;
            return this;
        }

        public PaymentStatusResponseBuilder withDebtorAccount(String debtorAccount) {
            this.debtorAccount = debtorAccount;
            return this;
        }

        public PaymentStatusResponseBuilder withScaChallenge(ScaChallenge scaChallenge) {
            this.scaChallenge = scaChallenge;
            return this;
        }

        public PaymentStatusResponseBuilder withFundsAvailable(Boolean fundsAvailable) {
            this.fundsAvailable = fundsAvailable;
            return this;
        }

        public PaymentStatusResponseBuilder withApprovalLink(String approvalLink) {
            this.approvalLink = approvalLink;
            return this;
        }

        public PaymentStatusResponseBuilder withMultiAuthorizationStatus(String multiAuthorizationStatus) {
            this.multiAuthorizationStatus = multiAuthorizationStatus;
            return this;
        }

        public PaymentStatusResponse build(){
            return new PaymentStatusResponse( this );
        }

    }

    public static PaymentStatusResponse fromJson( String json ){
        return new Gson().fromJson( json, PaymentStatusResponse.class );
    }

}
