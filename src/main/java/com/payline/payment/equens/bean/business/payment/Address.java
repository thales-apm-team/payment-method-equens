package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Address {

    public static final int ADDRESS_LINE_MAX_LENGTH = 70;

    @SerializedName("AddressLine")
    private List<String> addressLines;
    @SerializedName("PostCode")
    private String postCode;
    @SerializedName("TownName")
    private String townName;
    @SerializedName("Country")
    private String country;

    Address( AddressBuilder builder ) {
        this.addressLines = builder.addressLines;
        this.postCode = builder.postCode;
        this.townName = builder.townName;
        this.country = builder.country;
    }

    public List<String> getAddressLines() {
        return addressLines;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getCountry() {
        return country;
    }

    public static class AddressBuilder {

        private List<String> addressLines;
        private String postCode;
        private String townName;
        private String country;

        public AddressBuilder withAddressLines(List<String> addressLines) {
            this.addressLines = addressLines;
            return this;
        }

        public AddressBuilder withPostCode(String postCode) {
            this.postCode = postCode;
            return this;
        }

        public AddressBuilder withTownName(String townName) {
            this.townName = townName;
            return this;
        }

        public AddressBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Address build(){
            return new Address( this );
        }
    }
}
