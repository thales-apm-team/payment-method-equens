package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;

/** Information used for risk scoring by the ASPSP. */
public class RiskInformation {

    /**
     * Specifies the payment context. Payments for EcommerceGoods and EcommerceServices will be expected to have a
     * MerchantCategoryCode and MerchantCustomerIdentification populated.
     * Payments for EcommerceGoods will also have the DeliveryAddress populated.
     */
    @SerializedName("PaymentContextCode")
    private String paymentContextCode;
    /**
     * Category code conform to ISO 18245, related to the type of services or goods the merchant provides for the transaction.
     */
    @SerializedName("MerchantCategoryCode")
    private String merchantCategoryCode;
    /**
     * The unique customer identifier of the PSU with the merchant.
     */
    @SerializedName("MerchantCustomerId")
    private String merchantCustomerId;
    /** ? */
    @SerializedName("DeliveryAddress")
    private Address deliveryAddress;
    /** Payment channel type */
    @SerializedName("ChannelType")
    private String channelType;
    /** Additional information related to the channel. */
    @SerializedName("ChannelMetaData")
    private String channelMetaData;

    public RiskInformation( RiskInformationBuilder builder ){
        this.paymentContextCode = builder.paymentContextCode;
        this.merchantCategoryCode = builder.merchantCategoryCode;
        this.merchantCustomerId = builder.merchantCustomerId;
        this.deliveryAddress = builder.deliveryAddress;
        this.channelType = builder.channelType;
        this.channelMetaData = builder.channelMetaData;
    }

    public static class RiskInformationBuilder {

        private String paymentContextCode;
        private String merchantCategoryCode;
        private String merchantCustomerId;
        private Address deliveryAddress;
        private String channelType;
        private String channelMetaData;

        public RiskInformationBuilder withPaymentContextCode(String paymentContextCode) {
            this.paymentContextCode = paymentContextCode;
            return this;
        }

        public RiskInformationBuilder withMerchantCategoryCode(String merchantCategoryCode) {
            this.merchantCategoryCode = merchantCategoryCode;
            return this;
        }

        public RiskInformationBuilder withMerchantCustomerId(String merchantCustomerId) {
            this.merchantCustomerId = merchantCustomerId;
            return this;
        }

        public RiskInformationBuilder withDeliveryAddress(Address deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public RiskInformationBuilder withChannelType(String channelType) {
            this.channelType = channelType;
            return this;
        }

        public RiskInformationBuilder withChannelMetaData(String channelMetaData) {
            this.channelMetaData = channelMetaData;
            return this;
        }

        public RiskInformation build(){
            return new RiskInformation( this );
        }
    }
}
