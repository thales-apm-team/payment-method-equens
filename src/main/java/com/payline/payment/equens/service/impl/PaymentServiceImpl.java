package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.bean.business.fraud.PsuSessionInformation;
import com.payline.payment.equens.bean.business.payment.*;
import com.payline.payment.equens.bean.business.psu.Psu;
import com.payline.payment.equens.bean.business.psu.PsuCreateRequest;
import com.payline.payment.equens.bean.configuration.RequestConfiguration;
import com.payline.payment.equens.exception.InvalidDataException;
import com.payline.payment.equens.exception.PluginException;
import com.payline.payment.equens.utils.Constants;
import com.payline.payment.equens.utils.http.PisHttpClient;
import com.payline.payment.equens.utils.http.PsuHttpclient;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

    private PisHttpClient pisHttpClient = PisHttpClient.getInstance();
    private PsuHttpclient psuHttpclient = PsuHttpclient.getInstance();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse;

        try {
            // Init HTTP clients
            pisHttpClient.init( paymentRequest.getPartnerConfiguration() );
            psuHttpclient.init( paymentRequest.getPartnerConfiguration() );

            // Build request configuration
            RequestConfiguration requestConfiguration = RequestConfiguration.build( paymentRequest );

            // Create a new PSU
            Psu newPsu = psuHttpclient.createPsu( new PsuCreateRequest.PsuCreateRequestBuilder().build(), requestConfiguration );

            // Control on the input data (to avoid NullPointerExceptions)
            if( paymentRequest.getAmount() == null
                    || paymentRequest.getAmount().getCurrency() == null
                    || paymentRequest.getAmount().getAmountInSmallestUnit() == null ){
                throw new InvalidDataException("Missing or invalid paymentRequest.amount");
            }
            if( paymentRequest.getOrder() == null ){
                throw new InvalidDataException("paymentRequest.order is required");
            }

            // Check required contract properties
            List<String> requiredContractProperties = Arrays.asList(
                    Constants.ContractConfigurationKeys.CHANNEL_TYPE,
                    Constants.ContractConfigurationKeys.CHARGE_BEARER,
                    Constants.ContractConfigurationKeys.MERCHANT_IBAN,
                    Constants.ContractConfigurationKeys.PURPOSE_CODE,
                    Constants.ContractConfigurationKeys.SCA_METHOD
            );
            requiredContractProperties.forEach( key -> {
                if( paymentRequest.getContractConfiguration().getProperty( key ) == null
                        || paymentRequest.getContractConfiguration().getProperty( key ).getValue() == null ){
                    throw new InvalidDataException("\"" + key + "\" missing from contract properties");
                }
            });

            // Extract delivery address
            Address deliveryAddress = null;
            if( paymentRequest.getBuyer().getAddresses() != null ){
                deliveryAddress = this.buildAddress( paymentRequest.getBuyer().getAddressForType( Buyer.AddressType.DELIVERY ) );
            }

            // Build PaymentInitiationRequest (Equens) from PaymentRequest (Payline)
            PaymentInitiationRequest request = new PaymentInitiationRequest.PaymentInitiationRequestBuilder()
                    .withAspspId(
                            paymentRequest.getPaymentFormContext().getPaymentFormParameter().get( BankTransferForm.BANK_KEY )
                    )
                    .withEndToEndId( paymentRequest.getTransactionId() )
                    .withInitiatingPartyReferenceId( paymentRequest.getOrder().getReference() )
                    .withInitiatingPartyReturnUrl( paymentRequest.getEnvironment().getRedirectionReturnURL() )
                    .withRemittanceInformation( paymentRequest.getSoftDescriptor() )
                    .withRemittanceInformationStructured(
                            new RemittanceInformationStructured.RemittanceInformationStructuredBuilder()
                                    .withReference( paymentRequest.getOrder().getReference() )
                                    .build()
                    )
                    .withDebtorAccount(
                            new Account.AccountBuilder()
                                    .withIdentification( "" ) // TODO: where do we find this data ?!
                                    .build()
                    )
                    .withCreditorAccount(
                            new Account.AccountBuilder()
                                    .withIdentification(
                                            paymentRequest.getContractConfiguration().getProperty( Constants.ContractConfigurationKeys.MERCHANT_IBAN ).getValue()
                                    )
                                    .build()
                    )
                    .withPaymentAmount( this.convertAmount( paymentRequest.getAmount() ) )
                    .withPaymentCurrency( paymentRequest.getAmount().getCurrency().getCurrencyCode() )
                    .withPurposeCode(
                            paymentRequest.getContractConfiguration().getProperty( Constants.ContractConfigurationKeys.PURPOSE_CODE ).getValue()
                    )
                    .withPsuSessionInformation(
                            new PsuSessionInformation.PsuSessionInformationBuilder()
                                    .withIpAddress( paymentRequest.getBrowser() != null ? paymentRequest.getBrowser().getIp() : null )
                                    .withHeaderUserAgent( paymentRequest.getBrowser() != null ? paymentRequest.getBrowser().getUserAgent() : null )
                                    .build()
                    )
                    .withRiskInformation(
                            new RiskInformation.RiskInformationBuilder()
                                    .withMerchantCategoryCode( paymentRequest.getSubMerchant() != null ? paymentRequest.getSubMerchant().getSubMerchantMCC() : null )
                                    .withMerchantCustomerId( paymentRequest.getBuyer().getCustomerIdentifier() )
                                    .withDeliveryAddress( deliveryAddress )
                                    .withChannelType(
                                            paymentRequest.getContractConfiguration().getProperty( Constants.ContractConfigurationKeys.CHANNEL_TYPE ).getValue()
                                    )
                                    .build()
                    )
                    .withPreferredScaMethod(
                            paymentRequest.getContractConfiguration().getProperty( Constants.ContractConfigurationKeys.SCA_METHOD ).getValue()
                    )
                    .withChargeBearer(
                            paymentRequest.getContractConfiguration().getProperty( Constants.ContractConfigurationKeys.CHARGE_BEARER ).getValue()
                    )
                    .withPsuId( newPsu.getPsuId() )
                    .withPaymentProduct(
                            paymentRequest.getPartnerConfiguration().getProperty( Constants.PartnerConfigurationKeys.PAYMENT_PRODUCT )
                    )
                    .build();

            // Send the payment initiation request
            PaymentInitiationResponse paymentInitResponse = pisHttpClient.initPayment( request, requestConfiguration );

            // URL
            PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder redirectionRequestBuilder = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder.aRedirectionRequest()
                    .withUrl( paymentInitResponse.getAspspRedirectUrl() );

            // request context
            // TODO: check if there is a need for RequestContext (see Natixis integration results)

            // Build PaymentResponse
            paymentResponse = PaymentResponseRedirect.PaymentResponseRedirectBuilder.aPaymentResponseRedirect()
                    .withPartnerTransactionId( paymentInitResponse.getPaymentId() )
                    .withStatusCode( paymentInitResponse.getPaymentStatus().name() )
                    .withRedirectionRequest( new PaymentResponseRedirect.RedirectionRequest( redirectionRequestBuilder ) )
                    .build();
        }
        catch( PluginException e ){
            paymentResponse = e.toPaymentResponseFailureBuilder().build();
        }
        catch( RuntimeException e ){
            LOGGER.error("Unexpected plugin error", e);
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode( PluginException.runtimeErrorCode( e ) )
                    .withFailureCause( FailureCause.INTERNAL_ERROR )
                    .build();
        }

        return paymentResponse;
    }

    /**
     * Build a Equens <code>Address</code> from a Payline <code>Buyer.Address</code>.
     * In particular, manage the length of the address lines between Payline and Equens.
     *
     * @param paylineAddress the Payline address
     * @return The corresponding Equens address.
     */
    Address buildAddress( Buyer.Address paylineAddress ){
        Address deliveryAddress = null;

        if( paylineAddress != null ){
            List<String> lines = new ArrayList<>();

            // concat the full address line to split
            String toSplit = paylineAddress.getStreet1();
            if( paylineAddress.getStreet2() != null && !paylineAddress.getStreet2().isEmpty() ){
                toSplit += " " + paylineAddress.getStreet2();
            }

            // Split it in chunks of the max allowed length for an address line
            while( !toSplit.isEmpty() ){
                int chunkLength = Math.min(Address.ADDRESS_LINE_MAX_LENGTH, toSplit.length());

                // look for the first whitespace from the end of the chunk, to truncate properly
                int i = chunkLength;
                while( i > 0 && !Character.isWhitespace( toSplit.charAt(i-1) ) ){
                    i--;
                }
                // no whitespace: truncate brutally
                if( i == 0 ){
                    i = chunkLength;
                }
                // add the new line and remove the chunk from the initial string
                lines.add( toSplit.substring(0, i).trim() );
                toSplit = i == toSplit.length() ? "" : toSplit.substring(i).trim();
            }

            deliveryAddress = new Address.AddressBuilder()
                    .withAddressLines( lines )
                    .withPostCode( paylineAddress.getZipCode() )
                    .withTownName( paylineAddress.getCity() )
                    .withCountry( paylineAddress.getCountry() )
                    .build();
        }

        return deliveryAddress;
    }

    /**
     * Convert a Payline <code>Amount</code> into a string with a dot as decimals separator.
     *
     * @param amount the amount to convert
     * @return The amount formatted as a string
     */
    String convertAmount( Amount amount ){
        if (amount == null || amount.getAmountInSmallestUnit() == null || amount.getCurrency() == null) {
            return null;
        }
        Currency currency = amount.getCurrency();
        BigInteger amountInSmallestUnit = amount.getAmountInSmallestUnit();

        // number of digits of the currency
        int nbDigits = currency.getDefaultFractionDigits();

        // init string builder
        StringBuilder sb = new StringBuilder();
        sb.append(amountInSmallestUnit);

        // if necessary, add digits at the beginning (ex: convert "1" to "001" for EUR, for example)
        for (int i = sb.length(); i < nbDigits+1; i++) {
            sb.insert(0, "0");
        }

        // add the dot separator (ex: convert "001" to "0.01" for EUR)
        if( nbDigits > 0 ){
            sb.insert(sb.length() - nbDigits, ".");
        }

        return sb.toString();
    }
}
