package com.payline.payment.equens.utils.http;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.InvalidDataException;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.HashMap;

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
    }

    @AfterEach
    void verifyMocks(){
        /* verify that execute() method is never called ! it ensures the mocks are working properly and there is no
        false negative that could be related to a failed HTTP request sent to the partner API. */
        verify( pisHttpClient, never() ).execute( any( HttpRequestBase.class ) );
    }

    // --- Test EquensHttpClient#getAspsps ---

    @Test
    void getAspsps_nominal(){
        // given: authorization is valid and the partner API returns a valid response
        doReturn( MockUtils.anAuthorization() ).when( pisHttpClient ).authorize( any(RequestConfiguration.class) );
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
        GetAspspsResponse response = pisHttpClient.getAspsps( MockUtils.aRequestConfiguration() );

        // then: the list contains 1 Aspsp
        assertNotNull( response );
        assertEquals( 1, response.getAspsps().size() );
    }

    // TODO: test des cas d'erreur (code HTTP 400, content null, etc.)

}
