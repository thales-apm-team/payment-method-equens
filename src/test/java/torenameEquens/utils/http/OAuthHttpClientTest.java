package torenameEquens.utils.http;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;
import torenameEquens.exception.InvalidDataException;
import torenameEquens.exception.PluginException;
import torenameEquens.utils.MockUtils;
import torenameEquens.utils.TestUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static torenameEquens.utils.http.HttpTestUtils.mockHttpResponse;

public class OAuthHttpClientTest {

    /**
     * Private class required to test the Abstract class {@link OAuthHttpClient}.
     */
    private class TestableHttpClient extends OAuthHttpClient {
        @Override
        protected String authorizationHeaderValue() {
            return null;
        }
    }

    @InjectMocks
    private TestableHttpClient oAuthHttpClient;

    @Mock
    private CloseableHttpClient client;

    @BeforeEach
    void setup() throws NoSuchFieldException {
        // Init tested instance and inject mocks
        oAuthHttpClient = new TestableHttpClient();
        MockitoAnnotations.initMocks(this);

        // Manual init of private attributes
        FieldSetter.setField( oAuthHttpClient, OAuthHttpClient.class.getDeclaredField("retries"), 3);
        FieldSetter.setField( oAuthHttpClient, OAuthHttpClient.class.getDeclaredField("tokenEndpointUrl"), "https://authorization.domain.org/token");
    }

    @Test
    void authorize_alreadyAuthorized(){
        // given: a valid authorization is already stored in the client
        TestableHttpClient spiedClient = Mockito.spy( oAuthHttpClient );
        doReturn( true ).when( spiedClient ).isAuthorized();

        // when: calling the authorize method
        spiedClient.authorize();

        // then: no HTTP call is made
        verify( spiedClient, never() ).execute( any( HttpRequestBase.class ) );
    }

    @Test
    void authorize_invalidApiUrl() throws NoSuchFieldException {
        // given: the API base URL is invalid
        FieldSetter.setField( oAuthHttpClient, OAuthHttpClient.class.getDeclaredField("tokenEndpointUrl"), "https:||authorization.domain.org/token");

        // when calling the authorize method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> oAuthHttpClient.authorize());
    }

    /**
     * This is a test set of server response contents to an authorization request,
     * which SHOULD prevent the building of a valid Authorization.
     * In all these cases, the authorize() method is expected to throw an exception.
     */
    private static Stream<String> authorize_blockingResponseContent_set() {
        Stream.Builder<String> builder = Stream.builder();
        // non valid JSON content
        builder.accept( "{/}" );
        // valid JSON content, but not an object
        builder.accept( "[]" );
        // the response does not contain an access_token
        builder.accept( "{\"token_type\":\"Bearer\",\"expires_in\":1800,\"scope\":\"scope_value\"}" );

        return builder.build();
    }

    @ParameterizedTest
    @MethodSource("authorize_blockingResponseContent_set")
    void authorize_blockingResponseContent( String responseContent ) throws NoSuchFieldException {
        // given: the server returns a response with a non-sufficient content
        TestableHttpClient spiedClient = Mockito.spy( oAuthHttpClient );
        StringResponse response = new StringResponse();
        FieldSetter.setField( response, StringResponse.class.getDeclaredField("content"), responseContent);
        FieldSetter.setField( response, StringResponse.class.getDeclaredField("statusCode"), HttpStatus.SC_OK);
        FieldSetter.setField( response, StringResponse.class.getDeclaredField("statusMessage"), "OK");
        doReturn( response ).when( spiedClient ).execute( any(HttpPost.class) );

        // when: calling the authorize method, an exception is thrown
        assertThrows( PluginException.class, spiedClient::authorize );
    }

    /**
     * This is a test set of server response contents to an authorization request,
     * which SHOULD NOT prevent the service from building a valid Authorization instance.
     * In all these cases, a valid authorization must be built in the end.
     */
    private static Stream<String> authorize_nonBlockingResponseContent_set(){
        Stream.Builder<String> builder = Stream.builder();

        // nominal case
        builder.accept( "{\"access_token\":\"ABCD012345679\",\"token_type\":\"Bearer\",\"expires_in\":1800,\"scope\":\"scope_value\"}" );
        // no token type
        builder.accept( "{\"access_token\":\"ABCD012345679\",\"expires_in\":1800,\"scope\":\"scope_value\"}" );
        // no expiration delay
        builder.accept( "{\"access_token\":\"ABCD012345679\",\"token_type\":\"Bearer\",\"scope\":\"scope_value\"}" );
        // no scope
        builder.accept( "{\"access_token\":\"ABCD012345679\",\"token_type\":\"Bearer\",\"expires_in\":1800}" );

        return builder.build();
    }

    @ParameterizedTest
    @MethodSource("authorize_nonBlockingResponseContent_set")
    void authorize_nonBlockingResponseContent( String responseContent ) throws NoSuchFieldException {
        // given: the server returns a response with a sufficient content
        TestableHttpClient spiedClient = Mockito.spy( oAuthHttpClient );
        StringResponse response = new StringResponse();
        FieldSetter.setField( response, StringResponse.class.getDeclaredField("content"), responseContent);
        FieldSetter.setField( response, StringResponse.class.getDeclaredField("statusCode"), HttpStatus.SC_OK);
        FieldSetter.setField( response, StringResponse.class.getDeclaredField("statusMessage"), "OK");
        doReturn( response ).when( spiedClient ).execute( any(HttpPost.class) );

        // when: calling the authorize method
        spiedClient.authorize();

        // then: the client now contains a valid authorization
        assertTrue( spiedClient.isAuthorized() );
    }

    /**
     * This is a test set of server response errors to an authorization request.
     * In all these cases, the authorize() method is expected to throw a {@link PluginException}.
     */
    private static Stream<Arguments> authorize_error_set(){
        Stream.Builder<Arguments> builder = Stream.builder();

        // nominal case
        builder.accept( Arguments.of( "{\"error\":\"invalid_request\",\"error_description\":\"Some description of the error\",\"error_uri\":\"http://authorization.domain.org/token/errorInfo\"}", HttpStatus.SC_BAD_REQUEST, "Bad Request" ) );
        // only error attribute
        builder.accept( Arguments.of( "{\"error\":\"unauthorized_client\"}", HttpStatus.SC_UNAUTHORIZED, "Unauthorized" ) );
        // invalid JSON
        builder.accept( Arguments.of( "{/}", HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal Server Error" ) );
        // valid JSON, but does not match the spec
        builder.accept( Arguments.of( "[]", HttpStatus.SC_BAD_REQUEST, "Bad Request" ) );
        builder.accept( Arguments.of( "{\"error\":\"[invalid_request, invalid_client]}", HttpStatus.SC_BAD_REQUEST, "Bad Request" ) );

        return builder.build();
    }

    @ParameterizedTest
    @MethodSource("authorize_error_set")
    void authorize_error( String content, int statusCode, String statusMessage ) throws NoSuchFieldException {
        // given: the server returns an error
        TestableHttpClient spiedClient = Mockito.spy( oAuthHttpClient );
        StringResponse response = new StringResponse();
        FieldSetter.setField( response, StringResponse.class.getDeclaredField("content"), content);
        FieldSetter.setField( response, StringResponse.class.getDeclaredField("statusCode"), statusCode);
        FieldSetter.setField( response, StringResponse.class.getDeclaredField("statusMessage"), statusMessage);
        doReturn( response ).when( spiedClient ).execute( any(HttpPost.class) );

        // when: calling the authorize method, an exception is thrown
        assertThrows( PluginException.class, spiedClient::authorize );
    }

    @Test
    void execute_nominal() throws IOException {
        // given: a properly formatted request, which gets a proper response
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        int expectedStatusCode = 200;
        String expectedStatusMessage = "OK";
        String expectedContent = "{\"content\":\"fake\"}";
        doReturn( mockHttpResponse( expectedStatusCode, expectedStatusMessage, expectedContent, null ) )
                .when( client ).execute( request );

        // when: sending the request
        StringResponse stringResponse = oAuthHttpClient.execute( request );

        // then: the content of the StringResponse reflects the content of the HTTP response
        assertNotNull( stringResponse );
        assertEquals( expectedStatusCode, stringResponse.getStatusCode() );
        assertEquals( expectedStatusMessage, stringResponse.getStatusMessage() );
        assertEquals( expectedContent, stringResponse.getContent() );
    }

    @Test
    void execute_retry() throws IOException {
        // given: the first 2 requests end up in timeout, the third request gets a response
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        when( client.execute( request ) )
                .thenThrow( ConnectTimeoutException.class )
                .thenThrow( ConnectTimeoutException.class )
                .thenReturn( mockHttpResponse( 200, "OK", "content", null) );

        // when: sending the request
        StringResponse stringResponse = oAuthHttpClient.execute( request );

        // then: the client finally gets the response
        assertNotNull( stringResponse );
    }

    @Test
    void execute_retryFail() throws IOException {
        // given: a request which always gets an exception
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        doThrow( IOException.class ).when( client ).execute( request );

        // when: sending the request, a PluginException is thrown
        assertThrows( PluginException.class, () -> oAuthHttpClient.execute( request ) );
    }

    @Test
    void execute_invalidResponse() throws IOException {
        // given: a request that gets an invalid response (null)
        HttpGet request = new HttpGet("http://domain.test.fr/malfunctioning-endpoint");
        doReturn( null ).when( client ).execute( request );

        // when: sending the request, a PluginException is thrown
        assertThrows( PluginException.class, () -> oAuthHttpClient.execute( request ) );
    }

    @Test
    void isAuthorized_valid() throws NoSuchFieldException {
        // given: a valid authorization
        Authorization validAuth = MockUtils.anAuthorizationBuilder().build();
        FieldSetter.setField( oAuthHttpClient, OAuthHttpClient.class.getDeclaredField("authorization"), validAuth);

        // when called, isAuthorized method returns true
        assertTrue( oAuthHttpClient.isAuthorized() );
    }

    @Test
    void isAuthorized_null() throws NoSuchFieldException {
        // given: the client does not contain a valid authorization
        FieldSetter.setField( oAuthHttpClient, OAuthHttpClient.class.getDeclaredField("authorization"), null);

        // when called, isAuthorized method returns false
        assertFalse( oAuthHttpClient.isAuthorized() );
    }

    @Test
    void isAuthorized_expired() throws NoSuchFieldException {
        // given: an expired authorization
        Authorization expiredAuth = MockUtils.anAuthorizationBuilder()
                .withExpiresAt(TestUtils.addTime(new Date(), Calendar.HOUR, -1))
                .build();
        FieldSetter.setField( oAuthHttpClient, OAuthHttpClient.class.getDeclaredField("authorization"), expiredAuth);

        // when called, isAuthorized method returns false
        assertFalse( oAuthHttpClient.isAuthorized() );
    }

}
