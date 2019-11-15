package torenameEquens.bean.business.reachdirectory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import torenameEquens.bean.business.EquensApiResponse;

import java.util.List;

public class GetAspspsResponse extends EquensApiResponse {

    @SerializedName("Application")
    private String application;
    @SerializedName("ASPSP")
    private List<Aspsp> aspsps;

    public String getApplication() {
        return application;
    }

    public List<Aspsp> getAspsps() {
        return aspsps;
    }

    public static GetAspspsResponse fromJson(String json ){
        return new Gson().fromJson( json, GetAspspsResponse.class );
    }
}
