package com.payline.payment.equens.utils.http;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.business.payment.PaymentInitiationResponse;
import com.payline.payment.equens.bean.business.payment.PaymentStatusResponse;
import com.payline.payment.equens.bean.business.reachdirectory.Aspsp;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.PluginException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PisHttpClientTest {

    @Spy
    @InjectMocks
    private PisHttpClient pisHttpClient = new PisHttpClient();

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
        // Mock a valid authorization
        doReturn( MockUtils.anAuthorization() ).when( pisHttpClient ).authorize( any(RequestConfiguration.class) );
    }

    @AfterEach
    void verifyMocks(){
        /* verify that execute() method is never called ! it ensures the mocks are working properly and there is no
        false negative that could be related to a failed HTTP request sent to the partner API. */
        verify( pisHttpClient, never() ).execute( any( HttpRequestBase.class ) );
    }

    // --- Test PisHttpClient#getAspsps ---

    @Test
    void getAspsps_nominal(){
        // given: the partner API returns a valid success response
        String responseBody = "{" +
                "  \"MessageCreateDateTime\":\"2019-11-15T15:52:37.092+0000\"," +
                "  \"MessageId\":\"6f31954f-7ad6-4a63-950c-a2a363488e\"," +
                "  \"Application\":\"PIS\"," +
                "  \"ASPSP\":[" +
                "    {" +
                "      \"AspspId\":\"7005\"," +
                "      \"Name\":[\"Ing Bank\"]," +
                "      \"CountryCode\":\"NL\"," +
                "      \"Details\":[{\"ProtocolVersion\":\"ING_V_0_9_3\"}]," +
                "      \"BIC\":\"TODO\"}" +
                "  ]" +
                "}";
        doReturn( HttpTestUtils.mockStringResponse(200, "OK", responseBody ) )
                .when( pisHttpClient )
                .get( anyString(), anyList() );

        // when: calling the method
        GetAspspsResponse response = pisHttpClient.getAspsps( MockUtils.aRequestConfiguration() );

        // then: the list contains 1 Aspsp
        assertNotNull( response );
        assertEquals( 1, response.getAspsps().size() );
    }

    @Test
    void getAspsps_noListInResponse(){
        // given: the partner API returns a invalid success response, without any Aspsp list
        String responseBody = "{" +
                "  \"MessageCreateDateTime\":\"2019-11-15T15:52:37.092+0000\"," +
                "  \"MessageId\":\"6f31954f-7ad6-4a63-950c-a2a363488e\"," +
                "  \"Application\":\"PIS\"" +
                "}";
        doReturn( HttpTestUtils.mockStringResponse(200, "OK", responseBody ) )
                .when( pisHttpClient )
                .get( anyString(), anyList() );

        // when: calling the method
        GetAspspsResponse response = pisHttpClient.getAspsps( MockUtils.aRequestConfiguration() );

        // then: resulting list is null
        assertNull( response.getAspsps() );
    }

    @Test
    void getAspsps_error(){
        // given: the partner API returns a valid error response
        doReturn( HttpTestUtils.mockStringResponse(401, "Unauthorized", "" ) )
                .when( pisHttpClient )
                .get( anyString(), anyList() );

        // when: calling the method, then: an exception is thrown
        assertThrows( PluginException.class, () -> pisHttpClient.getAspsps( MockUtils.aRequestConfiguration() ) );
    }

    // --- Test PisHttpClient#initPayment ---

    @Test
    void initPayment_nominal(){
        // given: the partner API returns a valid success response
        String redirectionUrl = "https://xs2a.banking.co.at/xs2a-sandbox/m044/v1/pis/confirmation/btWMz6mTz7I3SOe4lMqXiwciqe6igXBCeebfVWlmZ8N8zVw_qRKMMuhlLLXtPrVcBeH6HIP2qhdTTZ1HINXSkg==_=_psGLvQpt9Q/authorisations/fa8e44a7-3bf7-4543-82d1-5a1163aaaaad";
        String responseContent = "{\n" +
                "    \"MessageCreateDateTime\": \"2019-11-19T16:35:52.244+0000\",\n" +
                "    \"MessageId\": \"e8683740-38be-4026-b48e-72089b023e\",\n" +
                "    \"PaymentId\": \"130436\",\n" +
                "    \"InitiatingPartyReferenceId\": \"REF1574181352\",\n" +
                "    \"PaymentStatus\": \"OPEN\",\n" +
                "    \"AspspRedirectUrl\": \"" + redirectionUrl + "\"\n" +
                "}";
        doReturn( HttpTestUtils.mockStringResponse(201, "Created", responseContent ) )
                .when( pisHttpClient )
                .post( anyString(), anyList(), any(HttpEntity.class) );

        // when: initializing a payment
        PaymentInitiationResponse response = pisHttpClient.initPayment(MockUtils.aPaymentInitiationRequest(), MockUtils.aRequestConfiguration());

        // then: the response contains the redirection URL
        assertNotNull( response );
        assertEquals( redirectionUrl, response.getAspspRedirectUrl() );

        // verify the post() method has been called and the content of the arguments passed
        ArgumentCaptor<List<Header>> headersCaptor = ArgumentCaptor.forClass( List.class );
        ArgumentCaptor<HttpEntity> bodyCaptor = ArgumentCaptor.forClass( HttpEntity.class );
        verify( pisHttpClient, times(1) ).post( anyString(), headersCaptor.capture(), bodyCaptor.capture() );
        this.verifyAuthorizationHeader( headersCaptor.getValue() );
        assertNotNull( bodyCaptor.getValue() );
    }

    @Test
    void initPayment_badRequest(){
        // given: the partner API returns a 400 Bad Request
        String responseContent = "{" +
                "    \"code\":\"002\"," +
                "    \"message\":\"The message does not comply the schema definition\"," +
                "    \"details\":\"Property paymentAmount : must not be null\"," +
                "    \"MessageCreateDateTime\":\"2019-11-25T15:02:36.555+0100\"," +
                "    \"MessageId\":\"c2a4ce10086547019b1d50411ea6a99e\"" +
                "}";
        doReturn( HttpTestUtils.mockStringResponse(400, "Bad Request", responseContent ) )
                .when( pisHttpClient )
                .post( anyString(), anyList(), any(HttpEntity.class) );

        // when: initializing a payment, then: an exception is thrown
        PluginException thrown = assertThrows(PluginException.class,
                () -> pisHttpClient.initPayment(MockUtils.aPaymentInitiationRequest(), MockUtils.aRequestConfiguration()) );
        assertNotNull(  thrown.getErrorCode() );
        assertNotNull(  thrown.getFailureCause() );
    }

    // TODO: test des cas en erreur

    // --- Test PisHttpClient#paymentStatus ---

    @Test
    void paymentStatus_nominal(){
        // given: the partner API returns a valid success response
        String paymentId = "130676";
        String responseContent = "{\n" +
                "    \"MessageCreateDateTime\": \"2019-11-20T13:44:35.115+0000\",\n" +
                "    \"MessageId\": \"ca58925c-57cc-44b0-a827-cd439fb87f\",\n" +
                "    \"PaymentId\": \"" + paymentId + "\",\n" +
                "    \"PaymentStatus\": \"AUTHORISED\",\n" +
                "    \"AspspPaymentId\": \"im7QC5rZ-jyNr237sJb6VqEnBd8uNDnU6b9-rnAYVxTNub1NwmkrY3CBGDMRXsx5BeH6HIP2qhdTTZ1HINXSkg==_=_psGLvQpt9Q\",\n" +
                "    \"InitiatingPartyReferenceId\": \"REF1574257016\",\n" +
                "    \"DebtorAgent\": \"BNPADEFF\",\n" +
                "    \"DebtorAccount\": \"AT880000000000000001\"\n" +
                "}";
        doReturn( HttpTestUtils.mockStringResponse(200, "OK", responseContent ) )
                .when( pisHttpClient )
                .get( anyString(), anyList() );

        // when: retrieving the payment status
        PaymentStatusResponse response = pisHttpClient.paymentStatus( paymentId, MockUtils.aRequestConfiguration(), false );

        // then: the response contains the status
        assertNotNull( response );
        assertNotNull( response.getPaymentStatus() );

        // verify the get() method has been called and the content of the arguments passed
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass( String.class );
        ArgumentCaptor<List<Header>> headersCaptor = ArgumentCaptor.forClass( List.class );
        verify( pisHttpClient, times(1) ).get( urlCaptor.capture(), headersCaptor.capture() );
        assertTrue( urlCaptor.getValue().contains(paymentId) );
        // TODO: if the use of autoConfirm is confirmed, test the presence of the parameter in the request URL
        this.verifyAuthorizationHeader( headersCaptor.getValue() );
    }

    // TODO: test des cas en erreur

    private void verifyAuthorizationHeader( List<Header> headers ){
        boolean headerPresent = false;
        for( Header h : headers ){
            if( HttpHeaders.AUTHORIZATION.equals( h.getName() ) ){
                headerPresent = true;
            }
        }
        assertTrue( headerPresent );
    }

}
