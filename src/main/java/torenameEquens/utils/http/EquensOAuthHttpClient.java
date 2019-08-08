package torenameEquens.utils.http;

abstract class EquensOAuthHttpClient extends OAuthHttpClient {

    @Override
    protected String authorizationHeaderValue() {
        // TODO !
        return null;
    }

    protected abstract String appName();

}
