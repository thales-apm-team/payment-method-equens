package com.payline.payment.equens.utils.http;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.business.reachdirectory.Aspsp;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        doReturn( HttpTestUtils.mockStringResponse(200, "OK", responseBody, null ) )
                .when( pisHttpClient )
                .get( anyString(), anyList() );

        // when: calling the method
        List<Aspsp> response = pisHttpClient.getAspsps( MockUtils.aRequestConfiguration() );

        // then: the list contains 1 Aspsp
        assertNotNull( response );
        assertEquals( 1, response.size() );
    }

    @Test
    void getAspsps_noListInResponse(){
        // given: the partner API returns a invalid success response, without any Aspsp list
        String responseBody = "{" +
                "  \"MessageCreateDateTime\":\"2019-11-15T15:52:37.092+0000\"," +
                "  \"MessageId\":\"6f31954f-7ad6-4a63-950c-a2a363488e\"," +
                "  \"Application\":\"PIS\"" +
                "}";
        doReturn( HttpTestUtils.mockStringResponse(200, "OK", responseBody, null ) )
                .when( pisHttpClient )
                .get( anyString(), anyList() );

        // when: calling the method
        List<Aspsp> response = pisHttpClient.getAspsps( MockUtils.aRequestConfiguration() );

        // then: resulting list is null
        assertNull( response );
    }

    // TODO: test des cas d'erreur (code HTTP 400, content null, etc.)

}
