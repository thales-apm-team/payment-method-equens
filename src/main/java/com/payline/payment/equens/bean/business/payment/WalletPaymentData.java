package com.payline.payment.equens.bean.business.payment;

import com.google.gson.Gson;

public class WalletPaymentData {
    /** the BIC for the creation of the wallet */
    private String bic;
    /** the IBAN for the creation of the wallet */
    private String iban;

    private WalletPaymentData(WalletPaymentDataBuilder builder) {
        bic = builder.bic;
        iban = builder.iban;
    }

    public static class WalletPaymentDataBuilder {
        private String bic;
        private String iban;

        public WalletPaymentDataBuilder withBic(String bic) {
            this.bic = bic;
            return this;
        }

        public WalletPaymentDataBuilder withIban(String iban) {
            this.iban = iban;
            return this;
        }

        public WalletPaymentData build() {
            return new WalletPaymentData(this);
        }
    }

    public String getBic() {
        return bic;
    }

    public String getIban() {
        return iban;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
