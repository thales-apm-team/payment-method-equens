package com.payline.payment.equens.utils;

import com.payline.payment.equens.bean.business.reachdirectory.Aspsp;
import com.payline.payment.equens.exception.InvalidDataException;
import com.payline.payment.equens.service.impl.ConfigurationServiceImpl;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PluginUtils {
    public static final String SEPARATOR = "&&&";

    /* Static utility class : no need to instantiate it (to please Sonar) */
    private PluginUtils() {
    }

    /**
     * Convert the path and headers of a {@link HttpRequestBase} to a readable {@link String}.
     * Mainly, for debugging purpose.
     *
     * @param httpRequest the request to convert
     * @return request method, path and headers as a string
     */
    public static String requestToString(HttpRequestBase httpRequest) {
        String ln = System.lineSeparator();
        String str = httpRequest.getMethod() + " " + httpRequest.getURI() + ln;

        List<String> strHeaders = new ArrayList<>();
        for (Header h : httpRequest.getAllHeaders()) {
            // For obvious security reason, the value of Authorization header is never printed in the logs
            if (HttpHeaders.AUTHORIZATION.equals(h.getName())) {
                String[] value = h.getValue().split(" ");
                strHeaders.add(h.getName() + ": " + (value.length > 1 ? value[0] : "") + " *****");
            } else {
                strHeaders.add(h.getName() + ": " + h.getValue());
            }
        }
        str += String.join(ln, strHeaders);

        if (httpRequest instanceof HttpPost) {
            try {
                str += ln + new BufferedReader(new InputStreamReader(((HttpPost) httpRequest).getEntity().getContent()))
                        .lines()
                        .collect(Collectors.joining(ln));
            } catch (IOException e) {
                str += ln + "<< Error retrieving request body >>";
            }
        }

        return str;
    }

    /**
     * Truncate the given string with the given length, if necessary.
     *
     * @param value  The string to truncate
     * @param length The maximum allowed length
     * @return The truncated string
     */
    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

    private static String extract(String s, int i) {
        return s.split(SEPARATOR)[i];
    }

    public static String extractBanks(String s) {
        return extract(s, 0);
    }

    public static String extractKey(String s) {
        return extract(s, 1);

    }

    /**
     * check if a String is null or empty
     *
     * @param s The string to check
     * @return true if the String is empty
     */
    public static boolean isEmpty(String s) {
        return (s == null || s.length() == 0);
    }

    /**
     * Try to find in aspsps list an aspsp with the given BIC11
     * if no aspsp found, try again by truncating the BIC11 into a BIC8
     *
     * @param aspsps The list of aspsp
     * @param bic    The BIC to find
     * @return The ID of the aspsp
     * @see https://payline.atlassian.net/browse/PAYLAPMEXT-221
     */
    public static String getAspspIdFromBIC(List<Aspsp> aspsps, String bic) {
        if (isEmpty(bic) || bic.length()<8){
            throw new InvalidDataException("Invalid bic:" + bic);
        }

        // try with the full BIC11
        String aspspId = filter(aspsps, bic);

        // no aspsp for this BIC11, we'll try with the BIC8
        if (aspspId == null) {
            aspspId = filter(aspsps, bic.substring(0, 8));
        }

        if (aspspId == null) {
            throw new InvalidDataException("Aspsp list does not contain the bic:" + bic);
        }

        // return its aspspId
        return aspspId;
    }

    public static String filter(List<Aspsp> aspsps, String bic) {
        // found aspsp with the correct Bic
        List<Aspsp> goodAspsps = aspsps.stream()
                .filter(x -> !PluginUtils.isEmpty(x.getBic()))
                .filter(x -> x.getBic().equalsIgnoreCase(bic))
                .collect(Collectors.toList());

        if (goodAspsps.isEmpty()) {
            return null;
        }

        // return its aspspId
        return goodAspsps.get(0).getAspspId();
    }

    // find the country of the bank from his BIC
    public static String getCountryCodeFromBIC(List<Aspsp> listAspsps, String bic) {
        if (isEmpty(bic) || bic.length()<8){
            throw new InvalidDataException("Invalid bic:" + bic);
        }

        if (listAspsps.isEmpty()) {
            throw new InvalidDataException("the list of Aspsps is empty");
        }

        for (Aspsp aspsp : listAspsps) {
            if (aspsp.getBic().equals(bic)) {
                return aspsp.getCountryCode();
            }
        }
        throw new InvalidDataException("Can't find a country for this BIC " + bic);
    }

    // create the list of countries accepted by the merchant
    public static List<String> createListCountry(String country) {
        List<String> listCountryCode = new ArrayList<>();
        if (PluginUtils.isEmpty(country)) {
            throw new InvalidDataException("Country in ContractConfiguration should not be empty");
        }

        // all the countries available for Equens
        if (country.trim().equalsIgnoreCase(ConfigurationServiceImpl.CountryCode.ALL)) {
            listCountryCode.add(ConfigurationServiceImpl.CountryCode.FR);
            listCountryCode.add(ConfigurationServiceImpl.CountryCode.ES);
        } else {
            listCountryCode.add(country.trim().toUpperCase());
        }
        return listCountryCode;
    }

    // check if the 2 first letter in an IBAN are the iso code of country available by the merchant
    public static boolean correctIban(List<String> listCountry, String iban) {

        if (listCountry.isEmpty()) {
            throw new InvalidDataException("listCountry should not be empty");
        }

        for (String s : listCountry) {
            String pattern = "(^" + s + "[0-9A-Z]*)";
            if (iban.trim().toUpperCase().matches(pattern)) {
                return true;
            }
        }
        return false;
    }
}