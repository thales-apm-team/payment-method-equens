package com.payline.payment.equens.bean.business.payment;

public enum PaymentStatus {

    OPEN("OPEN"),
    AUTHORIZED("AUTHORISED"), // As described in Swagger files...
    SETTLEMENT_IN_PROCESS("SETTLEMENT_IN_PROCESS"),
    SETTLEMENT_COMPLETED("SETTLEMENT_COMPLETED"),
    CANCELLED("CANCELLED"),
    ERROR("ERROR"),
    EXPIRED("EXPIRED"),
    PENDING("PENDING ");

    private String name;

    PaymentStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PaymentStatus fromName( String name ){
        for (PaymentStatus value : PaymentStatus.values()) {
            if( value.getName().equals( name ) ){
                return value;
            }
        }
        return null;
    }
}
