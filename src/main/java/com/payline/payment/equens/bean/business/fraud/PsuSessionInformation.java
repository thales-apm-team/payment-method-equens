package com.payline.payment.equens.bean.business.fraud;

import com.google.gson.annotations.SerializedName;

public class PsuSessionInformation {

    /**
     * IP Address of the PSU terminal when connecting to the PISP.
     */
    @SerializedName("IpAddress")
    private String ipAddress;

    /**
     * "User-Agent" header field sent by the PSU terminal when connecting to the PISP.
     */
    @SerializedName("HttpHeaderUserAgent")
    private String headerUserAgent;

    private PsuSessionInformation(PsuSessionInformationBuilder builder) {
        this.ipAddress = builder.ipAddress;
        this.headerUserAgent = builder.headerUserAgent;
    }

    public static final class PsuSessionInformationBuilder {
        private String ipAddress;
        private String headerUserAgent;

        public PsuSessionInformationBuilder withIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }


        public PsuSessionInformationBuilder withHeaderUserAgent(String headerUserAgent) {
            this.headerUserAgent = headerUserAgent;
            return this;
        }


        public PsuSessionInformation build() {
            return new PsuSessionInformation( this );
        }
    }
}
