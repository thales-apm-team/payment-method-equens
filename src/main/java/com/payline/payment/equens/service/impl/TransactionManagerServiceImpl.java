package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.bean.pmapi.TransactionAdditionalData;
import com.payline.pmapi.service.TransactionManagerService;

import java.util.HashMap;
import java.util.Map;

public class TransactionManagerServiceImpl implements TransactionManagerService {

    @Override
    public Map<String, String> readAdditionalData(String s, String s1) {
        Map<String, String> additionalData = new HashMap<>();

        TransactionAdditionalData transactionAdditionalData = TransactionAdditionalData.fromJson(s);
        if( transactionAdditionalData != null && transactionAdditionalData.getAspspPaymentId() != null ){
            additionalData.put("AspspPaymentId", transactionAdditionalData.getAspspPaymentId());
        }

        return additionalData;
    }

}
