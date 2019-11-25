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

    // This constructor won't be used, since it's a response object : it's instantiated by Gson through the fromJson() method
    PaymentStatusResponse( EquensApiMessageBuilder builder ){
        super(builder);
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public PaymentStatus getAspspPaymentId() {
        return aspspPaymentId;
    }

    public String getInitiatingPartyReferenceId() {
        return initiatingPartyReferenceId;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public String getDebtorAgent() {
        return debtorAgent;
    }

    public String getDebtorAccount() {
        return debtorAccount;
    }

    public ScaChallenge getScaChallenge() {
        return scaChallenge;
    }

    public Boolean getFundsAvailable() {
        return fundsAvailable;
    }

    public String getApprovalLink() {
        return approvalLink;
    }

    public String getMultiAuthorizationStatus() {
        return multiAuthorizationStatus;
    }

    public static PaymentStatusResponse fromJson( String json ){
        return new Gson().fromJson( json, PaymentStatusResponse.class );
    }

}
