package torenameEquens.bean.business.authorization;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Standard error response format to an OAuth access token request, as described in RFC 6749.
 * @see https://tools.ietf.org/html/rfc6749#section-5.2
 */
public class RFC6749AccessTokenErrorResponse {

    /**
     * A single ASCII error code from the following :
     * invalid_request, invalid_client, invalid_grant, unauthorized_client, unsupported_grant_type, invalid_scope
     */
    private String error;

    /**
     * Human-readable ASCII [USASCII] text providing additional information, used to assist
     * the client developer in understanding the error that occurred.
     */
    @SerializedName("error_description")
    private String errorDescription;

    /**
     * A URI identifying a human-readable web page with information about the error, used to
     * provide the client developer with additional information about the error.
     */
    @SerializedName("error_uri")
    private String errorUri;

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getErrorUri() {
        return errorUri;
    }

    public static RFC6749AccessTokenErrorResponse fromJson(String json ){
        return new Gson().fromJson( json, RFC6749AccessTokenErrorResponse.class );
    }

}
