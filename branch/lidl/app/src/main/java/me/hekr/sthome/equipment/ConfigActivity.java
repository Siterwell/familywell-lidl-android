package me.hekr.sthome.equipment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.firebase.iid.FirebaseInstanceId;
import com.igexin.sdk.PushManager;

import me.hekr.sdk.Hekr;
import me.hekr.sdk.inter.HekrCallback;
import me.hekr.sthome.DeviceListActivity;
import me.hekr.sthome.R;
import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.SettingItem;
import me.hekr.sthome.configuration.activity.BeforeConfigEsptouchActivity;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.push.logger.Log;
import me.hekr.sthome.tools.UnitTools;
import me.hekr.sthome.updateApp.UpdateAppAuto;
import me.hekr.sthome.xmipc.ActivityGuideDeviceAdd;

/**
 * Created by xjj on 2016/12/20.
 */
public class ConfigActivity extends TopbarSuperActivity implements View.OnClickListener{
    private static final String TAG = "ConfigActivity";
    private SettingItem wifitag,modifypwd,aboutitem,emergencyitem,switch_lan,wificonfig,gps_set;
    private DeviceDAO DDO;
    private String lan_now;
    private UpdateAppAuto updateAppAuto;
    private static final int LOGOUT_SUCCESS = 1;

    @Override
    protected void onCreateInit() {
        initlan();
        setUpViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_config2;
    }

    private void initlan(){
        UnitTools tools = new UnitTools(this);
        lan_now = tools.readLanguage();
    }

    private void switchLan(){
        UnitTools tools = new UnitTools(this);
        String  lan_new = tools.readLanguage();
        if(!lan_new.equals(lan_now)){
            finish();
        }
    }

    public void setUpViews() {
        DDO = new DeviceDAO(this);
        findViewById(R.id.btnAE).setOnClickListener(this);//go out
        findViewById(R.id.configration).setOnClickListener(this);//hardare online
        wificonfig = (SettingItem)findViewById(R.id.wificonfig);
        wifitag = (SettingItem)findViewById(R.id.wifitag);
        modifypwd = (SettingItem)findViewById(R.id.modifypwd);
        aboutitem = (SettingItem)findViewById(R.id.about);
        updateAppAuto = new UpdateAppAuto(this,aboutitem,false);
        emergencyitem =(SettingItem)findViewById(R.id.emergency);
        switch_lan = (SettingItem)findViewById(R.id.switch_lan);
        gps_set =(SettingItem)findViewById(R.id.gps_setting);
        switch_lan.setOnClickListener(this);
        wifitag.setOnClickListener(this);
        aboutitem.setOnClickListener(this);
        emergencyitem.setOnClickListener(this);
        modifypwd.setOnClickListener(this);
        wificonfig.setOnClickListener(this);
        gps_set.setOnClickListener(this);
        findViewById(R.id.fap).setOnClickListener(this);
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.setting), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);
        refresh();
        updateAppAuto.getUpdateInfo();
    }


    private void refresh(){
        String name = "";
            try{
                MyDeviceBean d = DDO.findByChoice(1);
                if("报警器".equals(d.getDeviceName())){
                    name = getResources().getString(R.string.my_home);

                }else{
                    name = d.getDeviceName();
                }

                wifitag.setDetailText( name +"("+ d.getDevTid().substring(d.getDevTid().length()-4)+")");
            }
            catch (Exception e){
                wifitag.setDetailText(getResources().getString(R.string.please_choose_device));
            }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(ControllerWifi.getInstance().choose_gateway){
            ControllerWifi.getInstance().choose_gateway = false;
            finish();
        }
        refresh();
        switchLan();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnAE://logout;

                ECAlertDialog elc = ECAlertDialog.buildAlert(this,getResources().getString(R.string.sure_to_logout), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showProgressDialog(getResources().getString(R.string.logouting));

                        String fcmtoken = FirebaseInstanceId.getInstance().getToken();
                        if(TextUtils.isEmpty(fcmtoken)) {
                            handler.sendEmptyMessageDelayed(LOGOUT_SUCCESS, 1000);
                        }else{

                            HekrUserAction.getInstance(ConfigActivity.this).unPushTagBind(fcmtoken, 3, new HekrUser.UnPushTagBindListener() {
                                @Override
                                public void unPushTagBindSuccess() {
                                    handler.sendEmptyMessageDelayed(LOGOUT_SUCCESS, 1000);
                                }

                                @Override
                                public void unPushTagBindFail(int errorCode) {
                                    if(errorCode == 1){
                                        handler.sendEmptyMessage(LOGOUT_SUCCESS);
                                    }else{
                                        showToast(UnitTools.errorCode2Msg(ConfigActivity.this, errorCode));
                                        hideProgressDialog();
                                    }
                                }
                            });

                        }

                        String getui = PushManager.getInstance().getClientid(ConfigActivity.this);
                        if(!TextUtils.isEmpty(getui)){

                            HekrUserAction.getInstance(ConfigActivity.this).unPushTagBind(getui, 0, new HekrUser.UnPushTagBindListener() {
                                @Override
                                public void unPushTagBindSuccess() {
                                    Log.i(TAG,"解绑个推成功");
                                }

                                @Override
                                public void unPushTagBindFail(int errorCode) {
                                    Log.i(TAG,"解绑个推失败:"+errorCode);
                                }
                            });
                        }

                    }
                });
                elc.show();


                break;
            case R.id.configration:
                startActivity(new Intent(ConfigActivity.this,  BeforeConfigEsptouchActivity.class));
                break;
            case R.id.wifitag:
                Intent i = new Intent(ConfigActivity.this,DeviceListActivity.class);
                startActivity(i);
                break;
            case R.id.about:
                gotoAboutActivity();
                break;
            case R.id.emergency:
                startActivity(new Intent(ConfigActivity.this,EmergencyAcitivity.class));
                break;
            case R.id.modifypwd:
                startActivity(new Intent(ConfigActivity.this,ChangePasswordActivity.class));
                break;
            case R.id.switch_lan:
                startActivity(new Intent(ConfigActivity.this,SwitchLanActivity.class));
                break;
            case R.id.wificonfig:
                openCameraAlert();
                break;
            case R.id.gps_setting:
                startActivity(new Intent(ConfigActivity.this,SettingGpsEnableActivity.class));
                break;
            case R.id.fap:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://safewith.me/base/");
                intent.setData(content_url);
                startActivity(intent);
                break;
            default:
                break;
        }
    }



    private void openCameraAlert(){
        Intent intent2 = new Intent(ConfigActivity.this,ActivityGuideDeviceAdd.class);
        startActivity(intent2);
    }

   private void gotoAboutActivity(){
      startActivity(new Intent(ConfigActivity.this,AboutActivity.class));

   }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGOUT_SUCCESS:


                    Hekr.getHekrUser().logout(new HekrCallback() {
                        @Override
                        public void onSuccess() {
                            hideProgressDialog();
                            HekrUserAction.getInstance(ConfigActivity.this).userLogout();
                            CCPAppManager.setClientUser(null);
                            finish();
                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            hideProgressDialog();

                            if(errorCode==1){
                                HekrUserAction.getInstance(ConfigActivity.this).userLogout();

                                CCPAppManager.setClientUser(null);
                                finish();
                            }else {
                                try {
                                    JSONObject d = JSON.parseObject(message);
                                    int code = d.getInteger("code");
                                    Toast.makeText(ConfigActivity.this,UnitTools.errorCode2Msg(ConfigActivity.this,code),Toast.LENGTH_LONG).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(ConfigActivity.this,UnitTools.errorCode2Msg(ConfigActivity.this,errorCode),Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    });


                    break;
            }

        }

    };


}
