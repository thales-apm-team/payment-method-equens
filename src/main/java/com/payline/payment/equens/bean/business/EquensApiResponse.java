package com.payline.payment.equens.bean.business;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public abstract class EquensApiResponse {

    @SerializedName("MessageCreateDateTime")
    private Date messageCreateDateTime;
    @SerializedName("MessageId")
    private String messageId;

    public Date getMessageCreateDateTime() {
        return messageCreateDateTime;
    }

    public String getMessageId() {
        return messageId;
    }
}
