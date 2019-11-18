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


    ScaChallenge( ScaChallengeBuilder builder ){
        this.authenticationType = builder.authenticationType;
        this.authenticationVersion = builder.authenticationVersion;
        this.authenticationMethodId = builder.authenticationMethodId;
        this.authenticationName = builder.authenticationName;
        this.authenticationExplanation = builder.authenticationExplanation;
        this.challengeData = builder.challengeData;
        this.challengeImage = builder.challengeImage;
        this.challengeImageLink = builder.challengeImageLink;
        this.challengeMaxLength = builder.challengeMaxLength;
        this.challengeFormat = builder.challengeFormat;
        this.challengeAdditionalInformation = builder.challengeAdditionalInformation;
    }

    public static class ScaChallengeBuilder {

        private String authenticationType;
        private String authenticationVersion;
        private String authenticationMethodId;
        private String authenticationName;
        private String authenticationExplanation;
        private String challengeData;
        private String challengeImage;
        private String challengeImageLink;
        private String challengeMaxLength;
        private String challengeFormat;
        private String challengeAdditionalInformation;

        public ScaChallengeBuilder withAuthenticationType(String authenticationType) {
            this.authenticationType = authenticationType;
            return this;
        }

        public ScaChallengeBuilder withAuthenticationVersion(String authenticationVersion) {
            this.authenticationVersion = authenticationVersion;
            return this;
        }

        public ScaChallengeBuilder withAuthenticationMethodId(String authenticationMethodId) {
            this.authenticationMethodId = authenticationMethodId;
            return this;
        }

        public ScaChallengeBuilder withAuthenticationName(String authenticationName) {
            this.authenticationName = authenticationName;
            return this;
        }

        public ScaChallengeBuilder withAuthenticationExplanation(String authenticationExplanation) {
            this.authenticationExplanation = authenticationExplanation;
            return this;
        }

        public ScaChallengeBuilder withChallengeData(String challengeData) {
            this.challengeData = challengeData;
            return this;
        }

        public ScaChallengeBuilder withChallengeImage(String challengeImage) {
            this.challengeImage = challengeImage;
            return this;
        }

        public ScaChallengeBuilder withChallengeImageLink(String challengeImageLink) {
            this.challengeImageLink = challengeImageLink;
            return this;
        }

        public ScaChallengeBuilder withChallengeMaxLength(String challengeMaxLength) {
            this.challengeMaxLength = challengeMaxLength;
            return this;
        }

        public ScaChallengeBuilder withChallengeFormat(String challengeFormat) {
            this.challengeFormat = challengeFormat;
            return this;
        }

        public ScaChallengeBuilder withChallengeAdditionalInformation(String challengeAdditionalInformation) {
            this.challengeAdditionalInformation = challengeAdditionalInformation;
            return this;
        }

        public ScaChallenge build(){
            return new ScaChallenge( this );
        }
    }

}
