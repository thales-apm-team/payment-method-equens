package com.payline.payment.equens.bean.business.fraud;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PsuSessionInformation {

    /**
     * Date and time of the most relevant PSU’s terminal request to the PISP.
     */
    @SerializedName("LastLogin")
    private Date lastLogin;

    /**
     * IP Address of the PSU terminal when connecting to the PISP.
     */
    @SerializedName("IpAddress")
    private String ipAddress;

    /**
     * IP Port of the PSU terminal when connecting to the PISP.
     */
    @SerializedName("IpPort")
    private Integer ipPort;

    /**
     * HTTP Method used for the most relevant PSU’s terminal request to the PISP.
     */
    @SerializedName("HttpMethod")
    private String httpMethod;

    /**
     * "User-Agent" header field sent by the PSU terminal when connecting to the PISP.
     */
    @SerializedName("HttpHeaderUserAgent")
    private String headerUserAgent;

    /**
     * "Referer" header field sent by the PSU terminal when connecting to the PISP.
     */
    @SerializedName("HttpHeaderReferer")
    private String headerReferer;

    /**
     * "Accept" header field sent by the PSU terminal when connecting to the PISP.
     */
    @SerializedName("HttpHeaderAccept")
    private String headerAccept;

    /**
     * "Accept-Charset" header field sent by the PSU terminal when connecting to the PISP.
     */
    @SerializedName("HttpHeaderAcceptCharset")
    private String headerAcceptCharset;

    /**
     * "Accept-Encoding" header field sent by the PSU terminal when connecting to the PISP.
     */
    @SerializedName("HttpHeaderAcceptEncoding")
    private String headerAcceptEncoding;

    /**
     * "Accept-Language" header field sent by the PSU terminal when connecting to the PISP.
     */
    @SerializedName("HttpHeaderAcceptLanguage")
    private String headerAcceptLanguage;

    /**
     * UUID (Universally Unique Identifier) for a device, which is used by the PSU, if available.
     */
    @SerializedName("DeviceId")
    private String deviceId;

    /**
     * The forwarded Geo Location of the corresponding HTTP request between PSU and TPP.
     */
    @SerializedName("GeoLocation")
    private String geoLocation;

    private PsuSessionInformation(PsuSessionInformationBuilder builder) {
        this.lastLogin = builder.lastLogin;
        this.ipAddress = builder.ipAddress;
        this.ipPort = builder.ipPort;
        this.httpMethod = builder.httpMethod;
        this.headerUserAgent = builder.headerUserAgent;
        this.headerReferer = builder.headerReferer;
        this.headerAccept = builder.headerAccept;
        this.headerAcceptCharset = builder.headerAcceptCharset;
        this.headerAcceptEncoding = builder.headerAcceptEncoding;
        this.headerAcceptLanguage = builder.headerAcceptLanguage;
        this.deviceId = builder.deviceId;
        this.geoLocation = builder.geoLocation;
    }

    public static final class PsuSessionInformationBuilder {
        private Date lastLogin;
        private String ipAddress;
        private Integer ipPort;
        private String httpMethod;
        private String headerUserAgent;
        private String headerReferer;
        private String headerAccept;
        private String headerAcceptCharset;
        private String headerAcceptEncoding;
        private String headerAcceptLanguage;
        private String deviceId;
        private String geoLocation;

        public PsuSessionInformationBuilder withLastLogin(Date lastLogin) {
            this.lastLogin = lastLogin;
            return this;
        }

        public PsuSessionInformationBuilder withIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public PsuSessionInformationBuilder withIpPort(Integer ipPort) {
            this.ipPort = ipPort;
            return this;
        }

        public PsuSessionInformationBuilder withHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public PsuSessionInformationBuilder withHeaderUserAgent(String headerUserAgent) {
            this.headerUserAgent = headerUserAgent;
            return this;
        }

        public PsuSessionInformationBuilder withHeaderReferer(String headerReferer) {
            this.headerReferer = headerReferer;
            return this;
        }

        public PsuSessionInformationBuilder withHeaderAccept(String headerAccept) {
            this.headerAccept = headerAccept;
            return this;
        }

        public PsuSessionInformationBuilder withHeaderAcceptCharset(String headerAcceptCharset) {
            this.headerAcceptCharset = headerAcceptCharset;
            return this;
        }

        public PsuSessionInformationBuilder withHeaderAcceptEncoding(String headerAcceptEncoding) {
            this.headerAcceptEncoding = headerAcceptEncoding;
            return this;
        }

        public PsuSessionInformationBuilder withHeaderAcceptLanguage(String headerAcceptLanguage) {
            this.headerAcceptLanguage = headerAcceptLanguage;
            return this;
        }

        public PsuSessionInformationBuilder withDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public PsuSessionInformationBuilder withGeoLocation(String geoLocation) {
            this.geoLocation = geoLocation;
            return this;
        }

        public PsuSessionInformation build() {
            return new PsuSessionInformation( this );
        }
    }
}
