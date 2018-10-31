package me.hekr.sthome.equipment.detail;

import android.view.View;
import android.widget.Toast;

import com.litesuits.android.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.event.ThcheckEvent;
import me.hekr.sthome.http.bean.DeviceBean;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by TracyHenry on 2018/3/21.
 */

public class TempControlSettingActivity extends TopbarSuperActivity {
    private static final String  TAG= "TempControlSettingActivity";
    private WheelView wheelView_set_temp,wheelView_set_temp2,wheelView_window_check,wheelView_tongsuo,wheelView_valve_check,wheelView_mode,wheelView_set_temp_xiaoshu,wheelView_set_temp_xiaoshu2;
    private ArrayList<String> modes = new ArrayList<>();
    private ArrayList<String> window_check = new ArrayList<>();
    private ArrayList<String> tongsuo_check = new ArrayList<>();
    private ArrayList<String> valve_check = new ArrayList<>();
    private ArrayList<String> set_xiaoshu = new ArrayList<>();
    private ArrayList<String> set_xiaoshu2 = new ArrayList<>();
    private ArrayList<String> items_temp = new ArrayList<>();
    private ArrayList<String> items_temp2 = new ArrayList<>();
    private SendEquipmentData sd;
    private EquipmentBean device;

    @Override
    protected void onCreateInit() {

        getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.param_setting), getResources().getString(R.string.save), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                String status = null;
                if(wheelView_mode.getCurrentItem()==0){

                    if(wheelView_set_temp.getCurrentItem()!= (items_temp.size()-1)){
                        status = getFrom(wheelView_set_temp.getCurrentItem(),
                                wheelView_set_temp_xiaoshu.getCurrentItem(),wheelView_window_check.getCurrentItem(),wheelView_valve_check.getCurrentItem(),
                                wheelView_tongsuo.getCurrentItem());
                    }else{
                        status = getFrom(wheelView_set_temp.getCurrentItem(),
                                wheelView_set_temp_xiaoshu2.getCurrentItem(),wheelView_window_check.getCurrentItem(),wheelView_valve_check.getCurrentItem(),
                                wheelView_tongsuo.getCurrentItem());
                    }

                }else {
                    if(wheelView_set_temp2.getCurrentItem()!=(items_temp2.size()-1)){
                        status = getFrom2(wheelView_set_temp2.getCurrentItem(),wheelView_set_temp_xiaoshu.getCurrentItem(),
                                wheelView_mode.getCurrentItem(),wheelView_window_check.getCurrentItem(),wheelView_valve_check.getCurrentItem(),
                                wheelView_tongsuo.getCurrentItem());
                    }else {
                        status = getFrom2(wheelView_set_temp2.getCurrentItem(),wheelView_set_temp_xiaoshu2.getCurrentItem(),
                                wheelView_mode.getCurrentItem(),wheelView_window_check.getCurrentItem(),wheelView_valve_check.getCurrentItem(),
                                wheelView_tongsuo.getCurrentItem());
                    }

                }



               Log.i(TAG,"status:"+status);
                sd.sendEquipmentCommand(device.getEqid(),status);
            }
        });

         wheelView_mode = (WheelView)findViewById(R.id.mode);
         wheelView_set_temp = (WheelView)findViewById(R.id.set_temp);
        wheelView_set_temp2 = (WheelView)findViewById(R.id.set_temp2);
        wheelView_set_temp_xiaoshu =(WheelView)findViewById(R.id.set_temp_xiaoshu);
        wheelView_set_temp_xiaoshu2 = (WheelView)findViewById(R.id.set_temp_xiaoshu2);
         wheelView_window_check = (WheelView)findViewById(R.id.window_check);
         wheelView_tongsuo = (WheelView)findViewById(R.id.tongsuo);
         wheelView_valve_check = (WheelView)findViewById(R.id.valve_check);
        initdata();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_temp_control;
    }


    private void initdata(){


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
        String[] tongsuo_check2 = {getResources().getString(R.string.close),getResources().getString(R.string.open)};
        String[] valve_check2 = {getResources().getString(R.string.disable),getResources().getString(R.string.enable)};
        String[] set_xiaoshu22 = {"0","5"};
          for(int i=5;i<16;i++){
              items_temp.add(String.valueOf(i));
          }
        for(int i=5;i<31;i++){
            items_temp2.add(String.valueOf(i));
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

        for(int i=0;i<set_xiaoshu22.length;i++){
            set_xiaoshu.add(set_xiaoshu22[i]);
        }
        set_xiaoshu2.add("0");

        wheelView_set_temp.setAdapter(new NumberAdapter(items_temp,30));
        wheelView_set_temp2.setAdapter(new NumberAdapter(items_temp2,30));
        wheelView_set_temp_xiaoshu.setAdapter(new NumberAdapter(set_xiaoshu,30));
        wheelView_set_temp_xiaoshu2.setAdapter(new NumberAdapter(set_xiaoshu2,30));
        wheelView_mode.setAdapter(new NumberAdapter(modes,30));
        wheelView_window_check.setAdapter(new NumberAdapter(window_check,30));
        wheelView_valve_check.setAdapter(new NumberAdapter(valve_check,30));
        wheelView_tongsuo.setAdapter(new NumberAdapter(tongsuo_check,30));

        try {
            String status1 = device.getState().substring(4,6);
            String status2 = device.getState().substring(6,8);
            String status3 = device.getState().substring(0,2);
            byte ds = (byte)Integer.parseInt(status1,16);
            byte ds2 = (byte)Integer.parseInt(status2,16);
            byte ds3 = (byte)Integer.parseInt(status3,16);
            byte status_window2 = (byte)((0x80) & ds3);
            byte status_valve2 = (byte)((0x40) & ds3);
            byte status_tongsuo = (byte)((0x20) & ds3);
            int mode2 = (int)((0x03) & (ds2));
            byte xiaoshu = (byte)((0x20) & ds);
            int sta =  ((0x1F) & ds);


            if(mode2>=0&&mode2<=2){
                wheelView_mode.setCurrentItem(mode2);
                if(mode2==0){
                    wheelView_set_temp.setVisibility(View.VISIBLE);
                    wheelView_set_temp2.setVisibility(View.GONE);
                    if(sta>=5&&sta<=15){
                        wheelView_set_temp.setCurrentItem(sta-5);
                        if(sta==15){
                            wheelView_set_temp_xiaoshu2.setVisibility(View.VISIBLE);
                            wheelView_set_temp_xiaoshu.setVisibility(View.GONE);
                        }else{
                            wheelView_set_temp_xiaoshu2.setVisibility(View.GONE);
                            wheelView_set_temp_xiaoshu.setVisibility(View.VISIBLE);
                        }
                    }else {
                        wheelView_set_temp.setCurrentItem(0);
                        wheelView_set_temp_xiaoshu2.setVisibility(View.GONE);
                        wheelView_set_temp_xiaoshu.setVisibility(View.VISIBLE);
                    }
                }else{
                    wheelView_set_temp.setVisibility(View.GONE);
                    wheelView_set_temp2.setVisibility(View.VISIBLE);
                    if(sta>=5&&sta<=30){
                        wheelView_set_temp2.setCurrentItem(sta-5);
                        if(sta==30){
                            wheelView_set_temp_xiaoshu2.setVisibility(View.VISIBLE);
                            wheelView_set_temp_xiaoshu.setVisibility(View.GONE);
                        }else{
                            wheelView_set_temp_xiaoshu2.setVisibility(View.GONE);
                            wheelView_set_temp_xiaoshu.setVisibility(View.VISIBLE);
                        }
                    }else {
                        wheelView_set_temp2.setCurrentItem(0);
                        wheelView_set_temp_xiaoshu2.setVisibility(View.GONE);
                        wheelView_set_temp_xiaoshu.setVisibility(View.VISIBLE);
                    }
                }
            }else {
                wheelView_mode.setCurrentItem(0);
            }
            wheelView_set_temp_xiaoshu.setCurrentItem(xiaoshu==0?0:1);
            wheelView_window_check.setCurrentItem(status_window2==0?0:1);
            wheelView_valve_check.setCurrentItem(status_valve2==0?0:1);
            wheelView_tongsuo.setCurrentItem(status_tongsuo==0?0:1);

            wheelView_set_temp.addChangingListener(new WheelView.OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if(newValue == (items_temp.size()-1)){
                        wheelView_set_temp_xiaoshu.setVisibility(View.GONE);
                        wheelView_set_temp_xiaoshu2.setVisibility(View.VISIBLE);
                    }else{
                        wheelView_set_temp_xiaoshu.setVisibility(View.VISIBLE);
                        wheelView_set_temp_xiaoshu2.setVisibility(View.GONE);
                    }
                }
            });

            wheelView_set_temp2.addChangingListener(new WheelView.OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if(newValue == (items_temp2.size()-1)){
                        wheelView_set_temp_xiaoshu.setVisibility(View.GONE);
                        wheelView_set_temp_xiaoshu2.setVisibility(View.VISIBLE);
                    }else{
                        wheelView_set_temp_xiaoshu.setVisibility(View.VISIBLE);
                        wheelView_set_temp_xiaoshu2.setVisibility(View.GONE);
                    }
                }
            });
            wheelView_mode.addChangingListener(new WheelView.OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if(newValue == 0){
                        wheelView_set_temp.setVisibility(View.VISIBLE);
                        wheelView_set_temp2.setVisibility(View.GONE);

                    }else {
                        wheelView_set_temp.setVisibility(View.GONE);
                        wheelView_set_temp2.setVisibility(View.VISIBLE);
                    }
                }
            });

        }catch (Exception e){
          e.printStackTrace();
        }


    }


    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items, int lengh) {
            super(items,lengh);
        }

    }


    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent() == SendCommand.EQUIPMENT_CONTROL){
            SendCommand.clearCommnad();
            Toast.makeText(TempControlSettingActivity.this,getResources().getString(R.string.data_syncing),Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private String getFrom(int set_temp,int set_temp_xiaoshu,int window_check,int valve_check,int tongsuo ){

        int set_temp_shiji = set_temp+5;
        byte [] ds= {0x00,0x00};

        ds[0]  = (byte)((window_check==0?0x00:0x80)|ds[0]);
        ds[0]  = (byte)((valve_check==0?0x00:0x40)|ds[0]);
        ds[0]  = (byte)((set_temp_xiaoshu==0?0x00:0x20)|ds[0]);
        ds[0]  = (byte)((set_temp_shiji&0x1F)|ds[0]);

        ds[1]  = (byte)((tongsuo==0?0x00:0x04)|ds[1]);
        ds[1]  = (byte)((0&0x03)|ds[1]);

        String str1 = ByteUtil.convertByte2HexString(ds[0]);
        String str2 = ByteUtil.convertByte2HexString(ds[1]);
        return ((str1+str2+"0000").toUpperCase());
    }

    private String getFrom2(int set_temp,int set_temp_xiaoshu,int mode,int window_check,int valve_check,int tongsuo ){

        int set_temp_shiji = set_temp+5;
        byte [] ds= {0x00,0x00};

        ds[0]  = (byte)((window_check==0?0x00:0x80)|ds[0]);
        ds[0]  = (byte)((valve_check==0?0x00:0x40)|ds[0]);
        ds[0]  = (byte)((set_temp_xiaoshu==0?0x00:0x20)|ds[0]);
        ds[0]  = (byte)((set_temp_shiji&0x1F)|ds[0]);

        ds[1]  = (byte)((tongsuo==0?0x00:0x04)|ds[1]);
        ds[1]  = (byte)((mode&0x03)|ds[1]);

        String str1 = ByteUtil.convertByte2HexString(ds[0]);
        String str2 = ByteUtil.convertByte2HexString(ds[1]);
        return ((str1+str2+"0000").toUpperCase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
