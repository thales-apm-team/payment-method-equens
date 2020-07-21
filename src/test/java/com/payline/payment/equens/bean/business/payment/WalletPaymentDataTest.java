package com.payline.payment.equens.bean.business.payment;

import com.payline.payment.equens.MockUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletPaymentDataTest {

    public WalletPaymentData create() {
        return MockUtils.aWalletPaymentData();
    }

    @Test
    void getBic() {
        Assertions.assertEquals("PSSTFRPP", create().getBic());
    }

    @Test
    void getIban() {
        Assertions.assertEquals("anIbanWithMoreThan8Charactere", create().getIban());
    }

    @Test
    void testToString() {
        Assertions.assertEquals("{\"bic\":\"PSSTFRPP\",\"iban\":\"anIbanWithMoreThan8Charactere\"}",
                create().toString());
    }
}