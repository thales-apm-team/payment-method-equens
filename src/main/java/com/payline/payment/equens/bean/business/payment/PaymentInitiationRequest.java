package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.equens.bean.business.EquensApiMessage;
import com.payline.payment.equens.bean.business.fraud.PsuSessionInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Payment initiation request.
 */
public class PaymentInitiationRequest extends EquensApiMessage {

    /** Identifies the debtor bank. This ID is taken from the reach directory. */
    @SerializedName("AspspId")
    private String aspspId;
    /** Unique identification assigned by the Initiating Party to unumbiguously identify the transaction.
     * This identification is passed on, unchanged, throughout the entire end-to-end chain.
     * Can be used for reconciliation by the Initiating Party.
     */
    @SerializedName("EndToEndId")
    private String endToEndId;
    /**
     * Reference to the payment created by the Initiating Party.
     * This Id will not be visible to the Payment Service User.
     */
    @SerializedName("InitiatingPartyReferenceId")
    private String initiatingPartyReferenceId;
    /** Callback URL to be used in case of a successful processing of the payment request. The URL should not be encoded. */
    @SerializedName("InitiatingPartyReturnUrl")
    private String initiatingPartyReturnUrl;
    /** External identification of the subsidiary initiating party. */
    @SerializedName("InitiatingPartySubId")
    private String initiatingPartySubId;
    /**
     * Information supplied to enable the matching of an entry with the items that the transfer is intended to settle.
     * This information will be visible to the Payment Service User.
     */
    @SerializedName("RemittanceInformation")
    private String remittanceInformation;
    /** ? */
    @SerializedName("RemittanceInformationStructured")
    private RemittanceInformationStructured remittanceInformationStructured;
    /** Identification of the account of the debtor to which a debit entry will be made as a result of the transaction. */
    @SerializedName("DebtorAccount")
    private Account debtorAccount;
    /** ? */
    @SerializedName("UltimateDebtor")
    private String ultimateDebtor;
    /** The name of the creditor. */
    @SerializedName("CreditorName")
    private String creditorName;
    /** Financial institution servicing an account for the creditor. */
    @SerializedName("CreditorAgent")
    private String creditorAgent;
    /** Identification of the creditor account. */
    @SerializedName("CreditorAccount")
    private Account creditorAccount;
    /** ? */
    @SerializedName("UltimateCreditor")
    private String ultimateCreditor;
    /** ? */
    @SerializedName("CreditorPostalAddress")
    private Address creditorPostalAddress;
    /** Amount of the payment. The decimal separator is a dot. */
    @SerializedName("PaymentAmount")
    private String paymentAmount;
    /** Currency of the payment. ISO 4217 currency codes should be used. */
    @SerializedName("PaymentCurrency")
    private String paymentCurrency;
    /** Specifies the purpose code that resulted in a payment initiation. */
    @SerializedName("PurposeCode")
    private String purposeCode;
    /** Data about the PSU session, this information is used for fraud detection by the ASPSP. */
    @SerializedName("PsuSessionInformation")
    private PsuSessionInformation psuSessionInformation;
    /** Information used for risk scoring by the ASPSP. */
    @SerializedName("RiskInformation")
    private RiskInformation riskInformation;
    /** Payment preferred SCA. */
    @SerializedName("PreferredScaMethod")
    private List<String> preferredScaMethod;
    /** Charge bearer */
    @SerializedName("ChargeBearer")
    private String chargeBearer;
    /** Id of the PSU */
    @SerializedName("PsuId")
    private String psuId;
    /** Username of the PSU with his bank. Unique identifier. */
    @SerializedName("AspspPsuId")
    private String aspspPsuId;
    /** Product code of ASPSP. */
    @SerializedName("AspspProductCode")
    private String aspspProductCode;
    /** Indicates the requested payment method. */
    @SerializedName("PaymentProduct")
    private String paymentProduct;


    PaymentInitiationRequest( PaymentInitiationRequestBuilder builder ) {
        super(builder);
        this.aspspId = builder.aspspId;
        this.endToEndId = builder.endToEndId;
        this.initiatingPartyReferenceId = builder.initiatingPartyReferenceId;
        this.initiatingPartyReturnUrl = builder.initiatingPartyReturnUrl;
        this.initiatingPartySubId = builder.initiatingPartySubId;
        this.remittanceInformation = builder.remittanceInformation;
        this.remittanceInformationStructured = builder.remittanceInformationStructured;
        this.debtorAccount = builder.debtorAccount;
        this.ultimateDebtor = builder.ultimateDebtor;
        this.creditorName = builder.creditorName;
        this.creditorAgent = builder.creditorAgent;
        this.creditorAccount = builder.creditorAccount;
        this.ultimateCreditor = builder.ultimateCreditor;
        this.creditorPostalAddress = builder.creditorPostalAddress;
        this.paymentAmount = builder.paymentAmount;
        this.paymentCurrency = builder.paymentCurrency;
        this.purposeCode = builder.purposeCode;
        this.psuSessionInformation = builder.psuSessionInformation;
        this.riskInformation = builder.riskInformation;
        this.preferredScaMethod = builder.preferredScaMethod;
        this.chargeBearer = builder.chargeBearer;
        this.psuId = builder.psuId;
        this.aspspPsuId = builder.aspspPsuId;
        this.aspspProductCode = builder.aspspProductCode;
        this.paymentProduct = builder.paymentProduct;
    }

    public static class PaymentInitiationRequestBuilder extends EquensApiMessageBuilder {

        private String aspspId;
        private String endToEndId;
        private String initiatingPartyReferenceId;
        private String initiatingPartyReturnUrl;
        private String initiatingPartySubId;
        private String remittanceInformation;
        private RemittanceInformationStructured remittanceInformationStructured;
        private Account debtorAccount;
        private String ultimateDebtor;
        private String creditorName;
        private String creditorAgent;
        private Account creditorAccount;
        private String ultimateCreditor;
        private Address creditorPostalAddress;
        private String paymentAmount;
        private String paymentCurrency;
        private String purposeCode;
        private PsuSessionInformation psuSessionInformation;
        private RiskInformation riskInformation;
        private List<String> preferredScaMethod;
        private String chargeBearer;
        private String psuId;
        private String aspspPsuId;
        private String aspspProductCode;
        private String paymentProduct;

        public PaymentInitiationRequestBuilder withAspspId(String aspspId) {
            this.aspspId = aspspId;
            return this;
        }

        public PaymentInitiationRequestBuilder withEndToEndId(String endToEndId) {
            this.endToEndId = endToEndId;
            return this;
        }

        public PaymentInitiationRequestBuilder withInitiatingPartyReferenceId(String initiatingPartyReferenceId) {
            this.initiatingPartyReferenceId = initiatingPartyReferenceId;
            return this;
        }

        public PaymentInitiationRequestBuilder withInitiatingPartyReturnUrl(String initiatingPartyReturnUrl) {
            this.initiatingPartyReturnUrl = initiatingPartyReturnUrl;
            return this;
        }

        public PaymentInitiationRequestBuilder withInitiatingPartySubId(String initiatingPartySubId) {
            this.initiatingPartySubId = initiatingPartySubId;
            return this;
        }

        public PaymentInitiationRequestBuilder withRemittanceInformation(String remittanceInformation) {
            this.remittanceInformation = remittanceInformation;
            return this;
        }

        public PaymentInitiationRequestBuilder withRemittanceInformationStructured(RemittanceInformationStructured remittanceInformationStructured) {
            this.remittanceInformationStructured = remittanceInformationStructured;
            return this;
        }

        public PaymentInitiationRequestBuilder withDebtorAccount(Account debtorAccount) {
            this.debtorAccount = debtorAccount;
            return this;
        }

        public PaymentInitiationRequestBuilder withUltimateDebtor(String ultimateDebtor) {
            this.ultimateDebtor = ultimateDebtor;
            return this;
        }

        public PaymentInitiationRequestBuilder withCreditorName(String creditorName) {
            this.creditorName = creditorName;
            return this;
        }

        public PaymentInitiationRequestBuilder withCreditorAgent(String creditorAgent) {
            this.creditorAgent = creditorAgent;
            return this;
        }

        public PaymentInitiationRequestBuilder withCreditorAccount(Account creditorAccount) {
            this.creditorAccount = creditorAccount;
            return this;
        }

        public PaymentInitiationRequestBuilder withUltimateCreditor(String ultimateCreditor) {
            this.ultimateCreditor = ultimateCreditor;
            return this;
        }

        public PaymentInitiationRequestBuilder withCreditorPostalAddress(Address creditorPostalAddress) {
            this.creditorPostalAddress = creditorPostalAddress;
            return this;
        }

        public PaymentInitiationRequestBuilder withPaymentAmount(String paymentAmount) {
            this.paymentAmount = paymentAmount;
            return this;
        }

        public PaymentInitiationRequestBuilder withPaymentCurrency(String paymentCurrency) {
            this.paymentCurrency = paymentCurrency;
            return this;
        }

        public PaymentInitiationRequestBuilder withPurposeCode(String purposeCode) {
            this.purposeCode = purposeCode;
            return this;
        }

        public PaymentInitiationRequestBuilder withPsuSessionInformation(PsuSessionInformation psuSessionInformation) {
            this.psuSessionInformation = psuSessionInformation;
            return this;
        }

        public PaymentInitiationRequestBuilder withRiskInformation(RiskInformation riskInformation) {
            this.riskInformation = riskInformation;
            return this;
        }

        public PaymentInitiationRequestBuilder addPreferredScaMethod(String preferredScaMethod) {
            if( this.preferredScaMethod == null ){
                this.preferredScaMethod = new ArrayList<>();
            }
            this.preferredScaMethod.add( preferredScaMethod );
            return this;
        }

        public PaymentInitiationRequestBuilder withChargeBearer(String chargeBearer) {
            this.chargeBearer = chargeBearer;
            return this;
        }

        public PaymentInitiationRequestBuilder withPsuId(String psuId) {
            this.psuId = psuId;
            return this;
        }

        public PaymentInitiationRequestBuilder withAspspPsuId(String aspspPsuId) {
            this.aspspPsuId = aspspPsuId;
            return this;
        }

        public PaymentInitiationRequestBuilder withAspspProductCode(String aspspProductCode) {
            this.aspspProductCode = aspspProductCode;
            return this;
        }

        public PaymentInitiationRequestBuilder withPaymentProduct(String paymentProduct) {
            this.paymentProduct = paymentProduct;
            return this;
        }

        public PaymentInitiationRequest build(){
            return new PaymentInitiationRequest( this );
        }

    }
}
