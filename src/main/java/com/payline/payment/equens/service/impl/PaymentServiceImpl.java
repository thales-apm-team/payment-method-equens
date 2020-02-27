package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.bean.GenericPaymentRequest;
import com.payline.payment.equens.bean.business.reachdirectory.GetAspspsResponse;
import com.payline.payment.equens.service.Payment;
import com.payline.payment.equens.utils.PluginUtils;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {
    private Payment payment = Payment.getInstance();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        GenericPaymentRequest genericPaymentRequest = new GenericPaymentRequest(paymentRequest);

        // extract BIC
        String bic = paymentRequest.getPaymentFormContext().getPaymentFormParameter().get(BankTransferForm.BANK_KEY);

        // get the aspspId from the BIC
        String aspspId = PluginUtils.getAspspIdFromBIC(
                    GetAspspsResponse.fromJson(PluginUtils.extractBanks(paymentRequest.getPluginConfiguration())).getAspsps()
                , bic);

        // execute the payment Request
        return payment.paymentRequest(genericPaymentRequest, aspspId);
    }
}
