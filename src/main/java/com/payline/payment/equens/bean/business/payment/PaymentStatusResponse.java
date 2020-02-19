package com.payline.payment.equens.bean.business.payment;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.payline.payment.equens.bean.business.EquensApiMessage;

/**
 * Response obtained from the payment status request.
 */
public class PaymentStatusResponse extends EquensApiMessage {

    /** Payment status */
    @SerializedName("PaymentStatus")
    private PaymentStatus paymentStatus;
    /** Id used by the ASPSP to refer to the payment. */
    @SerializedName("AspspPaymentId")
    private String aspspPaymentId;
    /** ? */
    @SerializedName("DebtorName")
    private String debtorName;
    /** ? */
    @SerializedName("DebtorAgent")
    private String debtorAgent;
    /** ? */
    @SerializedName("DebtorAccount")
    private String debtorAccount;

    // ScaMethods field is not mapped because it won't be used for now...
    // _links field is not mapped because its structure is complex and it won't be used for now...

    // This constructor won't be used, since it's a response object : it's instantiated by Gson through the fromJson() method
    PaymentStatusResponse( EquensApiMessageBuilder builder ){
        super(builder);
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getAspspPaymentId() {
        return aspspPaymentId;
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

    public static PaymentStatusResponse fromJson( String json ){
        return new Gson().fromJson( json, PaymentStatusResponse.class );
    }

}
