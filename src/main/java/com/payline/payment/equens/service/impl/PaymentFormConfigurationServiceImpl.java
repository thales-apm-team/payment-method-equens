package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.bean.business.reachdirectory.Aspsp;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.exception.InvalidDataException;
import com.payline.payment.equens.exception.PluginException;
import com.payline.payment.equens.service.LogoPaymentFormConfigurationService;
import com.payline.payment.equens.utils.PluginUtils;
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
            if (paymentFormConfigurationRequest.getPluginConfiguration() == null) {
                throw new InvalidDataException("Plugin configuration must not be null");
            }
            String countryCode = paymentFormConfigurationRequest.getOrder().getCountry(); // @see https://payline.atlassian.net/browse/PAYLAPMEXT-203
            List<SelectOption> banks = this.getBanks(paymentFormConfigurationRequest.getPluginConfiguration(), countryCode);

            // Build the payment form
            CustomForm form = BankTransferForm.builder()
                    .withBanks(banks)
                    .withDescription(i18n.getMessage("paymentForm.description", locale))
                    .withDisplayButton(true)
                    .withButtonText(i18n.getMessage("paymentForm.buttonText", locale))
                    .withCustomFields(new ArrayList<>())
                    .build();

            pfcResponse = PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                    .aPaymentFormConfigurationResponseSpecific()
                    .withPaymentForm(form)
                    .build();
        } catch (PluginException e) {
            pfcResponse = e.toPaymentFormConfigurationResponseFailureBuilder().build();
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            pfcResponse = PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder
                    .aPaymentFormConfigurationResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }

        return pfcResponse;
    }

    /**
     * This method parses the PluginConfiguration string to read the list of ASPSPs and convert it to a list of choices
     * for a select list. The key of each option is the AspspId and the value is "BIC - name".
     * PAYLAPMEXT-204: if BIC is null, the selection option's value will just be the name of the bank.
     * PAYLAPMEXT-203: filter the list using the countryCode (if provided) to keep only the banks which country code matches.
     *
     * @param pluginConfiguration The PluginConfiguration string
     * @param countryCode         The 2-letters country code
     * @return The list of banks, as select options.
     */
    List<SelectOption> getBanks(String pluginConfiguration, String countryCode) {
        final List<SelectOption> options = new ArrayList<>();

        if (pluginConfiguration == null) {
            LOGGER.warn("pluginConfiguration is null");
        } else {
            for (Aspsp aspsp : GetAspspsResponse.fromJson(PluginUtils.extractBanks(pluginConfiguration)).getAspsps()) {
                // filter by country code
                if (aspsp.getCountryCode() != null &&
                        (PluginUtils.isEmpty(countryCode) || countryCode.equalsIgnoreCase(aspsp.getCountryCode()))) {
                    // build the string to display in the select option value
                    List<String> values = new ArrayList<>();
                    if (!PluginUtils.isEmpty(aspsp.getBic())) {
                        values.add(aspsp.getBic());

                        if (aspsp.getName() != null && !aspsp.getName().isEmpty()) {
                            values.add(aspsp.getName().get(0));
                        }
                        // add the ASPSP to the select choices
                        options.add(SelectOption.SelectOptionBuilder.aSelectOption()
                                .withKey(aspsp.getBic())
                                .withValue(String.join(" - ", values))
                                .build());
                    }
                }
            }
        }
        return options;
    }
}
