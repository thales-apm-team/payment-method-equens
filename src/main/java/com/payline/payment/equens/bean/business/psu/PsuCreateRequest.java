package com.payline.payment.equens.bean.business.psu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Psu create request attributes are the union of the ones from Psu class and the ones from EquensApiMessage class.
 * As Java does not allow multiple inheritance, we must copy one of the 2 sets of attributes here.
 */
public class PsuCreateRequest extends Psu {

    @SerializedName("MessageCreateDateTime")
    private Date messageCreateDateTime;
    @SerializedName("MessageId")
    private String messageId;

    PsuCreateRequest( PsuCreateRequestBuilder builder ){
        super(builder);
        this.messageCreateDateTime = builder.messageCreateDateTime;
        this.messageId = builder.messageId;
    }

    public static class PsuCreateRequestBuilder extends PsuBuilder {

        private Date messageCreateDateTime = new Date();
        private String messageId = "PAYLINE" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        @Override
        public PsuCreateRequest build(){
            return new PsuCreateRequest(this);
        }
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();
        return gson.toJson( this );
    }

}
