package com.payline.payment.equens.service;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.GenericPaymentRequest;
import com.payline.payment.equens.bean.business.payment.Address;
import com.payline.payment.equens.bean.business.payment.PaymentInitiationRequest;
import com.payline.payment.equens.bean.business.psu.PsuCreateRequest;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.PluginException;
import com.payline.payment.equens.service.impl.PaymentServiceImpl;
import com.payline.payment.equens.utils.Constants;
import com.payline.payment.equens.utils.TestUtils;
import com.payline.payment.equens.utils.http.PisHttpClient;
import com.payline.payment.equens.utils.http.PsuHttpClient;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

class PaymentTest {

    @InjectMocks
    private Payment service;

    @Mock
    private PisHttpClient pisHttpClient;
    @Mock
    private PsuHttpClient psuHttpclient;

    @BeforeEach
    void setUp() {
        service = Payment.getInstance();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentRequest() {
        doReturn(MockUtils.aPsu()).when(psuHttpclient).createPsu(any(), any());
        doReturn(MockUtils.aPaymentInitiationResponse()).when(pisHttpClient).initPayment(any(), any());

        // when: calling paymentRequest() method
        PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();
        GenericPaymentRequest genericPaymentRequest = new GenericPaymentRequest(paymentRequest);
        PaymentResponse paymentResponse = service.paymentRequest(genericPaymentRequest, "123123");

        // then: the payment response is a success
        assertEquals(PaymentResponseRedirect.class, paymentResponse.getClass());
        TestUtils.checkPaymentResponse((PaymentResponseRedirect) paymentResponse);
    }


    @Test
    void paymentRequest_pisInitError(){
        // given: the pisHttpClient init throws an exception (invalid certificate data in PartnerConfiguration, for example)
        doThrow( new PluginException( "A problem occurred initializing SSL context", FailureCause.INVALID_DATA ) )
                .when( pisHttpClient )
                .init( any(PartnerConfiguration.class) );

        // when: calling paymentRequest() method

        PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();
        GenericPaymentRequest genericPaymentRequest = new GenericPaymentRequest(paymentRequest);
        PaymentResponse paymentResponse = service.paymentRequest( genericPaymentRequest, "123123" );

        // then: the exception is properly catch and the payment response is a failure
        assertEquals( PaymentResponseFailure.class, paymentResponse.getClass() );
        TestUtils.checkPaymentResponse( (PaymentResponseFailure) paymentResponse );
    }

    @Test
    void paymentRequest_psuCreateError(){
        // given: the creation of the PSU fails
        doThrow( new PluginException("partner error: 500 Internal Server Error") )
                .when( psuHttpclient )
                .createPsu( any(PsuCreateRequest.class), any(RequestConfiguration.class) );

        // when: calling paymentRequest() method
        PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();
        GenericPaymentRequest genericPaymentRequest = new GenericPaymentRequest(paymentRequest);
        PaymentResponse paymentResponse = service.paymentRequest( genericPaymentRequest, "123123" );

        // then: the exception is properly catch and the payment response is a failure
        assertEquals( PaymentResponseFailure.class, paymentResponse.getClass() );
        TestUtils.checkPaymentResponse( (PaymentResponseFailure) paymentResponse );
    }

    @Test
    void paymentRequest_paymentInitError(){
        // given: the payment initiation fails
        doReturn( MockUtils.aPsu() )
                .when( psuHttpclient )
                .createPsu( any(PsuCreateRequest.class), any(RequestConfiguration.class) );
        doThrow( new PluginException("partner error: 500 Internal Server Error") )
                .when( pisHttpClient )
                .initPayment( any(PaymentInitiationRequest.class), any(RequestConfiguration.class) );

        // when: calling paymentRequest() method
        PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();
        GenericPaymentRequest genericPaymentRequest = new GenericPaymentRequest(paymentRequest);
        PaymentResponse paymentResponse = service.paymentRequest(genericPaymentRequest, "123123");

        // then: the exception is properly catch and the payment response is a failure
        assertEquals( PaymentResponseFailure.class, paymentResponse.getClass() );
        TestUtils.checkPaymentResponse( (PaymentResponseFailure) paymentResponse );
    }

    @Test
    void paymentRequest_missingContractProperty(){
        // given: a property is missing from ContractConfiguration
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().remove( Constants.ContractConfigurationKeys.MERCHANT_IBAN );
        PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequestBuilder()
                .withContractConfiguration( contractConfiguration )
                .build();
        doReturn( MockUtils.aPsu() )
                .when( psuHttpclient )
                .createPsu( any(PsuCreateRequest.class), any(RequestConfiguration.class) );

        // when: calling paymentRequest() method
        GenericPaymentRequest genericPaymentRequest = new GenericPaymentRequest(paymentRequest);
        PaymentResponse paymentResponse = service.paymentRequest( genericPaymentRequest, "123123" );

        // then: the exception is properly catch and the payment response is a failure
        assertEquals( PaymentResponseFailure.class, paymentResponse.getClass() );
        TestUtils.checkPaymentResponse( (PaymentResponseFailure) paymentResponse );
    }


    @Test
    void buildAddress_nominal() {
        // given: a Payline address
        Buyer.Address input = MockUtils.aPaylineAddress();

        // when: feeding it to the method buildAddress()
        Address output = Payment.buildAddress(input);

        // then: output attributes are not null and the address lines are less than 70 chars long
        assertNotNull(output);
        assertNotNull(output.getCountry());
        assertNotNull(output.getPostCode());
        assertFalse(output.getAddressLines().isEmpty());
        output.getAddressLines().forEach(line -> assertTrue(line.length() <= Address.ADDRESS_LINE_MAX_LENGTH));
    }

    @Test
    void buildAddress_spaceManagement() {
        // given: an address line in which the index 69 is in the middle of a word
        Buyer.Address input = Buyer.Address.AddressBuilder.anAddress()
                .withStreet1("This is an address, but we need to be careful where to split it : notInTheMiddleOfThis")
                .build();

        // when: feeding it to the method buildAddress()
        Address output = Payment.buildAddress(input);

        assertEquals("This is an address, but we need to be careful where to split it :", output.getAddressLines().get(0));
        assertEquals("notInTheMiddleOfThis", output.getAddressLines().get(1));
    }

    @Test
    void buildAddress_veryLongChunk() {
        // given: an address line with a very long part
        Buyer.Address input = Buyer.Address.AddressBuilder.anAddress()
                .withStreet1("ThisIsAnAddressWithoutAnySpaceAndWeNeedToSplitItSomewaySoWeTruncateBrutallyInTheMiddle")
                .build();

        // when: feeding it to the method buildAddress()
        Address output = Payment.buildAddress(input);

        assertEquals("ThisIsAnAddressWithoutAnySpaceAndWeNeedToSplitItSomewaySoWeTruncateBru", output.getAddressLines().get(0));
        assertEquals("tallyInTheMiddle", output.getAddressLines().get(1));
    }

    @Test
    void convertAmount() {
        assertNull(Payment.convertAmount(null));
        // Euro
        assertEquals("0.01", Payment.convertAmount(new Amount(BigInteger.ONE, Currency.getInstance("EUR"))));
        assertEquals("1.00", Payment.convertAmount(new Amount(BigInteger.valueOf(100), Currency.getInstance("EUR"))));
        // Yen: no decimal
        assertEquals("100", Payment.convertAmount(new Amount(BigInteger.valueOf(100), Currency.getInstance("JPY"))));
        // Bahrain Dinar: 3 decimals
        assertEquals("0.100", Payment.convertAmount(new Amount(BigInteger.valueOf(100), Currency.getInstance("BHD"))));
    }
}