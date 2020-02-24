package com.payline.payment.equens.service.impl;

import com.payline.payment.equens.bean.GenericPaymentRequest;
import com.payline.payment.equens.service.Payment;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {
    private Payment payment = Payment.getInstance();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        GenericPaymentRequest genericPaymentRequest = new GenericPaymentRequest(paymentRequest);

        String aspspId = paymentRequest.getPaymentFormContext().getPaymentFormParameter().get(BankTransferForm.BANK_KEY);
        return payment.paymentRequest(genericPaymentRequest, aspspId);
    }
}
