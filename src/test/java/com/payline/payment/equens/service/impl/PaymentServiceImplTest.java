package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.business.payment.Address;
import com.payline.payment.equens.bean.business.payment.PaymentInitiationRequest;
import com.payline.payment.equens.bean.business.psu.PsuCreateRequest;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.utils.TestUtils;
import com.payline.payment.equens.utils.http.PisHttpClient;
import com.payline.payment.equens.utils.http.PsuHttpClient;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
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

public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl service;

    @Mock private PisHttpClient pisHttpClient;
    @Mock private PsuHttpClient psuHttpclient;

    @BeforeEach
    void setup(){
        service = new PaymentServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    // --- Test PaymentServiceImpl#buildAddress ---

    @Test
    void buildAddress_nominal(){
        // given: a Payline address
        Buyer.Address input = MockUtils.anAddress();

        // when: feeding it to the method buildAddress()
        Address output = service.buildAddress( input );

        // then: output attributes are not null and the address lines are less than 70 chars long
        assertNotNull( output );
        assertNotNull( output.getCountry() );
        assertNotNull( output.getPostCode() );
        assertFalse( output.getAddressLines().isEmpty() );
        output.getAddressLines().forEach( line -> {
            assertTrue( line.length() <= Address.ADDRESS_LINE_MAX_LENGTH );
        });
    }

    @Test
    void buildAddress_spaceManagement(){
        // given: an address line in which the index 69 is in the middle of a word
        Buyer.Address input = Buyer.Address.AddressBuilder.anAddress()
                .withStreet1("This is an address, but we need to be careful where to split it : notInTheMiddleOfThis")
                .build();

        // when: feeding it to the method buildAddress()
        Address output = service.buildAddress( input );

        assertEquals( "This is an address, but we need to be careful where to split it :", output.getAddressLines().get(0) );
        assertEquals( "notInTheMiddleOfThis", output.getAddressLines().get(1) );
    }

    @Test
    void buildAddress_veryLongChunk(){
        // given: an address line with a very long part
        Buyer.Address input = Buyer.Address.AddressBuilder.anAddress()
                .withStreet1("ThisIsAnAddressWithoutAnySpaceAndWeNeedToSplitItSomewaySoWeTruncateBrutallyInTheMiddle")
                .build();

        // when: feeding it to the method buildAddress()
        Address output = service.buildAddress( input );

        assertEquals( "ThisIsAnAddressWithoutAnySpaceAndWeNeedToSplitItSomewaySoWeTruncateBru", output.getAddressLines().get(0) );
        assertEquals( "tallyInTheMiddle", output.getAddressLines().get(1) );
    }

    // --- Test PaymentServiceImpl#convertAmount ---

    @Test
    void convertAmount(){
        assertNull( service.convertAmount( null ) );
        // Euro
        assertEquals( "0.01", service.convertAmount( new Amount(BigInteger.ONE, Currency.getInstance("EUR") ) ) );
        assertEquals( "1.00", service.convertAmount( new Amount(BigInteger.valueOf(100), Currency.getInstance("EUR") ) ) );
        // Yen: no decimal
        assertEquals("100", service.convertAmount( new Amount(BigInteger.valueOf(100), Currency.getInstance("JPY") ) ) );
        // Bahrain Dinar: 3 decimals
        assertEquals("0.100", service.convertAmount( new Amount(BigInteger.valueOf(100), Currency.getInstance("BHD") ) ) );
    }

    // --- Test PaymentServiceImpl#paymentRequest ---

    @Test
    void paymentRequest_nominal(){
        // given: a valid payment request and every HTTP call returns a success response
        PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();
        doReturn( MockUtils.aPsu() )
                .when( psuHttpclient )
                .createPsu( any(PsuCreateRequest.class), any(RequestConfiguration.class) );
        doReturn( MockUtils.aPaymentInitiationResponse() )
                .when( pisHttpClient )
                .initPayment( any(PaymentInitiationRequest.class), any(RequestConfiguration.class) );

        // when: calling paymentRequest() method
        PaymentResponse paymentResponse = service.paymentRequest( paymentRequest );

        // then: the payment response is a success
        assertEquals( PaymentResponseRedirect.class, paymentResponse.getClass() );
        TestUtils.checkPaymentResponse( (PaymentResponseRedirect) paymentResponse );
    }

    // TODO: more test on KO cases

}
