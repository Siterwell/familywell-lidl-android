package me.hekr.sthome.tools;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.hekr.sthome.R;


/**
 * Created by jishu0001 on 2016/8/30.
 */
public class UnitTools {
    private Context context;
    private static MediaPlayer mediaPlayer = null;
    public UnitTools(Context context){
        this.context = context;
    }

    /**
     * get usr Login infor
     * @param name
     * @param key
     */
    public void writeUserInro(String name,String key){
        SharedPreferences user = context.getSharedPreferences("user_info",0);
        SharedPreferences.Editor mydata = user.edit();
        mydata.putString("uName" ,name);
        mydata.putString("uKey",key);
        mydata.commit();
    }
    public Map readUserInfo() {
        Map<String,Object> chosed = new HashMap<>();
        SharedPreferences wode = context.getSharedPreferences("user_info",0);
        String name = wode.getString("uName",null);
        String key = wode.getString("uKey",null);
        chosed.put("name",name);
        chosed.put("key",key);
        return chosed;
    }

    /**
     * methodname:
     * 作者：Henry on 2017/3/6 8:53
     * 邮箱：xuejunju_4595@qq.com
     * 参数:context
     * 返回:当前运行的activity的名称
     */
    public static String getRunningActivityName(Context context){
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }

    /**
     * methodname:
     * 作者：Henry on 2017/3/6 8:54
     * 邮箱：xuejunju_4595@qq.com
     * 参数:设置容器第一次打开标志
     * 返回:
     */
    public static void writeFirstOpen(Context context, String activityName,boolean falg){
        SharedPreferences user = context.getSharedPreferences("user_info",0);
        SharedPreferences.Editor mydata = user.edit();
        mydata.putBoolean(activityName ,falg);
        mydata.commit();
    }

    /**
     * methodname:
     * 作者：Henry on 2017/3/6 8:56
     * 邮箱：xuejunju_4595@qq.com
     * 参数:读取是否第一次打开标志
     * 返回:
     */
    public static boolean readFirstOpen(Context context,String activityName) {
        SharedPreferences wode = context.getSharedPreferences("user_info",0);
        boolean flag = wode.getBoolean(activityName,false);

        return flag;
    }

    public static List<String> decode1(List<String> mlist) {
        String temp="";
        for(int i = 0;i<mlist.size();i++){
            for(int j=i+1;j<mlist.size();j++){
                if( Integer.parseInt(mlist.get(i))> Integer.parseInt( mlist.get(j))){
                    temp = mlist.get(i);
                    mlist.set(i,mlist.get(j));
                    mlist.set(j,temp);
                }
            }
        }
        return mlist;
    }

    public static String timeDecode(String timeIn, int tag) {
        String time  ="";
        switch (tag){
            case 6:
                if(timeIn != null && timeIn.length() == tag && tag==6){
                    String d = timeIn.substring(0,2);
                    String h = timeIn.substring(2,4);
                    String m = timeIn.substring(4,6);
                    String d1 =d;
//                    if(Integer.toHexString(Integer.parseInt(h)).length()<2){
//                        h1 = "0"+Integer.toHexString(Integer.parseInt(h));
//                    }else {
//                        h1 = Integer.toHexString(Integer.parseInt(h));
//                    }
                    String h1 ="";
                    if(Integer.toHexString(Integer.parseInt(h)).length()<2){
                        h1 = "0"+ Integer.toHexString(Integer.parseInt(h));
                    }else {
                        h1 = Integer.toHexString(Integer.parseInt(h));
                    }
                    String m1 ="";
                    if(Integer.toHexString(Integer.parseInt(m)).length()<2){
                        m1 = "0"+ Integer.toHexString(Integer.parseInt(m));
                    }else {
                        m1 = Integer.toHexString(Integer.parseInt(m));
                    }
                    time = d1 +h1+m1;
                }else {
                    time ="000000";
                }
                break;
            case 4:
                if(timeIn!= null && timeIn.length() == 4 && tag ==4){
                    String m = timeIn.substring(0,2);
                    String s = timeIn.substring(2,4);
                    String m1 ="";
                    if(Integer.toHexString(Integer.parseInt(m)).length()<2){
                        m1 = "0"+ Integer.toHexString(Integer.parseInt(m));
                    }else {
                        m1 = Integer.toHexString(Integer.parseInt(m));
                    }
                    String s1 ="";
                    if(Integer.toHexString(Integer.parseInt(s)).length()<2){
                        s1 = "0"+ Integer.toHexString(Integer.parseInt(s));
                    }else {
                        s1 = Integer.toHexString(Integer.parseInt(s));
                    }
                    time= m1+s1;
                }else{
                    time = "0000";
                }
                break;
            default:
                break;
        }
        return time;
    }


    /**
     *判断当前应用程序处于前台还是后台
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void playNotifycationMusic(Context context)
            throws IOException {
        // paly music ...

        LOG.I("UtilTools", "[RYAN] playNotifycationMusic > mediaPlayer = " + mediaPlayer);

        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
            return;
        }

        mediaPlayer = MediaPlayer.create(context, R.raw.phonering);

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
//        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public static void stopMusic(Context context)
            throws IOException {
        // paly music ...
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.phonering);
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer = null;
    }

    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    //获取屏幕的宽度
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }


    /**
     * write ssid code
     * @param name
     * @param key
     */
    public void writeSSidcode(String name,String key){
        SharedPreferences user = context.getSharedPreferences("user_info",0);
        SharedPreferences.Editor mydata = user.edit();
        mydata.putString(name ,key);
        mydata.commit();
    }

    public String readSSidcode(String key) {
        SharedPreferences wode = context.getSharedPreferences("user_info",0);
        String psd = wode.getString(key,null);

        return psd;
    }

    /**
     * 设置语言
     * @param Language
     */
    public void writeLanguage(String Language){
        SharedPreferences user = context.getSharedPreferences("user_info",0);
        SharedPreferences.Editor mydata = user.edit();
        mydata.putString("Language" ,Language);
        mydata.commit();
    }


    public String readShareLanguage() {
        SharedPreferences wode = context.getSharedPreferences("user_info",0);
        String name = wode.getString("Language","");

        return name;
    }

    public String readLanguage() {
        Resources resource = context.getResources();
        Configuration config = resource.getConfiguration();
        Locale locale = context.getResources().getConfiguration().locale;//获得local对象

        return locale.getLanguage();
    }

    public String shiftLanguage(Context context,String sta){

        Resources resource = context.getResources();
        Configuration config = resource.getConfiguration();
        Locale locale = context.getResources().getConfiguration().locale;//获得local对象
        String lan = locale.getLanguage();

        if(TextUtils.isEmpty(sta)){
            if("zh".equals(lan)){
                config.locale = Locale.CHINA;
                writeLanguage("zh");
            }else if("fr".equals(lan)){
                config.locale = Locale.FRENCH;
                writeLanguage("fr");
            }else if("de".equals(lan)){
                config.locale = Locale.GERMANY;
                writeLanguage("de");
            }else{
                config.locale = Locale.ENGLISH;
                writeLanguage("en");
            }

            context.getResources().updateConfiguration(config, null);
            return lan;
        }else{
            if("zh".equals(sta)){
                config.locale = Locale.CHINA;
                writeLanguage("zh");
            }else if("fr".equals(sta)){
                config.locale = Locale.FRENCH;
                writeLanguage("fr");
            }else if("de".equals(sta)){
                config.locale = Locale.GERMANY;
                writeLanguage("de");
            }else{
                config.locale = Locale.ENGLISH;
                writeLanguage("en");
            }

            context.getResources().updateConfiguration(config, null);
            return sta;
        }



    }

    /**
     * get usr Login infor
     * @param name
     * @param name
     */
    public void writeUserLog(String name){
        SharedPreferences user = context.getSharedPreferences("user_info",0);
        SharedPreferences.Editor mydata = user.edit();
        mydata.putString("userlist" ,name);
        mydata.commit();
    }
    public String readUserLog() {
        SharedPreferences wode = context.getSharedPreferences("user_info",0);
        String list = wode.getString("userlist","[]");
        return list;
    }




    /**
     * 错误码转换到错误信息！
     *
     * @param errorCode 错误码
     * @return 错误信息
     */
    public static String errorCode2Msg(Context context,int errorCode) {
        switch (errorCode) {
            case 3400001:
                return context.getResources().getString(R.string.phone_number_invalid);
            case 3400002:
                return context.getResources().getString(R.string.verify_code_error);
            case 3400003:
                return context.getResources().getString(R.string.validation_code_expired);
            case 3400005:
                return context.getResources().getString(R.string.too_many_verification_codes);
            case 3400006:
                return context.getResources().getString(R.string.invalid_request_type);
            case 3400007:
                return context.getResources().getString(R.string.invalid_old_password);
            case 3400008:
                return context.getResources().getString(R.string.has_been_registered);
            case 3400009:
                return context.getResources().getString(R.string.has_not_validated_yet);
            case 3400010:
                return context.getResources().getString(R.string.account_or_password_error);
            case 3400011:
                return context.getResources().getString(R.string.user_does_not_exist);
            case 3400012:
                return context.getResources().getString(R.string.invalid_message_token);
            case 3400013:
                return context.getResources().getString(R.string.account_has_been_authenticated);
            case 3400014:
                return context.getResources().getString(R.string.account_has_been_associated_with_a_three_party_account);
            case 500:
            case 5200000:
                return context.getResources().getString(R.string.service_internal_error);
            case 5400001:
                return context.getResources().getString(R.string.internal_error);
            case 5400002:
                return context.getResources().getString(R.string.app_repeat_logon);
            case 5400003:
                return context.getResources().getString(R.string.appTid_cannot_be_empty);
            case 5400004:
                return context.getResources().getString(R.string.authorization_relationship_already_exists);
            case 5400005:
                return context.getResources().getString(R.string.authorization_relationship_does_not_exist);
            case 5400006:
                return context.getResources().getString(R.string.binding_failed_due_to_network_reasons);
            case 5400007:
                return context.getResources().getString(R.string.binding_failed_due_to_timeout_reason);
            case 5400009:
                return context.getResources().getString(R.string.failed_to_modify_user_file);
            case 5400010:
                return context.getResources().getString(R.string.failed_to_verify_code);
            case 5400011:
                return context.getResources().getString(R.string.device_authorization_times_are_capped);
            case 5400012:
                return context.getResources().getString(R.string.failed_due_to_internal_error_binding);
            case 5400013:
                return context.getResources().getString(R.string.binding_failed_because_of_repeated_binding);
            case 5400014:
                return context.getResources().getString(R.string.device_does_not_belong_to_the_user);
            case 5400015:
                return context.getResources().getString(R.string.there_is_no_such_instruction);
            case 5400016:
                return context.getResources().getString(R.string.device_cannot_log_in_again);
            case 5400017:
                return context.getResources().getString(R.string.devTid_cannot_be_empty);
            case 5400018:
                return context.getResources().getString(R.string.create_timed_reservation_times_to_upper_limit);
            case 5400019:
                return context.getResources().getString(R.string.authorized_instruction_has_expired);
            case 5400020:
                return context.getResources().getString(R.string.this_instruction_is_not_supported);
            case 5400021:
                return context.getResources().getString(R.string.illegal_mail_token);
            case 5400022:
                return context.getResources().getString(R.string.illegal_old_password);
            case 5400023:
                return context.getResources().getString(R.string.illegal_checksum_code);
            case 5400024:
                return context.getResources().getString(R.string.device_cannot_be_found_due_to_an_internal_error);
            case 5400025:
                return context.getResources().getString(R.string.pid_does_not_exist);
            case 5400026:
                return context.getResources().getString(R.string.there_is_no_authority_on_this_instruction);
            case 5400027:
                return context.getResources().getString(R.string.specified_template_does_not_exist);
            case 5400028:
                return context.getResources().getString(R.string.device_cannot_be_found_due_to_an_incorrect_internal_condition);
            case 5400035:
                return context.getResources().getString(R.string.the_specified_task_does_not_exist);
            case 5400036:
                return context.getResources().getString(R.string.unable_to_create_duplicate_template);
            case 5400037:
                return context.getResources().getString(R.string.deviceid_does_not_match);
            case 5400039:
                return context.getResources().getString(R.string.user_does_not_exist2);
            case 5400040:
                return context.getResources().getString(R.string.verify_that_code_expires);
            case 5400041:
                return context.getResources().getString(R.string.check_code_failed_to_send);
            case 5400042:
                return context.getResources().getString(R.string.verify_that_the_code_type_is_not_valid);
            case 5400043:
                return context.getResources().getString(R.string.device_cannot_bind_forcibly);
            case 5500000:
                return context.getResources().getString(R.string.internal_service_error);
            case 6400001:
                return context.getResources().getString(R.string.reverse_registration_request_for_the_specified_id_does_not_exist);
            case 6400002:
                return context.getResources().getString(R.string.Illegal_reverse_licensing_request);
            case 6400003:
                return context.getResources().getString(R.string.only_the_owner_can_authorize_the_equipment_to_other_people);
            case 6400004:
                return context.getResources().getString(R.string.the_device_specified_for_devTid_does_not_exist);
            case 6400005:
                return context.getResources().getString(R.string.upper_limit_on_the_number_of_devices_that_can_be_accommodated_by_a_folder);
            case 6400006:
                return context.getResources().getString(R.string.cannot_create_folder_with_the_same_name);
            case 6400007:
                return context.getResources().getString(R.string.folder_specified_for_id_does_not_exist);
            case 6400008:
                return context.getResources().getString(R.string.reached_the_maximum_number_of_folders_created);
            case 6400009:
                return context.getResources().getString(R.string.root_directory_cannot_be_deleted);
            case 6400010:
                return context.getResources().getString(R.string.root_directory_cannot_be_renamed);
            case 6400011:
                return context.getResources().getString(R.string.specified_rule_does_not_exist);
            case 6400012:
                return context.getResources().getString(R.string.specified_timed_reservation_task_does_not_exist);
            case 6400013:
                return context.getResources().getString(R.string.unable_to_create_the_same_rule);
            case 6400014:
                return context.getResources().getString(R.string.The_same_timed_reservation_cannot_be_created);
            case 6400015:
                return context.getResources().getString(R.string.illegal_prodPubKey);
            case 6400016:
                return context.getResources().getString(R.string.there_is_no_authority_to_do_so);
            case 6400017:
                return context.getResources().getString(R.string.request_parameter_error);
            case 6400018:
                return context.getResources().getString(R.string.specified_SkyDrive_file_does_not_exist);
            case 6400020:
                return context.getResources().getString(R.string.infrared_code_cannot_be_found);
            case 6400021:
                return context.getResources().getString(R.string.ir_service_request_error);
            case 6400022:
                return context.getResources().getString(R.string.cannot_find_instruction_set);
            case 6400023:
                return context.getResources().getString(R.string.parameter_not_supported);
            case 6400024:
                return context.getResources().getString(R.string.parsing_JSON_failed);
            case 6500001:
                return context.getResources().getString(R.string.failed_to_delete_SkyDrive_file);
            case 6500002:
                return context.getResources().getString(R.string.failed_to_upload_SkyDrive_file);
            case 6500003:
                return context.getResources().getString(R.string.HTTP_network_call_failed);
            case 8200000:
                return context.getResources().getString(R.string.success_e);
            case 8400000:
                return context.getResources().getString(R.string.product_does_not_exist);
            case 8400001:
                return context.getResources().getString(R.string.protocol_template_does_not_exist);
            case 8400002:
                return context.getResources().getString(R.string.illegal_parameter);
            case 8400003:
                return context.getResources().getString(R.string.platform_parameter_error);
            case 8400004:
                return context.getResources().getString(R.string.specifies_that_PID_does_not_exist);
            case 9200000:
                return context.getResources().getString(R.string.success_e2);
            case 9400000:
                return context.getResources().getString(R.string.error_e);
            case 9400001:
                return context.getResources().getString(R.string.illegal_parameter2);
            case 9400002:
                return context.getResources().getString(R.string.action_does_not_exist);
            case 9400003:
                return context.getResources().getString(R.string.product_does_not_exist2);
            case 9400004:
                return context.getResources().getString(R.string.timeout);
            case 9400005:
                return context.getResources().getString(R.string.method_does_not_support);
            case 9500000:
                return context.getResources().getString(R.string.service_error);
            case 9500001:
                return context.getResources().getString(R.string.service_response_error);
            case 0:
                return context.getResources().getString(R.string.network_timeout);
            case 1:
                return context.getResources().getString(R.string.logon_information_expired_log_in_again);
            case 2:
                return context.getResources().getString(R.string.unknown_error);
            case 400016:
                return context.getResources().getString(R.string.operation_is_too_frequent_try_again_later);
            case 400017:
                return context.getResources().getString(R.string.today_operation_has_reached_an_upper_limit);
            case 11001:
                return context.getResources().getString(R.string.info_format_error);
            case 11002:
                return context.getResources().getString(R.string.info_null_error);
            case 11003:
                return context.getResources().getString(R.string.app_id_empty);
            case 11004:
                return context.getResources().getString(R.string.no_params_parameter);
            case 11005:
                return context.getResources().getString(R.string.no_connect);
            case 12001:
                return context.getResources().getString(R.string.net_error);
            case 13001:
                return context.getResources().getString(R.string.local_net_connect_error);
            case 13002:
                return context.getResources().getString(R.string.local_net_auth_timeout);
            case 20001:
                return context.getResources().getString(R.string.no_find_device);
            case 30001:
                return context.getResources().getString(R.string.page_loading_error);
          /*  case 400014:
                return "密码重置失败";*/
            default:
                // return String.valueOf(errorCode);
                return context.getResources().getString(R.string.server_exception_try_again) + errorCode;
        }
    }


    public static void main(String args[]) {
//		test_convertUint8toByte();
//		test_convertChar2Uint8();
//		test_splitUint8To2bytes();
//		test_combine2bytesToOne();
//		test_parseBssid();
        String abc = "40404040404040404040404061616124";
       // System.out.print(""+getAsciiFromString(abc));
//        System.out.print(""+ CoderUtils.getStringFromAscii(abc));
//		int[] arrayData = {1,2,4,5,6,7,5,6,7,3,8,9,10,12,11,20,30,40};
//		Arrays.sort(arrayData);
//		for (int a : arrayData){
//			System.out.print("" + a + ";");
//		}


    }

}
