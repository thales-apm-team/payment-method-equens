package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;

public class RemittanceInformationStructured {

    /** The actual reference */
    @SerializedName("Reference")
    private String reference;

    RemittanceInformationStructured( RemittanceInformationStructuredBuilder builder ){
        this.reference = builder.reference;
    }

    public static class RemittanceInformationStructuredBuilder {
        private String reference;

        public RemittanceInformationStructuredBuilder withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public RemittanceInformationStructured build(){
            return new RemittanceInformationStructured( this );
        }
    }


}
