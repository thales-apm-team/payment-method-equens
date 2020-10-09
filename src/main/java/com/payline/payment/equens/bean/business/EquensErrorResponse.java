package com.payline.payment.equens.bean.business;

public class EquensErrorResponse extends EquensApiMessage {

    private String code;
    private String message;
    private String details;

    EquensErrorResponse(EquensApiMessageBuilder builder) {
        super(builder);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

}
