package me.hekr.sthome.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import me.hekr.sdk.Constants;
import me.hekr.sdk.Hekr;
import me.hekr.sdk.dispatcher.IMessageFilter;
import me.hekr.sdk.http.HekrRawCallback;
import me.hekr.sdk.inter.HekrMsgCallback;
import me.hekr.sdk.utils.CacheUtil;
import me.hekr.sthome.DragFolderwidget.ApplicationInfo;
import me.hekr.sthome.MyApplication;
import me.hekr.sthome.R;
import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.AlertEvent;
import me.hekr.sthome.event.AutoSyncCompleteEvent;
import me.hekr.sthome.event.AutoSyncEvent;
import me.hekr.sthome.event.LogoutEvent;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.event.TokenTimeoutEvent;
import me.hekr.sthome.event.UdpConfigEvent;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.http.SiterConstantsUtil;
import me.hekr.sthome.http.bean.DcInfo;
import me.hekr.sthome.http.bean.DeviceBean;
import me.hekr.sthome.http.bean.UserBean;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.ResolveData;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.AccountUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.InforTotalReceiver;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.tools.SendOtherData;
import me.hekr.sthome.tools.SendSceneData;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by Administrator on 2017/7/3.
 */

public class SiterService extends Service {
    private static final int NOTIFICATION_ID = 101;
    private static final String CHANNEL_ID = "SiterService";
    private static final String CHANNEL_NAME = "My Background Service";

    private final String TAG = this.getClass().getName();
    private InforTotalReceiver inforTotalReceiver;
    private ResolveData resolveData;


    private SendEquipmentData sed;
    private SendSceneData sendSceneData;
    private SendOtherData sendOtherData;
    public final static String UDP_BROADCAST = "me.hekr.sthome.udp";
    public  int flag_btn_synceq;
    private  MyTask myTask;
    private static Timer timer = null;
    private static Timer timer_gateway = null;
    private  UpdateGateWayTask updateGateWayTask;
    private int count = 0;
    private int count_update_gateway = 0;
    private static boolean timer_of_sync_en = false;
    private boolean timer_of_sync_en_gateway = false;
    private UDPRecData udpRecData;
    private ExecutorService sendService,receiveservice;
    private boolean send_end_time = false;
    private String now_ssid = null;
    private int now_nettype = -1;
    private boolean lock_gateway =false;
    private ECAlertDialog ecAlertDialog;
    private IMessageFilter filter;
    public static MyDeviceBean choiceddevice;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LOG.I(TAG,"on onCreate");
        EventBus.getDefault().register(this);

        timer = new Timer();
        myTask = new MyTask();
        timer.schedule(myTask,0,1000);

        timer_gateway = new Timer();
        updateGateWayTask = new UpdateGateWayTask();
        timer_gateway.schedule(updateGateWayTask,0,1000);

        sendService = Executors.newSingleThreadExecutor();
        receiveservice = Executors.newSingleThreadExecutor();
        resolveData = new ResolveData(this) {
            @Override
            public void EndSyncScene() {

                    STEvent stEvent2 = new STEvent();
                    stEvent2.setRefreshevent(3);
                    EventBus.getDefault().post(stEvent2);
                   setCount(0);
                   setTimer_of_sync_en(false);
                   if(!send_end_time){
                       send_end_time = true;
                       sendOtherData.timeCheck();
                   }
                   EventBus.getDefault().post(new AutoSyncCompleteEvent());
            }

            @Override
            public void EndSyncEq() {

                if(isFlag_btn_synceq()==2){
                    setFlag_btn_synceq(0);
                    STEvent stEvent2 = new STEvent();
                    stEvent2.setRefreshevent(2);
                    stEvent2.setProgressText(getResources().getString(R.string.sync_eq_name));
                    EventBus.getDefault().post(stEvent2);
                    syncFromEquipmentName();
                }else if(isFlag_btn_synceq()==1){
                    setFlag_btn_synceq(0);
                    STEvent stEvent2 = new STEvent();
                    stEvent2.setRefreshevent(2);
                    stEvent2.setProgressText(getResources().getString(R.string.sync_scenes));
                    EventBus.getDefault().post(stEvent2);
                    syncFromScenes();
                }

            }

            @Override
            public void EndSyncEqName() {
                STEvent stEvent2 = new STEvent();
                stEvent2.setRefreshevent(3);
                EventBus.getDefault().post(stEvent2);
                setCount(0);
                setTimer_of_sync_en(false);
            }

            @Override
            public void EndSyncTimerInfo() {
                LOG.I(TAG,"时间同步结束");
                STEvent stEvent = new STEvent();
                stEvent.setRefreshevent(4);
                EventBus.getDefault().post(stEvent);
            }

            @Override
            public void Getinfolinstenr() {
                setCount(0);  //还原计时，如果到5则同步超时
            }

            @Override
            public void reFreshCurrentMode() {
                LOG.D(TAG, "[SCENE debug] reFreshCurrentMode ");
                STEvent stEvent2 = new STEvent();
                stEvent2.setRefreshevent(3);
                EventBus.getDefault().post(stEvent2);
            }
        };
        sendOtherData = new SendOtherData(this);
        sendSceneData = new SendSceneData(this) {
            @Override
            protected void sendEquipmentDataFailed() {

            }

            @Override
            protected void sendEquipmentDataSuccess() {

            }
        };

        sed = new SendEquipmentData(this) {
            @Override
            protected void sendEquipmentDataFailed() {

            }

            @Override
            protected void sendEquipmentDataSuccess() {

            }
        };
        super.onCreate();

        startForeground();
    }

    private void startForeground() {
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        Notification notification = builder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setContentTitle(getString(R.string.app_name))
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

        getNotificationManager().createNotificationChannel(channel);
        return CHANNEL_ID;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOG.D(TAG, "onStartCommand()");


        receiveAllMessage();
        if(inforTotalReceiver==null){
            inforTotalReceiver = new InforTotalReceiver(this,resolveData) {

            };
            IntentFilter filter = new IntentFilter();
            //只有持有相同的action的接受者才能接收此广播
            filter.addAction(SiterConstantsUtil.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
            filter.addAction(UDP_BROADCAST);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(inforTotalReceiver, filter);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        LOG.I(TAG,"onDestroy()");
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        if(inforTotalReceiver!=null){
            this.unregisterReceiver(inforTotalReceiver);
        }
        if(udpRecData!=null){
            udpRecData.close();
        }

        if(!receiveservice.isShutdown()){
            receiveservice.shutdown();
        }

        if(!sendService.isShutdown()){
            sendService.shutdown();
        }
        ControllerWifi.getInstance().wifiTag = false;

        if(timer!=null){
            timer.cancel();
            timer = null;
        }
    }


    /**
     * 主动接收消息
     */
    private void receiveAllMessage() {
        if(filter == null){
            filter = new IMessageFilter() {
                @Override
                public boolean doFilter(String in) {
                    return true;
                }
            };
            Hekr.getHekrClient().receiveMessage(filter, new HekrMsgCallback() {
                @Override
                public void onReceived(String msg) {
                    // 收到消息
                    Intent intent = new Intent(SiterConstantsUtil.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
                    intent.putExtra(SiterConstantsUtil.HEKR_WS_PAYLOAD,msg);
                    sendBroadcast(intent);
                }

                @Override
                public void onTimeout() {
                    // 主动接受不会有这个回调
                }

                @Override
                public void onError(int errorCode, String message) {
                    // 接收错误
                }
            });
        }

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

                LOG.I(TAG,"ConnectionPojo.getInstance().bind:"+ ConnectionPojo.getInstance().bind);
                LOG.I(TAG,"ConnectionPojo.getInstance().deviceTid:"+ ConnectionPojo.getInstance().deviceTid);
                LOG.I(TAG,"ConnectionPojo.getInstance().ctrlKey:"+ ConnectionPojo.getInstance().ctrlKey);
                LOG.I(TAG,"ConnectionPojo.getInstance().propubkey:"+ ConnectionPojo.getInstance().propubkey);
                LOG.I(TAG,"ConnectionPojo.getInstance().domain:"+ ConnectionPojo.getInstance().domain);
                LOG.I(TAG,"ConnectionPojo.getInstance().version:"+ ConnectionPojo.getInstance().binversion);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void ChooseGayaway(){

        STEvent stEvent = new STEvent();
        stEvent.setRefreshevent(2);
        stEvent.setProgressText(getResources().getString(R.string.sync_gateway));
        EventBus.getDefault().post(stEvent);
        final DeviceDAO deviceDAO = new DeviceDAO(this);
        final SysmodelDAO sysmodelDAO = new SysmodelDAO(this);
        final SceneDAO sceneDAO = new SceneDAO(this);
        final MyDeviceBean myDeviceBean = deviceDAO.findByChoice(1);
        setCount_update_gateway(0);
        setTimer_of_sync_en_gateway(true);
        HekrUserAction.getInstance(this).getDevices(0,80,new HekrUser.GetDevicesListener() {
            @Override
            public void getDevicesSuccess(final List<DeviceBean> devicesLists) {

                LOG.I(TAG,"第一次获取到网关列表信息:"+devicesLists.toString());
                setTimer_of_sync_en_gateway(false);
                setCount_update_gateway(0);
                if(lock_gateway) return;


                if(devicesLists.size()==0){
                    deviceDAO.deleteAll();
                    ConnectionPojo.getInstance().bind = null;
                    ConnectionPojo.getInstance().deviceTid = null;
                    ConnectionPojo.getInstance().ctrlKey = null;
                    ConnectionPojo.getInstance().IMEI =  null;
                    ConnectionPojo.getInstance().propubkey = null;
                    ConnectionPojo.getInstance().domain = null;
                    ConnectionPojo.getInstance().binversion = null;
                    AutoSyncCompleteEvent stEvent = new AutoSyncCompleteEvent();
                    stEvent.setFlag_devices_empty(true);
                    EventBus.getDefault().post(stEvent);

                }else{

                    new Thread(){
                        @Override
                        public void run() {
                            try {

                                HashSet<String> set = new HashSet<String>();

                                deviceDAO.deleteAll();
                                String vl = null;
                                choiceddevice = null;
                                for(DeviceBean bean:devicesLists){
                                    set.add(bean.getDcInfo().getConnectHost());
                                    MyDeviceBean bean1 = new MyDeviceBean();
                                    bean1.setDeviceName(bean.getDeviceName());
                                    bean1.setDevTid(bean.getDevTid());
                                    bean1.setOnline(bean.isOnline());
                                    bean1.setBindKey(bean.getBindKey());
                                    bean1.setCtrlKey(bean.getCtrlKey());
                                    bean1.setProductPublicKey(bean.getProductPublicKey());
                                    bean1.setBinType(bean.getBinType());
                                    bean1.setBinVersion(bean.getBinVersion());
                                    DcInfo dcInfo =  new DcInfo();
                                    dcInfo.setConnectHost(bean.getDcInfo().getConnectHost());
                                    bean1.setDcInfo(dcInfo);
                                    if(myDeviceBean!=null){
                                        if(bean.getDevTid().equals(myDeviceBean.getDevTid())
                                                &&bean.getCtrlKey().equals(myDeviceBean.getCtrlKey())
                                                &&bean.getBindKey().equals(myDeviceBean.getBindKey())){
                                            bean1.setChoice(1);
                                            vl = bean.getDeviceName();
                                            choiceddevice = bean1;
                                        }else{
                                            bean1.setChoice(0);
                                        }

                                    }else {
                                        if(bean1.isOnline()&& TextUtils.isEmpty(vl)){
                                            bean1.setChoice(1);
                                            choiceddevice = bean1;
                                        }else{
                                            bean1.setChoice(0);
                                        }
                                    }
                                    deviceDAO.addDevice(bean1);
                                }
                                if(devicesLists.size()>0) {
                                    Hekr.getHekrClient().setHosts(set);
                                }
                                if (TextUtils.isEmpty(vl) ) {

                                    DeviceBean deviceBean = null;

                                    deviceBean = (choiceddevice==null? devicesLists.get(0):choiceddevice);

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

                                    deviceDAO.updateDeivceChoice(deviceBean.getDevTid());
                                    ConnectionPojo.getInstance().deviceTid = deviceBean.getDevTid();
                                    ConnectionPojo.getInstance().bind = deviceBean.getBindKey();
                                    ConnectionPojo.getInstance().ctrlKey = deviceBean.getCtrlKey();
                                    ConnectionPojo.getInstance().propubkey = deviceBean.getProductPublicKey();
                                    ConnectionPojo.getInstance().domain = deviceBean.getDcInfo().getConnectHost();
                                    ConnectionPojo.getInstance().IMEI = Settings.Secure.getString(SiterService.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                                    ConnectionPojo.getInstance().binversion = deviceBean.getBinVersion();

                                    String name = deviceBean.getDeviceName();
                                    String status = "";
                                    boolean d = deviceBean.isOnline();
                                    if("报警器".equals(name)){
                                        name = getResources().getString(R.string.my_home);
                                    }
                                    if(d){
                                        status = getResources().getString(R.string.on_line);
                                    }else{
                                        status = getResources().getString(R.string.off_line);
                                    }
                                    Message message = new Message();
                                    message.obj =name+ status;
                                    message.what = 1;
                                     handler.sendMessage(message);
                                }else{
                                    String name = myDeviceBean.getDeviceName();
                                    String status = "";
                                    boolean d = myDeviceBean.isOnline();
                                    if("报警器".equals(name)){
                                        name = getResources().getString(R.string.my_home);
                                    }
                                    if(d){
                                        status = getResources().getString(R.string.on_line);
                                    }else{
                                        status = getResources().getString(R.string.off_line);
                                    }
                                    Message message = new Message();
                                    message.obj =name+ status;
                                    message.what = 1;
                                    handler.sendMessage(message);
                                }

                            }catch (Exception e){
                                LOG.I(TAG,"同步设备出错");
                                e.printStackTrace();
                            }
                        }
                    }.start();




                }
            }

            @Override
            public void getDevicesFail(int errorCode) {
                setTimer_of_sync_en_gateway(false);
                setCount_update_gateway(0);

                if(lock_gateway) return;

                if(errorCode == 0 && NetWorkUtils.getNetWorkType(SiterService.this)!=0){
                    Toast.makeText(SiterService.this, UnitTools.errorCode2Msg(SiterService.this,1),Toast.LENGTH_LONG).show();
                    LogoutEvent logoutEvent = new LogoutEvent();
                    EventBus.getDefault().post(logoutEvent);
                }
                else if(errorCode==1){
                    Toast.makeText(SiterService.this, UnitTools.errorCode2Msg(SiterService.this,1),Toast.LENGTH_LONG).show();
                    LogoutEvent logoutEvent = new LogoutEvent();
                    EventBus.getDefault().post(logoutEvent);
                }else{
                    Toast.makeText(SiterService.this, UnitTools.errorCode2Msg(SiterService.this,errorCode),Toast.LENGTH_LONG).show();
                    AutoSyncCompleteEvent autoSyncCompleteEvent = new AutoSyncCompleteEvent();
                    EventBus.getDefault().post(autoSyncCompleteEvent);
                }

            }
        });


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(lock_gateway)  return;

                    String status = (String)msg.obj;
                    Toast.makeText(SiterService.this,getResources().getString(R.string.connect_alert) + " " + status,Toast.LENGTH_SHORT).show();
                    STEvent stEvent = new STEvent();
                    stEvent.setRefreshevent(1);
                    EventBus.getDefault().post(stEvent);


                    int result = NetWorkUtils.getNetWorkType(SiterService.this);
                    if(result<=3){
                        handler.sendEmptyMessage(4);
                    }else{
                        STEvent stEvent3 = new STEvent();
                        stEvent3.setRefreshevent(2);
                        stEvent3.setProgressText(getResources().getString(R.string.search_local_net));
                        EventBus.getDefault().post(stEvent3);
                            initBroadcastreceiveUdp();
                            SeartchWifiData.MyTaskCallback taskCallback = new SeartchWifiData.MyTaskCallback() {
                                @Override
                                public void operationFailed() {
                                    LOG.I(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ failed");
                                    udpRecData.close();
                                    handler.sendEmptyMessage(4);
                                }

                                @Override
                                public void operationSuccess() {
                                    LOG.I(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ success");
                                    ControllerWifi.getInstance().wifiTag = true;
                                    initreceiveUdp();
                                    startUdp();
                                    handler.sendEmptyMessage(4);
                                }

                                @Override
                                public void doReSendAction() {
                                    searchUdp();
                                }
                            };

                            SeartchWifiData seartchWifiData = new SeartchWifiData(taskCallback);
                            seartchWifiData.startReSend();
                    }


                    break;
                case 2:
                    //同步无响应caozuo
                    setTimer_of_sync_en(false);
                    STEvent stevent = new STEvent();
                    stevent.setRefreshevent(6);
                    EventBus.getDefault().post(stevent);
                    if(!send_end_time){
                        send_end_time = true;
                        sendOtherData.timeCheck();
                    }

                    if(ControllerWifi.getInstance().wifiTag){
                        ControllerWifi.getInstance().wifiTag = false;
                    }
                    EventBus.getDefault().post(new AutoSyncCompleteEvent());
                    break;

                case 3:
                    setFlag_btn_synceq(1);
                    syncFromEquipment();
                    STEvent stEvent3 = new STEvent();
                    stEvent3.setRefreshevent(7);
                    stEvent3.setProgressText(getResources().getString(R.string.sync_eq));
                    EventBus.getDefault().post(stEvent3);
                    LOG.I(TAG,"定时开始1");
                    setCount(0);
                    setTimer_of_sync_en(true);
                    break;
                case 4:
                    setFlag_btn_synceq(1);
                    syncFromEquipment();
                    STEvent stEvent2 = new STEvent();
                    stEvent2.setRefreshevent(2);
                    stEvent2.setProgressText(getResources().getString(R.string.sync_eq));
                    EventBus.getDefault().post(stEvent2);
                    LOG.I(TAG,"定时开始1");
                    setCount(0);
                    setTimer_of_sync_en(true);
                    break;
                case 6:
                    //网关同步超时处理：
                    setTimer_of_sync_en_gateway(false);
                    setCount_update_gateway(0);
                    lock_gateway = true;
                    int result2 = NetWorkUtils.getNetWorkType(SiterService.this);

                    if(result2<=3){
                        handler.sendEmptyMessage(4);
                    }else{
                        STEvent stEvent4 = new STEvent();
                        stEvent4.setRefreshevent(2);
                        stEvent4.setProgressText(getResources().getString(R.string.search_local_net));
                        EventBus.getDefault().post(stEvent4);
                        initBroadcastreceiveUdp();
                        SeartchWifiData.MyTaskCallback taskCallback = new SeartchWifiData.MyTaskCallback() {
                            @Override
                            public void operationFailed() {
                                LOG.I(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ failed");
                                udpRecData.close();
                                handler.sendEmptyMessage(4);
                            }

                            @Override
                            public void operationSuccess() {
                                LOG.I(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ success");
                                ControllerWifi.getInstance().wifiTag = true;
                                initreceiveUdp();
                                startUdp();
                                handler.sendEmptyMessage(4);
                            }

                            @Override
                            public void doReSendAction() {
                                searchUdp();
                            }
                        };

                        SeartchWifiData seartchWifiData = new SeartchWifiData(taskCallback);
                        seartchWifiData.startReSend();
                    }
                    break;
            }
        }
    };


    /**
     * syn get device with devicename
     */
    private void syncFromEquipmentName() {
        if(ConnectionPojo.getInstance().deviceTid != null) {
            String crc = CoderUtils.getEqNameCRC(this, ConnectionPojo.getInstance().deviceTid);
            sed.synGetDeviceName(crc);
        }
    }

    /**
     * syn get device with crc
     */
    private void syncFromEquipment() {
        if(ConnectionPojo.getInstance().deviceTid != null) {
            String crc = CoderUtils.getEqCRC(this, ConnectionPojo.getInstance().deviceTid);
            sed.synGetDeviceStatus(crc);
        }
    }

    /**
     * syn get scenes with crc
     */
    private void syncFromScenes() {
        if(ConnectionPojo.getInstance().deviceTid != null) {
            SysmodelDAO sysmodelDAO = new SysmodelDAO(this);
            String groupcrc = CoderUtils.getSceneGroupCRC(this, ConnectionPojo.getInstance().deviceTid);
            String scenecrc = CoderUtils.getSceneCRC(this, ConnectionPojo.getInstance().deviceTid);

            String target  = "";
            try {
                target  = sysmodelDAO.findIdByChoice(ConnectionPojo.getInstance().deviceTid).getSid();
            }catch (Exception e){
                target = "9";
            }
            if(target == null){
                target = "9";
            }
            sendSceneData.synGetSceneInformation(target, groupcrc, scenecrc);
            resolveData.StartSyncScene();
        }
    }

    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
           switch (event.getServiceevent()){
               case 4:
                   //下拉刷新（刷新情景列表）
                   syncFromScenes();
                   LOG.I(TAG,"定时开始2");
                   setCount(0);
                   setTimer_of_sync_en(true);
                   break;
               case 1:
                   //后台刷新（刷新情景列表）
                   syncFromScenes();
                   break;
               case 2:
                   //前台刷新（刷新设备列表和设备名称列表）
                   setFlag_btn_synceq(2);
                   syncFromEquipment();
                   setCount(0);
                   setTimer_of_sync_en(true);
                   STEvent stEvent2 = new STEvent();
                   stEvent2.setRefreshevent(2);
                   stEvent2.setProgressText(getResources().getString(R.string.sync_eq));
                   EventBus.getDefault().post(stEvent2);
                   break;
               case 3:
                   //后台刷新（无转圈）
                   syncFromEquipmentName();
                   break;
               case 5:
                   //绑定获取udp信息
                   initBindUdp();
                   SeartchWifiDataForConfig.MyTaskCallback taskCallback = new SeartchWifiDataForConfig.MyTaskCallback() {
                       @Override
                       public void operationFailed() {
                           LOG.I(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ failed");
                           UdpConfigEvent udpConfigEvent = new UdpConfigEvent();
                           if(TextUtils.isEmpty(ControllerWifi.getInstance().deviceTid)){
                               udpConfigEvent.setFlag_result(0);
                           }else{
                               udpConfigEvent.setFlag_result(1);
                           }

                           EventBus.getDefault().post(udpConfigEvent);
                       }

                       @Override
                       public void operationSuccess() {
                           LOG.I(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ success");
                           UdpConfigEvent udpConfigEvent = new UdpConfigEvent();
                           udpConfigEvent.setFlag_result(2);
                           EventBus.getDefault().post(udpConfigEvent);
                       }

                       @Override
                       public void doReSendAction() {
                           startUdp();
                       }
                   };

                   SeartchWifiDataForConfig seartchWifiData = new SeartchWifiDataForConfig(taskCallback);
                   seartchWifiData.startReSend();
                   break;
               case 6:
                   STEvent stEvent3 = new STEvent();
                   stEvent3.setRefreshevent(7);
                   stEvent3.setProgressText(getResources().getString(R.string.search_local_net));
                   EventBus.getDefault().post(stEvent3);
                   initBroadcastreceiveUdp();
                    SeartchWifiData.MyTaskCallback taskCallback2 = new SeartchWifiData.MyTaskCallback() {
                        @Override
                        public void operationFailed() {
                            LOG.I(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ failed");
                            udpRecData.close();
                            ControllerWifi.getInstance().wifiTag = false;
                            handler.sendEmptyMessage(3);
                        }

                        @Override
                        public void operationSuccess() {
                            LOG.I(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ success");
                            ControllerWifi.getInstance().wifiTag = true;
                            initreceiveUdp();
                            startUdp();
                            handler.sendEmptyMessage(3);
                        }

                        @Override
                        public void doReSendAction() {
                            searchUdp();
                        }
                    };

                    SeartchWifiData seartchWifiData2= new SeartchWifiData(taskCallback2);
                    seartchWifiData2.startReSend();
                   break;
               case 7:
                   //监听网络变化做出相应动作
                   String newssid = event.getSsid();
                       int nettype = event.getNettype();
                       switch (nettype){
                           case 0:
                           case 1:
                           case 2:
                           case 3:
                               ControllerWifi.getInstance().wifiTag = false;
                               if(udpRecData!=null) udpRecData.close();
                               break;
                           case 4:
                               if((!newssid.equals(now_ssid)&&!TextUtils.isEmpty(now_ssid)) || (now_nettype<4&&!TextUtils.isEmpty(now_ssid))){
                                   initBroadcastreceiveUdp();
                                   SeartchWifiData.MyTaskCallback taskCallback3 = new SeartchWifiData.MyTaskCallback() {
                                       @Override
                                       public void operationFailed() {
                                           LOG.I(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ failed");
                                           udpRecData.close();
                                           ControllerWifi.getInstance().wifiTag = false;
                                       }

                                       @Override
                                       public void operationSuccess() {
                                           LOG.I(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ success");
                                           ControllerWifi.getInstance().wifiTag = true;
                                           initreceiveUdp();
                                           startUdp();
                                       }

                                       @Override
                                       public void doReSendAction() {
                                           searchUdp();
                                       }
                                   };

                                   SeartchWifiData seartchWifiData3= new SeartchWifiData(taskCallback3);
                                   seartchWifiData3.startReSend();
                               }
                               now_ssid = newssid;
                               break;
                       }
                   now_nettype = nettype;
                   break;
               case 8:
                   initSwitchUdp();
                   SeartchWifiDataForSwitchServer.MyTaskCallback myTaskCallback = new SeartchWifiDataForSwitchServer.MyTaskCallback() {
                       @Override
                       public void operationFailed() {
                          LOG.I(TAG,"++++切换后台失败");
                       }

                       @Override
                       public void operationSuccess() {
                           LOG.I(TAG,"++++切换后台成功");
                       }

                       @Override
                       public void doReSendAction() {
                          switchServer();
                       }
                   };
                   SeartchWifiDataForSwitchServer seartchWifiDataForSwitchServer = new SeartchWifiDataForSwitchServer(myTaskCallback);
                   seartchWifiDataForSwitchServer.startReSend();
                   break;
           }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)         //订阅事件TokenTimeoutEvent
    public  void onEventMainThread(TokenTimeoutEvent event){
        final String loginname = AccountUtil.getUsername();
        final String loginpsw = AccountUtil.getPassword();
        if(event.getType()==1){
            Hekr.getHekrUser().refreshToken(new HekrRawCallback() {
                @Override
                public void onSuccess(int httpCode, byte[] bytes) {
                    LOG.I(TAG,"刷新accesstoken成功");
                    UserBean userBean = new UserBean(loginname, loginpsw, CacheUtil.getUserToken(), CacheUtil.getString(Constants.REFRESH_TOKEN,""));
                    HekrUserAction.getInstance(SiterService.this).setUserCache(userBean);
                }

                @Override
                public void onError(int httpCode, byte[] bytes) {
                    if(httpCode == 1){

                        LogoutEvent logoutEvent = new LogoutEvent();
                        EventBus.getDefault().post(logoutEvent);

                    }
                }
            });
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onEventMainThread(AutoSyncEvent event){

        initCurrentGateway();
        ChooseGayaway();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)         //订阅事件AlertEvent
    public  void onEventMainThread(AlertEvent event){
        if(!TextUtils.isEmpty(event.getDeviceid()) && !TextUtils.isEmpty(HekrUserAction.getInstance(this).getJWT_TOKEN())){
            Log.i(TAG, "onEventMainThread: ");
            resolveAlertPushInfo(event.getContent(),event.getDeviceid());
        }
    }


    private void initBroadcastreceiveUdp() {
        LOG.I(TAG,"initBroadcastreceiveUdp");
        if(udpRecData!=null){
            udpRecData.close();
        }
        if(receiveservice!=null){
            receiveservice.shutdown();
        }


        String localAddress = NetWorkUtils.getLocalIpAddress(this);
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(localAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LOG.I(TAG, " send create ip failed");
        }



        InetAddress target = null;
            String targetip = localAddress.substring(0, localAddress.lastIndexOf(".") + 1) + 255;
        LOG.I(TAG," 广播接收udp地址 ===" + targetip);
            try {
                target = InetAddress.getByName(targetip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        DatagramSocket datagramSocket = null;
        try {

            datagramSocket = new DatagramSocket(1025, ip);
            ControllerWifi.getInstance().ds = datagramSocket;
        } catch (SocketException e) {
            e.printStackTrace();
        }


        receiveservice = Executors.newSingleThreadExecutor();
        udpRecData = new UDPRecData(datagramSocket,target,this,0);
        receiveservice.execute(udpRecData);
    }

    private void initreceiveUdp() {

            if(udpRecData!=null){
                udpRecData.close();
            }
            if(receiveservice!=null){
                receiveservice.shutdown();
            }

            LOG.I(TAG,"initreceiveUdp");
            DatagramSocket datagramSocket = null;
            try {
                    datagramSocket = new DatagramSocket(null);
                    datagramSocket.setReuseAddress(true);
                    datagramSocket.connect(ControllerWifi.getInstance().targetip,1025);
                    ControllerWifi.getInstance().ds = datagramSocket;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        LOG.I(TAG," 接收udp地址 ===" + ControllerWifi.getInstance().targetip.toString());
            receiveservice = Executors.newSingleThreadExecutor();
            udpRecData = new UDPRecData(ControllerWifi.getInstance().ds, ControllerWifi.getInstance().targetip,this,0);
            receiveservice.execute(udpRecData);


    }

    private void initBindUdp() {

        if(udpRecData!=null){
            udpRecData.close();
        }
        if(receiveservice!=null){
            receiveservice.shutdown();
        }


        LOG.I(TAG,"initBindUdp");
        LOG.I(TAG," 绑定的接收udp地址 ===" + ControllerWifi.getInstance().targetip.toString());
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(null);
            datagramSocket.setReuseAddress(true);
            datagramSocket.connect(ControllerWifi.getInstance().targetip,1025);
            ControllerWifi.getInstance().ds = datagramSocket;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        receiveservice = Executors.newSingleThreadExecutor();
        udpRecData = new UDPRecData(ControllerWifi.getInstance().ds, ControllerWifi.getInstance().targetip,this,1);
        receiveservice.execute(udpRecData);


    }

    private void initSwitchUdp() {

        if(udpRecData!=null){
            udpRecData.close();
        }
        if(receiveservice!=null){
            receiveservice.shutdown();
        }


        LOG.I(TAG,"initSwitchUdp");
        LOG.I(TAG," 绑定的接收udp地址 ===" + ControllerWifi.getInstance().targetip.toString());
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(null);
            datagramSocket.setReuseAddress(true);
            datagramSocket.connect(ControllerWifi.getInstance().targetip,1025);
            ControllerWifi.getInstance().ds = datagramSocket;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        receiveservice = Executors.newSingleThreadExecutor();
        udpRecData = new UDPRecData(ControllerWifi.getInstance().ds, ControllerWifi.getInstance().targetip,this,2);
        receiveservice.execute(udpRecData);


    }

    private void searchUdp(){
        try {
            String localAddress = NetWorkUtils.getLocalIpAddress(this);
            InetAddress target = null;
            String targetip = localAddress.substring(0,localAddress.lastIndexOf(".")+1)+255;
            try {
                target = InetAddress.getByName(targetip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            LOG.I(TAG," 发送搜索udp广播地址 ===" + target.toString());
            UDPSendData udpSendData = new UDPSendData(ControllerWifi.getInstance().ds,target,"IOT_KEY?"+ ConnectionPojo.getInstance().deviceTid+":LIDL01EN");
            sendService.execute(udpSendData);
            sendService.awaitTermination(50, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            LOG.I(TAG," targetip is null" );
        }
    }


    private void switchServer(){
        try {

            LOG.I(TAG," ControllerWifi.getInstance().targetip ===" + ControllerWifi.getInstance().targetip.toString());

            UDPSendData udpSendData = new UDPSendData(ControllerWifi.getInstance().ds, ControllerWifi.getInstance().targetip,"IOT_SWITCH:"+ ConnectionPojo.getInstance().deviceTid+":LIDL01EN");
            sendService.execute(udpSendData);
            sendService.awaitTermination(50, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            LOG.I(TAG," targetip is null" );
        }
    }

    private void startUdp(){
        try {

            LOG.I(TAG," ControllerWifi.getInstance().targetip ===" + ControllerWifi.getInstance().targetip.toString());

            UDPSendData udpSendData = new UDPSendData(ControllerWifi.getInstance().ds, ControllerWifi.getInstance().targetip,"IOT_KEY?"+ ConnectionPojo.getInstance().deviceTid+":LIDL01EN");
            sendService.execute(udpSendData);
            sendService.awaitTermination(50, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            LOG.I(TAG," targetip is null" );
        }
    }


    public int isFlag_btn_synceq() {
        return flag_btn_synceq;
    }

    //1代表点击网关或进入APP后的同步，2代表点击按钮主动同步
    public void setFlag_btn_synceq(int flag_synceq) {
        this.flag_btn_synceq = flag_synceq;
    }


    class MyTask extends TimerTask {
        @Override
        public void run() {


            if(isTimer_of_sync_en()) {
                LOG.I(TAG,"同步接收超时计数:"+count);
                count ++;
            }

            if(count >= 5){
                count = 0;

                handler.sendEmptyMessage(2);
            }

        }
    }

    class UpdateGateWayTask extends TimerTask {
        @Override
        public void run() {


            if(isTimer_of_sync_en_gateway()) {
                LOG.I(TAG,"同步网关接收超时计数:"+count_update_gateway);
                count_update_gateway ++;
            }

            if(count_update_gateway >= 4){
                count_update_gateway = 0;

                handler.sendEmptyMessage(6);
            }

        }
    }

    /**
     * doAlertShow:
     * 作者：Henry on 2017/3/13 15:47
     * 邮箱：xuejunju_4595@qq.com
     * 参数:aaaa:devicestatus,eqid:设备id,deviceid:网关id
     * 返回:
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void doAlertShow(String status,String dev_type,String eqid,String deviceid){
        DeviceDAO deviceDAO = new DeviceDAO(this);
        EquipDAO equipDAO = new EquipDAO(this);
        String ds = "";
        String place = "";
        String gateway = "";
        String title = "";
        boolean current_gateway = false;
        if("55".equals(status.substring(4,6))){
            ds = getString(R.string.alarm);
        }else if("BB".equals(status.substring(4,6))){
            ds = getString(R.string.test);
        }else {
            int volt = Integer.parseInt( status.substring(2,4),16);
            if(volt < 15){
                ds = getString(R.string.lowvolt_alarm);
            }
        }
        current_gateway = ConnectionPojo.getInstance().deviceTid.equals(deviceid)?true:false;


        DeviceBean deviceBean = deviceDAO.findByDeviceid(deviceid);
        EquipmentBean equipmentBean = equipDAO.findByeqid(eqid,deviceid);
        if(deviceBean == null){
            gateway = getResources().getString(R.string.my_home);
        }else{
            gateway = deviceBean.getDeviceName();
            if("报警器".equals(gateway)){
                gateway = getResources().getString(R.string.my_home);
            }
        }

        if(equipmentBean==null){
            place = NameSolve.getDefaultName(this,dev_type,eqid);
        }else{
            String eqname = equipmentBean.getEquipmentName();
            if(TextUtils.isEmpty(eqname)){
                place = NameSolve.getDefaultName(this,dev_type,eqid);
            }else{
                place = eqname;
            }
        }

        if(current_gateway){
            title = String.format(getResources().getString(R.string.now_gateway_eq_is_happen_has_eq),gateway,deviceid.substring(deviceid.length()-4),place,ds);
        }else{
            title = String.format(getResources().getString(R.string.other_gateway_eq_is_happen_has_eq),gateway,deviceid.substring(deviceid.length()-4),place,ds);
        }


        if(ecAlertDialog==null||!ecAlertDialog.isShowing()){
            ecAlertDialog = ECAlertDialog.buildAlert(MyApplication.getActivity(),
                    title,
                    getResources().getString(R.string.btn_silence),
                    getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                                    SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                            try {
                                UnitTools.stopMusic(SiterService.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sed.sendGateWaySilence();


                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                UnitTools.stopMusic(SiterService.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    });
            ecAlertDialog.setCancelable(false);
            ecAlertDialog.setCanceledOnTouchOutside(false);
            ecAlertDialog.show();
        }
        try {
            UnitTools.playNotifycationMusic(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * doGatewayAlertShow:
     * 作者：Henry on 2017/3/13 15:47
     * 邮箱：xuejunju_4595@qq.com
     * 参数:aaaa:devicestatus,eqid:设备id,deviceid:网关id
     * 返回:
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void doGatewayAlertShow(String status,String deviceid){

        String gateway = "";
        String title = "";
        boolean current_gateway = false;

        current_gateway = ConnectionPojo.getInstance().deviceTid.equals(deviceid)?true:false;


        if(current_gateway){
            title = String.format(getResources().getString(R.string.now_gateway_eq_is_happen_has_eq),gateway,deviceid.substring(deviceid.length()-4),status,"");
        }else{
            title = String.format(getResources().getString(R.string.other_gateway_eq_is_happen_has_eq),gateway,deviceid.substring(deviceid.length()-4),status,"");
        }




        if(ecAlertDialog==null||!ecAlertDialog.isShowing()){
            ecAlertDialog = ECAlertDialog.buildAlert(MyApplication.getActivity(),
                    title,
                    getResources().getString(R.string.btn_silence),
                    getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                                    SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                            try {
                                UnitTools.stopMusic(SiterService.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sed.sendGateWaySilence();


                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                UnitTools.stopMusic(SiterService.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    });
            ecAlertDialog.setCancelable(false);
            ecAlertDialog.setCanceledOnTouchOutside(false);
            ecAlertDialog.show();
        }
        try {
            UnitTools.playNotifycationMusic(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * doCXSMAlertShow:
     * 作者：Henry on 2017/3/13 15:48
     * 邮箱：xuejunju_4595@qq.com
     * 参数:aaaa:devicestatus,eqid:设备id,deviceid:网关id
     * 返回:wu
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void doCXSMAlertShow(String aaaa,String eqid,String deviceid){
        LOG.I(TAG, "[RYAN] doCXSMAlertShow");

        DeviceDAO deviceDAO = new DeviceDAO(this);
        EquipDAO equipDAO = new EquipDAO(this);
        String draw = aaaa.substring(4,6);
        int battery = Integer.parseInt(aaaa.substring(2,4),16);
        String ds = "";
        if("12".equals(draw)){
            ds = getResources().getString(R.string.cxalert2);
        }else if("15".equals(draw)){
            ds = getResources().getString(R.string.cxalert5);
        }else if("17".equals(draw)){
            ds = getResources().getString(R.string.cxalert7);
        }else if("19".equals(draw)){
            ds = getResources().getString(R.string.cxalert9);
        }else if("1B".equals(draw)){
            ds = getResources().getString(R.string.cxalert11);
        }else if(battery<15){
            //电池小于15%也报低电压
            ds = getResources().getString(R.string.low_battery);
        }
        String place = "";
        String gateway = "";
        String title = "";
        EquipmentBean equipmentBean = equipDAO.findByeqid(eqid,deviceid);
        MyDeviceBean deviceBean = deviceDAO.findByDeviceid(deviceid);
        boolean currentgateway = ConnectionPojo.getInstance().deviceTid.equals(deviceid)?true:false;

        LOG.I(TAG, "[RYAN] doCXSMAlertShow 111");

        if(deviceBean == null){
            gateway = getResources().getString(R.string.my_home);
        }else{
            gateway = deviceBean.getDeviceName();
            if("报警器".equals(gateway)){
                gateway = getResources().getString(R.string.my_home);
            }
        }

        if(equipmentBean==null){
            place = getResources().getString(R.string.cxsmalarm) + eqid;
        }else{
            String eqname = equipmentBean.getEquipmentName();
            if(TextUtils.isEmpty(eqname)){
                place = getResources().getString(R.string.cxsmalarm) + eqid;
            }else{
                place = eqname;
            }
        }

        if(currentgateway){
            title = String.format(getResources().getString(R.string.now_gateway_eq_is_happen_has_eq),gateway,deviceid.substring(deviceid.length()-4),place,ds);
        }else{
            title = String.format(getResources().getString(R.string.other_gateway_eq_is_happen_has_eq),gateway,deviceid.substring(deviceid.length()-4),place,ds);
        }

        LOG.I(TAG, "[RYAN] doCXSMAlertShow 222");

        if(ecAlertDialog==null||!ecAlertDialog.isShowing()){
            ecAlertDialog = ECAlertDialog.buildAlert(MyApplication.getActivity(), title,
                    getResources().getString(R.string.btn_silence),
                    getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sed.sendGateWaySilence();
                            try {
                                UnitTools.stopMusic(SiterService.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                UnitTools.stopMusic(SiterService.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            ecAlertDialog.setCancelable(false);
            ecAlertDialog.setCanceledOnTouchOutside(false);
            ecAlertDialog.show();
        }
        try {
            UnitTools.playNotifycationMusic(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * doLockAlertShow:
     * 作者：Henry on 2017/3/13 15:48
     * 邮箱：xuejunju_4595@qq.com
     * 参数:aaaa:devicestatus,eqid:设备id,deviceid:网关id
     * 返回:wu
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void doLockAlertShow(String aaaa,String eqid,String deviceid){
        DeviceDAO deviceDAO = new DeviceDAO(this);
        EquipDAO equipDAO = new EquipDAO(this);
        String draw = aaaa.substring(4,6);
        int battery = Integer.parseInt(aaaa.substring(2,4),16);
        String ds = "";
        if("10".equals(draw)){
            ds = getResources().getString(R.string.illegal_operation);
        }else if("20".equals(draw)){
            ds = getResources().getString(R.string.dismantle);
        }else if("30".equals(draw)){
            ds = getResources().getString(R.string.coercion);
        }else if(battery<15){
            //电池小于15%也报低电压
            ds = getResources().getString(R.string.low_battery);
        }
        String place = "";
        String gateway = "";
        String title = "";
        EquipmentBean equipmentBean = equipDAO.findByeqid(eqid,deviceid);
        MyDeviceBean deviceBean = deviceDAO.findByDeviceid(deviceid);
        boolean currentgateway = ConnectionPojo.getInstance().deviceTid.equals(deviceid)?true:false;


        if(deviceBean == null){
            gateway = getResources().getString(R.string.my_home);
        }else{
            gateway = deviceBean.getDeviceName();
            if("报警器".equals(gateway)){
                gateway = getResources().getString(R.string.my_home);
            }
        }

        if(equipmentBean==null){
            place = getResources().getString(R.string.lock) + eqid;
        }else{
            String eqname = equipmentBean.getEquipmentName();
            if(TextUtils.isEmpty(eqname)){
                place = getResources().getString(R.string.lock) + eqid;
            }else{
                place = eqname;
            }
        }

        if(currentgateway){
            title = String.format(getResources().getString(R.string.now_gateway_eq_is_happen_has_eq),gateway,deviceid.substring(deviceid.length()-4),place,ds);
        }else{
            title = String.format(getResources().getString(R.string.other_gateway_eq_is_happen_has_eq),gateway,deviceid.substring(deviceid.length()-4),place,ds);
        }



        if(ecAlertDialog==null||!ecAlertDialog.isShowing()){
            ecAlertDialog = ECAlertDialog.buildAlert(MyApplication.getActivity(), title,
                    getResources().getString(R.string.btn_silence),
                    getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sed.sendGateWaySilence();
                            try {
                                UnitTools.stopMusic(SiterService.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                UnitTools.stopMusic(SiterService.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            ecAlertDialog.setCancelable(false);
            ecAlertDialog.setCanceledOnTouchOutside(false);
            ecAlertDialog.show();
        }
        try {
            UnitTools.playNotifycationMusic(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * resoleLocalAlertPushInfo:选择的推送帧解析
     * 作者：Henry on 2017/3/13 14:24
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     */
    private void resolveAlertPushInfo(String info,String deviceid){
        try {
            EquipDAO equipDAO = new EquipDAO(this);
            String type = info.substring(4,6);
            if("AC".equals(type)){
                String mid = String.valueOf(Integer.parseInt(info.substring(6,8),16));
                doSceneAlertShow(mid,deviceid);
            }else if("AD".equals(type)) {
                ApplicationInfo applicationInfo = resolveData.updateEqFromPush(info,deviceid);

                if(applicationInfo!=null){
                      String status="";
                    if("0".equals(applicationInfo.getEqid())){
                        if("00000000".equals(applicationInfo.getState())){
                            status = getResources().getString(R.string.electric_city_break_off);
                        }else if("00000001".equals(applicationInfo.getState())){
                            status = getResources().getString(R.string.electric_city_normal);
                        }else if("00000002".equals(applicationInfo.getState())){
                            status = getResources().getString(R.string.battery_normal);
                        }else if("00000003".equals(applicationInfo.getState())){
                            status = getResources().getString(R.string.battery_low);
                        }
                        doGatewayAlertShow(status,deviceid);
                    }else{
                        int i = equipDAO.isIdExists(applicationInfo.getEqid(),deviceid);
                        if (i >= 1) {   //need check
                            resolveData.updateEq(deviceid,applicationInfo.getEqid(),applicationInfo.getEquipmentDesc(),applicationInfo.getState());

                            if(ConnectionPojo.getInstance().deviceTid.equals(deviceid)){
                                STEvent stEvent = new STEvent();
                                stEvent.setRefreshevent(5);
                                stEvent.setCurrent_deviceid(deviceid);
                                stEvent.setEq_id(applicationInfo.getEqid());
                                stEvent.setEq_type(applicationInfo.getEquipmentDesc());
                                stEvent.setEq_status(applicationInfo.getState());
                                EventBus.getDefault().post(stEvent);
                            }


                            if (NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(applicationInfo.getEquipmentDesc()))) {
                                doCXSMAlertShow(applicationInfo.getState(), applicationInfo.getEqid(), deviceid);
                            }else if(NameSolve.LOCK.equals(NameSolve.getEqType(applicationInfo.getEquipmentDesc()))){
                                doLockAlertShow(applicationInfo.getState(), applicationInfo.getEqid(), deviceid);
                            } else {
                                Log.i(TAG, "resolveAlertPushInfo: ");
                                doAlertShow(applicationInfo.getState(),applicationInfo.getEquipmentDesc(),applicationInfo.getEqid(),deviceid);
                            }

                        }else{
                            LOG.I(TAG,"data is not exist");
                        }
                    }


                }else{
                    LOG.I(TAG,"applicationInfo is null");
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * doSceneAlertShow:
     * 作者：Henry on 2017/3/13 15:26
     * 邮箱：xuejunju_4595@qq.com
     * 参数:mid 代表情景id, flag为true标识当前网关，false代表其他未选择的网关，deviceid为网关id;
     * 返回:
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void doSceneAlertShow(String mid,String deviceid){
        try {
            SceneDAO SED = new SceneDAO(this);
            DeviceDAO deviceDAO = new DeviceDAO(this);
            boolean current_gateway = false;
            String SceneName = "";
            String dname = "";
            String devidlast = "";

            current_gateway = ConnectionPojo.getInstance().deviceTid.equals(deviceid)?true:false;

            MyDeviceBean myDeviceBean =  deviceDAO.findByDeviceid(deviceid);

            if(myDeviceBean == null){
                dname = getResources().getString(R.string.my_home);
            }else{
                dname =myDeviceBean.getDeviceName();
            }
            devidlast = deviceid.substring(myDeviceBean.getDevTid().length()-4);
            SceneName = SED.findScenceBymid(mid,deviceid).getName();

            if("报警器".equals(dname)){
                dname = getResources().getString(R.string.my_home);
            }

            String title = "";
            if(current_gateway){
                if(TextUtils.isEmpty(SceneName)){
                    title = String.format(getResources().getString(R.string.now_gateway_scene_is_happen_has_no_scene),dname,devidlast,mid);
                }else{
                    title = String.format(getResources().getString(R.string.now_gateway_scene_is_happen_has_scene),dname,devidlast,SceneName);
                }
            }else{
                if(TextUtils.isEmpty(SceneName)){
                    title = String.format(getResources().getString(R.string.other_gateway_scene_is_happen_has_no_scene),dname,devidlast,mid);
                }else{
                    title = String.format(getResources().getString(R.string.other_gateway_scene_is_happen_has_scene),dname,devidlast,SceneName);
                }
            }

            if(ecAlertDialog==null||!ecAlertDialog.isShowing()){
                ecAlertDialog = ECAlertDialog.buildAlert(MyApplication.getActivity(),
                        title,
                        getResources().getString(R.string.btn_silence),
                        getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sed.sendGateWaySilence();
                                try {
                                    UnitTools.stopMusic(SiterService.this);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    UnitTools.stopMusic(SiterService.this);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                ecAlertDialog.setCancelable(false);
                ecAlertDialog.setCanceledOnTouchOutside(false);
                ecAlertDialog.show();
            }
            try {
                UnitTools.playNotifycationMusic(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static boolean isTimer_of_sync_en() {
        return timer_of_sync_en;
    }

    public  void setTimer_of_sync_en(boolean timer_of_sync_en) {
        this.timer_of_sync_en = timer_of_sync_en;
    }

    public int getCount_update_gateway() {
        return count_update_gateway;
    }

    public void setCount_update_gateway(int count_update_gateway) {
        this.count_update_gateway = count_update_gateway;
    }

    public boolean isTimer_of_sync_en_gateway() {
        return timer_of_sync_en_gateway;
    }

    public void setTimer_of_sync_en_gateway(boolean timer_of_sync_en_gateway) {
        this.timer_of_sync_en_gateway = timer_of_sync_en_gateway;
    }
}
