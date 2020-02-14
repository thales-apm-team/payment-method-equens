package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;

public class Account {

    @SerializedName("Identification")
    private String identification;

    Account( AccountBuilder builder ) {
        this.identification = builder.identification;
    }

    public static class AccountBuilder {

        private String identification;

        public AccountBuilder withIdentification(String identification) {
            this.identification = identification;
            return this;
        }

        public Account build(){
            return new Account( this );
        }
    }

}
