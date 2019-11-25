package com.payline.payment.equens.bean.business.payment;

import com.google.gson.annotations.SerializedName;

/**
 * Strong Customer Authentication challenge.
 */
public class ScaChallenge {

    /** Specifies the SCA type. */
    @SerializedName("AuthenticationType")
    private String authenticationType;
    /** Defines the ASPSP specific version of the SCA type. */
    @SerializedName("AuthenticationVersion")
    private String authenticationVersion;
    /** The unique ASPSP identifier of the authentication */
    @SerializedName("AuthenticationMethodId")
    private String authenticationMethodId;
    /** The name of the authentication method, if provided by the ASPSP. */
    @SerializedName("AuthenticationName")
    private String authenticationName;
    /** Additional explanation. */
    @SerializedName("AuthenticationExplanation")
    private String authenticationExplanation;
    /** ? */
    @SerializedName("ChallengeData")
    private String challengeData;
    /** ? */
    @SerializedName("ChallengeImage")
    private String challengeImage;
    /** ? */
    @SerializedName("ChallengeImageLink")
    private String challengeImageLink;
    /** ? */
    @SerializedName("ChallengeMaxLength")
    private String challengeMaxLength;
    /** ? */
    @SerializedName("ChallengeFormat")
    private String challengeFormat;
    /** ? */
    @SerializedName("ChallengeAdditionalInformation")
    private String challengeAdditionalInformation;


    public String getAuthenticationType() {
        return authenticationType;
    }

    public String getAuthenticationVersion() {
        return authenticationVersion;
    }

    public String getAuthenticationMethodId() {
        return authenticationMethodId;
    }

    public String getAuthenticationName() {
        return authenticationName;
    }

    public String getAuthenticationExplanation() {
        return authenticationExplanation;
    }

    public String getChallengeData() {
        return challengeData;
    }

    public String getChallengeImage() {
        return challengeImage;
    }

    public String getChallengeImageLink() {
        return challengeImageLink;
    }

    public String getChallengeMaxLength() {
        return challengeMaxLength;
    }

    public String getChallengeFormat() {
        return challengeFormat;
    }

    public String getChallengeAdditionalInformation() {
        return challengeAdditionalInformation;
    }
}
