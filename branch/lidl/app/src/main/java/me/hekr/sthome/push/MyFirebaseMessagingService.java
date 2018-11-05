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
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import me.hekr.sdk.Hekr;
import me.hekr.sthome.R;
import me.hekr.sthome.history.HistoryDataHandler;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.tools.NameSolve;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        try {
            Log.i(TAG, "Message notification payload: " + remoteMessage.getNotification());

            if(TextUtils.isEmpty(Hekr.getHekrUser().getToken())){

                return;
            }
            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.i(TAG, "Message data payload: " + remoteMessage.getData());

                PendingIntent pendingIntent = null;
                NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                int current_dev = 0;
                String action = null;
                DeviceDAO deviceDAO = new DeviceDAO(this);

                JSONObject jsonObject = new JSONObject(remoteMessage.getData());

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
                        Log.i(TAG,"code error");
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
                Vibrator vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
                long [] pattern = {300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400}; // 停止 开启 停止 开启
                vibrator.vibrate(pattern,-1); //重复两次上面的pattern 如果只想震动一次，index设为-1
                manager.notify((int)System.currentTimeMillis(),notification);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.i(TAG, "Short lived task is done.");
    }


    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.i(TAG,"onDeletedMessages");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }
}
