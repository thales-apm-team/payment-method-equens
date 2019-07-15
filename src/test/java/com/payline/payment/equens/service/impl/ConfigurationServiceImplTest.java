package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.PluginException;
import com.payline.payment.equens.utils.http.PisHttpClient;
import com.payline.payment.equens.utils.http.PsuHttpClient;
import com.payline.payment.equens.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.ListBoxParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.request.RetrievePluginConfigurationRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConfigurationServiceImplTest {

    /* I18nService is not mocked here on purpose, to validate the existence of all
    the messages related to this class, at least in the default locale */
    @Mock private PisHttpClient pisHttpClient;
    @Mock private PsuHttpClient psuHttpClient;
    @Mock private ReleaseProperties releaseProperties;

    @InjectMocks
    private ConfigurationServiceImpl service;

    @BeforeAll
    static void before(){
        // This allows to test the default messages.properties file (no locale suffix)
        Locale.setDefault( Locale.CHINESE );
    }

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @AfterAll
    static void after(){
        // Back to standard default locale
        Locale.setDefault( Locale.ENGLISH );
    }

    @Test
    void check_nominal(){
        // given: a valid configuration, including client ID / secret
        ContractParametersCheckRequest checkRequest = MockUtils.aContractParametersCheckRequest();
        doReturn( MockUtils.anAuthorization() ).when( pisHttpClient ).authorize( any(RequestConfiguration.class) );
        doReturn( MockUtils.anAuthorization() ).when( psuHttpClient ).authorize( any(RequestConfiguration.class) );

        // when: checking the configuration
        Map<String, String> errors = service.check( checkRequest );

        // then: error map is empty
        assertTrue( errors.isEmpty() );
    }

    @Test
    void check_emptyAccountInfo(){
        // given: an empty accountInfo
        ContractParametersCheckRequest checkRequest = MockUtils.aContractParametersCheckRequestBuilder()
                .withAccountInfo(new HashMap<>())
                .build();

        // when: checking the configuration
        Map<String, String> errors = service.check( checkRequest );

        // then: there is an error for each parameter, each error has a valid message and authorize methods are never called
        assertEquals(service.getParameters( Locale.getDefault() ).size(), errors.size() );
        for( Map.Entry<String, String> error : errors.entrySet() ){
            assertNotNull( error.getValue() );
            assertFalse( error.getValue().contains("???") );
        }
        verify( pisHttpClient, never() ).authorize( any( RequestConfiguration.class ) );
        verify( psuHttpClient, never() ).authorize( any( RequestConfiguration.class ) );
    }

    @Test
    void check_wrongPisAuthorization(){
        // given: the client ID or secret is wrong. The PIS authorization API returns an error.
        ContractParametersCheckRequest checkRequest = MockUtils.aContractParametersCheckRequest();
        doThrow( PluginException.class ).when( pisHttpClient ).authorize( any(RequestConfiguration.class) );

        // when: checking the configuration
        Map<String, String> errors = service.check( checkRequest );

        // then: no exception is thrown, but there are some errors
        assertTrue( errors.size() > 0 );
    }

    @Test
    void check_wrongPsuAuthorization(){
        // given: the client does not a a subscription to PSU Management (account problem on the partner's side). The PSU authorization API returns an error.
        ContractParametersCheckRequest checkRequest = MockUtils.aContractParametersCheckRequest();
        doReturn( MockUtils.anAuthorization() ).when( pisHttpClient ).authorize( any(RequestConfiguration.class) );
        doThrow( PluginException.class ).when( psuHttpClient ).authorize( any(RequestConfiguration.class) );

        // when: checking the configuration
        Map<String, String> errors = service.check( checkRequest );

        // then: no exception is thrown, but there are some errors
        assertTrue( errors.size() > 0 );
    }

    @Test
    void getName(){
        // when: calling the method getName
        String name = service.getName( Locale.getDefault() );

        // then: the method returns the name
        assertNotNull( name );
    }

    @ParameterizedTest
    @MethodSource("getLocales")
    void getParameters( Locale locale ) {
        // when: retrieving the contract parameters
        List<AbstractParameter> parameters = service.getParameters( locale );

        // then: each parameter has a unique key, a label and a description. List box parameters have at least 1 possible value.
        List<String> keys = new ArrayList<>();
        for( AbstractParameter param : parameters ){
            // 2 different parameters should not have the same key
            assertFalse( keys.contains( param.getKey() ) );
            keys.add( param.getKey() );

            // each parameter should have a label and a description
            assertNotNull( param.getLabel() );
            assertFalse( param.getLabel().contains("???") );
            assertNotNull( param.getDescription() );
            assertFalse( param.getDescription().contains("???") );

            // in case of a ListBoxParameter, it should have at least 1 value
            if( param instanceof ListBoxParameter ){
                assertFalse( ((ListBoxParameter) param).getList().isEmpty() );
            }
        }
    }
    /** Set of locales to test the getParameters() method. ZZ allows to search in the default messages.properties file. */
    static Stream<Locale> getLocales(){
        return Stream.of( Locale.FRENCH, Locale.ENGLISH, new Locale("ZZ") );
    }

    @Test
    void getReleaseInformation(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String version = "M.m.p";

        // given: the release properties are OK
        doReturn( version ).when( releaseProperties ).get("release.version");
        Calendar cal = new GregorianCalendar();
        cal.set(2019, Calendar.AUGUST, 19);
        doReturn( formatter.format( cal.getTime() ) ).when( releaseProperties ).get("release.date");

        // when: calling the method getReleaseInformation
        ReleaseInformation releaseInformation = service.getReleaseInformation();

        // then: releaseInformation contains the right values
        assertEquals(version, releaseInformation.getVersion());
        assertEquals(2019, releaseInformation.getDate().getYear());
        assertEquals(Month.AUGUST, releaseInformation.getDate().getMonth());
        assertEquals(19, releaseInformation.getDate().getDayOfMonth());
    }

    @Test
    void retrievePluginConfiguration_nominal(){
        // given: the HTTP client returns a proper response
        String input = MockUtils.aPluginConfiguration();
        doReturn( GetAspspsResponse.fromJson( input ) ).when( pisHttpClient ).getAspsps( any(RequestConfiguration.class) );

        RetrievePluginConfigurationRequest request = MockUtils.aRetrievePluginConfigurationRequestBuilder()
                .withPluginConfiguration("initial configuration")
                .build();

        // when: calling the method retrievePluginConfiguration
        String result = service.retrievePluginConfiguration( request );

        // then: the returned ASPSPs match the result of the HTTP request
        // We need to use a regexp here because the value of MessageCreateDateTime can change in the process. So the 2 strings won't be strictly equal.
        Pattern aspspListExtractor = Pattern.compile("^.*\"ASPSP\":\\[(.*)\\].*$");
        Matcher m1 = aspspListExtractor.matcher( input );
        assertTrue( m1.find() );
        Matcher m2 = aspspListExtractor.matcher( result );
        assertTrue( m2.find() );
        assertEquals( m1.group(1), m2.group(1) );
    }

    @Test
    void retrievePluginConfiguration_exception(){
        // given: the HTTP client throws an exception (partner API could not be reached, for example)
        doThrow( PluginException.class ).when( pisHttpClient ).getAspsps( any(RequestConfiguration.class) );

        String initialConfiguration = "initial configuration";
        RetrievePluginConfigurationRequest request = MockUtils.aRetrievePluginConfigurationRequestBuilder()
                .withPluginConfiguration(initialConfiguration)
                .build();

        // when: calling the method retrievePluginConfiguration
        String result = service.retrievePluginConfiguration( request );

        // then: the returned value contains the initial plugin configuration
        assertEquals( initialConfiguration, result );
    }

}
