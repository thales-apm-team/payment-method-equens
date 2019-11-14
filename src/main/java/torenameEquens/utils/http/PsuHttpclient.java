package torenameEquens.utils.http;

import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTTP client in charge of requesting the PSU Management API.
 */
public class PsuHttpclient extends EquensHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(PsuHttpclient.class);

    // --- Singleton Holder pattern + initialization BEGIN
    PsuHttpclient() {
    }
    private static class Holder {
        private static final PsuHttpclient instance = new PsuHttpclient();
    }
    public static PsuHttpclient getInstance() {
        return PsuHttpclient.Holder.instance;
    }

    // TODO: init (?)
    // --- Singleton Holder pattern + initialization END

    @Override
    protected String appName() {
        return "PSU";
    }
}
