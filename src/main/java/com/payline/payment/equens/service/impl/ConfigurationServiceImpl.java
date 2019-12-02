package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.PluginException;
import com.payline.payment.equens.utils.Constants;
import com.payline.payment.equens.utils.http.PisHttpClient;
import com.payline.payment.equens.utils.http.PsuHttpClient;
import com.payline.payment.equens.utils.i18n.I18nService;
import com.payline.payment.equens.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.ListBoxParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.request.RetrievePluginConfigurationRequest;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ConfigurationService;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigurationServiceImpl implements ConfigurationService {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationServiceImpl.class);

    private static final String I18N_CONTRACT_PREFIX = "contract.";

    public static final class ChannelType {
        public static final String ECOMMERCE = "Ecommerce";
    }

    public static final class ChargeBearer {
        public static final String CRED = "CRED";
        public static final String DEBT = "DEBT";
        public static final String SHAR = "SHAR";
        public static final String SLEV = "SLEV";
    }

    public static final class PurposeCode {
        public static final String CARPARK = "Carpark";
        public static final String COMMERCE = "Commerce";
        public static final String TRANSPORT = "Transport";
    }

    public static final class ScaMethod {
        public static final String REDIRECT = "Redirect";
    }

    private I18nService i18n = I18nService.getInstance();
    private PisHttpClient pisHttpClient = PisHttpClient.getInstance();
    private PsuHttpClient psuHttpClient = PsuHttpClient.getInstance();
    private ReleaseProperties releaseProperties = ReleaseProperties.getInstance();


    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        // Client name
        parameters.add( this.newInputParameter( Constants.ContractConfigurationKeys.CLIENT_NAME, true, locale ) );

        // Onboarding ID
        parameters.add( this.newInputParameter( Constants.ContractConfigurationKeys.ONBOARDING_ID, true, locale ) );

        // mérchant iban
        parameters.add( this.newInputParameter( Constants.ContractConfigurationKeys.MERCHANT_IBAN, true, locale ) );

        // channel type
        Map<String, String> channelTypes = new HashMap<>();
        channelTypes.put(ChannelType.ECOMMERCE, ChannelType.ECOMMERCE);
        parameters.add( this.newListBoxParameter( Constants.ContractConfigurationKeys.CHANNEL_TYPE, channelTypes, channelTypes.get(0), true, locale ) );

        // SCA method
        Map<String, String> scaMethods = new HashMap<>();
        scaMethods.put(ScaMethod.REDIRECT, ScaMethod.REDIRECT);
        parameters.add( this.newListBoxParameter( Constants.ContractConfigurationKeys.SCA_METHOD, scaMethods, scaMethods.get(0), true, locale ) );

        // Charge bearer
        Map<String, String> chargeBearers = new HashMap<>();
        chargeBearers.put(ChargeBearer.CRED, ChargeBearer.CRED);
        chargeBearers.put(ChargeBearer.DEBT, ChargeBearer.DEBT);
        chargeBearers.put(ChargeBearer.SHAR, ChargeBearer.SHAR);
        chargeBearers.put(ChargeBearer.SLEV, ChargeBearer.SLEV);
        parameters.add( this.newListBoxParameter( Constants.ContractConfigurationKeys.CHARGE_BEARER, chargeBearers, ChargeBearer.SLEV, true, locale ) );

        // purpose code
        Map<String, String> purposeCodes = new HashMap<>();
        purposeCodes.put(PurposeCode.CARPARK, PurposeCode.CARPARK);
        purposeCodes.put(PurposeCode.COMMERCE, PurposeCode.COMMERCE);
        purposeCodes.put(PurposeCode.TRANSPORT, PurposeCode.TRANSPORT);
        parameters.add( this.newListBoxParameter( Constants.ContractConfigurationKeys.PURPOSE_CODE, purposeCodes, PurposeCode.COMMERCE, true, locale ) );

        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest contractParametersCheckRequest) {
        final Map<String, String> errors = new HashMap<>();

        Map<String, String> accountInfo = contractParametersCheckRequest.getAccountInfo();
        Locale locale = contractParametersCheckRequest.getLocale();

        // check required fields
        for( AbstractParameter param : this.getParameters( locale ) ){
            if( param.isRequired() && accountInfo.get( param.getKey() ) == null ){
                String message = i18n.getMessage(I18N_CONTRACT_PREFIX + param.getKey() + ".requiredError", locale);
                errors.put( param.getKey(), message );
            }
        }

        // Check the clientName and onboarding ID
        String clientNameKey = Constants.ContractConfigurationKeys.CLIENT_NAME;
        String onboardingIdKey = Constants.ContractConfigurationKeys.ONBOARDING_ID;

        // If one of them is missing, no need to go further as they are both required to get an access token
        if( errors.containsKey( clientNameKey ) || errors.containsKey( onboardingIdKey ) ){
            return errors;
        }

        // We first need to replace these 2 values in the ContractConfiguration (to override the former validated values)
        RequestConfiguration altRequestConfiguration = RequestConfiguration.build( contractParametersCheckRequest );
        Map<String, ContractProperty> contractProperties = altRequestConfiguration.getContractConfiguration().getContractProperties();
        contractProperties.put( clientNameKey, new ContractProperty( accountInfo.get( clientNameKey ) ) );
        contractProperties.put( onboardingIdKey, new ContractProperty( accountInfo.get( onboardingIdKey ) ) );

        // Validate the merchant account on the 2 APIs (PIS and PSU) because they have separate subscriptions
        try {
            pisHttpClient.init(contractParametersCheckRequest.getPartnerConfiguration());
            pisHttpClient.authorize( altRequestConfiguration );

            psuHttpClient.init(contractParametersCheckRequest.getPartnerConfiguration());
            psuHttpClient.authorize( altRequestConfiguration );
        }
        catch( PluginException e ){
            errors.put( clientNameKey, e.getMessage());
            errors.put( onboardingIdKey, "" );
        }

        return errors;
    }

    @Override
    public String retrievePluginConfiguration(RetrievePluginConfigurationRequest retrievePluginConfigurationRequest) {
        try {
            RequestConfiguration requestConfiguration = RequestConfiguration.build( retrievePluginConfigurationRequest );

            // Init HTTP client
            pisHttpClient.init( requestConfiguration.getPartnerConfiguration() );

            // Retrieve account service providers list
            GetAspspsResponse apspsps = pisHttpClient.getAspsps( requestConfiguration );

            // Serialize the list (as JSON)
            return apspsps.toString();
        }
        catch( RuntimeException e ){
            LOGGER.error("Could not retrieve plugin configuration due to a plugin error", e );
            return retrievePluginConfigurationRequest.getPluginConfiguration();
        }
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate(LocalDate.parse(releaseProperties.get("release.date"), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .withVersion(releaseProperties.get("release.version"))
                .build();
    }

    @Override
    public String getName(Locale locale) {
        return i18n.getMessage("paymentMethod.name", locale);
    }

    /**
     * Build and return a new <code>InputParameter</code> for the contract configuration.
     *
     * @param key The parameter key
     * @param required Is this parameter required ?
     * @param locale The current locale
     * @return The new input parameter
     */
    private InputParameter newInputParameter( String key, boolean required, Locale locale ){
        InputParameter inputParameter = new InputParameter();
        inputParameter.setKey( key );
        inputParameter.setLabel( i18n.getMessage(I18N_CONTRACT_PREFIX + key + ".label", locale) );
        inputParameter.setDescription( i18n.getMessage(I18N_CONTRACT_PREFIX + key + ".description", locale) );
        inputParameter.setRequired( required );
        return inputParameter;
    }

    /**
     * Build and return a new <code>ListBoxParameter</code> for the contract configuration.
     *
     * @param key The parameter key
     * @param values All the possible values for the list box
     * @param defaultValue The key of the default value (which will be selected by default)
     * @param required Is this parameter required ?
     * @param locale The current locale
     * @return The new list box parameter
     */
    private ListBoxParameter newListBoxParameter( String key, Map<String, String> values, String defaultValue, boolean required, Locale locale ){
        ListBoxParameter listBoxParameter = new ListBoxParameter();
        listBoxParameter.setKey( key );
        listBoxParameter.setLabel( i18n.getMessage(I18N_CONTRACT_PREFIX + key + ".label", locale) );
        listBoxParameter.setDescription( i18n.getMessage(I18N_CONTRACT_PREFIX + key + ".description", locale) );
        listBoxParameter.setList( values );
        listBoxParameter.setRequired( required );
        listBoxParameter.setValue( defaultValue );
        return listBoxParameter;
    }

}


