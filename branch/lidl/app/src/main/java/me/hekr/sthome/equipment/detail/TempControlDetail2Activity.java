package me.hekr.sthome.equipment.detail;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.android.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.hekr.sdk.utils.CacheUtil;
import me.hekr.sthome.AddDeviceActivity;
import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.ColorArcProgressBar;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.commonBaseView.ProgressDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.event.ThcheckEvent;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.EmojiFilter;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.tools.SiterSDK;
import me.hekr.sthome.tools.UnitTools;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by jishu0001 on 2016/10/9.
 */
public class TempControlDetail2Activity extends AppCompatActivity implements OnClickListener{
    private static final String TAG = "THcheckDetail";
    private ImageView signal,quatity,deviceLogo;
    private TextView showStatus;
    private EquipmentBean device;
    private EquipDAO ED;
    private ImageView back_img;
    private ImageView valve_status,tongsuo_status,window_status;
    private ImageView timer_mode,handle_mode,fangdong_mode;
    private TextView  edt_txt,eq_name,battay_text;
    private RelativeLayout root;
    private ECAlertDialog alertDialog,install_alertDialog;
    private SendEquipmentData sd;
    private ColorArcProgressBar bar_setting_temp;
    private CheckBox window_check,valve_check,tongsuo_check;

    private int count_s;
    private boolean flag_set = false;
    private int setting_mode = -1;
    private Timer timer_set;
    private UpdateCommandTask timertask;
    private Button btnConfirm;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_temp_control2);
        initData();
        initViewGuider();
        timer_set = new Timer();
        timertask = new UpdateCommandTask();
        timer_set.schedule(timertask,0,1000);
    }


    private void initData() {
        EventBus.getDefault().register(this);
        sd = new SendEquipmentData(this) {
            @Override
            protected void sendEquipmentDataFailed() {

            }

            @Override
            protected void sendEquipmentDataSuccess() {

            }
        };
        try{
            device = (EquipmentBean) this.getIntent().getSerializableExtra("device");
        }catch(Exception e){
            Log.i("Detail socket","device is null");
        }


    }



    private void initViewGuider() {
        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        window_check = (CheckBox) findViewById(R.id.window_check);
        valve_check = (CheckBox)findViewById(R.id.valve_check);
        tongsuo_check = (CheckBox)findViewById(R.id.tongsuo_btn);
        valve_status = (ImageView)findViewById(R.id.valve_status);
        window_status = (ImageView)findViewById(R.id.window_status);
        tongsuo_status = (ImageView)findViewById(R.id.tongsuo_status);
        timer_mode = (ImageView)findViewById(R.id.timer_mode);
        handle_mode = (ImageView)findViewById(R.id.handle_mode);
        fangdong_mode = (ImageView)findViewById(R.id.fangdong_mode);
        timer_mode.setOnClickListener(this);
        handle_mode.setOnClickListener(this);
        fangdong_mode.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        bar_setting_temp = (ColorArcProgressBar)findViewById(R.id.setting_temp);
        battay_text = (TextView)findViewById(R.id.quantitydesc);
        back_img   = (ImageView)findViewById(R.id.goBack);
        bar_setting_temp.setOnClickListener(this);
        back_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edt_txt    = (TextView)findViewById(R.id.detailEdit);
        edt_txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ECListDialog ecListDialog = new ECListDialog(TempControlDetail2Activity.this,getResources().getStringArray(R.array.DeivceOperation));
                ecListDialog.setTitle(getResources().getString(R.string.manage));
                ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(Dialog d, int position) {

                        switch (position){
                            case 0:
                                SendCommand.Command = SendCommand.REPLACE_EQUIPMENT;
                                sd.replaceEquipment(device.getEqid());
                                Intent intent =new Intent(TempControlDetail2Activity.this,AddDeviceActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("eqid",device.getEqid());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                break;
                            case 1:
                                ECAlertDialog elc = ECAlertDialog.buildAlert(TempControlDetail2Activity.this,getResources().getString(R.string.delete_or_not), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SendCommand.Command = SendCommand.DELETE_EQUIPMENT_DETAIL;
                                        sd.deleteEquipment(device.getEqid());
                                    }
                                });
                                elc.show();
                                break;
                            case 2:
                                alertDialog = ECAlertDialog.buildAlert(TempControlDetail2Activity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.setDismissFalse(true);
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                                        String newname = text.getText().toString().trim();

                                        if(!TextUtils.isEmpty(newname)){

                                            try {
                                                if(newname.getBytes("GBK").length<=15){
                                                    if(!EmojiFilter.containsEmoji(newname)) {
                                                        alertDialog.setDismissFalse(true);
                                                        eq_name.setText(newname);
                                                        updateName(newname);
                                                        String ds = CoderUtils.getAscii(newname);
                                                        String dsCRC = ByteUtil.CRCmaker(ds);
                                                        SendCommand.Command = SendCommand.MODIFY_EQUIPMENT_NAME;
                                                        sd.modifyEquipmentName(device.getEqid(), ds + dsCRC);
                                                    }else {
                                                        alertDialog.setDismissFalse(false);
                                                        Toast.makeText(TempControlDetail2Activity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    alertDialog.setDismissFalse(false);
                                                    Toast.makeText(TempControlDetail2Activity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else{
                                            alertDialog.setDismissFalse(false);
                                            Toast.makeText(TempControlDetail2Activity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                                alertDialog.setContentView(R.layout.edit_alert);
                                alertDialog.setTitle(getResources().getString(R.string.update_name));
                                EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                                text.setText(device.getEquipmentName());
                                text.setSelection(device.getEquipmentName().length());

                                alertDialog.show();

                                break;
                            default:
                                break;
                        }

                    }
                });
                ecListDialog.show();
            }
        });
        root = (RelativeLayout)findViewById(R.id.root);
        //沉浸式设置支持API19
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = UnitTools.getStatusBarHeight(this);
            root.setPadding(0,top,0,0);
        }
        showStatus = (TextView) findViewById(R.id.showStatus);
        signal = (ImageView) findViewById(R.id.signalPosition);
        quatity = (ImageView) findViewById(R.id.quantityPosition);
        deviceLogo = (ImageView) findViewById(R.id.devicePosition);
        deviceLogo.setImageResource(R.drawable.detail14);
        eq_name = (TextView)findViewById(R.id.eq_name);
        eq_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        eq_name.setSelected(true);
        eq_name.setFocusable(true);
        eq_name.setFocusableInTouchMode(true);
        eq_name.setMarqueeRepeatLimit(-1);
        if(TextUtils.isEmpty(device.getEquipmentName())){
            eq_name.setText(getResources().getString(R.string.temp_controler)+device.getEqid());
        }else{
            eq_name.setText(device.getEquipmentName());
        }

        doStatusShow(device.getState());
        showBattery();
    }

    private void updateName(String edit) {
        if( !device.getEquipmentName().equals(edit)){

            device.setEquipmentName(edit);
            ED = new EquipDAO(this);
            try {
                ED.updateName(device);
            }catch (Exception e){
                Toast.makeText(TempControlDetail2Activity.this,getResources().getString(R.string.name_is_repeat),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doStatusShow(String aaaa) {
        try {

            String signal1 = aaaa.substring(0,2);
            String quantity1 = aaaa.substring(2,4);
            String status1 = aaaa.substring(4,6);
            String status2 = aaaa.substring(6,8);
            showStatus.setText(getResources().getString(R.string.normal));
            root.setBackgroundColor(getResources().getColor(R.color.dark_blue));
            int qqqq = Integer.parseInt(quantity1,16);
            if( qqqq <= 15 ){
                root.setBackgroundColor(getResources().getColor(R.color.device_warn));
                showStatus.setText(getResources().getString(R.string.low_battery));
            }
            quatity.setImageResource(ShowBascInfor.choseQPic(qqqq));
            battay_text.setText(ShowBascInfor.choseLNum(qqqq));
            if(signal1 != null){
                byte d = (byte)Integer.parseInt(signal1,16);
                signal.setImageResource(ShowBascInfor.choseSPic(ByteUtil.convertByte2HexString((byte)(d&0x07))));
            }

            byte ds = (byte)Integer.parseInt(status1,16);
            byte ds2 = (byte)Integer.parseInt(status2,16);
            byte ds3 = (byte)Integer.parseInt(signal1,16);

            int shineng_window2 = (((byte)((0x80) & ds3))==0?0:1);
            int shineng_valve2 =  (((byte)((0x40) & ds3))==0?0:1);
            int  shishi_temp2= (int)((0x3F) & (ds2>>2));
            int mode2 = (int)((0x03) & (ds2));
            byte status_window2 = (byte)((0x80) & ds);
            byte status_valve2 = (byte)((0x40) & ds);
            int status_tongsuo = (((byte)((0x20) & ds3))==0?0:1);
            int xiaoshu = (((byte)((0x20) & ds))==0?0:1);
            int sta =  ((0x1F) & ds);
            float setting_temp = ((float) sta)+(xiaoshu==0?0f:0.5f);


            if (status_tongsuo != 0) {
                tongsuo_status.setImageResource(R.drawable.ui361a_suo_select);
                tongsuo_check.setChecked(true);
            } else {
                tongsuo_status.setImageResource(R.drawable.ui361a_suo_disable);
                tongsuo_check.setChecked(false);
            }

            switch (mode2){
                case 0:
                    setting_mode = 0;
                    timer_mode.setImageResource(R.drawable.ui361a_timer);
                    handle_mode.setImageResource(R.drawable.ui361a_handle);
                    fangdong_mode.setImageResource(R.drawable.ui361a_fangdong_select);
                    bar_setting_temp.setMaxValues(15f);
                    if(install_alertDialog!=null && install_alertDialog.isShowing()){
                        install_alertDialog.dismiss();
                    }
                    break;
                case 1:
                    setting_mode = 1;
                    timer_mode.setImageResource(R.drawable.ui361a_timer_select);
                    handle_mode.setImageResource(R.drawable.ui361a_handle);
                    fangdong_mode.setImageResource(R.drawable.ui361a_fangdong);
                    bar_setting_temp.setMaxValues(30f);
                    if(install_alertDialog!=null && install_alertDialog.isShowing()){
                        install_alertDialog.dismiss();
                    }
                    break;
                case 2:
                    setting_mode = 2;
                    timer_mode.setImageResource(R.drawable.ui361a_timer);
                    handle_mode.setImageResource(R.drawable.ui361a_handle_select);
                    fangdong_mode.setImageResource(R.drawable.ui361a_fangdong);
                    bar_setting_temp.setMaxValues(30f);
                    if(install_alertDialog!=null && install_alertDialog.isShowing()){
                        install_alertDialog.dismiss();
                    }
                    break;
                case 3:
                    showStatus.setText(getResources().getString(R.string.install_mode));
                    setting_mode = 2;
                    timer_mode.setImageResource(R.drawable.ui361a_timer);
                    handle_mode.setImageResource(R.drawable.ui361a_handle_select);
                    fangdong_mode.setImageResource(R.drawable.ui361a_fangdong);
                    bar_setting_temp.setMaxValues(30f);
                    if((install_alertDialog == null || !install_alertDialog.isShowing()) && setting_temp <= 30.0f){
                        install_alertDialog = ECAlertDialog.buildPositiveAlert(this,R.string.gs361_install_construction,null);
                        install_alertDialog.setCancelable(true);
                        install_alertDialog.setCanceledOnTouchOutside(false);
                        install_alertDialog.show();
                    }
                    break;
            }

            if(setting_temp > 30.0f){
                bar_setting_temp.setCurrentValues(0f);
                bar_setting_temp.setSubCurrentValues(0f);
                showStatus.setText(getResources().getString(R.string.offline));
                root.setBackgroundColor(getResources().getColor(R.color.device_offine));
            }else{
                ED= new EquipDAO(this);
                if(mode2==0){
                    ED.updateFangTemp(device.getEqid(), device.getDeviceid(),String.valueOf(setting_temp));
                }else if(mode2 == 1){
                    ED.updateAutoTemp(device.getEqid(), device.getDeviceid(),String.valueOf(setting_temp));
                }else if(mode2 == 2){
                    ED.updateHandTemp(device.getEqid(), device.getDeviceid(),String.valueOf(setting_temp));
                }

                bar_setting_temp.setCurrentValues(setting_temp);
                bar_setting_temp.setSubCurrentValues(shishi_temp2);

                if(shineng_window2!=0) {
                    window_check.setChecked(true);
                    if (status_window2 != 0) {
                        window_status.setImageResource(R.drawable.ui361a_window_open);
                    } else {
                        window_status.setImageResource(R.drawable.ui361a_window_normal);
                    }
                }else {
                    window_status.setImageResource(R.drawable.ui361a_window_disable);
                    window_check.setChecked(false);
                }
                if(shineng_valve2!=0) {
                    valve_check.setChecked(true);
                    if (status_valve2 != 0) {
                        valve_status.setImageResource(R.drawable.ui361a_famen_error);
                    } else {
                        valve_status.setImageResource(R.drawable.ui361a_famen_normal);
                    }
                }else{
                    valve_status.setImageResource(R.drawable.ui361a_famen_disable);
                    valve_check.setChecked(false);
                }


            }




//            if(cache_setting_temp!=0&&cache_setting_xiaoshu>=0&&cahce_tongsuo>=0&&cahce_valve>=0&&cahce_window>=0
//                &&cache_mode >= 0
//                    &&cache_setting_temp == sta && cache_setting_xiaoshu==xiaoshu
//                    && cache_mode == mode2
//                    && cahce_tongsuo == status_tongsuo
//                    && cahce_valve == shineng_valve2
//                    && cahce_window == shineng_window2){
//               handler.sendEmptyMessage(1);
//
//            }
//            cache_setting_xiaoshu = -1;
//            cache_setting_temp = 0;
//            cahce_tongsuo = -1;
//            cache_mode = -1;
//            cahce_window = -1;
//            cahce_valve = -1;

        }catch (Exception e){
            showStatus.setText(getResources().getString(R.string.offline));
            quatity.setImageResource(ShowBascInfor.choseQPic(100));
            battay_text.setText(ShowBascInfor.choseLNum(100));
            bar_setting_temp.setCurrentValues(0f);
            bar_setting_temp.setSubCurrentValues(0f);
            setting_mode = 2;
            timer_mode.setImageResource(R.drawable.ui361a_timer);
            handle_mode.setImageResource(R.drawable.ui361a_handle_select);
            fangdong_mode.setImageResource(R.drawable.ui361a_fangdong);
            if(install_alertDialog!=null && install_alertDialog.isShowing()){
                install_alertDialog.dismiss();
            }
        }

    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(timer_set!=null){
            timer_set.cancel();
            timer_set = null;
            timertask = null;
        }
    }
    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent() == SendCommand.MODIFY_EQUIPMENT_NAME){
            SendCommand.clearCommnad();
            Toast.makeText(TempControlDetail2Activity.this,getResources().getString(R.string.update_name_success),Toast.LENGTH_SHORT).show();
        }else if(event.getEvent() == SendCommand.DELETE_EQUIPMENT_DETAIL){
            SendCommand.clearCommnad();
            Toast.makeText(TempControlDetail2Activity.this,getResources().getString(R.string.delete_success),Toast.LENGTH_SHORT).show();
            ED = new EquipDAO(TempControlDetail2Activity.this);
            ED.deleteByEqid(device);
            ThcheckEvent thcheckEvent = new ThcheckEvent();
            EventBus.getDefault().post(thcheckEvent);
            finish();
        }else if(event.getEvent()==SendCommand.EQUIPMENT_CONTROL){
            SendCommand.clearCommnad();
            updateDeviceStatus();
            handler.sendEmptyMessage(1);
            //Toast.makeText(TempControlDetail2Activity.this,getResources().getString(R.string.data_syncing),Toast.LENGTH_SHORT).show();
        }

        if(event.getRefreshevent()==5){
            String new_status = event.getEq_status();
            String new_eqid = event.getEq_id();
            String new_eqtype = event.getEq_type();
            String new_deviceid = event.getCurrent_deviceid();

            if(new_deviceid.equals(device.getDeviceid())&&new_eqid.equals(device.getEqid())&&new_eqtype.equals(device.getEquipmentDesc())){
                device.setState(new_status);
                doStatusShow(new_status);
            }
        }
    }


    private void showBattery(){
        try {
            if("1".equals(device.getEquipmentDesc().substring(0,1))){
                battay_text.setVisibility(View.GONE);
                quatity.setVisibility(View.GONE);
            }else{
                battay_text.setVisibility(View.VISIBLE);
                quatity.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            Log.i(TAG,"data err");
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.timer_mode:
                if(setting_mode==1){
                    Intent intent = new Intent(this,TempControlTimerListActivity.class);
                    intent.putExtra("eqid",device.getEqid());
                    startActivity(intent);
                }else {
                    setting_mode = 1;
                    ED = new EquipDAO(this);

                    bar_setting_temp.setMaxValues(30f);
                    String autotemp = ED.findTempByAutoModel(device.getDeviceid(),device.getEqid());
                    if(TextUtils.isEmpty(autotemp)){
                        bar_setting_temp.setCurrentValues(21f);
                    }else {
                        bar_setting_temp.setCurrentValues(Float.parseFloat(autotemp));
                    }
                    timer_mode.setImageResource(R.drawable.ui361a_timer_select);
                    handle_mode.setImageResource(R.drawable.ui361a_handle);
                    fangdong_mode.setImageResource(R.drawable.ui361a_fangdong);
                }

                break;
            case R.id.handle_mode:
                setting_mode = 2;
                bar_setting_temp.setMaxValues(30f);
                String handtemp = ED.findTempByHandModel(device.getDeviceid(),device.getEqid());
                if(TextUtils.isEmpty(handtemp)){
                    bar_setting_temp.setCurrentValues(21f);
                }else {
                    bar_setting_temp.setCurrentValues(Float.parseFloat(handtemp));
                }
                timer_mode.setImageResource(R.drawable.ui361a_timer);
                handle_mode.setImageResource(R.drawable.ui361a_handle_select);
                fangdong_mode.setImageResource(R.drawable.ui361a_fangdong);
                break;
            case R.id.fangdong_mode:
                setting_mode = 0;
                bar_setting_temp.setMaxValues(15f);
                String fangtemp = ED.findTempByFangModel(device.getDeviceid(),device.getEqid());
                if(TextUtils.isEmpty(fangtemp)){
                    bar_setting_temp.setCurrentValues(5f);
                }else {
                    bar_setting_temp.setCurrentValues(Float.parseFloat(fangtemp));
                }
                timer_mode.setImageResource(R.drawable.ui361a_timer);
                handle_mode.setImageResource(R.drawable.ui361a_handle);
                fangdong_mode.setImageResource(R.drawable.ui361a_fangdong_select);
                break;
            case R.id.btnConfirm:
                progressDialog = new ProgressDialog(this,R.string.wait);
                progressDialog.show();
                float da = bar_setting_temp.getCurrentValues();
                int settemtp = (int)Math.floor(da);
                int settemtpxiaoshu = (String.valueOf(da).contains(".5")?1:0);
                int valve = (valve_check.isChecked()?1:0);
                int window = (window_check.isChecked()?1:0);
                int tongsuo = (tongsuo_check.isChecked()?1:0);
                flag_set = true;
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(device.getEqid(),getSetFrom(settemtp,settemtpxiaoshu,setting_mode,valve,window,tongsuo));
                break;
        }

    }


    private String getSetFrom(int set_temp,int set_temp_xiaoshu,int mode,int valve,int window,int suo){

        int set_temp_shiji = set_temp;
        byte [] ds= {0x00,0x00};

        ds[0]  = (byte)((window==0?0x00:0x80)|ds[0]);
        ds[0]  = (byte)((valve==0?0x00:0x40)|ds[0]);
        ds[0]  = (byte)((set_temp_xiaoshu==0?0x00:0x20)|ds[0]);
        ds[0]  = (byte)((set_temp_shiji&0x1F)|ds[0]);

        ds[1]  = (byte)((suo==0?0x00:0x04)|ds[1]);
        ds[1]  = (byte)((mode&0x03)|ds[1]);

        String str1 = ByteUtil.convertByte2HexString(ds[0]);
        String str2 = ByteUtil.convertByte2HexString(ds[1]);
        return ((str1+str2+"0000").toUpperCase());
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    flag_set = false;
                    count_s = 0;
                    if(progressDialog!=null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog =null;
                        Toast.makeText(TempControlDetail2Activity.this,getResources().getString(R.string.data_syncing),Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    flag_set = false;
                    count_s = 0;
                    if(progressDialog!=null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog =null;
                    }
                    doStatusShow(device.getState());
                    Toast.makeText(TempControlDetail2Activity.this,getResources().getString(R.string.failed),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    class UpdateCommandTask extends TimerTask {
        @Override
        public void run() {
            if(flag_set) {
                android.util.Log.i(TAG,"设置命令超时计数:"+count_s);
                count_s ++;
            }

            if(count_s >= 5){
                count_s = 0;

                handler.sendEmptyMessage(2);
            }

        }
    }

    private void updateDeviceStatus(){
        try {
            float da = bar_setting_temp.getCurrentValues();
            int settemtp = (int)Math.floor(da);
            int settemtpxiaoshu = (String.valueOf(da).contains(".5")?1:0);
            int valve = (valve_check.isChecked()?1:0);
            int window = (window_check.isChecked()?1:0);
            int tongsuo = (tongsuo_check.isChecked()?1:0);
            String status = device.getState();
            Log.i(TAG,"修改前:"+status);
            byte[]ds = ByteUtil.hexStr2Bytes(status);
            ds[0]  = (byte)(ds[0]&0x7F|(window==0?0x00:0x80));
            ds[0]  = (byte)(ds[0]&0xBF|(valve==0?0x00:0x40));
            ds[0]  = (byte)(ds[0]&0xDF|(tongsuo==0?0x00:0x20));
            ds[2]  = (byte)(ds[2]&0xDF|(settemtpxiaoshu==0?0x00:0x20));
            ds[2]  = (byte)(ds[2]&0xE0|(settemtp&0x1F));
            ds[3]  = (byte)(ds[3]&0xFC|(setting_mode&0x03));
            String str1 = ByteUtil.convertByte2HexString(ds[0]);
            String str2 = ByteUtil.convertByte2HexString(ds[1]);
            String str3 = ByteUtil.convertByte2HexString(ds[2]);
            String str4 = ByteUtil.convertByte2HexString(ds[3]);
            device.setState(str1+str2+str3+str4);
            ED = new EquipDAO(this);
            EquipmentBean applicationInfo = new EquipmentBean();
            applicationInfo.setDeviceid(device.getDeviceid());
            applicationInfo.setEqid(device.getEqid());
            applicationInfo.setEquipmentDesc(device.getEquipmentDesc());
            applicationInfo.setState(str1+str2+str3+str4);
            Log.i(TAG,"修改后:"+str1+str2+str3+str4);
            ED.update(applicationInfo);
        }catch (Exception e){

        }

    }

}
