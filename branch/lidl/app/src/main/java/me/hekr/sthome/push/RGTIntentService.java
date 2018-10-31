package me.hekr.sthome.push;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.litesuits.android.log.Log;

import org.json.JSONException;
import org.json.JSONObject;

import me.hekr.sthome.R;
import me.hekr.sthome.history.HistoryDataHandler;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.NoticeDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.tools.NameSolve;

/**
 * Created by jishu0001 on 2017/2/21.
 */

public class RGTIntentService extends GTIntentService {
    private static final String ACTION_INTENT_TEST = "com.sunny";
    private NoticeDAO ND;
    public RGTIntentService(){
//        AlarmNotification alarmNotification = new AlarmNotification(context);
//        IntentFilter filter = new IntentFilter(ACTION_INTENT_TEST);
//        registerReceiver(alarmNotification, filter);
    }
    /**
     * 为了观察透传数据变化.
     */
    private static int cnt;

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        android.util.Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.d(TAG, "receiver payload = " + data);
            // 测试消息为了观察数据变化
            if (data.equals("收到一条透传测试消息")) {
                data = data + "-" + cnt;
                cnt++;
            }
            sendMessage(data, 0);
        }

        Log.d(TAG, "----------------------------------------------------------------------------------------------");
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        android.util.Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);

        sendMessage(clientid, 1);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        android.util.Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        String text = "设置标签失败, 未知异常";
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = "还未登陆成功";
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = "该应用已经在黑名单中,请联系售后支持!";
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = "已存 tag 超过限制";
                break;

            default:
                break;
        }

        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + text);
    }

    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sendMessage(String data, int what) {
        switch (what){
            case 0:
                Log.i(TAG,"data+++++"+data);
                try {
                    if(TextUtils.isEmpty(HekrUserAction.getInstance(this).getJWT_TOKEN())){
                        return;
                    }
                    // Check if message contains a data payload.
                        PendingIntent pendingIntent = null;
                        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                        int current_dev = 0;
                        String action = null;
                        DeviceDAO deviceDAO = new DeviceDAO(this);
                        org.json.JSONObject jsonObject = new org.json.JSONObject(data);

                        String devid = null;

                        devid = jsonObject.getString("devTid");


                        if(devid.equals(deviceDAO.findByChoice(1).getDevTid())){
                            current_dev = 1;
                        }else{
                            current_dev = 0;
                        }


                        String devname = deviceDAO.findByDeviceid(devid).getDeviceName();
                        if("报警器".equals(devname)){
                            devname = getResources().getString(R.string.my_home);
                        }
                        com.litesuits.android.log.Log.i(TAG,"devname+++++"+devname);
                        if(jsonObject.has("login") && jsonObject.getBoolean("login")==true){
                            action = getResources().getString(R.string.gateway_login);
                        }else if(jsonObject.has("loginout") && jsonObject.getBoolean("loginout")==true){
                            action = getResources().getString(R.string.gateway_login_out);
                        }else{

                            Intent intent1 = new Intent(this, MainActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent1.putExtra("current_dev",current_dev);

                            pendingIntent = PendingIntent.getActivity(this, 35, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                            String answer_content2 = jsonObject.getString("data");
                            JSONObject jsonObject_in = new JSONObject(answer_content2);
                            String answer_content = jsonObject_in.getString("answer_content");
                            if(answer_content.length()>6) {
                                if ("AC".equals(answer_content.substring(4, 6))) {
                                    String mid = String.valueOf(Integer.parseInt(answer_content.substring(6,8),16));
                                    SceneDAO sceneDAO = new SceneDAO(this);
                                    SceneBean sceneBean = sceneDAO.findScenceBymid(mid,devid);
                                    if(sceneBean!=null && !TextUtils.isEmpty(sceneBean.getName())){
                                        action = getResources().getString(R.string.scene) + ":(" + sceneBean.getName()+")" + getResources().getString(R.string.trigger);
                                    }else{
                                        action = getResources().getString(R.string.scene) + ":(id = " + mid +")" + getResources().getString(R.string.trigger);
                                    }
                                } else if ("AD".equals(answer_content.substring(4, 6))) {
                                    String eqid = String.valueOf(Integer.parseInt(answer_content.substring(6,10),16));
                                    String type = answer_content.substring(10,14);
                                    String status = answer_content.substring(14,22);
                                    String alertinfo = HistoryDataHandler.getAlert(this,type,status);
                                    EquipDAO equipDAO = new EquipDAO(this);
                                    EquipmentBean equipmentBean = equipDAO.findByeqid(eqid,devid);
                                    if(equipmentBean != null && !TextUtils.isEmpty(equipmentBean.getEquipmentName())){
                                        action = equipmentBean.getEquipmentName()+alertinfo;
                                    }else{
                                        action = NameSolve.getDefaultName(this,type,eqid)+alertinfo;

                                    }
                                }
                            }else {
                                android.util.Log.i(TAG,"code error");
                            action = getResources().getString(R.string.receive_one_notice);
                        }

                        }


                        // 通过Notification.Builder来创建通知，注意API Level
                        // API16之后才支持
                        Notification.Builder builder= new Notification.Builder(this)
                                .setContentTitle((current_dev==1?getResources().getString(R.string.current_gateway):getResources().getString(R.string.other_gateway)) +":"+devname)
                                .setContentText(action)
                                .setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent)
                                .setContentIntent(pendingIntent);
                        //兼容nexusandroid5.0
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            builder.setSmallIcon(R.mipmap.ic_launcher_alpha);
                        } else {
                            builder.setSmallIcon(R.mipmap.ic_launcher);
                        }
                        Notification notification = builder.build(); // 需要注意build()是在API
                        // level16及之后增加的，API11可以使用getNotificatin()来替代
                        notification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
                        notification.defaults |= Notification.DEFAULT_SOUND;
                        manager.notify((int)System.currentTimeMillis(),notification);
                    Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    long [] pattern = {300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400}; // 停止 开启 停止 开启
                    vibrator.vibrate(pattern,-1); //重复两次上面的pattern 如果只想震动一次，index设为-1
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 1:
                break;
            default:
                break;
        }


    }


}
