package me.hekr.sthome.configuration.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.hekr.sthome.R;
import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.DateUtil;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ProgressDialog;
import me.hekr.sthome.commonBaseView.RoundProgressView;
import me.hekr.sthome.configuration.EsptouchTask;
import me.hekr.sthome.configuration.IEsptouchListener;
import me.hekr.sthome.configuration.IEsptouchResult;
import me.hekr.sthome.configuration.IEsptouchTask;
import me.hekr.sthome.configuration.task.__IEsptouchTask;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.event.UdpConfigEvent;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.http.bean.DcInfo;
import me.hekr.sthome.http.bean.DeviceBean;
import me.hekr.sthome.http.bean.DeviceStatusBean;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.SendOtherData;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by gc-0001 on 2017/2/15.
 */
public class EsptouchAnimationActivity extends TopbarSuperActivity implements View.OnClickListener{
    private final String TAG = EsptouchAnimationActivity.class.getName();
    private RoundProgressView roundProgressView;
    private EspWifiAdminSimple mWifiAdmin;
    private Timer timer = null;
    private TextView textView;
    private MyTask timerTask;
    private int count = 0;
    private final int SPEED1 = 1;
    private final int SPEED2 = 20;
    private final int SPEED3 = 20000;
    private int Now_speed;
    private String apSsid;
    private String apPassword;
    private String apBssid;
    private String  isSsidHiddenStr;
    private String taskResultCountStr;
    private EsptouchAsyncTask3 task3;
    private IEsptouchTask mEsptouchTask;
    private String already_deivce_name;

    private HekrUserAction hekrUserAction;
    private Button btn_retry;
    private int flag = -1;  //1代表绑定成功,2代表绑定失败，3代表已绑定其他设备,4代表回调绑定失败
    private SysmodelDAO sysmodelDAO;
    private SceneDAO sceneDAO;
    private String failmsg = null;
    private SendOtherData sendOtherData;
    private String choosetoDeviceid;
    private String gatewaytype;
    private int result_udpbind = -1;
    private ProgressDialog progressDialog;
    private ECAlertDialog ecAlertDialog;
    private int count_of_bind = 0;
    private TextView fail_reason_view;
    @Override
    protected void onCreateInit() {
        EventBus.getDefault().register(this);
        initssidinfo();
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_esp_animation;
    }

    private void init(){
        fail_reason_view = (TextView)findViewById(R.id.fail_reason);
        fail_reason_view.setText(getClickableSpan());
        fail_reason_view.setMovementMethod(LinkMovementMethod.getInstance());
        fail_reason_view.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        gatewaytype = getIntent().getStringExtra("gatewaytype");
        sysmodelDAO = new SysmodelDAO(this);
        sceneDAO = new SceneDAO(this);
        Now_speed = SPEED1;
        btn_retry =(Button)findViewById(R.id.retry);
        btn_retry.setOnClickListener(this);
        textView = (TextView)findViewById(R.id.tishi);
        textView.setText(getResources().getString(R.string.esptouch_is_configuring));
        roundProgressView = (RoundProgressView)findViewById(R.id.roundprogress);
        roundProgressView.setMax(1f);
        roundProgressView.setProgress(0.00f);
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.net_configuration), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);

        hekrUserAction = HekrUserAction.getInstance(this);


        timer = new Timer();
        timerTask = new MyTask();
        timer.schedule(timerTask,0,1);
        task3 = new EsptouchAsyncTask3();
        task3.execute(apSsid, apBssid, apPassword,
                isSsidHiddenStr, taskResultCountStr);

    }

    private void UpdateInfo(int count){
        roundProgressView.setProgress(((float) count)/200000f);
    }

    private void initssidinfo(){
        sendOtherData = new SendOtherData(this);

        mWifiAdmin = new EspWifiAdminSimple(this);
        apSsid = getIntent().getStringExtra("ssid");
        apPassword = getIntent().getStringExtra("psw");
        apBssid = mWifiAdmin.getWifiConnectedBssid();
        isSsidHiddenStr = "NO";
        taskResultCountStr = "1";
        if (__IEsptouchTask.DEBUG) {
            Log.i(TAG, "mBtnConfirm is clicked, mEdtApSsid = " + apSsid
                    + ", " + " mEdtApPassword = " + apPassword);
        }

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int progress = (int)msg.obj;
                    UpdateInfo(progress);
                    break;
                case 2:

                    roundProgressView.setErrStatus();

                    Now_speed = SPEED1;
                    if(timer!=null){
                        timer.cancel();
                        timer = null;
                        count = 0;
                    }

                    if (mEsptouchTask != null) {
                        mEsptouchTask.interrupt();
                    }
                    if(task3!=null){
                        task3.cancel(true);
                    }

                    btn_retry.setVisibility(View.VISIBLE);
                    fail_reason_view.setVisibility(View.VISIBLE);
                    switch (flag){
                        case 6:
                            textView.setText(getResources().getString(R.string.local_fail_to_get_info));
                            break;
                        case 7:
                            textView.setText(getResources().getString(R.string.gateway_wifi_connect_but_not_connect_service));
                            if(ecAlertDialog==null || (ecAlertDialog!=null && !ecAlertDialog.isShowing()))
                            {
                                ecAlertDialog = ECAlertDialog.buildAlert(EsptouchAnimationActivity.this, getResources().getString(R.string.gateway_lan_mode_tip), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        choosetoDeviceid = ControllerWifi.getInstance().deviceTid;
                                        MyDeviceBean bean = new MyDeviceBean();
                                        bean.setChoice(1);
                                        bean.setDeviceName(getResources().getString(R.string.my_home));
                                        bean.setDevTid(choosetoDeviceid);
                                        bean.setBindKey("");
                                        bean.setCtrlKey("");
                                        bean.setOnline(false);
                                        DcInfo dcInfo = new DcInfo();
                                        dcInfo.setConnectHost("");
                                        bean.setDcInfo(dcInfo);
                                        bean.setProductPublicKey("");
                                        bean.setBinVersion("");
                                        bean.setBinType("");
                                        DeviceDAO DDO = new DeviceDAO(EsptouchAnimationActivity.this);
                                        DDO.deleteAllDeivceChoice();

                                        if(DDO.finddeviceCount(choosetoDeviceid)<1)
                                        DDO.addDevice(bean);


                                        SysModelBean sysModelBean1 = new SysModelBean();
                                        sysModelBean1.setChice("N");
                                        sysModelBean1.setSid("0");
                                        sysModelBean1.setModleName("在家");
                                        sysModelBean1.setDeviceid(choosetoDeviceid);
                                        sysModelBean1.setColor("F0");

                                        SysModelBean sysModelBean2 = new SysModelBean();
                                        sysModelBean2.setChice("N");
                                        sysModelBean2.setSid("1");
                                        sysModelBean2.setModleName("离家");
                                        sysModelBean2.setDeviceid(choosetoDeviceid);
                                        sysModelBean2.setColor("F1");

                                        SysModelBean sysModelBean3 = new SysModelBean();
                                        sysModelBean3.setChice("N");
                                        sysModelBean3.setSid("2");
                                        sysModelBean3.setModleName("睡眠");
                                        sysModelBean3.setDeviceid(choosetoDeviceid);
                                        sysModelBean3.setColor("F2");

                                        sysmodelDAO.addinit(sysModelBean1);
                                        sysmodelDAO.addinit(sysModelBean2);
                                        sysmodelDAO.addinit(sysModelBean3);

                                        SceneBean sceneBean = new SceneBean();
                                        sceneBean.setDeviceid(choosetoDeviceid);
                                        sceneBean.setSid("-1");
                                        sceneBean.setMid("129");
                                        sceneBean.setCode("");
                                        sceneBean.setName("");
                                        sceneDAO.addinit(sceneBean);

                                        SceneBean sceneBean2 = new SceneBean();
                                        sceneBean2.setDeviceid(choosetoDeviceid);
                                        sceneBean2.setSid("-1");
                                        sceneBean2.setMid("130");
                                        sceneBean2.setCode("");
                                        sceneBean2.setName("");
                                        sceneDAO.addinit(sceneBean2);

                                        SceneBean sceneBean3 = new SceneBean();
                                        sceneBean3.setDeviceid(choosetoDeviceid);
                                        sceneBean3.setSid("-1");
                                        sceneBean3.setMid("131");
                                        sceneBean3.setCode("");
                                        sceneBean3.setName("");
                                        sceneDAO.addinit(sceneBean3);

                                        SceneBean sceneBean4 = new SceneBean();
                                        sceneBean4.setDeviceid(choosetoDeviceid);
                                        sceneBean4.setSid("1");
                                        sceneBean4.setMid("129");
                                        sceneBean4.setCode("");
                                        sceneBean4.setName("");
                                        sceneDAO.addinit(sceneBean4);

                                        SceneBean sceneBean5 = new SceneBean();
                                        sceneBean5.setDeviceid(choosetoDeviceid);
                                        sceneBean5.setSid("1");
                                        sceneBean5.setMid("130");
                                        sceneBean5.setCode("");
                                        sceneBean5.setName("");
                                        sceneDAO.addinit(sceneBean5);

                                        SceneBean sceneBean6 = new SceneBean();
                                        sceneBean6.setDeviceid(choosetoDeviceid);
                                        sceneBean6.setSid("2");
                                        sceneBean6.setMid("130");
                                        sceneBean6.setCode("");
                                        sceneBean6.setName("");
                                        sceneDAO.addinit(sceneBean6);

                                        Log.i(TAG,"使用内网，切换到 choosetoDeviceid:"+choosetoDeviceid);

                                        //刷新主页头部
                                        STEvent stEvent = new STEvent();
                                        stEvent.setRefreshevent(1);
                                        EventBus.getDefault().post(stEvent);

                                        //刷新主页数据
                                        STEvent stEvent2 = new STEvent();
                                        stEvent2.setRefreshevent(3);
                                        EventBus.getDefault().post(stEvent2);
                                        //开启搜索局域网服务
                                        STEvent stEvent3= new STEvent();
                                        stEvent3.setServiceevent(6);
                                        EventBus.getDefault().post(stEvent3);
                                        ControllerWifi.getInstance().choose_gateway =true;
                                        ecAlertDialog.dismiss();
                                    }
                                });
                                ecAlertDialog.setCanceledOnTouchOutside(false);
                                ecAlertDialog.show();
                            }

                            break;
                        case 5:
                            textView.setText(getResources().getString(R.string.failed_Esptouch_check_eq));
                            break;

                        case 0:
                            textView.setText(getResources().getString(R.string.failed_Esptouch_check_net));
                            break;

                        case 2:
                        case 4:
                            if(!TextUtils.isEmpty(failmsg))
                                textView.setText(failmsg);
                            break;
                        case 8:
                            if(!TextUtils.isEmpty(failmsg))
                            textView.setText(failmsg);
                            btn_retry.setText(getResources().getString(R.string.re_login));
                            break;
                        case 3:

                            if(TextUtils.isEmpty(already_deivce_name)){
                                textView.setText(getResources().getString(R.string.device_already_bind));

                            }else{
                                String text = String.format(getResources().getString(R.string.device_already_bind_to), ControllerWifi.getInstance().deviceTid,already_deivce_name);
                                textView.setText(text);
                            }


                            break;
                    }
                    break;
                case 3:
                    Log.i(TAG,"跳转到成功页面");
                    if(timer!=null){
                        timer.cancel();
                        timer = null;
                    }
                    Intent tent = new Intent(EsptouchAnimationActivity.this, EsptouchSuccessActivity.class);
                    tent.putExtra("devid",choosetoDeviceid);
                    startActivity(tent);
                    finish();
                    break;
                case 4:
                    if(count_of_bind<3){
                        count_of_bind ++;
                        HekrUserAction.getInstance(EsptouchAnimationActivity.this).deviceBindStatusAndBind(ControllerWifi.getInstance().deviceTid,ControllerWifi.getInstance().bind, new HekrUser.GetBindStatusAndBindListener() {
                            @Override
                            public void getStatusSuccess(final List<DeviceStatusBean> deviceStatusBeanLists) {
                                Log.i(TAG,"deviceStatusBeanLists"+deviceStatusBeanLists.toString());

                                if(deviceStatusBeanLists.get(0).isForceBind()){
                                    choosetoDeviceid = deviceStatusBeanLists.get(0).getDevTid();
                                    flag = 1;
                                }else{
                                    if(deviceStatusBeanLists.get(0).isBindToUser()){

                                        final ControllerWifi controllerWifi = ControllerWifi.getInstance();
                                        Log.i(TAG, "device tid2="+controllerWifi.deviceTid+" +bind key="+controllerWifi.bind);
                                        hekrUserAction.queryOwner(controllerWifi.deviceTid, controllerWifi.bind, new HekrUser.GetQueryOwnerListener() {
                                            @Override
                                            public void queryOwnerSuccess(String message) {
                                                if(message.equals(CCPAppManager.getClientUser().getPhoneNumber())
                                                        ||  message.equals(CCPAppManager.getClientUser().getEmail())   ){
                                                    choosetoDeviceid = deviceStatusBeanLists.get(0).getDevTid();
                                                    flag = 1;
                                                }else{
                                                    already_deivce_name = message;
                                                    if(btn_retry.getVisibility()==View.VISIBLE){
                                                        String text = String.format(getResources().getString(R.string.device_already_bind_to),controllerWifi.deviceTid,already_deivce_name);
                                                        textView.setText(text);
                                                    }
                                                    Log.i(TAG,"已绑定其他设备");
                                                    flag = 3;
                                                }

                                            }

                                            @Override
                                            public void queryOwnerFail(int errorCode) {
                                                Log.i(TAG,"queryOwnerFail:errorCode:==="+errorCode);
                                            }
                                        });


                                    }
                                }



                            }
                            @Override
                            public void getStatusFail(int errorCode) {
                                if(errorCode != 1){
                                    flag = 4;
                                }else {
                                    flag = 8;
                                    ControllerWifi.getInstance().choose_gateway =true;
                                }
                                failmsg = UnitTools.errorCode2Msg(EsptouchAnimationActivity.this,errorCode);
                                Log.i(TAG,"getStatusFail:errorCode:==="+errorCode);
                            }
                            @Override
                            public void bindDeviceSuccess(DeviceBean deviceBean) {
//                            ConnectionPojo.getInstance().deviceTid = deviceBean.getDevTid();
//                            ConnectionPojo.getInstance().bind = deviceBean.getBindKey();
//                            ConnectionPojo.getInstance().ctrlKey = deviceBean.getCtrlKey();
//                            ConnectionPojo.getInstance().propubkey = deviceBean.getProductPublicKey();
                                choosetoDeviceid = deviceBean.getDevTid();
                                MyDeviceBean bean = new MyDeviceBean();
                                bean.setChoice(1);
                                bean.setDeviceName(deviceBean.getDeviceName());
                                bean.setDevTid(deviceBean.getDevTid());
                                bean.setBindKey(deviceBean.getBindKey());
                                bean.setCtrlKey(deviceBean.getCtrlKey());
                                bean.setOnline(deviceBean.isOnline());
                                DcInfo info = new DcInfo();
                                info.setConnectHost(deviceBean.getDcInfo().getConnectHost());
                                bean.setDcInfo(info);
                                bean.setProductPublicKey(deviceBean.getProductPublicKey());
                                bean.setBinVersion(deviceBean.getBinVersion());
                                bean.setBinType(deviceBean.getBinType());
                                DeviceDAO DDO = new DeviceDAO(EsptouchAnimationActivity.this);
                                DDO.deleteAllDeivceChoice();
                                if(DDO.finddeviceCount(choosetoDeviceid)<1)
                                    DDO.addDevice(bean);


                                SysModelBean sysModelBean1 = new SysModelBean();
                                sysModelBean1.setChice("N");
                                sysModelBean1.setSid("0");
                                sysModelBean1.setModleName("在家");
                                sysModelBean1.setDeviceid(deviceBean.getDevTid());
                                sysModelBean1.setColor("F0");

                                SysModelBean sysModelBean2 = new SysModelBean();
                                sysModelBean2.setChice("N");
                                sysModelBean2.setSid("1");
                                sysModelBean2.setModleName("离家");
                                sysModelBean2.setDeviceid(deviceBean.getDevTid());
                                sysModelBean2.setColor("F1");

                                SysModelBean sysModelBean3 = new SysModelBean();
                                sysModelBean3.setChice("N");
                                sysModelBean3.setSid("2");
                                sysModelBean3.setModleName("睡眠");
                                sysModelBean3.setDeviceid(deviceBean.getDevTid());
                                sysModelBean3.setColor("F2");

                                sysmodelDAO.addinit(sysModelBean1);
                                sysmodelDAO.addinit(sysModelBean2);
                                sysmodelDAO.addinit(sysModelBean3);

                                SceneBean sceneBean = new SceneBean();
                                sceneBean.setDeviceid(deviceBean.getDevTid());
                                sceneBean.setSid("-1");
                                sceneBean.setMid("129");
                                sceneBean.setCode("");
                                sceneBean.setName("");
                                sceneDAO.addinit(sceneBean);

                                SceneBean sceneBean2 = new SceneBean();
                                sceneBean2.setDeviceid(deviceBean.getDevTid());
                                sceneBean2.setSid("-1");
                                sceneBean2.setMid("130");
                                sceneBean2.setCode("");
                                sceneBean2.setName("");
                                sceneDAO.addinit(sceneBean2);

                                SceneBean sceneBean3 = new SceneBean();
                                sceneBean3.setDeviceid(deviceBean.getDevTid());
                                sceneBean3.setSid("-1");
                                sceneBean3.setMid("131");
                                sceneBean3.setCode("");
                                sceneBean3.setName("");
                                sceneDAO.addinit(sceneBean3);

                                SceneBean sceneBean4 = new SceneBean();
                                sceneBean4.setDeviceid(deviceBean.getDevTid());
                                sceneBean4.setSid("1");
                                sceneBean4.setMid("129");
                                sceneBean4.setCode("");
                                sceneBean4.setName("");
                                sceneDAO.addinit(sceneBean4);

                                SceneBean sceneBean5 = new SceneBean();
                                sceneBean5.setDeviceid(deviceBean.getDevTid());
                                sceneBean5.setSid("1");
                                sceneBean5.setMid("130");
                                sceneBean5.setCode("");
                                sceneBean5.setName("");
                                sceneDAO.addinit(sceneBean5);

                                SceneBean sceneBean6 = new SceneBean();
                                sceneBean6.setDeviceid(deviceBean.getDevTid());
                                sceneBean6.setSid("2");
                                sceneBean6.setMid("130");
                                sceneBean6.setCode("");
                                sceneBean6.setName("");
                                sceneDAO.addinit(sceneBean6);

                                flag = 1;
                            }
                            @Override
                            public void bindDeviceFail(int errorCode) {
                                if(errorCode == 5400043){
                                    final ControllerWifi controllerWifi = ControllerWifi.getInstance();
                                    Log.i(TAG, "device tid2="+controllerWifi.deviceTid+" +bind key="+controllerWifi.bind);
                                    hekrUserAction.queryOwner(controllerWifi.deviceTid, controllerWifi.bind, new HekrUser.GetQueryOwnerListener() {
                                        @Override
                                        public void queryOwnerSuccess(String message) {
                                            if(message.equals(CCPAppManager.getClientUser().getPhoneNumber())
                                                    ||  message.equals(CCPAppManager.getClientUser().getEmail())   ){
                                                choosetoDeviceid = controllerWifi.deviceTid;
                                                flag = 1;
                                            }else{
                                                already_deivce_name = message;
                                                if(btn_retry.getVisibility()==View.VISIBLE){
                                                    String text = String.format(getResources().getString(R.string.device_already_bind_to),controllerWifi.deviceTid,already_deivce_name);
                                                    textView.setText(text);
                                                }
                                                Log.i(TAG,"已绑定其他设备");
                                                flag = 3;
                                            }

                                        }

                                        @Override
                                        public void queryOwnerFail(int errorCode) {
                                            Log.i(TAG,"queryOwnerFail:errorCode:==="+errorCode);
                                            if(errorCode != 1){
                                                flag = 2;
                                            }else {
                                                flag = 8;
                                                ControllerWifi.getInstance().choose_gateway =true;
                                            }
                                            failmsg = UnitTools.errorCode2Msg(EsptouchAnimationActivity.this,errorCode);
                                        }
                                    });
                                }
                                else{
                                    flag = 2;
                                    failmsg = UnitTools.errorCode2Msg(EsptouchAnimationActivity.this,errorCode);
                                }

                            }
                        });


                    }else{
                        count_of_bind = 0;
                        flag = 2;
                        failmsg = getResources().getString(R.string.network_timeout);
                    }


                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.retry:
                if(flag==8){
                    finish();
                }else {
                    Intent intent = new Intent(EsptouchAnimationActivity.this,BeforeConfigEsptouchActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    class MyTask extends TimerTask{
        @Override
        public void run() {

            if(count>=200000){
               if(flag==1){

                   if(timer!=null){
                       timer.cancel();
                       timer = null;
                   }
                   handler.sendMessage(handler.obtainMessage(3));
               }
                else handler.sendMessage(handler.obtainMessage(2,flag));
            }else{
                count = count+Now_speed;
                handler.sendMessage(handler.obtainMessage(1, count));

                if(count>=120000){
                    if(flag<=0){
                        Now_speed = SPEED1;
                    }else if(flag==1){
                        Now_speed = SPEED2;
                    }else {
                        Now_speed = SPEED3;
                    }

                }



            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        count_of_bind = 0;
        EventBus.getDefault().unregister(this);
        if(timer!=null){
            timer.cancel();
            timer = null;
        }

        if (mEsptouchTask != null) {
            mEsptouchTask.interrupt();
        }
        result_udpbind = 0;
        if(task3!=null){

            task3.cancel(true);
            task3 = null;
        }


    }


    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        Now_speed = SPEED2;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                String ds = getResources().getString(R.string.is_connected_to_wifi);
                String text = String.format(ds,result.getBssid());
                Toast.makeText(EsptouchAnimationActivity.this, text,
                        Toast.LENGTH_LONG).show();
                textView.setText(getResources().getString(R.string.device_has_been_find));
            }

        });
    }

    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
            ConnectionPojo.getInstance().deviceTid = "GS193".equals(gatewaytype)?("RPMA_"+result.getBssid()):("ST_"+result.getBssid());
        }
    };


    private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {



        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {

            synchronized (mLock) {
                if (__IEsptouchTask.DEBUG) {
                    Log.i(TAG, "progress dialog is canceled");
                }
                if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
                }
            }

        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            List<IEsptouchResult> resultList =null;
            try {
                int taskResultCount = -1;
                synchronized (mLock) {
                    // !!!NOTICE
                    String apSsid = mWifiAdmin.getWifiConnectedSsidAscii(params[0]);
                    String apBssid = params[1];
                    String apPassword = params[2];
                    String isSsidHiddenStr = params[3];
                    String taskResultCountStr = params[4];
                    boolean isSsidHidden = false;
                    if ("YES".equals(isSsidHiddenStr)) {
                        isSsidHidden = true;
                    }
                    taskResultCount = Integer.parseInt(taskResultCountStr);
                    mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword,
                            isSsidHidden, EsptouchAnimationActivity.this);
                    Log.i(TAG, "配网开始");

                    mEsptouchTask.setEsptouchListener(myListener);
                }
                resultList = mEsptouchTask.executeForResults(taskResultCount);
                IEsptouchResult ir = resultList.get(0);
                if (!ir.isCancelled()) {
                    ControllerWifi.getInstance().targetip = ir.getInetAddress();

                    if (ir.isSuc()) {
                        //暂时不切换后台
//                        STEvent stEvent = new STEvent();
//                        stEvent.setServiceevent(8);
//                        EventBus.getDefault().post(stEvent);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                        STEvent stEvent = new STEvent();
                        stEvent.setServiceevent(5);
                        EventBus.getDefault().post(stEvent);

                        while (result_udpbind<0){

                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if(result_udpbind >0){
                            sendOtherData.timeCheck();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sendOtherData.timeZoneCheck(DateUtil.getCurrentTimeZone());
                        }


                }
                return resultList;
            }catch (Exception e){
                flag = 0;
                handler.sendMessage(handler.obtainMessage(2,flag));
                return resultList;
            }

        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
//			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
//					.setEnabled(true);
//			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
//					"Confirm");
            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                    flag = 0;
                    final ControllerWifi controllerWifi = ControllerWifi.getInstance();
                    Log.i(TAG, "device tid="+controllerWifi.deviceTid+" +bind key="+controllerWifi.bind);

                    if(result_udpbind==1){
                        flag = 7;
                        return;
                    }else if(result_udpbind == 0){
                        flag = 6;
                        return;
                    }

                handler.sendEmptyMessage(4);

            }
        }
    }

    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(UdpConfigEvent event){
        result_udpbind = event.getFlag_result();
        Log.i(TAG,"result_udpbind:"+result_udpbind);
    }


    @Subscribe          //订阅事件Event
    public  void onEventMainThread(STEvent event){
        if(event.getRefreshevent()==7){
            progressDialog.setPressText(event.getProgressText());
            if(!progressDialog.isShowing()){
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }else if(event.getRefreshevent()==3){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
                finish();
            }
        }else if(event.getRefreshevent()==6){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
                finish();
            }
        }
    }

    private SpannableString getClickableSpan() {
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EsptouchAnimationActivity.this, QuestionActivity.class));
            }
        };

        SpannableString spanableInfo = new SpannableString(
                getResources().getString(R.string.config_fail_reason));

        int end = spanableInfo.length();
        spanableInfo.setSpan(new Clickable(l), 0, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanableInfo;
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
}
