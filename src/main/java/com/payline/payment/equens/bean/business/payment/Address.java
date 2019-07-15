package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Address {

    public static final int ADDRESS_LINE_MAX_LENGTH = 70;

    @SerializedName("AddressLine")
    private List<String> addressLines;
    @SerializedName("StreetName")
    private String streetName;
    @SerializedName("BuildingNumber")
    private String buildingNumber;
    @SerializedName("PostCode")
    private String postCode;
    @SerializedName("TownName")
    private String townName;
    @SerializedName("CountrySubDivision")
    private List<String> countrySubDivisions;
    @SerializedName("Country")
    private String country;

    Address( AddressBuilder builder ) {
        this.addressLines = builder.addressLines;
        this.streetName = builder.streetName;
        this.buildingNumber = builder.buildingNumber;
        this.postCode = builder.postCode;
        this.townName = builder.townName;
        this.countrySubDivisions = builder.countrySubDivisions;
        this.country = builder.country;
    }

    public List<String> getAddressLines() {
        return addressLines;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getTownName() {
        return townName;
    }

    public List<String> getCountrySubDivisions() {
        return countrySubDivisions;
    }

    public String getCountry() {
        return country;
    }

    public static class AddressBuilder {

        private List<String> addressLines;
        private String streetName;
        private String buildingNumber;
        private String postCode;
        private String townName;
        private List<String> countrySubDivisions;
        private String country;

        public AddressBuilder addAddressLine( String addressLine ){
            if( addressLines == null ){
                addressLines = new ArrayList<>();
            }
            addressLines.add( addressLine );
            return this;
        }

        public AddressBuilder withAddressLines(List<String> addressLines) {
            this.addressLines = addressLines;
            return this;
        }

        public AddressBuilder withStreetName(String streetName) {
            this.streetName = streetName;
            return this;
        }

        public AddressBuilder withBuildingNumber(String buildingNumber) {
            this.buildingNumber = buildingNumber;
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

        public AddressBuilder addCountrySubDivision( String countrySubDivision ){
            if( countrySubDivisions == null ){
                countrySubDivisions = new ArrayList<>();
            }
            countrySubDivisions.add( countrySubDivision );
            return this;
        }

        public AddressBuilder withCountrySubDivision(List<String> countrySubDivisions) {
            this.countrySubDivisions = countrySubDivisions;
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
