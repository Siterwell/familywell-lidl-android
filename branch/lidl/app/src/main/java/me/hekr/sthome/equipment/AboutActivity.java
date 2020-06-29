package me.hekr.sthome.equipment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.hekr.sdk.Hekr;
import me.hekr.sdk.inter.HekrMsgCallback;
import me.hekr.sthome.MyApplication;
import me.hekr.sthome.R;
import me.hekr.sthome.ServeIntroActivity;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.commonBaseView.LoadingProceedDialog;
import me.hekr.sthome.commonBaseView.SettingItem;
import me.hekr.sthome.event.LogoutEvent;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.http.bean.DeviceBean;
import me.hekr.sthome.http.bean.FirmwareBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.tools.Config;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.UnitTools;
import me.hekr.sthome.updateApp.ProgressEvent;
import me.hekr.sthome.updateApp.UpdateAppAuto;
import me.hekr.sthome.updateApp.UpdateService;

/**
 * Created by gc-0001 on 2017/1/20.
 */
public class AboutActivity extends TopbarSuperActivity implements View.OnClickListener{
    private final String TAG  = "AboutActivity";
    private SettingItem version_txt,app_txt,sale,yinsi;
    private TextView siter;
    private LinearLayout downing_txt;
    private TextView intro_txt;
    private DeviceBean currentDevice;
    private FirmwareBean file;
    private UpdateAppAuto updateAppAuto;
    private ProgressBar bar;
    private ECListDialog ecListDialog;
    private ECAlertDialog ecAlertDialog;
    public LoadingProceedDialog loadingProceedDialog;

    @Override
    protected void onCreateInit() {
        EventBus.getDefault().register(this);
        initGuider();
        initview();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    private void initGuider(){
        getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.about), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 finish();
            }
        },null);
    }

    private void initview(){
        bar         = findViewById(R.id.jindu);
        downing_txt = findViewById(R.id.downing);
        if(UpdateService.flag_updating){
            downing_txt.setVisibility(View.VISIBLE);
        }else{
            downing_txt.setVisibility(View.GONE);
        }
        siter       = findViewById(R.id.guanwang);
        yinsi       = findViewById(R.id.yinsi);
        sale        = findViewById(R.id.sale);
        app_txt     = findViewById(R.id.app_version);
        updateAppAuto = new UpdateAppAuto(this,app_txt,true);
        version_txt = findViewById(R.id.gateway_version);
        intro_txt =   findViewById(R.id.intro);
        String verName = Config.getVerName(this, getPackageName());
        app_txt.setDetailText(verName);
        siter.setText(getWebSite(siter));
//        siter.setMovementMethod(LinkMovementMethod.getInstance());//必须设置否则无效
        sale.setOnClickListener(this);
        yinsi.setOnClickListener(this);
        try {
            if(!TextUtils.isEmpty(ConnectionPojo.getInstance().deviceTid)){
                HekrUserAction.getInstance(this).getDevices(0,80,new HekrUser.GetDevicesListener() {
                    @Override
                    public void getDevicesSuccess(List<DeviceBean> devicesLists) {
                        for(DeviceBean d : devicesLists){
                            if(ConnectionPojo.getInstance().deviceTid.equals(d.getDevTid())){
                                currentDevice = d;
                                doActions();
                            }
                        }
                    }
                    @Override
                    public void getDevicesFail(int errorCode) {
                    }
                });

                version_txt.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        if(file != null){
                            checkUpdatefirm(true);

                        }else{
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(AboutActivity.this, getResources().getString(R.string.fireware_is_lastest),null);
                            ecAlertDialog.show();
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        updateAppAuto.getUpdateInfo();

        intro_txt.setText(R.string.about_app);

    }

    private void doActionSend() {
        String abc = "{\"msgId\":16810,\"action\":\"devUpgrade\"," +
                "\"params\":{\"appTid\":\""+ ConnectionPojo.getInstance().IMEI +"\"," +
                "\"devTid\":\""+ currentDevice.getDevTid() +"\"," +
                "\"ctrlKey\":\""+ currentDevice.getCtrlKey() +"\"," +
                "\"binUrl\":\""+ file.getBinUrl() +"\"," +
                "\"md5\":\""+ file.getMd5() +"\"," +
                "\"binType\":\""+ file.getLatestBinType() +"\"," +
                "\"binVer\":\""+ file.getLatestBinVer() +"\"," +
                "\"size\":"+ file.getSize() +"}}";
        try {
            LOG.D(TAG, "[RYAN] ++  start FW upgrade > domain = " + ConnectionPojo.getInstance().domain);
            Hekr.getHekrClient().sendMessage(new JSONObject(abc), new HekrMsgCallback() {
                @Override
                public void onReceived(String msg) {
                    LOG.D(TAG, "[RYAN] ++ FW upgrade > onReceived : " + msg);
                }

                @Override
                public void onTimeout() {
                    LOG.D(TAG, "[RYAN] ++ FW upgrade > onTimeout");
                }

                @Override
                public void onError(int errorCode, String message) {
                    LOG.E(TAG,"[RYAN] doActionSend > onError > " + message);
                }
            }, ConnectionPojo.getInstance().domain);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(ProgressEvent event){
        int progress = event.getUpdateprogress();
        Log.i(TAG,"progress"+progress);
        if(progress<100){
            downing_txt.setVisibility(View.VISIBLE);
            bar.setProgress(progress);
        }else{
            bar.setProgress(progress);
            downing_txt.setVisibility(View.GONE);
        }

    }

    private void doActions() {
        HekrUserAction.getInstance(this).checkFirmwareUpdate(
                currentDevice.getDevTid(),
                currentDevice.getProductPublicKey(),
                currentDevice.getBinType(),
                currentDevice.getBinVersion(), new HekrUser.CheckFwUpdateListener() {
            @Override
            public void checkNotNeedUpdate() {
                version_txt.setDetailText(currentDevice.getBinVersion());
                version_txt.setNewUpdateVisibility(false);
            }

            @Override
            public void checkNeedUpdate(FirmwareBean firmwareBean) {
                file = firmwareBean;
                version_txt.setDetailText("v " + currentDevice.getBinVersion());
                version_txt.setNewUpdateVisibility(true);
            }

            @Override
            public void checkFail(int errorCode) {
                Toast.makeText(AboutActivity.this, UnitTools.errorCode2Msg(AboutActivity.this,errorCode),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private SpannableString getWebSite(TextView tv) {
        final String link;
        final UnitTools unitTools = new UnitTools(this);
        if("zh".equals(unitTools.readLanguage())){
            link = "http://www.elro.eu";
        }else{
            link = "http://www.elro.eu";
        }

        SpannableString content = new SpannableString(link);
        content.setSpan(new UnderlineSpan(), 0, link.length(), 0);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(link);
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        return content;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sale:
                openPhoneAlert();
                break;
            case R.id.yinsi:
                startActivity(new Intent(this, ServeIntroActivity.class));
                break;
        }

    }

    /**
     * 内部类，用于截获点击富文本后的事件
     */
    class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.black));
            ds.setUnderlineText(true);    //去除超链接的下划线
        }
    }


    private void openPhoneAlert(){


        ecListDialog = new ECListDialog(this,getResources().getStringArray(R.array.lianxi));
        ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
            @Override
            public void onDialogItemClick(Dialog d, int position) {

                switch (position){
                    case 0:
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+getResources().getStringArray(R.array.lianxi)[0]));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        break;
                    case 1:
                        Intent data=new Intent(Intent.ACTION_SENDTO);
                        data.setData(Uri.parse("mailto:"+getResources().getStringArray(R.array.lianxi)[1]));
                        startActivity(data);
                        break;
                    default:
                        break;
                }

            }
        });
        ecListDialog.show();



    }

    private void checkUpdatefirm(final boolean first){
        if (currentDevice == null) {
            return;
        }

        if(currentDevice.isOnline() || !first){
            HekrUserAction.getInstance(this).checkFirmwareUpdate(currentDevice.getDevTid(),
                    currentDevice.getProductPublicKey(),
                    currentDevice.getBinType(),
                    currentDevice.getBinVersion(),
                    new HekrUser.CheckFwUpdateListener() {

                @Override
                public void checkNotNeedUpdate() {
                }

                @Override
                public void checkNeedUpdate(FirmwareBean firmwareBean) {
                    file = firmwareBean;
                    if(ecAlertDialog==null||!ecAlertDialog.isShowing()){
                        String message = null;
                        String comfirm = null;
                        if(first){
                            message = getString(R.string.firewarm_to_update);
                            comfirm =  getResources().getString(R.string.ok);
                        }else {
                            message = getResources().getString(R.string.fail_upgrade);
                            comfirm = getResources().getString(R.string.retry);
                        }

                        ecAlertDialog = ECAlertDialog.buildAlert(MyApplication.getActivity(),
                                message,
                                getResources().getString(R.string.now_not_to_update),
                                comfirm,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        doActionSend();
                                        loadingProceedDialog = new LoadingProceedDialog(AboutActivity.this);
                                        loadingProceedDialog.setResultListener(new LoadingProceedDialog.ResultListener() {
                                            @Override
                                            public void result(boolean success) {
                                                if(success){
                                                    Toast.makeText(AboutActivity.this,getResources().getString(R.string.success_upgrade),Toast.LENGTH_LONG).show();
                                                }else {
                                                    checkUpdatefirm(false);
                                                }
                                            }

                                            @Override
                                            public void proceed() {
                                                HekrUserAction.getInstance(AboutActivity.this).getDevices(currentDevice.getDevTid(), new HekrUser.GetDevicesListener() {
                                                    @Override
                                                    public void getDevicesSuccess(List<DeviceBean> devicesLists) {
                                                        if(devicesLists!=null && devicesLists.size()>0){
                                                            DeviceBean otaDevice = devicesLists.get(0);
                                                            LOG.D(TAG, "[RYAN] FW upgrading > current: " + currentDevice.getBinVersion() +
                                                                    ", ota: " + otaDevice.getBinVersion());
                                                            if(!currentDevice.getBinVersion().equals(otaDevice.getBinVersion())){
                                                                if(loadingProceedDialog!=null) loadingProceedDialog.setFlag_success(true);
                                                                version_txt.setEnabled(false);
                                                                version_txt.setNewUpdateVisibility(false);
                                                                version_txt.setDetailText(otaDevice.getBinVersion());

                                                                final DeviceDAO deviceDAO = new DeviceDAO(getApplicationContext());
                                                                deviceDAO.updateDeivceBinversion(otaDevice.getDevTid(), otaDevice.getBinVersion());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void getDevicesFail(int errorCode) {
                                                        LOG.I(TAG,"更新获取网关信息错误："+errorCode);
                                                    }
                                                });
                                            }
                                        });
                                        loadingProceedDialog.setPressText(getResources().getText(R.string.is_upgrading));
                                        loadingProceedDialog.setCancelable(false);
                                        loadingProceedDialog.show();
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
