package com.payline.payment.equens.bean;

import com.payline.pmapi.bean.Request;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.common.SubMerchant;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.WalletPaymentRequest;

import java.util.Locale;

public class GenericPaymentRequest implements Request {
    private Locale locale;
    private Amount amount;
    private Order order;
    private Buyer buyer;
    private ContractConfiguration contractConfiguration;
    private Browser browser;
    private Environment environment;
    private PaymentFormContext paymentFormContext;
    private RequestContext requestContext;
    private String transactionId;
    private String softDescriptor;
    private boolean captureNow;
    private PartnerConfiguration partnerConfiguration;
    private SubMerchant subMerchant;
    private String merchantName;
    private String pluginConfiguration;

    public GenericPaymentRequest(PaymentRequest request) {
        this.locale = request.getLocale();
        this.amount = request.getAmount();
        this.order = request.getOrder();
        this.buyer = request.getBuyer();
        this.contractConfiguration = request.getContractConfiguration();
        this.browser = request.getBrowser();
        this.environment = request.getEnvironment();
        this.paymentFormContext = request.getPaymentFormContext();
        this.requestContext = request.getRequestContext();
        this.transactionId = request.getTransactionId();
        this.softDescriptor = request.getSoftDescriptor();
        this.captureNow = request.isCaptureNow();
        this.partnerConfiguration = request.getPartnerConfiguration();
        this.subMerchant = request.getSubMerchant();
        this.merchantName = request.getMerchantName();
        this.pluginConfiguration = request.getPluginConfiguration();
    }

    public GenericPaymentRequest(WalletPaymentRequest request){
        this.locale = request.getLocale();
        this.amount = request.getAmount();
        this.order = request.getOrder();
        this.buyer = request.getBuyer();
        this.contractConfiguration = request.getContractConfiguration();
        this.browser = request.getBrowser();
        this.environment = request.getEnvironment();
        this.paymentFormContext = request.getPaymentFormContext();
        this.requestContext = request.getRequestContext();
        this.transactionId = request.getTransactionId();
        this.softDescriptor = request.getSoftDescriptor();
        this.captureNow = request.isCaptureNow();
        this.partnerConfiguration = request.getPartnerConfiguration();
        this.subMerchant = request.getSubMerchant();
        this.merchantName = request.getMerchantName();
        this.pluginConfiguration = request.getPluginConfiguration();
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public ContractConfiguration getContractConfiguration() {
        return this.contractConfiguration;
    }

    public Locale getLocale() {
        return locale;
    }

    public Amount getAmount() {
        return amount;
    }

    public Order getOrder() {
        return order;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public Browser getBrowser() {
        return browser;
    }

    public PaymentFormContext getPaymentFormContext() {
        return paymentFormContext;
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSoftDescriptor() {
        return softDescriptor;
    }

    public boolean isCaptureNow() {
        return captureNow;
    }

    public PartnerConfiguration getPartnerConfiguration() {
        return partnerConfiguration;
    }

    public SubMerchant getSubMerchant() {
        return subMerchant;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getPluginConfiguration() {
        return pluginConfiguration;
    }
}
