package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.MockUtils;
import com.payline.payment.equens.bean.business.payment.Address;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceImplTest {

    @Test
    void buildAddress_nominal(){
        // given: a Payline address
        Buyer.Address input = MockUtils.anAddress();

        // when: feeding it to the method buildAddress()
        Address output = PaymentServiceImpl.buildAddress( input );

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
        Address output = PaymentServiceImpl.buildAddress( input );

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
        Address output = PaymentServiceImpl.buildAddress( input );

        assertEquals( "ThisIsAnAddressWithoutAnySpaceAndWeNeedToSplitItSomewaySoWeTruncateBru", output.getAddressLines().get(0) );
        assertEquals( "tallyInTheMiddle", output.getAddressLines().get(1) );
    }

    @Test
    void convertAmount(){
        assertNull( PaymentServiceImpl.convertAmount( null ) );
        // Euro
        assertEquals( "0.01", PaymentServiceImpl.convertAmount( new Amount(BigInteger.ONE, Currency.getInstance("EUR") ) ) );
        assertEquals( "1.00", PaymentServiceImpl.convertAmount( new Amount(BigInteger.valueOf(100), Currency.getInstance("EUR") ) ) );
        // Yen: no decimal
        assertEquals("100", PaymentServiceImpl.convertAmount( new Amount(BigInteger.valueOf(100), Currency.getInstance("JPY") ) ) );
        // Bahrain Dinar: 3 decimals
        assertEquals("0.100", PaymentServiceImpl.convertAmount( new Amount(BigInteger.valueOf(100), Currency.getInstance("BHD") ) ) );
    }

}
