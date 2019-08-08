package torenameEquens.services.impl;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;
import torenameEquens.bean.configuration.RequestConfiguration;
import torenameEquens.exception.PluginException;
import torenameEquens.utils.http.PisHttpClient;
import torenameEquens.utils.http.PsuHttpclient;

public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

    private PisHttpClient pisHttpClient = PisHttpClient.getInstance();
    private PsuHttpclient psuHttpclient = PsuHttpclient.getInstance();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = null; // TODO: remove useless initialization

        try {
            // Init HTTP clients
            // TODO

            // Build request configuration
            RequestConfiguration requestConfiguration = RequestConfiguration.build( paymentRequest );

            // Build Payment from PaymentRequest
            // TODO: init payment request
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
}
