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
    private DeviceBean deviceBean;
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
                        Set<String> set = new HashSet<>();
                        for(DeviceBean d : devicesLists){
                            set.add(d.getDcInfo().getConnectHost());
                            if(ConnectionPojo.getInstance().deviceTid.equals(d.getDevTid())){
                                deviceBean = d;
                                doActions();
                            }
                        }
                        Hekr.getHekrClient().setHosts(set);
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
                "\"params\":{\"appTid\":\""+ ConnectionPojo.getInstance().IMEI+"\"," +
                "\"devTid\":\""+deviceBean.getDevTid()+"\"," +
                "\"ctrlKey\":\""+deviceBean.getCtrlKey()+"\"," +
                "\"binUrl\":\""+file.getBinUrl()+"\"," +
                "\"md5\":\""+file.getMd5()+"\"," +
                "\"binType\":\""+file.getLatestBinType()+"\"," +
                "\"binVer\":\""+file.getLatestBinVer()+"\"," +
                "\"size\":"+file.getSize()+"}}";
        try {
            Hekr.getHekrClient().sendMessage(new JSONObject(abc), new HekrMsgCallback() {
                @Override
                public void onReceived(String msg) {

                }

                @Override
                public void onTimeout() {

                }

                @Override
                public void onError(int errorCode, String message) {
                    LOG.E(TAG,"doActionSend > onError > " + message);
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
        HekrUserAction.getInstance(this).checkFirmwareUpdate(deviceBean.getDevTid(), deviceBean.getProductPublicKey(), deviceBean.getBinType(), deviceBean.getBinVersion(), new HekrUser.CheckFwUpdateListener() {
            @Override
            public void checkNotNeedUpdate() {
                version_txt.setDetailText(deviceBean.getBinVersion());
                version_txt.setNewUpdateVisibility(false);
            }

            @Override
            public void checkNeedUpdate(FirmwareBean firmwareBean) {
                file = firmwareBean;
                version_txt.setDetailText("v "+deviceBean.getBinVersion());
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
            link = "https://safewith.me/base/";
        }else{
            link = "https://safewith.me/base/";
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
        final DeviceDAO deviceDAO = new DeviceDAO(this);
        final DeviceBean d = deviceDAO.findByChoice(1);
        if(d!=null && ((first && d.isOnline()) || !first)){
            HekrUserAction.getInstance(this).checkFirmwareUpdate(d.getDevTid(),d.getProductPublicKey(), d.getBinType(), d.getBinVersion(), new HekrUser.CheckFwUpdateListener() {
                @Override
                public void checkNotNeedUpdate() {
                }

                @Override
                public void checkNeedUpdate(FirmwareBean firmwareBean) {
                    file = firmwareBean;
                    if(ecAlertDialog==null||!ecAlertDialog.isShowing()){
                        String s = null;
                        String s2 = null;
                        if(first){
                            s = String.format(getResources().getString(R.string.firewarm_to_update),file.getLatestBinVer());
                            s2 =  getResources().getString(R.string.ok);
                        }else {
                            s = getResources().getString(R.string.fail_upgrade);
                            s2 = getResources().getString(R.string.retry);
                        }

                        ecAlertDialog = ECAlertDialog.buildAlert(MyApplication.getActivity(),
                                s,
                                getResources().getString(R.string.now_not_to_update),
                                s2,
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
                                                HekrUserAction.getInstance(AboutActivity.this).getDevices(d.getDevTid(), new HekrUser.GetDevicesListener() {
                                                    @Override
                                                    public void getDevicesSuccess(List<DeviceBean> devicesLists) {
                                                        if(devicesLists!=null &&devicesLists.size()>0){
                                                            DeviceBean deviceBean = devicesLists.get(0);
                                                            if(!d.getBinVersion().equals(deviceBean.getBinVersion())){
                                                                if(loadingProceedDialog!=null) loadingProceedDialog.setFlag_success(true);
                                                                version_txt.setEnabled(false);
                                                                version_txt.setNewUpdateVisibility(false);
                                                                version_txt.setDetailText(deviceBean.getBinVersion());
                                                                deviceDAO.updateDeivceBinversion(deviceBean.getDevTid(),deviceBean.getBinVersion());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void getDevicesFail(int errorCode) {
                                                        com.litesuits.android.log.Log.i(TAG,"更新获取网关信息错误："+errorCode);
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
