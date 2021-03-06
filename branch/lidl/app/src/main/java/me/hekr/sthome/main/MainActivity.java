package me.hekr.sthome.main;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.firebase.iid.FirebaseInstanceId;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.litesuits.android.log.Log;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import me.hekr.sdk.Constants;
import me.hekr.sdk.Hekr;
import me.hekr.sdk.HekrSDK;
import me.hekr.sdk.inter.HekrCallback;
import me.hekr.sdk.inter.HekrMsgCallback;
import me.hekr.sdk.utils.CacheUtil;
import me.hekr.sthome.LoginActivity;
import me.hekr.sthome.MyApplication;
import me.hekr.sthome.R;
import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.commonBaseView.CustomViewPager;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ProgressDialog;
import me.hekr.sthome.configuration.activity.BeforeConfigEsptouchActivity;
import me.hekr.sthome.equipment.EmergencyEditActivity;
import me.hekr.sthome.event.LogoutEvent;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.event.TokenTimeoutEvent;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.http.SiterConstantsUtil;
import me.hekr.sthome.http.bean.DeviceBean;
import me.hekr.sthome.http.bean.FirmwareBean;
import me.hekr.sthome.http.bean.UserBean;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.service.SiterService;
import me.hekr.sthome.tools.Config;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.ECPreferenceSettings;
import me.hekr.sthome.tools.ECPreferences;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SystemTintManager;
import me.hekr.sthome.tools.SystemUtil;
import me.hekr.sthome.tools.UnitTools;
import me.hekr.sthome.updateApp.UpdateAppAuto;

/**
 * Created by xjj on 2016/12/12.
 */
public class MainActivity extends AppCompatActivity implements DeviceFragment.SetPagerView{
    private static final String TAG = "MainActivity";
    private CustomViewPager viewPager;// 页卡内容
    private List<Fragment> fragments;// Tab页面列表
    private RadioGroup bottomRg;
    private RadioButton radio;
    private RadioButton radio1;
    private RadioButton radio2;
    private RadioButton radio3;
    private int currIndex=0;
    private SystemTintManager tintManager;
    private ProgressDialog mProgressDialog;
    private int changeIndex;
    private SysmodelDAO sysmodelDAO;
    private UpdateAppAuto updateAppAuto;
    private FirmwareBean file;
    private ECAlertDialog ecAlertDialog;
    public static boolean flag_checkfireware;
    protected void onCreate(Bundle savedInstanceState) {
        initCurrentGateway();
        ConnectionPojo.getInstance().open_app = 1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);
        EventBus.getDefault().register(this);
        updateAppAuto = new UpdateAppAuto(this);
        initView();
        if(getIntent().getBooleanExtra("empty",false)){
            Intent tent = new Intent(this, BeforeConfigEsptouchActivity.class);
            tent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(tent);
        }else{
            updateAppAuto.initCheckUpate();
            checkUpdatefirm();
        }
    }

    private void initView() {
        InitViewPager();
        initialView();
        initSystemBar();
        initGTService();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initGTService() {


        /**
         * tag setting
         */
        if (!TextUtils.isEmpty(HekrSDK.getPid())) {
            String[] tags = new String[]{HekrSDK.getPid()};
            Tag[] tagParam = new Tag[tags.length];
            for (int i = 0; i < tags.length; i++) {
                Tag t = new Tag();
                t.setName(tags[i]);
                tagParam[i] = t;
            }
            PushManager.getInstance().setTag(this, tagParam, "100861");
        }
        /**
         * alias setting
         */
        if(!TextUtils.isEmpty(CCPAppManager.getUserId())){
            boolean t = PushManager.getInstance().bindAlias(this, CCPAppManager.getUserId());
            String abc = t? "设置成功" : "设置失败";
            Log.i(TAG,abc+"set alias uid =" + CCPAppManager.getUserId());
        }



            if( "honor".equals(SystemUtil.getDeviceBrand().toLowerCase()) || "huawei".equals(SystemUtil.getDeviceBrand().toLowerCase())){

                com.huawei.android.pushagent.api.PushManager.requestToken(this);
            }
            else if("xiaomi".equals(SystemUtil.getDeviceBrand().toLowerCase())){
                String ds = MiPushClient.getRegId(this);
                Log.i(TAG,"小米平台CLIENTID："+ds);
                if(!TextUtils.isEmpty(ds)) {
                    STEvent stEvent = new STEvent();
                    stEvent.setRefreshevent(13);
                    stEvent.setFcm_token(ds);
                    EventBus.getDefault().post(stEvent);
                }else{
                    Log.i(TAG, "小米平台CLIENTID为空");
                }
            }
            else{

                String fcmclientid = FirebaseInstanceId.getInstance().getToken();
                if(!TextUtils.isEmpty(fcmclientid)){
                    Log.i(TAG,"FCM平台CLIENTID："+fcmclientid);
                    SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
                    ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_DOMAIN;
                    String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
//                    if("hekr.me".equals(autoflag)){
//                        String cid = PushManager.getInstance().getClientid(this);
//                        if(!TextUtils.isEmpty(cid)) {
//                            Log.i(TAG, "个推client id =" + cid);
//                            STEvent stEvent = new STEvent();
//                            stEvent.setRefreshevent(11);
//                            stEvent.setFcm_token(cid);
//                            EventBus.getDefault().post(stEvent);
//
//                            HekrUserAction.getInstance(this).unPushTagBind(fcmclientid, 3, new HekrUser.UnPushTagBindListener() {
//                                @Override
//                                public void unPushTagBindSuccess() {
//                                    Log.i(TAG,"个推绑定的同时解绑FCM成功");
//                                }
//
//                                @Override
//                                public void unPushTagBindFail(int errorCode) {
//                                    Log.i(TAG,"个推绑定的同时解绑FCM失败");
//                                }
//                            });
//
//                        }else{
//                            Log.i(TAG, "个推client id为空");
//                        }
//                    }else{

                    STEvent stEvent = new STEvent();
                    stEvent.setRefreshevent(9);
                    stEvent.setFcm_token(fcmclientid);
                    EventBus.getDefault().post(stEvent);

                        HekrUserAction.getInstance(this).unPushTagBind(PushManager.getInstance().getClientid(this), 0, new HekrUser.UnPushTagBindListener() {
                            @Override
                            public void unPushTagBindSuccess() {
                                Log.i(TAG,"FCM绑定的同时解绑个推成功");
                            }

                            @Override
                            public void unPushTagBindFail(int errorCode) {
                                Log.i(TAG,"FCM绑定的同时解绑个推失败");
                            }
                        });

//                    }


                }else{
                    String cid = PushManager.getInstance().getClientid(this);
                    if(!TextUtils.isEmpty(cid)) {
                        Log.i(TAG, "个推client id =" + cid);
                        STEvent stEvent = new STEvent();
                        stEvent.setRefreshevent(11);
                        stEvent.setFcm_token(cid);
                        EventBus.getDefault().post(stEvent);
                    }else{
                        Log.i(TAG, "个推client id为空");
                    }
                }
            }




    }

    /**
     * 初始化Viewpager页
     */
    private void InitViewPager() {
        sysmodelDAO = new SysmodelDAO(this);
        viewPager = (CustomViewPager) findViewById(R.id.vPager);
        fragments = new ArrayList<Fragment>();
        fragments.add(new HomeFragment());
        SceneFragment sceneFragment = new SceneFragment();
        fragments.add(sceneFragment);
        DeviceFragment deviceFragment = new DeviceFragment();
        deviceFragment.setInterfacePagerView(this);
        fragments.add(deviceFragment);
        viewPager.setAdapter(new myPagerAdapter(getSupportFragmentManager(),
                fragments));
        viewPager.setCurrentItem(ConnectionPojo.getInstance().index_home);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }


    private void initialView(){
        bottomRg = (RadioGroup)findViewById(R.id.bottomRg);
        radio1=(RadioButton)this.findViewById(R.id.radioButton1);
        radio2=(RadioButton)this.findViewById(R.id.radioButton2);
        radio3=(RadioButton)this.findViewById(R.id.radioButton3);
        radio1.setOnClickListener(l);
        radio2.setOnClickListener(l);
        radio3.setOnClickListener(l);
        switch (ConnectionPojo.getInstance().index_home){
            case 0:
                radio = radio1;
                radio.setTextColor(getResources().getColor(R.color.text_color_selected));
                setRadioImage(R.mipmap.main2);
                break;
            case 1:
                radio = radio2;
                radio.setTextColor(getResources().getColor(R.color.text_color_selected));
                setRadioImage(R.mipmap.scene2);
                break;
            case 2:
                radio =  radio3;
                radio.setTextColor(getResources().getColor(R.color.text_color_selected));
                setRadioImage(R.mipmap.device2);
                break;
        }

    }



    private void setRadioImage(int id){
        Drawable myImage=getResources().getDrawable(id);
        radio.setCompoundDrawablesWithIntrinsicBounds(null, myImage, null, null);
    }

    private void resetRadioButton(RadioButton button){
        button.setTextColor(getResources().getColor(R.color.text_color));
        switch(button.getId()){
            case R.id.radioButton1:
                setRadioImage(R.mipmap.main1);
                break;
            case R.id.radioButton2:
                setRadioImage(R.mipmap.scene1);
                break;
            case R.id.radioButton3:
                setRadioImage(R.mipmap.device1);
                break;
        }
    }

    private View.OnClickListener l =new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
           // if(!ControllerSyncScene.getInstance().sync_server){
                if(radio==null||(radio!=null&&radio.getId()!=arg0.getId())){

                    switch(arg0.getId()){
                        case R.id.radioButton1:
                            if(!((DeviceFragment)fragments.get(2)).getTouchon()){

                                if(radio!=null){
                                    resetRadioButton(radio);
                                }
                                radio=(RadioButton)arg0.findViewById(arg0.getId());
                                radio.setTextColor(getResources().getColor(R.color.text_color_selected));

                                setRadioImage(R.mipmap.main2);
                                viewPager.setCurrentItem(0,false);
                            }


                            break;
                        case R.id.radioButton2:
                            if(!((DeviceFragment)fragments.get(2)).getTouchon()){

                                if(radio!=null){
                                    resetRadioButton(radio);
                                }
                                radio=(RadioButton)arg0.findViewById(arg0.getId());
                                radio.setTextColor(getResources().getColor(R.color.text_color_selected));
                                setRadioImage(R.mipmap.scene2);
                                viewPager.setCurrentItem(1,false);
                            }



                            break;
                        case R.id.radioButton3:
                            if(radio!=null){
                                resetRadioButton(radio);
                            }
                            radio=(RadioButton)arg0.findViewById(arg0.getId());
                            radio.setTextColor(getResources().getColor(R.color.text_color_selected));
                            setRadioImage(R.mipmap.device2);
                            viewPager.setCurrentItem(2,false);
                            break;
                    }

                }
        //    }

        }
    };


    private void changeRadio(int index){
        if(radio!=null){
            resetRadioButton(radio);
            switch(index){
                case 0:
                    radio=(RadioButton)bottomRg.findViewById(R.id.radioButton1);
                    setRadioImage(R.mipmap.main2);
                    break;
                case 1:
                    radio=(RadioButton)bottomRg.findViewById(R.id.radioButton2);
                    setRadioImage(R.mipmap.scene2);
                    break;
                case 2:
                    radio=(RadioButton)bottomRg.findViewById(R.id.radioButton3);
                    setRadioImage(R.mipmap.device2);
                    break;
            }
            radio.setTextColor(getResources().getColor(R.color.text_color_selected));
        }
    }

    @Override
    public void setdrag(boolean flag) {
        if(viewPager!=null) viewPager.setScanScroll(flag);
    }


    /**
     * 为选项卡绑定监听器
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {




        public void onPageScrollStateChanged(int index) {
            try {
                changeIndex = index;
                Log.i(TAG,"onPageScrollStateChanged:"+index);
                if(index==0){
                    switch (currIndex){
                        case 0:
                            tintManager.setStatusBarDarkMode2(false, MainActivity.this);
                            break;
                        case 1:
                            tintManager.setStatusBarDarkMode2(true, MainActivity.this);
                            if(ConnectionPojo.getInstance().deviceTid != null){
                                STEvent stEvent =new STEvent();
                                stEvent.setServiceevent(1);
                                EventBus.getDefault().post(stEvent);
                                if((fragments.get(1)) != null){
                                    ((SceneFragment) fragments.get(1)).refresh();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.connect_equipment_alert),Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 2:
                            tintManager.setStatusBarDarkMode2(true, MainActivity.this);
                            if(ConnectionPojo.getInstance().deviceTid != null){
                                if (fragments.get(2) != null) {
                                    STEvent stEvent =new STEvent();
                                    stEvent.setServiceevent(3);
                                    EventBus.getDefault().post(stEvent);
                                    ((DeviceFragment) fragments.get(2)).init();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.connect_equipment_alert),Toast.LENGTH_SHORT).show();
                            }
                            break;

                    }

                }
            }catch (NullPointerException e){
               Log.i(TAG,"tintManager is null");
            }

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int index) {
            currIndex = index;
            changeRadio(currIndex);
            ConnectionPojo.getInstance().index_home = index;


            switch (currIndex){
                case 0:
                    if(changeIndex == 0){
                        tintManager.setStatusBarDarkMode2(false, MainActivity.this);
                    }
                    break;
                case 1:
                    if(changeIndex == 0) {
                        tintManager.setStatusBarDarkMode2(true, MainActivity.this);
                        if (index == 1 && ConnectionPojo.getInstance().deviceTid != null) {
                            STEvent stEvent =new STEvent();
                            stEvent.setServiceevent(1);
                            EventBus.getDefault().post(stEvent);
                            if((fragments.get(1)) != null){
                                ((SceneFragment) fragments.get(1)).refresh();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.connect_equipment_alert), Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case 2:
                    if(changeIndex == 0) {
                        tintManager.setStatusBarDarkMode2(true, MainActivity.this);
                        if (ConnectionPojo.getInstance().deviceTid != null) {
                            if (index == 2 && (fragments.get(2)) != null) {
                                STEvent stEvent =new STEvent();
                                stEvent.setServiceevent(3);
                                EventBus.getDefault().post(stEvent);
                                ((DeviceFragment) fragments.get(2)).init();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.connect_equipment_alert), Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }



    /**
     * 定义适配器
     */
    class myPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public myPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        /**
         * 得到每个页面
         */
        @Override
        public Fragment getItem(int arg0) {
            return (fragmentList == null || fragmentList.size() == 0) ? null
                    : fragmentList.get(arg0);
        }

        /**
         * 每个页面的title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        /**
         * 页面的总个数
         */
        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private long exitTime = 0;

    private void goBack(){

        if(((HomeFragment)fragments.get(0)).isAlarmListOpened()){
            ((HomeFragment)fragments.get(0)).closeTheAlarmList();
        }else{
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                String ds = String.format(getResources().getString(R.string.exit_hint), Config.getAppName(MainActivity.this));
                Toast.makeText(getApplicationContext(),ds,
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                this.finish();
            }
        }


    }




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
       Log.i(TAG,"onNewIntent");

        try {
            int current = intent.getIntExtra("current_dev",0);
            if(current==1){
                viewPager.setCurrentItem(0,false);
                ((HomeFragment) fragments.get(0)).openTheAlarmList();
            }
            UnitTools.stopMusic(MainActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(MainActivity.flag_checkfireware == true){
                MainActivity.flag_checkfireware = false;
                checkUpdatefirm();
            }
        }catch (Exception e){
            Log.i(TAG,"已退出了");
        }
        if(TextUtils.isEmpty(HekrUserAction.getInstance(this).getJWT_TOKEN())){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }



    public void initSystemBar(){
        tintManager = new SystemTintManager(this);// 创建状态栏的管理实例
        tintManager.setStatusBarDarkMode2(currIndex==0?false:true, this);//false 状态栏字体颜色是白色 true 颜色是黑色
    }
    @Subscribe(threadMode = ThreadMode.MAIN)       //退出登录事件（refreshtoken过期）
    public  void onEventMainThread(final LogoutEvent event){

        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        final String loginname = getUsername();
        final String loginpsw = getPassword();

        Hekr.getHekrUser().login(loginname, loginpsw, new HekrCallback() {
            @Override
            public void onSuccess() {
                UserBean userBean = new UserBean(loginname, loginpsw, CacheUtil.getUserToken(), CacheUtil.getString(Constants.REFRESH_TOKEN,""));
                HekrUserAction.getInstance(MainActivity.this).setUserCache(userBean);
            }

            @Override
            public void onError(int errorCode, String message) {
                HekrUserAction.getInstance(MainActivity.this).userLogout();
                CCPAppManager.setClientUser(null);
                ControllerWifi.getInstance().wifiTag = false;

                try {
                    ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_HUAWEI_TOKEN, "", true);
                } catch (InvalidClassException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    @Subscribe(threadMode = ThreadMode.MAIN)         //订阅事件FirstEvent
    public  void onEventMainThread(final STEvent event){
        if(event.getRefreshevent()==2){

            String d = event.getProgressText();

            if(mProgressDialog==null || !mProgressDialog.isShowing()){
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setPressText(d);
                mProgressDialog.show();
            }else if(mProgressDialog.isShowing()){
                mProgressDialog.setPressText(d);
            }
        }else if(event.getRefreshevent()==3){
            if(mProgressDialog!=null && mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
            if(currIndex==0){
                ((HomeFragment)fragments.get(0)).refreshSysmode();

            }else if(currIndex == 1){
                ((SceneFragment)fragments.get(1)).refresh();
                ((HomeFragment)fragments.get(0)).refreshSysmode();
            }else if(currIndex == 2){
                ((DeviceFragment)fragments.get(2)).refresh();
            }

        }else if(event.getRefreshevent()==5 && currIndex == 2){
            if(!SiterService.isTimer_of_sync_en())
            ((DeviceFragment)fragments.get(2)).refresh();
        }else if(event.getRefreshevent()==6){
            if(mProgressDialog!=null && mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
            if(currIndex==0){
                ((HomeFragment)fragments.get(0)).refreshSysmode();

            }
        }else if(event.getRefreshevent()==8){
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else if(event.getRefreshevent() == 9){
                    String token = event.getFcm_token();
                    if(!TextUtils.isEmpty(token)){

                        HekrUserAction.getInstance(MainActivity.this).pushTagBind(token, 3, new HekrUser.PushTagBindListener() {
                            @Override
                            public void pushTagBindSuccess() {
                                android.util.Log.i(TAG,"FCM绑定成功getSuccess:");
                            }

                            @Override
                            public void pushTagBindFail(int errorCode) {
                                android.util.Log.i(TAG,"FCM绑定失败getFail:"+errorCode);
                                if(errorCode==1){
                                    LogoutEvent tokenTimeoutEvent = new LogoutEvent();
                                    EventBus.getDefault().post(tokenTimeoutEvent);
                                }
                            }
                        });

                    }

        }else if(event.getRefreshevent() == 11){
            if(!"xiaomi".equals(SystemUtil.getDeviceBrand().toLowerCase())){

                        String token = event.getFcm_token();
                        if(!TextUtils.isEmpty(token)){

                            HekrUserAction.getInstance(MainActivity.this).pushTagBind(token, 0, new HekrUser.PushTagBindListener() {
                                @Override
                                public void pushTagBindSuccess() {
                                    android.util.Log.i(TAG,"个推绑定成功getSuccess:");
                                }

                                @Override
                                public void pushTagBindFail(int errorCode) {
                                    android.util.Log.i(TAG,"个推绑定失败getFail:"+errorCode);
                                    if(errorCode==1){
                                        LogoutEvent tokenTimeoutEvent = new LogoutEvent();
                                        EventBus.getDefault().post(tokenTimeoutEvent);
                                    }
                                }
                            });
                        }
            }

        }else if(event.getRefreshevent() == 12){
                    String token = event.getFcm_token();
                    if(!TextUtils.isEmpty(token)){

                        HekrUserAction.getInstance(MainActivity.this).pushTagBind(token, 2, new HekrUser.PushTagBindListener() {
                            @Override
                            public void pushTagBindSuccess() {
                                android.util.Log.i(TAG,"HUAWEI绑定成功getSuccess:");
                            }

                            @Override
                            public void pushTagBindFail(int errorCode) {
                                android.util.Log.i(TAG,"HUAWEI绑定失败getFail:"+errorCode);
                                if(errorCode==1){
                                    LogoutEvent tokenTimeoutEvent = new LogoutEvent();
                                    EventBus.getDefault().post(tokenTimeoutEvent);
                                }
                            }
                        });

                    }
        }else if(event.getRefreshevent()==13) {
            String token = event.getFcm_token();
            if (!TextUtils.isEmpty(token)) {


                HekrUserAction.getInstance(MainActivity.this).pushTagBind(token, 1, new HekrUser.PushTagBindListener() {
                    @Override
                    public void pushTagBindSuccess() {
                        android.util.Log.i(TAG, "小米绑定成功getSuccess:");
                    }

                    @Override
                    public void pushTagBindFail(int errorCode) {
                        android.util.Log.i(TAG, "小米绑定失败getFail:" + errorCode);
                        if (errorCode == 1) {
                            LogoutEvent tokenTimeoutEvent = new LogoutEvent();
                            EventBus.getDefault().post(tokenTimeoutEvent);
                        }
                    }
                });
            }
        }
        try {
            if(event.getEvent()== SendCommand.CHOOSE_SCENE_GROUP){

                if(((SceneFragment)fragments.get(1)).getHandleSceneGroupSid()!=-1){
                    sysmodelDAO.updateChoice(String.valueOf(((SceneFragment)fragments.get(1)).getHandleSceneGroupSid()), ConnectionPojo.getInstance().deviceTid);
                    ((SceneFragment)fragments.get(1)).setHandleSceneGroupSid(-1);
                }else if(((HomeFragment)fragments.get(0)).getNowmodeindex()!=-1){
                    sysmodelDAO.updateChoice(String.valueOf(((HomeFragment)fragments.get(0)).getNowmodeindex()), ConnectionPojo.getInstance().deviceTid);
                }

                ((HomeFragment)fragments.get(0)).refreshSysmode();
                ((SceneFragment)fragments.get(1)).refresh();
                SendCommand.clearCommnad();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
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
        return autoflag;
    }

    private void initCurrentGateway(){

        try{
            DeviceDAO deviceDAO = new DeviceDAO(this);
            SysmodelDAO sysmodelDAO = new SysmodelDAO(this);
            SceneDAO sceneDAO = new SceneDAO(this);
            MyDeviceBean myDeviceBean = deviceDAO.findByChoice(1);
            if(myDeviceBean!=null){
                ConnectionPojo.getInstance().bind = myDeviceBean.getBindKey();
                ConnectionPojo.getInstance().deviceTid = myDeviceBean.getDevTid();
                ConnectionPojo.getInstance().ctrlKey = myDeviceBean.getCtrlKey();
                ConnectionPojo.getInstance().IMEI =  Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                ConnectionPojo.getInstance().propubkey = myDeviceBean.getProductPublicKey();
                ConnectionPojo.getInstance().domain = myDeviceBean.getDcInfo().getConnectHost();
                ConnectionPojo.getInstance().binversion = myDeviceBean.getBinVersion();
                SysModelBean sysModelBean1 = new SysModelBean();
                sysModelBean1.setChice("N");
                sysModelBean1.setSid("0");
                sysModelBean1.setModleName("在家");
                sysModelBean1.setDeviceid(myDeviceBean.getDevTid());
                sysModelBean1.setColor("F0");

                SysModelBean sysModelBean2 = new SysModelBean();
                sysModelBean2.setChice("N");
                sysModelBean2.setSid("1");
                sysModelBean2.setModleName("离家");
                sysModelBean2.setDeviceid(myDeviceBean.getDevTid());
                sysModelBean2.setColor("F1");

                SysModelBean sysModelBean3 = new SysModelBean();
                sysModelBean3.setChice("N");
                sysModelBean3.setSid("2");
                sysModelBean3.setModleName("睡眠");
                sysModelBean3.setDeviceid(myDeviceBean.getDevTid());
                sysModelBean3.setColor("F2");

                sysmodelDAO.addinit(sysModelBean1);
                sysmodelDAO.addinit(sysModelBean2);
                sysmodelDAO.addinit(sysModelBean3);


                SceneBean sceneBean = new SceneBean();
                sceneBean.setDeviceid(myDeviceBean.getDevTid());
                sceneBean.setSid("-1");
                sceneBean.setMid("129");
                sceneBean.setCode("");
                sceneBean.setName("");
                sceneDAO.addinit(sceneBean);

                SceneBean sceneBean2 = new SceneBean();
                sceneBean2.setDeviceid(myDeviceBean.getDevTid());
                sceneBean2.setSid("-1");
                sceneBean2.setMid("130");
                sceneBean2.setCode("");
                sceneBean2.setName("");
                sceneDAO.addinit(sceneBean2);

                SceneBean sceneBean3 = new SceneBean();
                sceneBean3.setDeviceid(myDeviceBean.getDevTid());
                sceneBean3.setSid("-1");
                sceneBean3.setMid("131");
                sceneBean3.setCode("");
                sceneBean3.setName("");
                sceneDAO.addinit(sceneBean3);

                SceneBean sceneBean4 = new SceneBean();
                sceneBean4.setDeviceid(myDeviceBean.getDevTid());
                sceneBean4.setSid("1");
                sceneBean4.setMid("129");
                sceneBean4.setCode("");
                sceneBean4.setName("");
                sceneDAO.addinit(sceneBean4);

                SceneBean sceneBean5 = new SceneBean();
                sceneBean5.setDeviceid(myDeviceBean.getDevTid());
                sceneBean5.setSid("1");
                sceneBean5.setMid("130");
                sceneBean5.setCode("");
                sceneBean5.setName("");
                sceneDAO.addinit(sceneBean5);

                SceneBean sceneBean6 = new SceneBean();
                sceneBean6.setDeviceid(myDeviceBean.getDevTid());
                sceneBean6.setSid("2");
                sceneBean6.setMid("130");
                sceneBean6.setCode("");
                sceneBean6.setName("");
                sceneDAO.addinit(sceneBean6);

                android.util.Log.i(TAG,"ConnectionPojo.getInstance().bind:"+ ConnectionPojo.getInstance().bind);
                android.util.Log.i(TAG,"ConnectionPojo.getInstance().deviceTid:"+ ConnectionPojo.getInstance().deviceTid);
                android.util.Log.i(TAG,"ConnectionPojo.getInstance().ctrlKey:"+ ConnectionPojo.getInstance().ctrlKey);
                android.util.Log.i(TAG,"ConnectionPojo.getInstance().propubkey:"+ ConnectionPojo.getInstance().propubkey);
                android.util.Log.i(TAG,"ConnectionPojo.getInstance().domain:"+ ConnectionPojo.getInstance().domain);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    private void doActionSend() {
        DeviceDAO deviceDAO = new DeviceDAO(this);
        DeviceBean d = deviceDAO.findByChoice(1);
        if(d!=null){
            String abc = "{\"msgId\":16810,\"action\":\"devUpgrade\"," +
                    "\"params\":{\"appTid\":\""+ ConnectionPojo.getInstance().IMEI+"\"," +
                    "\"devTid\":\""+d.getDevTid()+"\"," +
                    "\"ctrlKey\":\""+d.getCtrlKey()+"\"," +
                    "\"binUrl\":\""+file.getBinUrl()+"\"," +
                    "\"md5\":\""+file.getMd5()+"\"," +
                    "\"binType\":\""+file.getLatestBinType()+"\"," +
                    "\"binVer\":\""+file.getLatestBinVer()+"\"," +
                    "\"size\":"+file.getSize()+"}}";
            try {
                Hekr.getHekrClient().sendMessage(new org.json.JSONObject(abc), new HekrMsgCallback() {
                    @Override
                    public void onReceived(String msg) {

                    }

                    @Override
                    public void onTimeout() {

                    }

                    @Override
                    public void onError(int errorCode, String message) {

                    }
                }, ConnectionPojo.getInstance().domain);



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void checkUpdatefirm(){
        DeviceDAO deviceDAO = new DeviceDAO(this);
        DeviceBean d = deviceDAO.findByChoice(1);
        if(d!=null ){
            HekrUserAction.getInstance(this).checkFirmwareUpdate(d.getDevTid(),d.getProductPublicKey(), d.getBinType(), d.getBinVersion(), new HekrUser.CheckFwUpdateListener() {
                @Override
                public void checkNotNeedUpdate() {
                }

                @Override
                public void checkNeedUpdate(FirmwareBean firmwareBean) {
                    file = firmwareBean;
                    if(ecAlertDialog==null||!ecAlertDialog.isShowing()){
                        ecAlertDialog = ECAlertDialog.buildAlert(MyApplication.getActivity(),
                                String.format(getResources().getString(R.string.firewarm_to_update),file.getLatestBinVer()),
                                getResources().getString(R.string.now_not_to_update),
                                getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        doActionSend();
                                        Toast.makeText(MainActivity.this,getResources().getString(R.string.fireware_is_updating),Toast.LENGTH_LONG).show();
                                    }

                                });

                        ecAlertDialog.show();
                    }
                }

                @Override
                public void checkFail(int errorCode) {
                    if(errorCode==1){
                        LogoutEvent logoutEvent = new LogoutEvent();
                        EventBus.getDefault().post(logoutEvent);
                    }
                }
            });
        }
    }

}
