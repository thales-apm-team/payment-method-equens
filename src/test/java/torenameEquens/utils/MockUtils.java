package torenameEquens.utils;

import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.Environment;
import torenameEquens.bean.configuration.RequestConfiguration;
import torenameEquens.utils.http.Authorization;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class that generates mocks of frequently used objects.
 */
public class MockUtils {

    public static Authorization.AuthorizationBuilder anAuthorizationBuilder(){
        return new Authorization.AuthorizationBuilder()
                .withAccessToken("ABCD1234567890")
                .withTokenType("Bearer")
                .withExpiresAt( TestUtils.addTime(new Date(), Calendar.HOUR, 1) );
    }

    /**
     * Generate a valid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfiguration(){
        // TODO: change it
        ContractConfiguration contractConfiguration = new ContractConfiguration("ChangeMe", new HashMap<>());
        // TODO: complete it
        return contractConfiguration;
    }

    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment(){
        return new Environment("http://notificationURL.com",
                "http://redirectionURL.com",
                "http://redirectionCancelURL.com",
                true);
    }

    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration(){
        Map<String, String> partnerConfigurationMap = new HashMap<>();
        // TODO: complete it
        Map<String, String> sensitiveConfigurationMap = new HashMap<>();
        // TODO: complete it
        return new PartnerConfiguration( partnerConfigurationMap, sensitiveConfigurationMap );
    }

    /**
     * Generate a valid {@link RequestConfiguration}.
     */
    public static RequestConfiguration aRequestConfiguration(){
        return new RequestConfiguration( aContractConfiguration(), anEnvironment(), aPartnerConfiguration() );
    }

}
