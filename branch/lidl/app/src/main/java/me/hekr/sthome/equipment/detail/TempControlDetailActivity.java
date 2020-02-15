package me.hekr.sthome.equipment.detail;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.android.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import me.hekr.sthome.AddDeviceActivity;
import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.event.ThcheckEvent;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.EmojiFilter;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.tools.UnitTools;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by jishu0001 on 2016/10/9.
 */
public class TempControlDetailActivity extends AppCompatActivity implements OnClickListener{
    private static final String TAG = "THcheckDetail";
    private ImageView signal,quatity,deviceLogo;
    private TextView showStatus;
    private TextView mode,window_status,valve_status,set_temp,shishi_temp,tongsuo_status;
    private LinearLayout set_temp_btn,window_btn,valve_btn,mode_btn,tongsuo_btn;
    private EquipmentBean device;
    private EquipDAO ED;
    private ImageView back_img;
    private TextView  edt_txt,eq_name,battay_text;
    private LinearLayout root;
    private ECAlertDialog alertDialog;
    private SendEquipmentData sd;
    private ArrayList<String> modes = new ArrayList<>();
    private ArrayList<String> window_check = new ArrayList<>();
    private ArrayList<String> tongsuo_check = new ArrayList<>();
    private ArrayList<String> valve_check = new ArrayList<>();
    private ArrayList<String> set_xiaoshu = new ArrayList<>();
    private ArrayList<String> items_temp = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_temp_control);
        initData();
        initViewGuider();
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

        String[] modes2 = {getResources().getString(R.string.cold_mode),getResources().getString(R.string.auto_mode),getResources().getString(R.string.handle_mode)};
        String[] window_check2 = {getResources().getString(R.string.disable),getResources().getString(R.string.enable)};
        String[] tongsuo_check2 = {getResources().getString(R.string.disable),getResources().getString(R.string.enable)};
        String[] valve_check2 = {getResources().getString(R.string.disable),getResources().getString(R.string.enable)};
        String[] set_xiaoshu2 = {"0","5"};
        for(int i=5;i<31;i++){
            items_temp.add(String.valueOf(i));
        }

        for(int i=0;i<modes2.length;i++){
            modes.add(modes2[i]);
        }

        for(int i=0;i<window_check2.length;i++){
            window_check.add(window_check2[i]);
        }

        for(int i=0;i<tongsuo_check2.length;i++){
            tongsuo_check.add(tongsuo_check2[i]);
        }

        for(int i=0;i<valve_check2.length;i++){
            valve_check.add(valve_check2[i]);
        }

        for(int i=0;i<set_xiaoshu2.length;i++){
            set_xiaoshu.add(set_xiaoshu2[i]);
        }

    }



    private void initViewGuider() {
        battay_text = (TextView)findViewById(R.id.quantitydesc);
        back_img   = (ImageView)findViewById(R.id.goBack);
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
                ECListDialog ecListDialog = new ECListDialog(TempControlDetailActivity.this,getResources().getStringArray(R.array.DeivceOperation));
                ecListDialog.setTitle(getResources().getString(R.string.manage));
                ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(Dialog d, int position) {

                        switch (position){
                            case 0:
                                SendCommand.Command = SendCommand.REPLACE_EQUIPMENT;
                                sd.replaceEquipment(device.getEqid());
                                Intent intent =new Intent(TempControlDetailActivity.this,AddDeviceActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("eqid",device.getEqid());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                break;
                            case 1:
                                ECAlertDialog elc = ECAlertDialog.buildAlert(TempControlDetailActivity.this,getResources().getString(R.string.delete_or_not), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                alertDialog = ECAlertDialog.buildAlert(TempControlDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                                if(newname.getBytes("UTF-8").length<=15){
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
                                                        Toast.makeText(TempControlDetailActivity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    alertDialog.setDismissFalse(false);
                                                    Toast.makeText(TempControlDetailActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else{
                                            alertDialog.setDismissFalse(false);
                                            Toast.makeText(TempControlDetailActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
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
        root = (LinearLayout)findViewById(R.id.root);
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
        mode = (TextView)findViewById(R.id.mode);
        window_status  =(TextView)findViewById(R.id.window_status);
        valve_status  = (TextView)findViewById(R.id.valve_status);
        tongsuo_status = (TextView)findViewById(R.id.tongsuo);
        set_temp = (TextView)findViewById(R.id.set_temp);
        shishi_temp = (TextView)findViewById(R.id.shishi_temp);
        set_temp_btn = (LinearLayout)findViewById(R.id.set_temp_btn);
        window_btn = (LinearLayout)findViewById(R.id.window_status_btn);
        valve_btn  = (LinearLayout)findViewById(R.id.valve_status_btn);
        mode_btn   = (LinearLayout)findViewById(R.id.mode_btn);
        tongsuo_btn = (LinearLayout)findViewById(R.id.tongsuo_btn);
        set_temp_btn.setOnClickListener(this);
        window_btn.setOnClickListener(this);
        valve_btn.setOnClickListener(this);
        mode_btn.setOnClickListener(this);
        tongsuo_btn.setOnClickListener(this);
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
                Toast.makeText(TempControlDetailActivity.this,getResources().getString(R.string.name_is_repeat),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doStatusShow(String aaaa) {
        try {
            root.setBackgroundColor(getResources().getColor(R.color.device_normal));
            String signal1 = aaaa.substring(0,2);
            String quantity1 = aaaa.substring(2,4);
            String status1 = aaaa.substring(4,6);
            String status2 = aaaa.substring(6,8);
            showStatus.setText(getResources().getString(R.string.normal));
            int qqqq = Integer.parseInt(quantity1,16);
            if( qqqq <= 15 ){
                root.setBackgroundColor(getResources().getColor(R.color.device_warn));
                showStatus.setText(getResources().getString(R.string.low_battery));
            }
            quatity.setImageResource(ShowBascInfor.choseQPic(qqqq));
            battay_text.setText(ShowBascInfor.choseLNum(qqqq));
            if(signal1 != null){
               byte d = (byte)Integer.parseInt(signal1,16);
                signal.setImageResource(ShowBascInfor.choseSPic(ByteUtil.convertByte2HexString((byte)(d&0x3F))));
            }

            byte ds = (byte)Integer.parseInt(status1,16);
            byte ds2 = (byte)Integer.parseInt(status2,16);
            byte ds3 = (byte)Integer.parseInt(signal1,16);

            byte shineng_window2 = (byte)((0x80) & ds3);
            byte shineng_valve2 = (byte)((0x40) & ds3);
            int  shishi_temp2= (int)((0x3F) & (ds2>>2));
            int mode2 = (int)((0x03) & (ds2));
            byte status_window2 = (byte)((0x80) & ds);
            byte status_valve2 = (byte)((0x40) & ds);
            byte status_tongsuo = (byte)((0x20) & ds3);
            byte xiaoshu = (byte)((0x20) & ds);
            int sta =  ((0x1F) & ds);
              set_temp.setText(sta+(xiaoshu==0?".0":".5")+"℃");

           if(shineng_window2!=0) {
               if (status_window2 != 0) {
                   window_status.setText(getResources().getString(R.string.open));
               } else {
                   window_status.setText(getResources().getString(R.string.close));
               }
           }else {
               window_status.setText(getResources().getString(R.string.disable));
           }
            if(shineng_valve2!=0) {
                valve_status.setVisibility(View.VISIBLE);
                if (status_valve2 != 0) {
                    valve_status.setText(getResources().getString(R.string.open));
                } else {
                    valve_status.setText(getResources().getString(R.string.close));
                }
            }else{
                valve_status.setText(getResources().getString(R.string.disable));
            }

                if (status_tongsuo != 0) {
                    tongsuo_status.setText(getResources().getString(R.string.open));
                } else {
                    tongsuo_status.setText(getResources().getString(R.string.close));
                }

            switch (mode2){
                case 0:
                    mode.setText(getResources().getString(R.string.cold_mode));
                    break;
                case 1:
                    mode.setText(getResources().getString(R.string.auto_mode));
                    break;
                case 2:
                    mode.setText(getResources().getString(R.string.handle_mode));
                    break;
                case 3:
                    mode.setText(getResources().getString(R.string.install_mode));
                    break;

            }

            shishi_temp.setText(shishi_temp2+"℃");

        }catch (Exception e){
            root.setBackgroundColor(getResources().getColor(R.color.device_offine));
            showStatus.setTextColor(getResources().getColor(R.color.device_offine));
            showStatus.setText(getResources().getString(R.string.offline));
            quatity.setImageResource(ShowBascInfor.choseQPic(100));
            battay_text.setText(ShowBascInfor.choseLNum(100));
        }

    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent() == SendCommand.MODIFY_EQUIPMENT_NAME){
            SendCommand.clearCommnad();
            Toast.makeText(TempControlDetailActivity.this,getResources().getString(R.string.update_name_success),Toast.LENGTH_SHORT).show();
        }else if(event.getEvent() == SendCommand.DELETE_EQUIPMENT_DETAIL){
            SendCommand.clearCommnad();
            Toast.makeText(TempControlDetailActivity.this,getResources().getString(R.string.delete_success),Toast.LENGTH_SHORT).show();
            ED = new EquipDAO(TempControlDetailActivity.this);
            ED.deleteByEqid(device);
            ThcheckEvent thcheckEvent = new ThcheckEvent();
            EventBus.getDefault().post(thcheckEvent);
            finish();
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
             case R.id.set_temp_btn:
                 Intent intent = new Intent(this,TempControlSettingActivity.class);
                 intent.putExtra("device",device);
             startActivity(intent);

//                 alertDialog = ECAlertDialog.buildAlert(TempControlDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         alertDialog.setDismissFalse(true);
//                     }
//                 }, new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         alertDialog.setDismissFalse(true);
//                         WheelView wheelView_temp = (WheelView) alertDialog.getContent().findViewById(R.id.set_temp);
//                         WheelView wheelView_temp_xiaoshu = (WheelView) alertDialog.getContent().findViewById(R.id.set_temp_xiaoshu);
//                         SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
//                         String status = getSetTempFrom(wheelView_temp.getCurrentItem(),wheelView_temp_xiaoshu.getCurrentItem());
//                         Log.i(TAG,"status:"+status);
//                         sd.sendEquipmentCommand(device.getEqid(),status);
//
//                     }
//                 });
//                 alertDialog.setContentView(R.layout.edit_wheel_temp_alert);
//                 alertDialog.setTitle(getResources().getString(R.string.setting_temp));
//                 WheelView wheelView_temp = (WheelView) alertDialog.getContent().findViewById(R.id.set_temp);
//                 WheelView wheelView_temp_xiaoshu = (WheelView) alertDialog.getContent().findViewById(R.id.set_temp_xiaoshu);
//                 wheelView_temp.setAdapter(new NumberAdapter(items_temp,30));
//                 wheelView_temp_xiaoshu.setAdapter(new NumberAdapter(set_xiaoshu,30));
//
//                 try {
//                     String status1 = device.getState().substring(4,6);
//
//
//                     byte ds = (byte)Integer.parseInt(status1,16);
//
//                     byte xiaoshu = (byte)((0x20) & ds);
//                     int sta =  ((0x1F) & ds);
//
//                     if(sta>=5&&sta<=30){
//                         wheelView_temp.setCurrentItem(sta-5);
//                     }else {
//                         wheelView_temp.setCurrentItem(0);
//                     }
//                     wheelView_temp_xiaoshu.setCurrentItem(xiaoshu==0?0:1);
//
//                 }catch (Exception e){
//                     e.printStackTrace();
//                     wheelView_temp.setCurrentItem(0);
//                     wheelView_temp_xiaoshu.setCurrentItem(0);
//                 }
//
//
//
//
//                 alertDialog.show();


                 break;
             case R.id.window_status_btn:
                 Intent intent2 = new Intent(this,TempControlSettingActivity.class);
                 intent2.putExtra("device",device);
                 startActivity(intent2);
//                 alertDialog = ECAlertDialog.buildAlert(TempControlDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         alertDialog.setDismissFalse(true);
//                     }
//                 }, new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         alertDialog.setDismissFalse(true);
//                         WheelView wheelView_win = (WheelView) alertDialog.getContent().findViewById(R.id.tet);
//                         SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
//                         String status = getWindowEnableFrom(wheelView_win.getCurrentItem());
//                         Log.i(TAG,"status:"+status);
//                         sd.sendEquipmentCommand(device.getEqid(),status);
//
//                     }
//                 });
//                 alertDialog.setContentView(R.layout.edit_wheel_alert);
//                 alertDialog.setTitle(getResources().getString(R.string.please_set_window_enable));
//                 WheelView wheelView_win = (WheelView) alertDialog.getContent().findViewById(R.id.tet);
//                 wheelView_win.setAdapter(new NumberAdapter(window_check,30));
//
//                 try {
//                     String signal1 = device.getState().substring(0,2);
//                     byte ds3 = (byte)Integer.parseInt(signal1,16);
//
//                     byte shineng_window2 = (byte)((0x80) & ds3);
//                     wheelView_win.setCurrentItem(shineng_window2==0?0:1);
//
//                 }catch (Exception e){
//                     e.printStackTrace();
//                     wheelView_win.setCurrentItem(0);
//                 }
//
//                 alertDialog.show();
                 break;
             case R.id.valve_status_btn:
                 Intent intent3 = new Intent(this,TempControlSettingActivity.class);
                 intent3.putExtra("device",device);
                 startActivity(intent3);
//                 alertDialog = ECAlertDialog.buildAlert(TempControlDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         alertDialog.setDismissFalse(true);
//                     }
//                 }, new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         alertDialog.setDismissFalse(true);
//                         WheelView wheelView_win = (WheelView) alertDialog.getContent().findViewById(R.id.tet);
//                         SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
//                         String status = getValveEnableFrom(wheelView_win.getCurrentItem());
//                         Log.i(TAG,"status:"+status);
//                         sd.sendEquipmentCommand(device.getEqid(),status);
//
//                     }
//                 });
//                 alertDialog.setContentView(R.layout.edit_wheel_alert);
//                 alertDialog.setTitle(getResources().getString(R.string.please_set_valve_enable));
//                 WheelView wheelView_valve = (WheelView) alertDialog.getContent().findViewById(R.id.tet);
//                 wheelView_valve.setAdapter(new NumberAdapter(valve_check,30));
//
//                 try {
//                     String signal1 = device.getState().substring(0,2);
//                     byte ds3 = (byte)Integer.parseInt(signal1,16);
//                     byte shineng_valve2 = (byte)((0x40) & ds3);
//
//                     wheelView_valve.setCurrentItem(shineng_valve2==0?0:1);
//
//                 }catch (Exception e){
//                     e.printStackTrace();
//                     wheelView_valve.setCurrentItem(0);
//                 }
//
//                 alertDialog.show();
                 break;
             case R.id.mode_btn:
                 Intent intent4 = new Intent(this,TempControlSettingActivity.class);
                 intent4.putExtra("device",device);
                 startActivity(intent4);
//                 alertDialog = ECAlertDialog.buildAlert(TempControlDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         alertDialog.setDismissFalse(true);
//                     }
//                 }, new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         alertDialog.setDismissFalse(true);
//                         WheelView wheelView_win = (WheelView) alertDialog.getContent().findViewById(R.id.tet);
//                         SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
//                         String status = getModeFrom(wheelView_win.getCurrentItem());
//                         Log.i(TAG,"status:"+status);
//                         sd.sendEquipmentCommand(device.getEqid(),status);
//
//                     }
//                 });
//                 alertDialog.setContentView(R.layout.edit_wheel_alert);
//                 alertDialog.setTitle(getResources().getString(R.string.work_mode));
//                 WheelView wheelView_mode = (WheelView) alertDialog.getContent().findViewById(R.id.tet);
//                 wheelView_mode.setAdapter(new NumberAdapter(modes,30));
//
//
//                 try {
//                     String status2 = device.getState().substring(6,8);
//                     byte ds2 = (byte)Integer.parseInt(status2,16);
//                     int mode2 = (int)((0x03) & (ds2));
//                     if(mode2>=0&&mode2<=2){
//                         wheelView_mode.setCurrentItem(mode2);
//                     }else {
//                         wheelView_mode.setCurrentItem(0);
//                     }
//
//                 }catch (Exception e){
//                     e.printStackTrace();
//                     wheelView_mode.setCurrentItem(0);
//                 }
//
//                 alertDialog.show();
                 break;
             case R.id.tongsuo_btn:
                 Intent intent5 = new Intent(this,TempControlSettingActivity.class);
                 intent5.putExtra("device",device);
                 startActivity(intent5);
//                 alertDialog = ECAlertDialog.buildAlert(TempControlDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         alertDialog.setDismissFalse(true);
//                     }
//                 }, new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         alertDialog.setDismissFalse(true);
//                         WheelView wheelView_win = (WheelView) alertDialog.getContent().findViewById(R.id.tet);
//                         SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
//                         String status = getTongsuoFrom(wheelView_win.getCurrentItem());
//                         Log.i(TAG,"status:"+status);
//                         sd.sendEquipmentCommand(device.getEqid(),status);
//
//                     }
//                 });
//                 alertDialog.setContentView(R.layout.edit_wheel_alert);
//                 alertDialog.setTitle(getResources().getString(R.string.please_set_tongsuo_enable));
//                 WheelView wheelView_tongsuo = (WheelView) alertDialog.getContent().findViewById(R.id.tet);
//                 wheelView_tongsuo.setAdapter(new NumberAdapter(tongsuo_check,30));
//
//                 try {
//                     String signal1 = device.getState().substring(0,2);
//                     byte ds3 = (byte)Integer.parseInt(signal1,16);
//                     byte status_tongsuo = (byte)((0x20) & ds3);
//                     wheelView_tongsuo.setCurrentItem(status_tongsuo==0?0:1);
//
//                 }catch (Exception e){
//                     e.printStackTrace();
//                     wheelView_tongsuo.setCurrentItem(0);
//                 }
//
//                 alertDialog.show();
                 break;
         }
    }

    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items, int lengh) {
            super(items,lengh);
        }

    }


    private String getSetTempFrom(int set_temp,int set_temp_xiaoshu){

        int set_temp_shiji = set_temp+5;
        byte [] ds= {0x00,0x00};
        String status1 = device.getState().substring(4,6);
        String status2 = device.getState().substring(6,8);
        String status3 = device.getState().substring(0,2);
        byte d = (byte)Integer.parseInt(status1,16);
        byte ds2 = (byte)Integer.parseInt(status2,16);
        byte ds3 = (byte)Integer.parseInt(status3,16);

        byte enable_window2 = (byte)((0x80) & ds3);
        byte enable_valve2 = (byte)((0x40) & ds3);
        byte status_tongsuo = (byte)((0x20) & ds3);
        int mode2 = (int)((0x03) & (ds2));
        byte xiaoshu = (byte)((0x20) & d);
        int sta =  ((0x1F) & d);


        ds[0]  = (byte)((enable_window2==0?0x00:0x80)|ds[0]);
        ds[0]  = (byte)((enable_valve2==0?0x00:0x40)|ds[0]);
        ds[0]  = (byte)((set_temp_xiaoshu==0?0x00:0x20)|ds[0]);
        ds[0]  = (byte)((set_temp_shiji&0x1F)|ds[0]);

        ds[1]  = (byte)((status_tongsuo==0?0x00:0x04)|ds[1]);
        ds[1]  = (byte)((mode2&0x03)|ds[1]);

        String str1 = ByteUtil.convertByte2HexString(ds[0]);
        String str2 = ByteUtil.convertByte2HexString(ds[1]);
        return ((str1+str2+"0000").toUpperCase());
    }

    private String getWindowEnableFrom(int window_check){

        byte [] ds= {0x00,0x00};
        String status1 = device.getState().substring(4,6);
        String status2 = device.getState().substring(6,8);
        String status3 = device.getState().substring(0,2);
        byte d = (byte)Integer.parseInt(status1,16);
        byte ds2 = (byte)Integer.parseInt(status2,16);
        byte ds3 = (byte)Integer.parseInt(status3,16);

        byte enable_window2 = (byte)((0x80) & ds3);
        byte enable_valve2 = (byte)((0x40) & ds3);
        byte status_tongsuo = (byte)((0x20) & ds3);
        int mode2 = (int)((0x03) & (ds2));
        byte xiaoshu = (byte)((0x20) & d);
        int sta =  ((0x1F) & d);


        ds[0]  = (byte)((window_check==0?0x00:0x80)|ds[0]);
        ds[0]  = (byte)((enable_valve2==0?0x00:0x40)|ds[0]);
        ds[0]  = (byte)((xiaoshu==0?0x00:0x20)|ds[0]);
        ds[0]  = (byte)((sta&0x1F)|ds[0]);

        ds[1]  = (byte)((status_tongsuo==0?0x00:0x04)|ds[1]);
        ds[1]  = (byte)((mode2&0x03)|ds[1]);

        String str1 = ByteUtil.convertByte2HexString(ds[0]);
        String str2 = ByteUtil.convertByte2HexString(ds[1]);
        return ((str1+str2+"0000").toUpperCase());
    }

    private String getValveEnableFrom(int valve_check){

        byte [] ds= {0x00,0x00};
        String status1 = device.getState().substring(4,6);
        String status2 = device.getState().substring(6,8);
        String status3 = device.getState().substring(0,2);
        byte d = (byte)Integer.parseInt(status1,16);
        byte ds2 = (byte)Integer.parseInt(status2,16);
        byte ds3 = (byte)Integer.parseInt(status3,16);

        byte enable_window2 = (byte)((0x80) & ds3);
        byte enable_valve2 = (byte)((0x40) & ds3);
        byte status_tongsuo = (byte)((0x20) & ds3);
        int mode2 = (int)((0x03) & (ds2));
        byte xiaoshu = (byte)((0x20) & d);
        int sta =  ((0x1F) & d);


        ds[0]  = (byte)((enable_window2==0?0x00:0x80)|ds[0]);
        ds[0]  = (byte)((valve_check==0?0x00:0x40)|ds[0]);
        ds[0]  = (byte)((xiaoshu==0?0x00:0x20)|ds[0]);
        ds[0]  = (byte)((sta&0x1F)|ds[0]);

        ds[1]  = (byte)((status_tongsuo==0?0x00:0x04)|ds[1]);
        ds[1]  = (byte)((mode2&0x03)|ds[1]);

        String str1 = ByteUtil.convertByte2HexString(ds[0]);
        String str2 = ByteUtil.convertByte2HexString(ds[1]);
        return ((str1+str2+"0000").toUpperCase());
    }

    private String getModeFrom(int mode3){

        byte [] ds= {0x00,0x00};
        String status1 = device.getState().substring(4,6);
        String status2 = device.getState().substring(6,8);
        String status3 = device.getState().substring(0,2);
        byte d = (byte)Integer.parseInt(status1,16);
        byte ds2 = (byte)Integer.parseInt(status2,16);
        byte ds3 = (byte)Integer.parseInt(status3,16);

        byte enable_window2 = (byte)((0x80) & ds3);
        byte enable_valve2 = (byte)((0x40) & ds3);
        byte status_tongsuo = (byte)((0x20) & ds3);
        int mode2 = (int)((0x03) & (ds2));
        byte xiaoshu = (byte)((0x20) & d);
        int sta =  ((0x1F) & d);


        ds[0]  = (byte)((enable_window2==0?0x00:0x80)|ds[0]);
        ds[0]  = (byte)((enable_valve2==0?0x00:0x40)|ds[0]);
        ds[0]  = (byte)((xiaoshu==0?0x00:0x20)|ds[0]);
        ds[0]  = (byte)((sta&0x1F)|ds[0]);

        ds[1]  = (byte)((status_tongsuo==0?0x00:0x04)|ds[1]);
        ds[1]  = (byte)((mode3&0x03)|ds[1]);

        String str1 = ByteUtil.convertByte2HexString(ds[0]);
        String str2 = ByteUtil.convertByte2HexString(ds[1]);
        return ((str1+str2+"0000").toUpperCase());
    }

    private String getTongsuoFrom(int tongsuo){

        byte [] ds= {0x00,0x00};
        String status1 = device.getState().substring(4,6);
        String status2 = device.getState().substring(6,8);
        String status3 = device.getState().substring(0,2);
        byte d = (byte)Integer.parseInt(status1,16);
        byte ds2 = (byte)Integer.parseInt(status2,16);
        byte ds3 = (byte)Integer.parseInt(status3,16);

        byte enable_window2 = (byte)((0x80) & ds3);
        byte enable_valve2 = (byte)((0x40) & ds3);
        byte status_tongsuo = (byte)((0x20) & ds3);
        int mode2 = (int)((0x03) & (ds2));
        byte xiaoshu = (byte)((0x20) & d);
        int sta =  ((0x1F) & d);


        ds[0]  = (byte)((enable_window2==0?0x00:0x80)|ds[0]);
        ds[0]  = (byte)((enable_valve2==0?0x00:0x40)|ds[0]);
        ds[0]  = (byte)((xiaoshu==0?0x00:0x20)|ds[0]);
        ds[0]  = (byte)((sta&0x1F)|ds[0]);

        ds[1]  = (byte)((tongsuo==0?0x00:0x04)|ds[1]);
        ds[1]  = (byte)((mode2&0x03)|ds[1]);

        String str1 = ByteUtil.convertByte2HexString(ds[0]);
        String str2 = ByteUtil.convertByte2HexString(ds[1]);
        return ((str1+str2+"0000").toUpperCase());
    }

}
