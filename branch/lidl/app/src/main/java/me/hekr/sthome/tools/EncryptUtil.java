package me.hekr.sthome.tools;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by ryanhsueh on 2018/12/11
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class EncryptUtil {

    private final static Base64.Encoder encoder = Base64.getEncoder();
    private final static Base64.Decoder decoder = Base64.getDecoder();

    public static String encrypt(final String text) {
        if (text == null) {
            return "";
        }
        try{
            byte[] TextByte = text.getBytes(StandardCharsets.UTF_8);
            if (TextByte != null) {
                return encoder.encodeToString(TextByte);
            }
        }catch (IllegalArgumentException e){
            return "";
        }
        return "";
    }

    public static String decrypt(final String text) {
        if (text == null) {
            return "";
        }
        try{
            byte[] TextByte = decoder.decode(text);
            if (TextByte != null) {
                return new String(TextByte, StandardCharsets.UTF_8);
            }
        }catch (IllegalArgumentException e){
            return "";
        }
        return "";
    }
}
