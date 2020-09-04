package com.payline.payment.equens.bean.business.payment;

public class PaymentData {
    /** the BIC for the creation of the wallet */
    private String bic;
    /** the IBAN for the creation of the wallet */
    private String iban;

    private PaymentData(PaymentDataBuilder builder) {
        bic = builder.bic;
        iban = builder.iban;
    }

    public static class PaymentDataBuilder {
        private String bic;
        private String iban;

        public PaymentDataBuilder withBic(String bic) {
            this.bic = bic;
            return this;
        }

        public PaymentDataBuilder withIban(String iban) {
            this.iban = iban;
            return this;
        }

        public PaymentData build() {
            return new PaymentData(this);
        }
    }

    public String getBic() {
        return bic;
    }

    public String getIban() {
        return iban;
    }
}
