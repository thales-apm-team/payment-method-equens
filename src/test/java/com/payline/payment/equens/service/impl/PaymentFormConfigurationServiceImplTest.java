package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.utils.i18n.I18nService;
import com.payline.pmapi.bean.paymentform.bean.field.SelectOption;
import com.payline.pmapi.bean.paymentform.bean.form.AbstractPaymentForm;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

public class PaymentFormConfigurationServiceImplTest {

    @InjectMocks
    private PaymentFormConfigurationServiceImpl service;

    @Mock
    private I18nService i18n;

    @BeforeEach
    void setup(){
        service = new PaymentFormConfigurationServiceImpl();
        MockitoAnnotations.initMocks(this);

        // We consider by default that i18n behaves normally
        doReturn( "message" )
                .when( i18n )
                .getMessage( anyString(), any(Locale.class) );
    }

    @Test
    void getPaymentFormConfiguration_nominal(){
        // given: the plugin configuration contains 2 french banks and the locale is FRANCE
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequestBuilder()
                .withLocale( Locale.FRANCE )
                .withPluginConfiguration( "{\"Application\":\"PIS\"," +
                        "\"ASPSP\":[" +
                            "{\"AspspId\": \"1402\", \"Name\": [\"Banque Fédérative du Crédit Mutuel\"], \"CountryCode\": \"FR\", \"BIC\": \"CMCIFRPA\"}," +
                            "{\"AspspId\": \"1409\", \"Name\": [\"La Banque Postale\"], \"CountryCode\": \"FR\", \"BIC\": \"PSSTFRPP\"}" +
                        "],\"MessageCreateDateTime\":\"2019-11-15T16:52:37.092+0100\",\"MessageId\":\"6f31954f-7ad6-4a63-950c-a2a363488e\"}"
                )
                .build();

        // when: calling getPaymentFormConfiguration method
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration( request );

        // then: response is a success, the form is a BankTransferForm and the number of banks is correct
        assertEquals(PaymentFormConfigurationResponseSpecific.class, response.getClass());
        AbstractPaymentForm form = ((PaymentFormConfigurationResponseSpecific) response).getPaymentForm();
        assertNotNull( form.getButtonText() );
        assertNotNull( form.getDescription() );
        assertEquals(BankTransferForm.class, form.getClass());
        BankTransferForm bankTransferForm = (BankTransferForm) form;
        assertEquals(2, bankTransferForm.getBanks().size());
    }

    @Test
    void getPaymentFormConfiguration_invalidPluginConfiguration(){
        // given: the plugin configuration is invalid
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequestBuilder()
                .withPluginConfiguration( "{not valid" )
                .build();

        // when: calling getPaymentFormConfiguration method
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration( request );

        // then: response is a failure
        assertEquals(PaymentFormConfigurationResponseFailure.class, response.getClass());
        assertNotNull( ((PaymentFormConfigurationResponseFailure)response).getErrorCode() );
        assertNotNull( ((PaymentFormConfigurationResponseFailure)response).getFailureCause() );
    }

    @Test
    void getBanks_aspspWithoutBic(){
        // @see https://payline.atlassian.net/browse/PAYLAPMEXT-204
        // @see https://payline.atlassian.net/browse/PAYLAPMEXT-219
        // given: in the PluginConfiguration, one ASPSP has no BIC (null)
        String pluginConfiguration = "{\"Application\":\"PIS\"," +
                "\"ASPSP\":[" +
                    "{\"AspspId\":\"224\",\"CountryCode\":\"DE\",\"Name\":[\"08/15direkt\"]}" +
                "],\"MessageCreateDateTime\":\"2019-11-15T16:52:37.092+0100\",\"MessageId\":\"6f31954f-7ad6-4a63-950c-a2a363488e\"}";

        // when: calling getBanks method
        List<SelectOption> result = service.getBanks( pluginConfiguration, Locale.GERMANY.getCountry() );

        // then: the aspsp is ignered because there is no BIC
        assertTrue( result.isEmpty() );
    }

    @Test
    void getBanks_filterAspspByCountryCode() {
        // @see: https://payline.atlassian.net/browse/PAYLAPMEXT-203
        // given: the PluginConfiguration contains 3 banks (1 FR, 1 ES, 1 without CountryCode) and the given country code if "FR"
        String pluginConfiguration = "{\"Application\":\"PIS\"," +
                "\"ASPSP\":[" +
                    "{\"AspspId\": \"1402\", \"Name\": [\"Banque Fédérative du Crédit Mutuel\"], \"CountryCode\": \"FR\", \"BIC\": \"CMCIFRPA\"}," +
                    "{\"AspspId\": \"1601\", \"Name\": [\"BBVA\"], \"CountryCode\": \"ES\", \"BIC\": \"BBVAESMM\"}," +
                    "{\"AspspId\": \"1409\", \"Name\": [\"La Banque Postale\"], \"BIC\": \"PSSTFRPP\"}" +
                "],\"MessageCreateDateTime\":\"2019-11-15T16:52:37.092+0100\",\"MessageId\":\"6f31954f-7ad6-4a63-950c-a2a363488e\"}";

        // when: calling getBanks method
        List<SelectOption> result = service.getBanks( pluginConfiguration, Locale.FRANCE.getCountry() );

        // then: there is only 1 bank choice at the end
        assertEquals(1, result.size());
    }
}
