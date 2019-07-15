package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;

public class Account {

    @SerializedName("Identification")
    private String identification;
    @SerializedName("SecondaryIdentification")
    private String secondaryIdentification;
    @SerializedName("Currency")
    private String currency;

    Account( AccountBuilder builder ) {
        this.identification = builder.identification;
        this.secondaryIdentification = builder.secondaryIdentification;
        this.currency = builder.currency;
    }

    public static class AccountBuilder {

        private String identification;
        private String secondaryIdentification;
        private String currency;

        public AccountBuilder withIdentification(String identification) {
            this.identification = identification;
            return this;
        }

        public AccountBuilder withSecondaryIdentification(String secondaryIdentification) {
            this.secondaryIdentification = secondaryIdentification;
            return this;
        }

        public AccountBuilder withCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Account build(){
            return new Account( this );
        }
    }

}
