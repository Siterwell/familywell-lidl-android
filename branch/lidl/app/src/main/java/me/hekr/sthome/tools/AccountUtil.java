package me.hekr.sthome.tools;

import android.content.SharedPreferences;

/**
 * Created by ryanhsueh on 2018/12/11
 */
public class AccountUtil {

    private static String decrypt(ECPreferenceSettings setting) {
        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        String text = sharedPreferences.getString(setting.getId(), (String) setting.getDefaultValue());
        return EncryptUtil.decrypt(text);
    }

    public static String getUsername(){
        return decrypt(ECPreferenceSettings.SETTINGS_USERNAME);
    }

    public static  String getPassword(){
        return decrypt(ECPreferenceSettings.SETTINGS_PASSWORD);
    }

    public static  String getDomain(){
        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings setting = ECPreferenceSettings.SETTINGS_DOMAIN;
        return sharedPreferences.getString(setting.getId(), (String) setting.getDefaultValue());
    }

    public static boolean forceLogout() {
        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings setting = ECPreferenceSettings.SETTINGS_FORCE_LOGOUT;
        return sharedPreferences.getBoolean(setting.getId(), (boolean) setting.getDefaultValue());
    }

}
