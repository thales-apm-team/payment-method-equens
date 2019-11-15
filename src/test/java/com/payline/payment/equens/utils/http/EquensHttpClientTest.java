package com.payline.payment.equens.utils.http;

import com.payline.payment.equens.utils.security.RSAHolder;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.tomitribe.auth.signatures.Signature;
import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.InvalidDataException;
import com.payline.payment.equens.utils.Constants;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EquensHttpClientTest {

    /**
     * Private class required to test the sbstract class {@link EquensHttpClient}.
     */
    private class TestableHttpClient extends EquensHttpClient {
        @Override
        protected String appName() {
            return "TEST";
        }
    }

    private static final String HTTP_SIGNATURE_PATTERN = "Signature\\s+((keyId|signature|algorithm|created|expires|headers)=\".*\"[\\s,]*){2,6}";

    @Spy
    @InjectMocks
    private TestableHttpClient equensHttpClient;

    @Mock
    private RSAHolder rsaHolder;

    @BeforeEach
    void setup() throws NoSuchFieldException {
        // Init tested instance and inject mocks
        equensHttpClient = new TestableHttpClient();
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void verifyMocks() throws IOException {
        /* verify that execute() method is never called ! it ensures the mocks are working properly and there is no
        false negative that could be related to a failed request to the partner API. */
        verify( equensHttpClient, never() ).execute( any( HttpRequestBase.class ) );
    }

    // --- Test EquensHttpClient#authorizationHeaders ---

    @Test
    void authorizationHeaders_missingclientName(){
        // given: client name is missing from ContractConfiguration
        String uri = "http://test.domain.fr/path";
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().remove(Constants.ContractConfigurationKeys.CLIENT_NAME);
        RequestConfiguration requestConfiguration = new RequestConfiguration(contractConfiguration, MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

        // when: calling authorizationHeaders(), then: an exception is thrown
        assertThrows(InvalidDataException.class, () -> equensHttpClient.authorizationHeaders(uri, requestConfiguration));
    }

    @Test
    void authorizationHeaders_missingOnboardingId(){
        // given: onboarding id is missing from ContractConfiguration
        String uri = "http://test.domain.fr/path";
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().remove(Constants.ContractConfigurationKeys.ONBOARDING_ID);
        RequestConfiguration requestConfiguration = new RequestConfiguration(contractConfiguration, MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

        // when: calling authorizationHeaders(), then: an exception is thrown
        assertThrows(InvalidDataException.class, () -> equensHttpClient.authorizationHeaders(uri, requestConfiguration));
    }

    @Test
    void authorizationHeaders_nominal(){
        // given: the method generateSignature returns a valid signature
        String uri = "http://test.domain.fr/path";
        RequestConfiguration requestConfiguration = MockUtils.aRequestConfiguration();
        doReturn( MockUtils.aSignature() ).when( equensHttpClient ).generateSignature( anyString(), anyMap() );

        // when: calling authorizationHeaders() method
        Map<String, String> headers = equensHttpClient.authorizationHeaders( uri, requestConfiguration );

        // then: every authorization header required by Equens is present AND the header Authorization contains a valid signature
        assertNotNull( headers );
        assertFalse( headers.isEmpty() );
        assertEquals( equensHttpClient.appName(), headers.get( EquensHttpClient.AUTH_HEADER_APP ) );
        assertNotNull( headers.get( EquensHttpClient.AUTH_HEADER_CLIENT ) );
        assertNotNull( headers.get( EquensHttpClient.AUTH_HEADER_DATE ) );
        assertNotNull( headers.get( EquensHttpClient.AUTH_HEADER_ID ) );
        assertNotNull( headers.get( HttpHeaders.AUTHORIZATION ) );
        assertTrue( headers.get( HttpHeaders.AUTHORIZATION ).matches( HTTP_SIGNATURE_PATTERN ) );
    }

    // --- Test EquensHttpClient#generateSignature ---

    @Test
    void generateSignature_nominal() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        // given: valid input & RsaHolder returns valid pk and certificate
        String uri = "http://test.domain.fr/path";
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put( EquensHttpClient.AUTH_HEADER_APP, "TEST" );
        headers.put( EquensHttpClient.AUTH_HEADER_CLIENT, "Thales" );
        headers.put( EquensHttpClient.AUTH_HEADER_DATE, "01/01/2021" );
        headers.put( EquensHttpClient.AUTH_HEADER_ID, "0" );
        doReturn( MockUtils.aPrivateKey() ).when( rsaHolder ).getPrivateKey();
        doReturn( MockUtils.aClientCertificate() ).when( rsaHolder ).getClientCertificate();

        // when: generating the signature
        Signature signature = equensHttpClient.generateSignature( uri, headers );

        // then: the signature is valid
        assertNotNull( signature );
        assertTrue( signature.toString().matches( HTTP_SIGNATURE_PATTERN ) );
    }
}
