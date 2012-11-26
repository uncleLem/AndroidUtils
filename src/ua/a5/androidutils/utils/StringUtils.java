package ua.a5.androidutils.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Andrew Stoyaltsev
 * @email andrew.v.stoyaltsev@gmail.com
 */
public class StringUtils {

    public static boolean isNullOrEmpty(String s) {
        return (s == null || s.length() == 0);
    }

    /**
     * @param s   source string
     * @param del delimiter
     * @return result
     */
    public static String trim(String s, String del) {
        String result = s;
        if (s.startsWith(del)) {
            result = result.substring(del.length());
        }
        if (s.endsWith(del)) {
            result = result.substring(0, s.length() - del.length());
        }
        return result;
    }

    /**
     * @param is input stream
     * @return result
     */
    public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().trim();
    }

}
