package me.hekr.sthome;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InvalidClassException;

import me.hekr.sdk.Constants;
import me.hekr.sdk.Hekr;
import me.hekr.sdk.inter.HekrCallback;
import me.hekr.sdk.utils.CacheUtil;
import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.equipment.ConfigActivity;
import me.hekr.sthome.equipment.EmergencyEditActivity;
import me.hekr.sthome.event.AlertEvent;
import me.hekr.sthome.event.AutoSyncCompleteEvent;
import me.hekr.sthome.event.AutoSyncEvent;
import me.hekr.sthome.event.LogoutEvent;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.http.bean.UserBean;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.tools.ECPreferenceSettings;
import me.hekr.sthome.tools.ECPreferences;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by TracyHenry on 2018/5/9.
 */

public class InitActivity extends AppCompatActivity {
private final static String TAG = "InitActivity";
private ImageView imageView1;
private boolean empty;
private boolean flag = false;
private static boolean flag_login_timeout = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        imageView1 = (ImageView)findViewById(R.id.imageView1);
        propetyAnim2(imageView1);
        boolean flag = getIntent().getBooleanExtra("login_flag",false);

        if(!flag){
            login();
        }else{
            EventBus.getDefault().post(new AutoSyncEvent());
        }


    }

    private String getUsername(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_USERNAME;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

    private String getPassword(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_PASSWORD;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return CoderUtils.getDecrypt(autoflag);
    }

    private void login(){

       LOG.I(TAG,"自动登录");
       handler.sendEmptyMessageDelayed(2,4000);
        Hekr.getHekrUser().login(getUsername(), getPassword(), new HekrCallback() {
            @Override
            public void onSuccess() {
                LOG.I(TAG,"自动登录成功");
                UserBean userBean = new UserBean(getUsername(), getPassword(), CacheUtil.getUserToken(), CacheUtil.getString(Constants.REFRESH_TOKEN,""));
                HekrUserAction.getInstance(InitActivity.this).setUserCache(userBean);
                if(!flag_login_timeout){
                    flag_login_timeout = true;
                EventBus.getDefault().post(new AutoSyncEvent());
                }


            }

            @Override
            public void onError(int errorCode, String message) {

                try {
                    JSONObject d = JSON.parseObject(message);
                    int code = d.getInteger("code");
                    //密码错误
                    if(code == 3400010){
                        HekrUserAction.getInstance(InitActivity.this).userLogout();
                        CCPAppManager.setClientUser(null);
                        startActivity(new Intent(InitActivity.this,LoginActivity.class));
                        finish();
                    }else {
                        if(!flag_login_timeout){
                            flag_login_timeout = true;
                        EventBus.getDefault().post(new AutoSyncEvent());
                    }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    LOG.I(TAG,"自动登录失败");
                    if(!flag_login_timeout){
                        flag_login_timeout = true;
                    EventBus.getDefault().post(new AutoSyncEvent());
                }
                }


            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    try {
                        empty  = (boolean)msg.obj;
                        String ds = CCPAppManager.getClientUser().getDescription();
                        if(TextUtils.isEmpty(ds)){
                            ECAlertDialog ecAlertDialog2 = ECAlertDialog.buildAlert(MyApplication.getActivity(), R.string.no_emergency_call, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(InitActivity.this, MainActivity.class);
                                    intent.putExtra("empty",empty);
                                    startActivity(intent);
                                    finish();

                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(InitActivity.this, EmergencyEditActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                            ecAlertDialog2.setCanceledOnTouchOutside(false);
                            ecAlertDialog2.show();
                        }else{
                            Intent intent = new Intent(InitActivity.this, MainActivity.class);
                            intent.putExtra("empty",empty);
                            startActivity(intent);
                            finish();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    if(!flag_login_timeout){
                        flag_login_timeout = true;
                        EventBus.getDefault().post(new AutoSyncEvent());
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)         //订阅事件AlertEvent
    public  void onEventMainThread(AutoSyncCompleteEvent event){
        Message message = Message.obtain();
        message.what = 1;
        message.obj = event.isFlag_devices_empty();
        handler.sendMessage(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)       //退出登录事件（refreshtoken过期）
    public  void onEventMainThread(final LogoutEvent event){

                HekrUserAction.getInstance(this).userLogout();
                CCPAppManager.setClientUser(null);
                ControllerWifi.getInstance().wifiTag = false;

                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();


    }

    /**
     * 半透明背景消失的动画
     * @param iv
     */
    public void propetyAnim2(final ImageView iv){
        PropertyValuesHolder valuesHolder4_alph = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofPropertyValuesHolder(iv, valuesHolder4_alph);
        objectAnimator4.setDuration(3000).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!flag){
            flag =true;
        }else{
            Intent intent = new Intent(InitActivity.this, MainActivity.class);
            intent.putExtra("empty",empty);
            startActivity(intent);
            finish();
        }

    }



}
