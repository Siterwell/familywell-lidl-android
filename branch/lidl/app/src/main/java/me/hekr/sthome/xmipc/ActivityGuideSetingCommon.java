package me.hekr.sthome.xmipc;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.CameraParam;
import com.lib.funsdk.support.config.OPTimeQuery;
import com.lib.funsdk.support.config.OPTimeSetting;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunLoginType;
import com.lib.funsdk.support.utils.DeviceConfigType;
import com.lib.funsdk.support.widget.TimeTextView;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.model.modelbean.ClientUser;
import me.hekr.sthome.model.modelbean.MonitorBean;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by Administrator on 2017/8/30.
 */

public class ActivityGuideSetingCommon extends TopbarIpcSuperActivity implements View.OnClickListener,OnFunDeviceOptListener {
   private final static String TAG = ActivityGuideSetingCommon.class.getName();
    private TextView textView_name;
    private ImageButton imageButton_flip,imageButton_mirror;
    private TimeTextView timeTextView;
    private RelativeLayout updatename_liner,updatetime_liner;
    private int id;
    private String devname;
    private FunDevice mFunDevice;
    private ECAlertDialog alertDialog;
    private String devid;
    private List<MonitorBean> lists;
    private List<MonitorBean> list;
    private com.alibaba.fastjson.JSONObject object;
    /**
     * 本界面需要获取到的设备配置信息（监控类）
     */
    private final String[] DEV_CONFIGS_FOR_CAMERA = {

            // 获取参数:CameraParam -> 图像上下翻转/图像左右翻转/背光补偿/降噪
            CameraParam.CONFIG_NAME};

    @Override
    protected void onCreateInit() {
        initview();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_setting_common;
    }


    private void initview(){
        lists = CCPAppManager.getClientUser().getMonitorList();
        id = getIntent().getIntExtra("FUN_DEVICE_ID", 0);


        mFunDevice = FunSupport.getInstance().findDeviceById(id);
        if ( null == mFunDevice ) {
            finish();
            return;
        }
        devid = getIntent().getStringExtra("FUN_DEVICE_SN");
        for (int i = 0; i< CCPAppManager.getClientUser().getMonitorList().size(); i++){
            if(devid.equals(CCPAppManager.getClientUser().getMonitorList().get(i).getDevid())){
                devname = CCPAppManager.getClientUser().getMonitorList().get(i).getName();
                break;
            }
        }

        textView_title.setText(getResources().getString(R.string.common_setting));
        updatename_liner = (RelativeLayout)findViewById(R.id.updatename);
        updatetime_liner = (RelativeLayout)findViewById(R.id.updatetime);
        textView_name = (TextView)findViewById(R.id.name);
        timeTextView  =  (TimeTextView)findViewById(R.id.time);
        imageButton_flip = (ImageButton)findViewById(R.id.btnSwitchCameraFlip);
        imageButton_mirror = (ImageButton)findViewById(R.id.btnSwitchCameraMirror);
        imageButton_setting.setVisibility(View.GONE);
        textView_name.setText(devname);
        textView_back.setOnClickListener(this);
        updatename_liner.setOnClickListener(this);
        updatetime_liner.setOnClickListener(this);
        imageButton_flip.setOnClickListener(this);
        imageButton_mirror.setOnClickListener(this);

        // 注册设备操作监听
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);
        requestTimeInfo();
        // 获取报警配置信息
        tryGetCameraConfig();
    }


    private void tryGetCameraConfig() {
        if (null != mFunDevice) {

            showWaitDialog();

            for (String configName : DEV_CONFIGS_FOR_CAMERA) {

                // 删除老的配置信息
                mFunDevice.invalidConfig(configName);

                //根据是否需要传通道号 重新搜索新的配置信息
                if (contains(DeviceConfigType.DeviceConfigCommon, configName)) {
                    FunSupport.getInstance().requestDeviceConfig(mFunDevice,
                            configName);
                }else if (contains(DeviceConfigType.DeviceConfigByChannel, configName)) {
                    FunSupport.getInstance().requestDeviceConfig(mFunDevice, configName, mFunDevice.CurrChannel);
                }

            }
        }
    }

    /**
     * 判断是否所有需要的配置都获取到了
     *
     * @return
     */
    private boolean isAllConfigGetted() {
        for (String configName : DEV_CONFIGS_FOR_CAMERA) {
            if (null == mFunDevice.getConfig(configName)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCurrentUsefulConfig(String configName) {
        for (int i = 0; i < DEV_CONFIGS_FOR_CAMERA.length; i++) {
            if (DEV_CONFIGS_FOR_CAMERA[i].equals(configName)) {
                return true;
            }
        }
        return false;
    }

    private void refreshCameraConfig() {


            CameraParam cameraParam = (CameraParam) mFunDevice.getConfig(CameraParam.CONFIG_NAME);
            if (null != cameraParam) {
                // 图像上下翻转
                imageButton_flip.setSelected(cameraParam.getPictureFlip());

                // 图像左右翻转
                imageButton_mirror.setSelected(cameraParam.getPictureMirror());

            }
    }


    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.backBtnInTopLayout:
                 finish();
                 break;
             case R.id.updatename:
                 alertDialog = ECAlertDialog.buildAlert(ActivityGuideSetingCommon.this, getResources().getString(R.string.update_name), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         alertDialog.setDismissFalse(true);
                     }
                 }, new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                         final String newname = text.getText().toString().trim();

                         if(!TextUtils.isEmpty(newname)){

                             if(newname.length()<=10){
                                 showProgressDialog(getResources().getString(R.string.wait));
                                 alertDialog.setDismissFalse(true);
                                 list = lists;
                                 for(int i=0;i<list.size();i++){
                                     if(list.get(i).getDevid().equals(devid)){
                                         list.get(i).setName(newname);
                                         object = new com.alibaba.fastjson.JSONObject();
                                         com.alibaba.fastjson.JSONObject object2 = new com.alibaba.fastjson.JSONObject();
                                         object2.put("monitor",list.toString());
                                         object.put("extraProperties",object2);

                                         HekrUserAction.getInstance(ActivityGuideSetingCommon.this).setProfile(object, new HekrUser.SetProfileListener() {
                                             @Override
                                             public void setProfileSuccess() {
                                                 hideProgressDialog();
                                                 Toast.makeText(ActivityGuideSetingCommon.this, getResources().getString(R.string.success),Toast.LENGTH_LONG).show();
                                                 ClientUser clientUser = CCPAppManager.getClientUser();
                                                 clientUser.setMonitor(list.toString());
                                                 CCPAppManager.setClientUser(clientUser);
                                                 lists = CCPAppManager.getClientUser().getMonitorList();
                                                 devname = newname;
                                                 textView_name.setText(newname);
                                             }

                                             @Override
                                             public void setProfileFail(int errorCode) {
                                                 hideProgressDialog();
                                                 Toast.makeText(ActivityGuideSetingCommon.this, UnitTools.errorCode2Msg(ActivityGuideSetingCommon.this,errorCode),Toast.LENGTH_LONG).show();
                                             }
                                         });
                                         break;

                                     }
                                 }

                             }else{
                                 alertDialog.setDismissFalse(false);
                                 Toast.makeText(ActivityGuideSetingCommon.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                             }


                         }
                         else{
                             alertDialog.setDismissFalse(false);
                             Toast.makeText(ActivityGuideSetingCommon.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
                         }

                     }
                 });
                 alertDialog.setContentView(R.layout.edit_alert);
                 alertDialog.setTitle( getResources().getString(R.string.update_name));
                 EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);


                 text.setText(devname);
                 text.setSelection(devname.length());
                 alertDialog.show();
                 break;
             case R.id.updatetime:
                 alertDialog = ECAlertDialog.buildAlert(ActivityGuideSetingCommon.this, getResources().getString(R.string.sync_ipc_time), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         alertDialog.setDismissFalse(true);
                     }
                 }, new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         if(FunSupport.getInstance().getLoginType()== FunLoginType.LOGIN_BY_AP||
                                 FunSupport.getInstance().getLoginType()== FunLoginType.LOGIN_BY_LOCAL){
                             showWaitDialog();
                             FunSupport.getInstance().requestSyncSummer(mFunDevice);

                         }
                         else{
                             Toast.makeText(ActivityGuideSetingCommon.this, R.string.device_time_in_AP_or_local_mode, Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
                 alertDialog.show();
                 break;
             case R.id.btnSwitchCameraFlip:
                 CameraParam cameraParam = (CameraParam) mFunDevice.getConfig(CameraParam.CONFIG_NAME);
                 if (null != cameraParam) {
                       showWaitDialog();
                     // 图像上下翻转
                          cameraParam.setPictureFlip(!imageButton_flip.isSelected());
                          FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, cameraParam);
                 }
                 break;
             case R.id.btnSwitchCameraMirror:
                 CameraParam cameraParam2 = (CameraParam) mFunDevice.getConfig(CameraParam.CONFIG_NAME);
                 if (null != cameraParam2) {
                     showWaitDialog();
                     // 图像上下翻转
                     cameraParam2.setPictureMirror(!imageButton_mirror.isSelected());
                     FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, cameraParam2);
                 }

                 break;

         }
    }

    //同步设备时区
    private void syncDevZone(Calendar calendar) {
        if (calendar == null) {
            return;
        }
        float zoneOffset = (float) calendar.get(java.util.Calendar.ZONE_OFFSET);
        float zone = (float) (zoneOffset / 60.0 / 60.0 / 1000.0);// 时区，东时区数字为正，西时区为负
        FunSupport.getInstance().requestSyncDevZone(mFunDevice, (int) (-zone * 60));
    }

    //同步设备时间（这个时间同步 设备端如果开启了NTP服务器同步的话，
    // 这个设置是不起作通的，因为设备会到服务器那边同步时间，
    // 所以这个时候只需要同步时区就可以了）
    private void syncDevTime(String sysTime) {
        OPTimeSetting devtimeInfo = (OPTimeSetting)mFunDevice.checkConfig(OPTimeSetting.CONFIG_NAME);
        devtimeInfo.setmSysTime(sysTime);

        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, devtimeInfo);
    }

    /**
     * 请求获取系统信息:SystemInfo/Status.NatInfo
     */
    private void requestTimeInfo() {

        // 获取时间
        FunSupport.getInstance().requestDeviceCmdGeneral(
                mFunDevice, new OPTimeQuery());
    }


    private void refreshSystemInfo() {
        if ( null != mFunDevice ) {



            OPTimeQuery showDevtimeQuery = (OPTimeQuery) mFunDevice
                    .getConfig(OPTimeQuery.CONFIG_NAME);
            if (null != showDevtimeQuery) {
                String mOPTimeQuery = showDevtimeQuery.getOPTimeQuery();
                timeTextView.setText(mOPTimeQuery);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date;
                try {
                    date = sdf.parse(mOPTimeQuery);
                    timeTextView.setDevSysTime(date.getTime());
                    timeTextView.onStartTimer();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onDeviceLoginSuccess(FunDevice funDevice) {

    }

    @Override
    public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
        if ( null != mFunDevice
                && funDevice.getId() == mFunDevice.getId()) {

            if(OPTimeQuery.CONFIG_NAME.equals(configName)){
                hideWaitDialog();
                refreshSystemInfo();
            }else if(JsonConfig.GENERAL_LOCATION.equals(configName)){
                Calendar cal = Calendar.getInstance(Locale.getDefault());
                String sysTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()).format(cal.getTime());
                syncDevZone(cal);
                syncDevTime(sysTime);
            }
            else if(isCurrentUsefulConfig(configName)){
                if (isAllConfigGetted()) {
                    hideWaitDialog();
                }
                refreshCameraConfig();
            }


        }

    }

    @Override
    public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
        hideWaitDialog();
        Toast.makeText(this, FunError.getErrorStr(errCode),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {


            if (null != mFunDevice && funDevice.getId() == mFunDevice.getId()) {

                if ( OPTimeSetting.CONFIG_NAME.equals(configName) ) {

                    // 重新获取时间
                    FunSupport.getInstance().requestDeviceCmdGeneral(
                            mFunDevice, new OPTimeQuery());
                }else{

                hideWaitDialog();
                refreshCameraConfig();
              }

        }
    }

    @Override
    public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
        hideWaitDialog();
        Toast.makeText(this, FunError.getErrorStr(errCode),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeviceChangeInfoSuccess(FunDevice funDevice) {

    }

    @Override
    public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceOptionSuccess(FunDevice funDevice, String option) {

    }

    @Override
    public void onDeviceOptionFailed(FunDevice funDevice, String option, Integer errCode) {

    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice) {

    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

    }

    @Override
    public void onDeviceFileListGetFailed(FunDevice funDevice) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FunSupport.getInstance().removeOnFunDeviceOptListener(this);
    }
}
