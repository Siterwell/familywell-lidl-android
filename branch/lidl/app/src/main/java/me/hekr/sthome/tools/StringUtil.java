package me.hekr.sthome.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by ryanhsueh on 2019/1/24
 */
public class StringUtil {
    public static final String UTF_8 = "UTF-8";

    public static String encodeUTF8(String src) {
        String dst = src;

        try {
            dst = URLEncoder.encode(src, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return dst;
    }

    public static String decodeUTF8(String src) {
        String dst = src;
        try {
            dst = URLDecoder.decode(src, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return dst;
    }

}
