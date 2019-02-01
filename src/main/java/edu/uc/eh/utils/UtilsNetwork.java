package edu.uc.eh.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by chojnasm on 8/11/15.
 */
public class UtilsNetwork {
    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static BufferedReader downloadFile(String link){
        URL url;
        BufferedReader in = null;
        try {


            String encodedLink = fixedEncodeURI(link);
//            System.out.println("Before encoding");
//            System.out.println(link);
//            System.out.println("After encoding");
//            System.out.println(encodedLink);
//            System.out.println("----------------");
            url = new URL(encodedLink);
            in = new BufferedReader(new InputStreamReader(url.openStream()));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }
    public static String fixedEncodeURI(String str)  {
        System.out.println(str);
        String decodeURL = str.replaceAll("\\[", "%5B").replaceAll("\\]","%5D");

//        try {
//            String prevURL="";
//            String decodeURL= str;
//            while(!prevURL.equals(decodeURL))
//            {
//                prevURL=decodeURL;
//                decodeURL= URLDecoder.decode( decodeURL, "UTF-8" );
//            }
//
//            return decodeURL;
//        } catch (UnsupportedEncodingException e) {
//            return "Issue while decoding" +e.getMessage();
//        }
//

        return decodeURL;
    }
}
