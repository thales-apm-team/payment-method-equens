package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;

/**
 * Information used for transporting transaction fees by the ASPSP.
 */
public class TransactionFees {

    /**
     * A code allocated to a currency by a Maintenance Agency under an international identification scheme,
     * as described in the latest edition of the international standard ISO 4217 "Codes for the representation
     * of currencies and funds".
     */
    @SerializedName("Currency")
    private String currency;
    /** Amount */
    @SerializedName("Amount")
    private String amount;


    TransactionFees( TransactionFeesBuilder builder ) {
        this.currency = builder.currency;
        this.amount = builder.amount;
    }

    public static class TransactionFeesBuilder {

        private String currency;
        private String amount;

        public TransactionFeesBuilder withCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public TransactionFeesBuilder withAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public TransactionFees build(){
            return new TransactionFees( this );
        }
    }
}
