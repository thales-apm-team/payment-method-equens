import org.tomitribe.auth.signatures.Base64;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static final String REL_PATH_AUTHORIZATION = "/authorize/token";
    private static final String REL_PATH_PAYMENT_INIT = "/pis/v1/payments";
    private static final String REL_PATH_PAYMENT_CONFIRM = "/pis/v1/payments/{paymentId}/confirmation";
    private static final String REL_PATH_PAYMENT_STATUS = "/pis/v1/payments/{paymentId}/status";

    private static String accessToken(){
        return "7B5cCVcHT6eNHg8RvMr6o9vWlv7wETnV3GDgCiy7J1mDUEZ2qFzeEs";
    }

    public static void main( String[] args ) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        // Method conf
        String endpoint = REL_PATH_AUTHORIZATION;

        // Configuration (partner / contract)
        String keyStoreType = "pkcs12";
        String keyStorePath = "D:\\Monext.Payline\\MdP\\Equens (Instant Payments)\\EquensWorldlineTest.p12";
        char[] passwd = "EquensCert2019".toCharArray();
        String alias = "selfsigned";
        String paymentsApiBaseUrl = "https://xs2a.awltest.de/xs2a/routingservice/services";
        String onboardingID = "123456";

        // Load the keystore and recover the private key
        // @see https://www.baeldung.com/java-keystore
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        ks.load(new FileInputStream(keyStorePath), passwd);
        Certificate cert = ks.getCertificate(alias);
        Key pk = ks.getKey(alias, passwd);

        String sha1 = sha1( cert.getEncoded() );

        // Request headers
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("app", "PIS");
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        headers.put("client", "MarketPay");
        headers.put("date", df.format(new Date()));
        headers.put("id", onboardingID);
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        for(Map.Entry<String, String> entry : headers.entrySet() ){
            System.out.println( entry.getKey() + ": " + entry.getValue() );
        }

        String requestUri = paymentsApiBaseUrl + endpoint;
        String method = "POST";

        // Create a signer
        // @see https://github.com/tomitribe/http-signatures-java
        Signature signature = new Signature(sha1.replace(":", ""), "rsa-sha256", null, "app", "client", "id", "date");
        Signer signer = new Signer(pk, signature);

        // Sign the HTTP message
        // @see https://github.com/tomitribe/http-signatures-java
        signature = signer.sign(method, requestUri, headers);
        System.out.println("Authorization: " + signature.toString());
    }

    private static Date add( Date to, int days ){
        Calendar cal = Calendar.getInstance();
        cal.setTime( to );
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    private static String sha1( byte[] block ) throws NoSuchAlgorithmException {
        return toHexString(MessageDigest.getInstance("SHA1").digest( block ));
    }

    private static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len - 1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }

    private static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

}
