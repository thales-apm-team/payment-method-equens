package com.payline.payment.equens.service.impl;

import com.payline.pmapi.service.TransactionManagerService;

import java.util.HashMap;
import java.util.Map;

public class TransactionManagerServiceImpl implements TransactionManagerService {

    @Override
    public Map<String, String> readAdditionalData(String s, String s1) {
        // TODO: check if there is a need for transaction additional data
        return new HashMap<>();
    }

}
