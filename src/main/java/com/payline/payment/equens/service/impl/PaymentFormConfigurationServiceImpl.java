package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.bean.business.reachdirectory.Aspsp;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.exception.InvalidDataException;
import com.payline.payment.equens.exception.PluginException;
import com.payline.payment.equens.service.LogoPaymentFormConfigurationService;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.paymentform.bean.field.SelectOption;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentFormConfigurationServiceImpl extends LogoPaymentFormConfigurationService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentFormConfigurationServiceImpl.class);

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        PaymentFormConfigurationResponse pfcResponse;
        try {
            Locale locale = paymentFormConfigurationRequest.getLocale();

            // build the banks list from the plugin configuration
            if( paymentFormConfigurationRequest.getPluginConfiguration() == null ){
                throw new InvalidDataException("Plugin configuration must not be null");
            }
            final List<SelectOption> aspsps = new ArrayList<>();
            for( Aspsp aspsp : GetAspspsResponse.fromJson( paymentFormConfigurationRequest.getPluginConfiguration() ).getAspsps() ){
                // filter by country code
                // TODO: validate that the filter is performed using the locale (@see PAYLAPMEXT-203). Correct if necessary.
                if( aspsp.getCountryCode() != null &&
                        ( locale.getCountry() == null || locale.getCountry().equalsIgnoreCase(  aspsp.getCountryCode()) )){
                    // build the string to display in the select option value
                    List<String> values = new ArrayList<>();
                    if( aspsp.getBic() != null && !aspsp.getBic().isEmpty() ){
                        values.add( aspsp.getBic() );
                    }
                    if( aspsp.getName() != null && !aspsp.getName().isEmpty() ){
                        values.add( aspsp.getName().get(0) );
                    }
                    // add the ASPSP to the select choices
                    aspsps.add( SelectOption.SelectOptionBuilder.aSelectOption()
                            .withKey( aspsp.getAspspId() )
                            .withValue( String.join(" - ", values) )
                            .build() );
                }
            }

            // Build the payment form
            CustomForm form = BankTransferForm.builder()
                    .withBanks( aspsps )
                    .withDescription( i18n.getMessage( "paymentForm.description", locale ) )
                    .withDisplayButton( true )
                    .withButtonText( i18n.getMessage( "paymentForm.buttonText", locale ) )
                    .withCustomFields( new ArrayList<>() )
                    .build();

            pfcResponse = PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                    .aPaymentFormConfigurationResponseSpecific()
                    .withPaymentForm( form )
                    .build();
        }
        catch( PluginException e ){
            pfcResponse = e.toPaymentFormConfigurationResponseFailureBuilder().build();
        }
        catch( RuntimeException e ){
            LOGGER.error("Unexpected plugin error", e);
            pfcResponse = PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder
                    .aPaymentFormConfigurationResponseFailure()
                    .withErrorCode( PluginException.runtimeErrorCode( e ) )
                    .withFailureCause( FailureCause.INTERNAL_ERROR )
                    .build();
        }

        return pfcResponse;
    }
}
