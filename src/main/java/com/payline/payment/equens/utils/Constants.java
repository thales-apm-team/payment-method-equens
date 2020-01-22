package com.payline.payment.equens.utils;

/**
 * Support for constants used everywhere in the plugin sources.
 */
public class Constants {

    /**
     * Keys for the entries in ContractConfiguration map.
     */
    public static class ContractConfigurationKeys {

        public static final String CHANNEL_TYPE = "channelType";
        public static final String CHARGE_BEARER = "chargeBearer";
        public static final String CLIENT_NAME = "clientName";
        public static final String MERCHANT_IBAN = "merchantIban";
        public static final String MERCHANT_NAME = "merchantName";
        public static final String ONBOARDING_ID = "onboardingId";
        public static final String PURPOSE_CODE = "purposeCode";
        public static final String SCA_METHOD = "scaMethod";


        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private ContractConfigurationKeys(){}
    }

    /**
     * Keys for the entries in PartnerConfiguration maps.
     */
    public static class PartnerConfigurationKeys {

        public static final String API_BASE_URL = "apisUrl";
        public static final String CLIENT_CERTIFICATE = "clientCertificate";
        public static final String CLIENT_PRIVATE_KEY = "clientPrivateKey";
        public static final String PAYLINE_CLIENT_NAME = "paylineclientName";
        public static final String PAYLINE_ONBOARDING_ID = "paylineOnboardingId";
        public static final String PAYMENT_PRODUCT = "paymentProduct";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private PartnerConfigurationKeys(){}
    }

    /**
     * Keys for the entries in RequestContext data.
     */
    public static class RequestContextKeys {

        public static final String PAYMENT_ID = "paymentId";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private RequestContextKeys(){}
    }

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private Constants(){}

}
