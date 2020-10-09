package com.payline.payment.equens.bean.business.reachdirectory;

import com.google.gson.annotations.SerializedName;

public class Detail {

    @SerializedName("Api")
    private String api;

    @SerializedName("FieldName")
    private String fieldName;

    @SerializedName("Value")
    private String value;

    @SerializedName("ProtocolVersion")
    private String protocolVersion;

    public String getApi() {
        return api;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValue() {
        return value;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
