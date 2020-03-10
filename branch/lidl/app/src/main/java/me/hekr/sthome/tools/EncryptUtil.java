package me.hekr.sthome.tools;

import android.util.Base64;

/**
 * Created by ryanhsueh on 2018/12/11
 */

public class EncryptUtil {

    public static String encrypt(final String text) {
        if (text == null) {
            return "";
        }
        try {
            byte[] TextByte = text.getBytes();
            if (TextByte != null) {
                return Base64.encodeToString(TextByte, Base64.DEFAULT);
            }
        } catch (IllegalArgumentException e) {
            return "";
        }
        return "";
    }

    public static String decrypt(final String text) {
        if (text == null) {
            return "";
        }
        try {
            byte[] TextByte = Base64.decode(text, Base64.DEFAULT);
            if (TextByte != null) {
                return new String(TextByte);
            }
        } catch (IllegalArgumentException e) {
            return "";
        }
        return "";
    }

}
