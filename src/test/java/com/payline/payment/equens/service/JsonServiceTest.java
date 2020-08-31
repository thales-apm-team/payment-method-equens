package com.payline.payment.equens.service;

import com.payline.payment.equens.bean.business.payment.WalletPaymentData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonServiceTest {
    JsonService jsonService = JsonService.getInstance();

    String s = "{\"bic\":\"PSSTFRPP\",\"iban\":\"anIban\"}";
    WalletPaymentData paymentData = new WalletPaymentData.WalletPaymentDataBuilder()
            .withBic("PSSTFRPP")
            .withIban("anIban")
            .build();

    @Test
    void fromJson() {
        WalletPaymentData walletPaymentData = jsonService.fromJson(s, WalletPaymentData.class);
        Assertions.assertEquals(paymentData.getBic(), walletPaymentData.getBic());
        Assertions.assertEquals(paymentData.getIban(), walletPaymentData.getIban());
    }


    @Test
    void toJson() {
        Assertions.assertEquals(s, jsonService.toJson(paymentData));
    }
}
