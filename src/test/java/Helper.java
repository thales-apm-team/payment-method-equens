import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.tomitribe.auth.signatures.Algorithm.RSA_SHA256;

public class Helper {

    private static final String REL_PATH_AUTHORIZATION = "/authorize/token";
    private static final String REL_PATH_PAYMENT_INIT = "/pis/v1/payments";
    private static final String REL_PATH_PAYMENT_CONFIRM = "/pis/v1/payments/{paymentId}/confirmation";
    private static final String REL_PATH_PAYMENT_STATUS = "/pis/v1/payments/{paymentId}/status";

    public static void main( String[] args ) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        System.out.println("AUTHORIZATION");

        // Configuration (partner / contract)
        String keyStoreType = "pkcs12";
        //String keyStorePath = "D:\\Monext.Payline\\MdP\\Equens (Instant Payments)\\EquensWorldlineTest_20190704.p12";
        String keyStorePath = "/home/vagrant/Documents/Monext/Mdp/Equens_Worldline/Equens20190912.p12";
        char[] passwd = "EquensCert2019".toCharArray();
        String alias = "selfsigned";
        String paymentsApiBaseUrl = "https://xs2a.awltest.de/xs2a/routingservice/services";
        String onboardingID = "000061";

        // URI
        String requestUri = paymentsApiBaseUrl + REL_PATH_AUTHORIZATION;
        String method = "POST";
        System.out.println( method + " " + requestUri );

        // Load the keystore and recover the private key
        // @see https://www.baeldung.com/java-keystore
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        ks.load(new FileInputStream(keyStorePath), passwd);
        Certificate cert = ks.getCertificate(alias);
        Key pk = ks.getKey(alias, passwd);

        String sha1 = sha1( cert.getEncoded() );

        // Request headers
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("App", "PIS");
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        headers.put("Client", "MarketPay");
        headers.put("Date", df.format(new Date()));
        headers.put("Id", onboardingID);
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        for(Map.Entry<String, String> entry : headers.entrySet() ){
            System.out.println( entry.getKey() + ": " + entry.getValue() );
        }

        // Create a signer
        // @see https://github.com/tomitribe/http-signatures-java
        Signature signature = new Signature(sha1.replace(":", ""), RSA_SHA256, null, "app", "client", "id", "date");
        Signer signer = new Signer(pk, signature);

        // Sign the HTTP message
        // @see https://github.com/tomitribe/http-signatures-java
        signature = signer.sign(method, requestUri, headers);
        System.out.println("Authorization: " + signature.toString().replace(RSA_SHA256.getPortableName(), RSA_SHA256.getJmvName()));

        // PAYMENT INIT
        System.out.println("\nPAYMENT INIT");
        String uuid = UUID.randomUUID().toString();
        System.out.println("X-Request-ID: " + uuid);

        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String current = isoDateFormat.format(new Date());
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String transactionId = "MONEXT" + timestamp;
        String orderReference = "REF" + timestamp;
        String jsonBody = "{\n" +
                "  \"MessageCreateDateTime\": \"" + current + "\",\n" +
                "  \"MessageId\": \"" + uuid + "\",\n" +
                "  \"AspspId\": \"506\",\n" +
                "  \"EndToEndId\": \"" + transactionId + "\",\n" +
                "  \"InitiatingPartyReferenceId\": \"" + orderReference + "\",\n" +
                "  \"RemittanceInformationStructured\": {\n" +
                "    \"Reference\": \"" + orderReference + "\"\n" +
                "  },\n" +
                "  \"PaymentAmount\": \"10.00\",\n" +
                "  \"PaymentCurrency\": \"EUR\",\n" +
                "  \"PsuId\": \"21\",\n" +
                "  \"PaymentProduct\": \"Instant\"\n" +
                "}";
        System.out.println( "\n" + jsonBody );
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
