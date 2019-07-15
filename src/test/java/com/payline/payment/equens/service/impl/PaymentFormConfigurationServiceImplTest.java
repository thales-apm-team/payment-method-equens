package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.business.reachdirectory.Aspsp;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.utils.i18n.I18nService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    }

    @Test
    void getPaymentFormConfiguration_nominal(){
        // given: i18n service behaves normally and the plugin configuration is correct
        doReturn( "message" )
                .when( i18n )
                .getMessage( anyString(), any(Locale.class) );
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequest();
        List<Aspsp> aspsps = GetAspspsResponse.fromJson( request.getPluginConfiguration() ).getAspsps();

        // when: calling getPaymentFormConfiguration method
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration( request );

        // then: response is a success, the form is a BankTransferForm and the number of banks is correct
        assertEquals(PaymentFormConfigurationResponseSpecific.class, response.getClass());
        AbstractPaymentForm form = ((PaymentFormConfigurationResponseSpecific) response).getPaymentForm();
        assertNotNull( form.getButtonText() );
        assertNotNull( form.getDescription() );
        assertEquals(BankTransferForm.class, form.getClass());
        BankTransferForm bankTransferForm = (BankTransferForm) form;
        assertEquals(aspsps.size(), bankTransferForm.getBanks().size());
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



}
