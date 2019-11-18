package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;

public class RemittanceInformationStructured {

    /** The actuel reference */
    @SerializedName("Reference")
    private String reference;
    @SerializedName("ReferenceType")
    private String referenceType;
    @SerializedName("ReferenceIssuer")
    private String referenceIssuer;

    RemittanceInformationStructured( RemittanceInformationStructuredBuilder builder ){
        this.reference = builder.reference;
        this.referenceType = builder.referenceType;
        this.referenceIssuer = builder.referenceIssuer;
    }

    public static class RemittanceInformationStructuredBuilder {
        private String reference;
        private String referenceType;
        private String referenceIssuer;

        public RemittanceInformationStructuredBuilder withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public RemittanceInformationStructuredBuilder withReferenceType(String referenceType) {
            this.referenceType = referenceType;
            return this;
        }

        public RemittanceInformationStructuredBuilder withReferenceIssuer(String referenceIssuer) {
            this.referenceIssuer = referenceIssuer;
            return this;
        }

        public RemittanceInformationStructured build(){
            return new RemittanceInformationStructured( this );
        }
    }


}
