package com.payline.payment.equens.bean.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class EquensApiMessage {

    @SerializedName("MessageCreateDateTime")
    private Date messageCreateDateTime;
    @SerializedName("MessageId")
    private String messageId;

    protected EquensApiMessage( EquensApiMessageBuilder builder ){
        this.messageCreateDateTime = builder.messageCreateDateTime;
        this.messageId = builder.messageId;
    }

    public Date getMessageCreateDateTime() {
        return messageCreateDateTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public static class EquensApiMessageBuilder {

        private Date messageCreateDateTime = new Date();
        private String messageId = "PAYLINE" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        public EquensApiMessageBuilder withMessageCreateDateTime(Date messageCreateDateTime) {
            this.messageCreateDateTime = messageCreateDateTime;
            return this;
        }

        public EquensApiMessageBuilder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        // no "build" method as EquensApiMessage is abstract
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();
        return gson.toJson( this );
    }
}
