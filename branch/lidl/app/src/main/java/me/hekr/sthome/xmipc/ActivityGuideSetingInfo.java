package me.hekr.sthome.xmipc;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.EDEV_JSON_ID;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunLog;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.JsonConfig;
import com.lib.funsdk.support.config.StatusNetInfo;
import com.lib.funsdk.support.config.SystemInfo;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.utils.UIFactory;
import com.lib.sdk.bean.DefaultConfigBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.model.modelbean.ClientUser;
import me.hekr.sthome.model.modelbean.MonitorBean;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by Administrator on 2017/8/30.
 */

public class ActivityGuideSetingInfo extends TopbarIpcSuperActivity implements View.OnClickListener,OnFunDeviceOptListener,IFunSDKResult {
   private final static String TAG = ActivityGuideSetingInfo.class.getName();

    private static final long TIMEOUT_MS = 60000; // 60s

    private TextView textView_sn,textView_type,textView_net_type,textView_net_status;
    private ImageView imageView;
    private int id;
    private FunDevice mFunDevice;
    private Bitmap mQrCodeBmp = null;
    private DefaultConfigBean mdefault = null;
    private int mHandler;
    private ECAlertDialog ecAlertDialog_ipc;
    private Button btn_reset,btn_delete;
    private ECAlertDialog alertDialog;
    private String devid;
    private List<MonitorBean> lists;
    private List<MonitorBean> list;
    private com.alibaba.fastjson.JSONObject object;

    private Handler errorHandler = new Handler();
    private Runnable errorRunnable = new Runnable() {
        @Override
        public void run() {
            LOG.E(TAG, "[ERROR] factory reset timeout");
            hideProgressDialog();
            showToast(R.string.failed);
        }
    };


    @Override
    protected void onCreateInit() {
        initview();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_setting_info;
    }



    private void initview(){
        lists = CCPAppManager.getClientUser().getMonitorList();
        id = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
        mFunDevice = FunSupport.getInstance().findDeviceById(id);
        if ( null == mFunDevice ) {
            finish();
            return;
        }
        devid = mFunDevice.getDevSn();
        imageView = (ImageView)findViewById(R.id.imgDeviceQRCode);
        textView_title.setText(getResources().getString(R.string.common));
        textView_sn = (TextView)findViewById(R.id.sn);
        textView_sn.setTextIsSelectable(true);
        textView_type = (TextView)findViewById(R.id.type);
        textView_net_type = (TextView)findViewById(R.id.net_type);
        textView_net_status = (TextView)findViewById(R.id.net_status);
        btn_reset = (Button)findViewById(R.id.reset);
        btn_delete = (Button)findViewById(R.id.delete);
        btn_reset.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        textView_back.setOnClickListener(this);
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);


        mdefault = new DefaultConfigBean();


        mHandler = FunSDK.RegUser(this);
        requestSystemInfo();
    }


    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.backBtnInTopLayout:
                 finish();
                 break;
             case R.id.reset:
                 DeviceDefaltConfig();
                 errorHandler.removeCallbacks(errorRunnable);
                 errorHandler.postDelayed(errorRunnable, TIMEOUT_MS);
                 break;
             case R.id.delete:
                 alertDialog = ECAlertDialog.buildAlert(this,getResources().getString(R.string.confirm_delete_monitor), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         list =  new ArrayList<>();
                         list =  lists;

                         for(int i=0;i<list.size();i++){

                             if(list.get(i).getDevid().equals(devid)){
                                 list.remove(i);
                                 object = new com.alibaba.fastjson.JSONObject();
                                 com.alibaba.fastjson.JSONObject object2 = new com.alibaba.fastjson.JSONObject();
                                 object2.put("monitor",list.toString());
                                 object.put("extraProperties",object2);

                                 HekrUserAction.getInstance(ActivityGuideSetingInfo.this).setProfile(object, new HekrUser.SetProfileListener() {
                                     @Override
                                     public void setProfileSuccess() {
                                         Log.i(TAG,"修改后的数据："+list.toString());
                                         ClientUser clientUser = CCPAppManager.getClientUser();
                                         clientUser.setMonitor(list.toString());
                                         CCPAppManager.setClientUser(clientUser);
                                         lists = CCPAppManager.getClientUser().getMonitorList();

                                         Intent intent = new Intent(ActivityGuideSetingInfo.this, ActivityGuideDeviceCamera.class);
                                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                         startActivity(intent);
                                         finish();
                                     }

                                     @Override
                                     public void setProfileFail(int errorCode) {
                                         Toast.makeText(ActivityGuideSetingInfo.this, UnitTools.errorCode2Msg(ActivityGuideSetingInfo.this,errorCode),Toast.LENGTH_LONG).show();
                                     }
                                 });


                                 break;
                             }
                         }
                     }
                 });
                 alertDialog.show();
                 break;
         }
    }


    /**
     * 请求获取系统信息:SystemInfo/Status.NatInfo
     */
    private void requestSystemInfo() {

        // 获取系统信息
        FunSupport.getInstance().requestDeviceConfig(
                mFunDevice, SystemInfo.CONFIG_NAME);

        // 获取NAT状态
        FunSupport.getInstance().requestDeviceConfig(
                mFunDevice, StatusNetInfo.CONFIG_NAME);
    }

    private void refreshSystemInfo() {
        if ( null != mFunDevice ) {
            SystemInfo systemInfo = (SystemInfo)mFunDevice.getConfig(SystemInfo.CONFIG_NAME);
            if ( null != systemInfo ) {

                // 序列号
                textView_sn.setText(systemInfo.getSerialNo());

                // 设备型号
                textView_type.setText(systemInfo.getHardware());

                // 设备连接方式
                textView_net_type.setText(getResources().getString(getConnectTypeStringId(mFunDevice.getNetConnectType())));

                // 生成二维码
                Bitmap qrCodeBmp = UIFactory.createCode(
                        systemInfo.getSerialNo(), 600, 0xff202020);
                if ( null != qrCodeBmp ) {
                    if ( null != mQrCodeBmp ) {
                        mQrCodeBmp.recycle();
                    }
                    mQrCodeBmp = qrCodeBmp;
                    imageView.setImageBitmap(qrCodeBmp);
                }

            }

            StatusNetInfo netInfo = (StatusNetInfo)mFunDevice.getConfig(StatusNetInfo.CONFIG_NAME);
            if ( null != netInfo ) {
                if("Conneted".equals(netInfo.getNatStatus())){
                    textView_net_status.setText(getResources().getString(R.string.connected));
                }else{
                    textView_net_status.setText(getResources().getString(R.string.unconnected));
                }

            }
        }
    }

    // 0: p2p 1:转发 2IP直连
    private int getConnectTypeStringId(int netConnectType) {
        if ( netConnectType == 0 ) {
            return R.string.device_net_connect_type_p2p;
        } else if ( netConnectType == 1 ) {
            return R.string.device_net_connect_type_transmit;
        } else if ( netConnectType == 2 ) {
            return R.string.device_net_connect_type_ip;
        } else if ( netConnectType == 5) {
            return R.string.device_net_connect_type_rps;
        }

        return R.string.device_net_connect_type_unknown;
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
                && funDevice.getId() == mFunDevice.getId()
                && ( SystemInfo.CONFIG_NAME.equals(configName)
                || StatusNetInfo.CONFIG_NAME.equals(configName)) ) {
            refreshSystemInfo();
        }
    }

    @Override
    public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
        //Toast.makeText(this, FunError.getErrorStr(errCode),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {

    }

    @Override
    public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
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

        if ( null != mQrCodeBmp ) {
            mQrCodeBmp.recycle();
            mQrCodeBmp = null;
        }


        FunSupport.getInstance().removeOnFunDeviceOptListener(this);
    }

    @Override
    public int OnFunSDKResult(Message msg, MsgContent msgContent) {


        FunLog.d(TAG, "msg.what : " + msg.what);
        FunLog.d(TAG, "msg.arg1 : " + msg.arg1 + " [" + FunError.getErrorStr(msg.arg1) + "]");
        FunLog.d(TAG, "msg.arg2 : " + msg.arg2);
        if (null != msgContent) {
            FunLog.d(TAG, "msgContent.sender : " + msgContent.sender);
            FunLog.d(TAG, "msgContent.seq : " + msgContent.seq);
            FunLog.d(TAG, "msgContent.str : " + msgContent.str);
            FunLog.d(TAG, "msgContent.arg3 : " + msgContent.arg3);
            FunLog.d(TAG, "msgContent.pData : " + msgContent.pData);
        }

        hideWaitDialog();
        switch (msg.what) {
            case EUIMSG.DEV_SET_JSON:
            {
                if (msg.arg1 < 0) {
                    showToast(FunError.getErrorStr(msg.arg1));
                }else {
                    if (msgContent.str.equals(JsonConfig.OPERATION_DEFAULT_CONFIG)) {
                        JSONObject object = new JSONObject();
                        object.put("Action", "Reboot");
                        System.out.print("TTT--------->> " + object.toString());
                        FunSDK.DevCmdGeneral(mHandler, mFunDevice.devSn, EDEV_JSON_ID.OPMACHINE, JsonConfig.OPERATION_MACHINE, 1024, 5000,
                                HandleConfigData.getSendData(JsonConfig.OPERATION_MACHINE, "0x1", object).getBytes(), -1, 0);
                        showToast(R.string.device_system_info_defaultconfigsucc);

                        errorHandler.removeCallbacks(errorRunnable);
                        hideProgressDialog();
                    }
                }
            }
            break;
        }


        return 0;
    }


    private void DeviceDefaltConfig() {

        if(ecAlertDialog_ipc==null || !ecAlertDialog_ipc.isShowing())
        {
            ecAlertDialog_ipc = ECAlertDialog.buildAlert(this, R.string.reset_to_default_set, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showProgressDialog(getResources().getString(R.string.wait));
                    mdefault.setAllConfig(1);
                    FunSDK.DevSetConfigByJson(mHandler, mFunDevice.devSn, JsonConfig.OPERATION_DEFAULT_CONFIG, HandleConfigData.getSendData(JsonConfig.OPERATION_DEFAULT_CONFIG, "0x1", mdefault), -1, 20000, mFunDevice.getId());
                }
            });

            ecAlertDialog_ipc.show();
        }

    }
}
