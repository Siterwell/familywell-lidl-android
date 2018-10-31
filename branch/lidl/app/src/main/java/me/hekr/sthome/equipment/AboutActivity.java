package me.hekr.sthome.equipment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import me.hekr.sthome.R;
import me.hekr.sthome.ServeIntroActivity;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.commonBaseView.SettingItem;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.http.bean.DeviceBean;
import me.hekr.sthome.http.bean.FirmwareBean;
import me.hekr.sthome.tools.Config;
import me.hekr.sthome.tools.ConnectionPojo;
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
        bar         = (ProgressBar)findViewById(R.id.jindu);
        downing_txt = (LinearLayout)findViewById(R.id.downing);
        if(UpdateService.flag_updating){
            downing_txt.setVisibility(View.VISIBLE);
        }else{
            downing_txt.setVisibility(View.GONE);
        }
        siter       = (TextView)findViewById(R.id.guanwang);
        yinsi       = (SettingItem)findViewById(R.id.yinsi);
        sale        = (SettingItem)findViewById(R.id.sale);
        app_txt     = (SettingItem)findViewById(R.id.app_version);
        updateAppAuto = new UpdateAppAuto(this,app_txt,true);
        version_txt = (SettingItem)findViewById(R.id.gateway_version);
        intro_txt =   (TextView)findViewById(R.id.intro);
        String verName = Config.getVerName(this, getPackageName());
        app_txt.setDetailText(verName);
        siter.setText(getClickableSpan(0));
        siter.setMovementMethod(LinkMovementMethod.getInstance());//必须设置否则无效
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
                            String ds = String.format(getResources().getString(R.string.firewarm_to_update),file.getLatestBinVer());
                        ecAlertDialog = ECAlertDialog.buildAlert(AboutActivity.this, ds,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                doActionSend();
                                version_txt.setEnabled(false);
                                version_txt.setNewUpdateVisibility(false);
                            }
                        });
                        ecAlertDialog.show();

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

        intro_txt.setText(getResources().getString(R.string.about_app));

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

    private SpannableString getClickableSpan(int start) {
        String dd = "";
        final UnitTools unitTools = new UnitTools(this);
        if("zh".equals(unitTools.readLanguage())){
            dd = "http://www.elro.eu";
        }else{
            dd = "http://www.elro.eu";
        }


        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                if("zh".equals(unitTools.readLanguage())){
                    Uri content_url = Uri.parse("http://www.elro.eu");
                    intent.setData(content_url);
                    startActivity(intent);
                }else{
                    Uri content_url = Uri.parse("http://www.elro.eu");
                    intent.setData(content_url);
                    startActivity(intent);
                }
            }
        };




        SpannableString spanableInfo = new SpannableString(dd);

        int end = spanableInfo.length();
        spanableInfo.setSpan(new Clickable(l), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanableInfo;
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
}
