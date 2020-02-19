package com.payline.payment.equens.utils.http;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.business.psu.Psu;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.InvalidDataException;
import com.payline.payment.equens.exception.PluginException;
import com.payline.payment.equens.utils.properties.ConfigProperties;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PsuHttpClientTest {

    @Spy
    @InjectMocks
    private PsuHttpClient psuHttpClient = new PsuHttpClient();

    @Mock private ConfigProperties config;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
        // Mock a valid authorization
        doReturn( MockUtils.anAuthorization() ).when( psuHttpClient ).authorize( any(RequestConfiguration.class) );
        // Mock the config properties
        doReturn( "/psumgmt/v1/psus" ).when( config ).get( "api.psu.psus" );
    }

    @AfterEach
    void verifyMocks(){
        /* verify that execute() method is never called ! it ensures the mocks are working properly and there is no
        false negative that could be related to a failed HTTP request sent to the partner API. */
        verify( psuHttpClient, never() ).execute( any( HttpRequestBase.class ) );
    }

    // --- Test PsuHttpclient#createPsu ---

    @Test
    void createPsu_nominal(){
        // given: the partner API returns a valid success response
        String responseBody = "{" +
                "  \"MessageCreateDateTime\":\"2019-11-18T09:50:32.724+0000\"," +
                "  \"MessageId\":\"d43a25cd1f29436ca40597429c9242fc\"," +
                "  \"Psu\":{" +
                "    \"PsuId\":\"303\"," +
                "    \"Address\":{\"AddressLines\":[{},{}]}," +
                "    \"Status\":\"ACTIVE\"" +
                "  }" +
                "}";
        doReturn( HttpTestUtils.mockStringResponse(201, "Created", responseBody ) )
                .when( psuHttpClient )
                .post( anyString(), anyList(), any(HttpEntity.class) );

        // when: calling the method
        Psu createdPsu = psuHttpClient.createPsu( MockUtils.aPsuCreateRequest(), MockUtils.aRequestConfiguration() );

        // then: created PSU returned contains the right values
        assertNotNull( createdPsu );
        assertEquals( "303", createdPsu.getPsuId() );
        assertEquals( "ACTIVE", createdPsu.getStatus() );
    }

    @Test
    void createPsu_invalidConfig(){
        // given: the config property containing the path is missing
        doReturn( null ).when( config ).get( "api.psu.psus" );

        // when: calling the method, then: an exception is thrown
        assertThrows( InvalidDataException.class, () -> psuHttpClient.createPsu( MockUtils.aPsuCreateRequest(), MockUtils.aRequestConfiguration() ) );
    }

    @Test
    void createPsu_noPsuInResponse(){
        // given: the partner API returns a invalid success response, without the PSU data
        String responseBody = "{" +
                "  \"MessageCreateDateTime\":\"2019-11-18T09:50:32.724+0000\"," +
                "  \"MessageId\":\"d43a25cd1f29436ca40597429c9242fc\"" +
                "}";
        doReturn( HttpTestUtils.mockStringResponse(201, "Created", responseBody ) )
                .when( psuHttpClient )
                .post( anyString(), anyList(), any(HttpEntity.class) );

        // when: calling the method
        Psu createdPsu = psuHttpClient.createPsu( MockUtils.aPsuCreateRequest(), MockUtils.aRequestConfiguration() );

        // then: no error encountered, but returned object is null
        assertNull( createdPsu );
    }

    @Test
    void createPsu_missingMessageId(){
        // given: the partner API returns an error response
        String responseBody = "{\n" +
                "    \"MessageCreateDateTime\": \"2019-12-03T15:39:47.226+0000\",\n" +
                "    \"MessageId\": \"a6c264d15f1a40f0800e4667bebff622\",\n" +
                "    \"code\": \"002\",\n" +
                "    \"message\": \"The message does not comply the schema definition\",\n" +
                "    \"details\": \"Property messageId : must not be null\"\n" +
                "}";
        doReturn( HttpTestUtils.mockStringResponse(400, "Bad Request", responseBody ) )
                .when( psuHttpClient )
                .post( anyString(), anyList(), any(HttpEntity.class) );

        // when: calling the method
        PluginException thrown = assertThrows(PluginException.class,
                () -> psuHttpClient.createPsu( MockUtils.aPsuCreateRequest(), MockUtils.aRequestConfiguration() ) );
        assertNotNull(  thrown.getErrorCode() );
        assertNotNull(  thrown.getFailureCause() );
    }

}
