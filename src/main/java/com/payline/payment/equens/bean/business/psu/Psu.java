package com.payline.payment.equens.bean.business.psu;

import com.google.gson.annotations.SerializedName;

public class Psu {

    @SerializedName("PsuId")
    private String psuId;
    @SerializedName("FirstName")
    private String firstName;
    @SerializedName("LastName")
    private String lastName;
    @SerializedName("Email")
    private String email;
    @SerializedName("PhoneNumber")
    private String phoneNumber;
    //@SerializedName("Address")
    // TODO: map the Address
    @SerializedName("Status")
    private String status;

    protected Psu( PsuBuilder builder ){
        this.psuId = builder.psuId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.status = builder.status;
    }

    public String getPsuId() {
        return psuId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public static class PsuBuilder {

        private String psuId;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        // TODO: map the Address
        private String status;

        public PsuBuilder withPsuId(String psuId) {
            this.psuId = psuId;
            return this;
        }

        public PsuBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public PsuBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public PsuBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public PsuBuilder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public PsuBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Psu build(){
            return new Psu( this );
        }
    }

}
