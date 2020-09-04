package com.payline.payment.equens.bean.business.payment;

import com.payline.payment.equens.MockUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PaymentDataTest {

    public PaymentData create() {
        return MockUtils.aPaymentData();
    }

    @Test
    void getBic() {
        Assertions.assertEquals("PSSTFRPP", create().getBic());
    }

    @Test
    void getIban() {
        Assertions.assertEquals("anIbanWithMoreThan8Charactere", create().getIban());
    }

}