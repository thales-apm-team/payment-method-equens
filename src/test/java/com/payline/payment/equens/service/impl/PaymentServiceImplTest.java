package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.service.Payment;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl service;

    @Mock
    private Payment payment;

    @BeforeEach
    void setup(){
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

        doReturn(responseRedirect).when(payment).paymentRequest(any(), anyString());

        // when: calling paymentRequest() method
        PaymentResponse paymentResponse = service.paymentRequest( paymentRequest );

        // then: the payment response is a success
        assertEquals( PaymentResponseRedirect.class, paymentResponse.getClass() );
    }

}
