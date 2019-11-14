package torenameEquens.bean.business.authorization;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Standard response format to an OAuth access token request, as described in RFC 6749.
 * @see https://tools.ietf.org/html/rfc6749#section-5.1
 */
public class RFC6749AccessTokenSuccessResponse {

    /**
     * The access token issued by the authorization server
     */
    @SerializedName("access_token")
    private String accessToken;

    /**
     * The type of the token issued as described in Section 7.1
     */
    @SerializedName("token_type")
    private String tokenType;

    /**
     * The lifetime in seconds of the access token.
     * For example, the value "3600" denotes that the access token will expire in one hour
     * from the time the response was generated.
     */
    @SerializedName("expires_in")
    private Integer expiresIn;

    /**
     * The refresh token, which can be used to obtain new access tokens using the same
     * authorization grant as described in Section 6.
     */
    @SerializedName("refresh_token")
    private String refreshToken;

    /**
     * The scope of the access token as described by Section 3.3.
     */
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public static RFC6749AccessTokenSuccessResponse fromJson(String json ){
        return new Gson().fromJson( json, RFC6749AccessTokenSuccessResponse.class );
    }
}
