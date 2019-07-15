package com.payline.payment.equens.bean.business;

import com.google.gson.Gson;

public class EquensErrorResponse extends EquensApiMessage {

    private String code;
    private String message;
    private String details;
    private Link link;

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

    public Link getLink() {
        return link;
    }

    private static class Link {
        private String href;

        public String getHref() {
            return href;
        }
    }

    public static EquensErrorResponse fromJson( String json ){
        return new Gson().fromJson( json, EquensErrorResponse.class );
    }

}
