package com.payline.payment.equens.bean.business.reachdirectory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.payline.payment.equens.bean.business.EquensApiMessage;

import java.util.List;

public class GetAspspsResponse extends EquensApiMessage {

    @SerializedName("ASPSP")
    private List<Aspsp> aspsps;

    GetAspspsResponse(EquensApiMessageBuilder builder) {
        super(builder);
    }

    public List<Aspsp> getAspsps() {
        return aspsps;
    }

    public static GetAspspsResponse fromJson(String json ){
        return new Gson().fromJson( json, GetAspspsResponse.class );
    }
}
