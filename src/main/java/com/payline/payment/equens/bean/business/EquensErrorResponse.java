package com.payline.payment.equens.bean.business;

import com.google.gson.Gson;

public class EquensErrorResponse extends EquensApiMessage {

    private String code;
    private String message;
    private String details;
    private Link link;

    EquensErrorResponse(EquensErrorResponseBuilder builder) {
        super(builder);
        this.code = builder.code;
        this.message = builder.message;
        this.details = builder.details;
        this.link = builder.link;
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

        Link( String href ){
            this.href = href;
        }

        public String getHref() {
            return href;
        }
    }

    public static class EquensErrorResponseBuilder extends EquensApiMessageBuilder {

        private String code;
        private String message;
        private String details;
        private Link link;

        public EquensErrorResponseBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public EquensErrorResponseBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public EquensErrorResponseBuilder withDetails(String details) {
            this.details = details;
            return this;
        }

        public EquensErrorResponseBuilder withLink(String href) {
            this.link = new Link( href );
            return this;
        }

        public EquensErrorResponse build(){
            return new EquensErrorResponse( this );
        }
    }

    public static EquensErrorResponse fromJson( String json ){
        return new Gson().fromJson( json, EquensErrorResponse.class );
    }

}
