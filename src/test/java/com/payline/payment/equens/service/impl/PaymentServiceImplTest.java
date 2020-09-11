package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.business.payment.PaymentData;
import com.payline.payment.equens.service.GenericPaymentService;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl service;

    @Mock
    private GenericPaymentService genericPaymentService;

    @Captor
    ArgumentCaptor<PaymentData> paymentDataCaptor;

    @BeforeEach
    void setup() {
        service = new PaymentServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentRequest_nominal() throws Exception {
        // given: a valid payment request
        PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();

        PaymentResponseRedirect.RedirectionRequest redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                .aRedirectionRequest()
                .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                .withUrl(new URL("http://www.foo.com"))
                .build();

        PaymentResponseRedirect responseRedirect = PaymentResponseRedirect.PaymentResponseRedirectBuilder
                .aPaymentResponseRedirect()
                .withPartnerTransactionId("123123")
                .withStatusCode("foo")
                .withRedirectionRequest(redirectionRequest)
                .build();

        doReturn(responseRedirect).when(genericPaymentService).paymentRequest(any(), paymentDataCaptor.capture());

        // when: calling paymentRequest() method
        PaymentResponse paymentResponse = service.paymentRequest(paymentRequest);

        // then: the payment response is a success
        assertEquals(PaymentResponseRedirect.class, paymentResponse.getClass());
        assertEquals("PSSTFRPT", paymentDataCaptor.getValue().getBic());
        assertEquals("FR1234567891234567891234", paymentDataCaptor.getValue().getIban());
    }

    @Test
    void paymentRequest_OnlyBic() throws Exception {
        // given: a valid payment request
        Map<String,String> parameters = new HashMap<>();
        parameters.put(BankTransferForm.BANK_KEY, "FOOO");
        PaymentFormContext context = PaymentFormContext.PaymentFormContextBuilder.aPaymentFormContext()
                .withPaymentFormParameter(parameters)
                .build();

        PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequestBuilder()
                .withPaymentFormContext(context)
                .build();

        PaymentResponseRedirect.RedirectionRequest redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                .aRedirectionRequest()
                .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                .withUrl(new URL("http://www.foo.com"))
                .build();

        PaymentResponseRedirect responseRedirect = PaymentResponseRedirect.PaymentResponseRedirectBuilder
                .aPaymentResponseRedirect()
                .withPartnerTransactionId("123123")
                .withStatusCode("foo")
                .withRedirectionRequest(redirectionRequest)
                .build();

        doReturn(responseRedirect).when(genericPaymentService).paymentRequest(any(), paymentDataCaptor.capture());

        // when: calling paymentRequest() method
        PaymentResponse paymentResponse = service.paymentRequest(paymentRequest);

        // then: the payment response is a success
        assertEquals(PaymentResponseRedirect.class, paymentResponse.getClass());
        assertEquals("FOOO", paymentDataCaptor.getValue().getBic());
        assertNull(paymentDataCaptor.getValue().getIban());
    }

    @Test
    void paymentRequest_OnlyIBAN() throws Exception {
        // given: a valid payment request
        Map<String,String> parameters = new HashMap<>();
        parameters.put(BankTransferForm.IBAN_KEY, "FOOO");
        PaymentFormContext context = PaymentFormContext.PaymentFormContextBuilder.aPaymentFormContext()
                .withSensitivePaymentFormParameter(parameters)
                .build();

        PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequestBuilder()
                .withPaymentFormContext(context)
                .build();

        PaymentResponseRedirect.RedirectionRequest redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                .aRedirectionRequest()
                .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                .withUrl(new URL("http://www.foo.com"))
                .build();

        PaymentResponseRedirect responseRedirect = PaymentResponseRedirect.PaymentResponseRedirectBuilder
                .aPaymentResponseRedirect()
                .withPartnerTransactionId("123123")
                .withStatusCode("foo")
                .withRedirectionRequest(redirectionRequest)
                .build();

        doReturn(responseRedirect).when(genericPaymentService).paymentRequest(any(), paymentDataCaptor.capture());

        // when: calling paymentRequest() method
        PaymentResponse paymentResponse = service.paymentRequest(paymentRequest);

        // then: the payment response is a success
        assertEquals(PaymentResponseRedirect.class, paymentResponse.getClass());
        assertNull(paymentDataCaptor.getValue().getBic());
        assertEquals("FOOO", paymentDataCaptor.getValue().getIban());
    }

}
