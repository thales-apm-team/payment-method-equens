package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.bean.GenericPaymentRequest;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.exception.PluginException;
import com.payline.payment.equens.service.Payment;
import com.payline.payment.equens.utils.PluginUtils;
import com.payline.payment.equens.utils.security.RSAUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.WalletPaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentWalletService;
import org.apache.logging.log4j.Logger;

public class PaymentWalletServiceImpl implements PaymentWalletService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentWalletServiceImpl.class);

    private RSAUtils rsaUtils = RSAUtils.getInstance();
    private Payment payment = Payment.getInstance();

    @Override
    public PaymentResponse walletPaymentRequest(WalletPaymentRequest walletPaymentRequest) {
        try {
            GenericPaymentRequest genericPaymentRequest = new GenericPaymentRequest(walletPaymentRequest);

            // get decrypted wallet data (BIC)
            String encryptedBic = walletPaymentRequest.getWallet().getPluginPaymentData();
            String key = PluginUtils.extractKey(walletPaymentRequest.getPluginConfiguration());
            String bic = rsaUtils.decrypt(encryptedBic, key);

            // get the aspspId from the BIC
            String aspspId = PluginUtils.getAspspIdFromBIC(
                    GetAspspsResponse.fromJson(PluginUtils.extractBanks(walletPaymentRequest.getPluginConfiguration())).getAspsps()
                    , bic);

            return payment.paymentRequest(genericPaymentRequest, aspspId);
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
    }
}
