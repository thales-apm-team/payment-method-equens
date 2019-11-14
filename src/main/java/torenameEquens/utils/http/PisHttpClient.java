package torenameEquens.utils.http;

import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTTP client in charge of requesting the PIS (Payment Initiation Service) API.
 */
public class PisHttpClient extends EquensHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(PisHttpClient.class);

    // --- Singleton Holder pattern + initialization BEGIN
    PisHttpClient() {
    }
    private static class Holder {
        private static final PisHttpClient instance = new PisHttpClient();
    }
    public static PisHttpClient getInstance() {
        return Holder.instance;
    }

    // TODO: init (?)
    // --- Singleton Holder pattern + initialization END

    @Override
    protected String appName() {
        return "PIS";
    }

}
