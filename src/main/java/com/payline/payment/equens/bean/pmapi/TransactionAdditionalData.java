package com.payline.payment.equens.bean.pmapi;

import com.google.gson.Gson;

public class TransactionAdditionalData {

    private String aspspPaymentId;

    public TransactionAdditionalData(String aspspPaymentId) {
        this.aspspPaymentId = aspspPaymentId;
    }

    public String getAspspPaymentId() {
        return aspspPaymentId;
    }

    @Override
    public String toString() {
        return new Gson().toJson( this );
    }

    public static TransactionAdditionalData fromJson(String json ){
        return new Gson().fromJson( json, TransactionAdditionalData.class );
    }
}
