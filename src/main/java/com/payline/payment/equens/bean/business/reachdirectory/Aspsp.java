package com.payline.payment.equens.bean.business.reachdirectory;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Aspsp {

    @SerializedName("AspspId")
    private String aspspId;
    @SerializedName("BIC")
    private String bic;
    @SerializedName("CountryCode")
    private String countryCode;
    // Do not map Details because it would require another bean that we would not use anyway...
    @SerializedName("Name")
    private List<String> name;

    public String getAspspId() {
        return aspspId;
    }

    public String getBic() {
        return bic;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public List<String> getName() {
        return name;
    }
}
