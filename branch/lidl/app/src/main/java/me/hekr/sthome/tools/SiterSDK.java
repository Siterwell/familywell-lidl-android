package me.hekr.sthome.tools;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.util.Scanner;

import me.hekr.sdk.utils.CacheUtil;


/*
@class SiterSDK
@autor henry
@time 2019/3/27 1:52 PM
@email xuejunju_4595@qq.com
用来读取配置文件做换皮项目适配
*/
public class SiterSDK {

    private static boolean isSDKInited = false;
    private static final String TAG = SiterSDK.class.getSimpleName();
    private static Context application;
    public static String SETTINGS_CONFIG_INSTRUCTION = "com.siterwell.sthome.instruction";
    public static String SETTINGS_CONFIG_WEBSITE = "com.siterwell.sthome.website";
    public static String SETTINGS_CONFIG_VER = "com.siterwell.sthome.appver";
    public static String SETTINGS_CONFIG_DOWN_URL = "com.siterwell.sthome.downurl";
    public static String SETTINGS_CONFIG_MI_APPID = "com.siterwell.sthome.mi.appid";
    public static String SETTINGS_CONFIG_MI_APPKEY = "com.siterwell.sthome.mi.appkey";
    public static String SETTINGS_CONFIG_UDP_SETTING = "com.siterwell.sthome.upd.setting";
    /**
     * 初始化SDK, id 必须传入json格式文件的资源id。文件格式详见SDK文档。
     *
     * @param context 请使用getApplicationContext()
     * @param id    json格式的配置信息
     */
    public static void init(Context context, int id) {
        if (nowProcess(context) && !isSDKInited) {
            CacheUtil.init(application);
            initProjInfo(context,id);
            isSDKInited = true;
        } else {
            Log.e(TAG, "SDK is not on the application's process");
        }
    }


    /**
     * 初始化sdk的信息
     */
    private static void initProjInfo(Context context, int id) {
        String json =convertStreamToString(context, id);
        initAppInfo(json);
    }

    /**
     * 初始化sdk的信息
     */
    private static void initAppInfo(String json) {
        try {
            if (!TextUtils.isEmpty(json)) {
                org.json.JSONObject jsonConfig = new org.json.JSONObject(json);
                CacheUtil.putString(SETTINGS_CONFIG_INSTRUCTION, jsonConfig.getJSONObject("instruction").toString());
                CacheUtil.putString(SETTINGS_CONFIG_WEBSITE, jsonConfig.getJSONObject("offical_web").toString());
                CacheUtil.putString(SETTINGS_CONFIG_VER,jsonConfig.getString("app_upgrade_url"));
                CacheUtil.putString(SETTINGS_CONFIG_DOWN_URL,jsonConfig.getString("app_download_url"));
                CacheUtil.putString(SETTINGS_CONFIG_MI_APPID, jsonConfig.getJSONObject("mi_config").getString("appid"));
                CacheUtil.putString(SETTINGS_CONFIG_MI_APPKEY, jsonConfig.getJSONObject("mi_config").getString("appkey"));
                CacheUtil.putString(SETTINGS_CONFIG_UDP_SETTING,jsonConfig.getString("udp_config"));
            } else {
                throw new IllegalArgumentException("Json is empty");
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException("Json format is incorrect");
        }
    }

    /**
     * 判断sdk是否在当前进程
     */
    private static boolean nowProcess(Context context) {
        String processName = me.hekr.sdk.utils.OsUtil.getProcessName(context, android.os.Process.myPid());
        return !TextUtils.isEmpty(processName) && TextUtils.equals(context.getPackageName(), processName);
    }

    public static String convertStreamToString(Context mContext, int id) {
        InputStream is = mContext.getResources().openRawResource(id);
        String s = "";
        try {
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            if (scanner.hasNext()) s = scanner.next();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
