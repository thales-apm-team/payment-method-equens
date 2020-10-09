package com.payline.payment.equens.service;

import com.payline.payment.equens.bean.business.payment.PaymentData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonServiceTest {
    JsonService jsonService = JsonService.getInstance();

    String expectedJson = "{\"bic\":\"PSSTFRPP\",\"iban\":\"anIban\"}";
    PaymentData expectedPaymentData = new PaymentData.PaymentDataBuilder()
            .withBic("PSSTFRPP")
            .withIban("anIban")
            .build();

    @Test
    void fromJson() {
        PaymentData paymentData = jsonService.fromJson(expectedJson, PaymentData.class);
        Assertions.assertEquals(expectedPaymentData.getBic(), paymentData.getBic());
        Assertions.assertEquals(expectedPaymentData.getIban(), paymentData.getIban());
    }


    @Test
    void toJson() {
        Assertions.assertEquals(expectedJson, jsonService.toJson(expectedPaymentData));
    }
}
