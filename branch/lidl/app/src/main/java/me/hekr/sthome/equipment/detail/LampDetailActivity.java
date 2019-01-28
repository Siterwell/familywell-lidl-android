package me.hekr.sthome.equipment.detail;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.android.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import me.hekr.sthome.MyApplication;
import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.EmojiFilter;
import me.hekr.sthome.tools.MyInforHandler;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendEquipmentData;

/**
 * Created by jishu0001 on 2016/10/13.
 */
public class LampDetailActivity extends TopbarSuperActivity {
    private static final  int GETBACK_SUCCESS = 1;
    private static final int GETBACK_FAILED = 2;
    private MyInforHandler myInforHandler;

    private static final String TAG = "LampDetail";
    private MyApplication myApplication;
    private EquipDAO ED;
    private LinearLayout drawBack;
    private ImageView deviceLogo;
    private TextView historyInfo,showStatus,red1,green1,blue1,rg1,rb1,gb1,open1,close1,alarm1,colorful1;
    private ImageView signal,quantity;
    private String signal1="",quantity1="",valid="",status2="",history1="",eqid="";
    //    private SwitchCompat switcher;
    private JSONObject json;
    private EquipmentBean eq,device;
    private SendEquipmentData sd;
    private TextView[] tag;
    private int num =0;
    private int xianzhuang = 0; //condition
    //    private int[] imageQ = new int[]{
//            R.drawable.q0,
//            R.drawable.q1,
//            R.drawable.q2,
//            R.drawable.q3,
//            R.drawable.q4,
//    };
    private int[] imageS = new int[]{
            R.drawable.s0,
            R.drawable.s1,
            R.drawable.s2,
            R.drawable.s3
    };

    @Override
    protected void onCreateInit() {
        initData();
        initViewGuider();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail_lamp;
    }


    private void initData() {
        EventBus.getDefault().register(this);
        myInforHandler = new MyInforHandler() {
            @Override
            protected void operationSuccess() {
                Toast.makeText(LampDetailActivity.this,getResources().getString(R.string.operation_success),Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void operationFailed() {
                Toast.makeText(LampDetailActivity.this,getResources().getString(R.string.operation_failed),Toast.LENGTH_SHORT).show();
            }
        };
        try{
            device = (EquipmentBean) this.getIntent().getSerializableExtra("device");
        }catch(Exception e){
            Log.i(TAG,"device is null");
        }
        sd= new SendEquipmentData(this) {
            @Override
            protected void sendEquipmentDataFailed() {
//                Toast.makeText(LampDetailActivity.this,"operation failed",Toast.LENGTH_LONG).show();
                myInforHandler.sendEmptyMessage(GETBACK_FAILED);
            }

            @Override
            protected void sendEquipmentDataSuccess() {
//                Toast.makeText(LampDetailActivity.this,"operation success",Toast.LENGTH_LONG).show();
                myInforHandler.sendEmptyMessage(GETBACK_SUCCESS);
            }
        };
        myApplication = (MyApplication) getApplicationContext();
    }

    private void initViewGuider() {
        eqid= device.getEqid();
        String ds = "";
        if(TextUtils.isEmpty(device.getEquipmentName())){
            ds = getResources().getString(R.string.lamp)+device.getEqid();
        }else{
            ds = device.getEquipmentName();
        }
        final EditText deviceName = getTopBarView().getEditTitle();
        getTopBarView().setTopBarStatus(2, 2, ds, getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(CoDetailActivity.this,"go back",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(SocketDetailActivity.this, EquipListActivity.class));
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit = getTopBarView().getEditTitle().getText().toString().trim();
                if(!TextUtils.isEmpty(edit)){
                    try {
                        if(edit.getBytes("UTF-8").length<=15){
                            if(!EmojiFilter.containsEmoji(edit)) {
                                getTopBarView().setEditTitle(edit);
                                updateName(edit);
                                String ds = CoderUtils.getAscii(edit);
                                String dsCRC= ByteUtil.CRCmaker(ds);
                                SendCommand.Command = SendCommand.MODIFY_EQUIPMENT_NAME;
                                sd.modifyEquipmentName(device.getEqid(),ds + dsCRC);
                            }else {
                                Toast.makeText(LampDetailActivity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            Toast.makeText(LampDetailActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(LampDetailActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
                }
            }
        });

        showStatus = (TextView) findViewById(R.id.showStatus);
        drawBack = (LinearLayout) findViewById(R.id.drawBack);
        signal = (ImageView) findViewById(R.id.signalPosition);
        deviceLogo = (ImageView) findViewById(R.id.devicePosition);
        deviceLogo.setImageResource(R.drawable.detail12);
        historyInfo = (TextView) findViewById(R.id.deviceHistory);

        red1 = (TextView) findViewById(R.id.lRed);
        green1 = (TextView) findViewById(R.id.lGreen);
        blue1 = (TextView) findViewById(R.id.lBlue);
        rg1 = (TextView) findViewById(R.id.redGreen);
        rb1 = (TextView) findViewById(R.id.redBlue);
        gb1 = (TextView) findViewById(R.id.greenBlue);
        open1 = (TextView) findViewById(R.id.open);
        close1 = (TextView) findViewById(R.id.close);
        alarm1 = (TextView) findViewById(R.id.alarm);
        colorful1 = (TextView) findViewById(R.id.colorful);
        tag = new TextView[]{
                red1,
                green1,
                blue1,
                rg1,
                rb1,
                gb1,
                open1,
                close1,
                alarm1,
                colorful1
        };

        red1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sentData("30ffffff");
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(eqid,"30ffffff");
//                if(myApplication.isWifiTag()){
//                    try {
//                        Thread.sleep(1000);
//                        if(myApplication.isFaceback()){
//                            Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                        }else {
//                            for (int i = 0; i< 3; i++){
//                                sd.sendEquipmentCommand(eqid,"30ffffff");
//                                Thread.sleep(1000);
//                                if(myApplication.isFaceback()){
//                                    Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                                    break;
//                                }else {
//                                    sd.sendEquipmentCommand(eqid,"30ffffff");
//                                    Toast.makeText(LampDetailActivity.this,"wre fighting",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            if(!myApplication.isFaceback()){
//                                Toast.makeText(LampDetailActivity.this,"please try again",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });
        green1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sentData("31ffffff");
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(eqid,"31ffffff");
//                if(myApplication.isWifiTag()){
//                    try {
//                        Thread.sleep(1000);
//                        if(myApplication.isFaceback()){
//                            Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                        }else {
//                            for (int i = 0; i< 3; i++){
//                                sd.sendEquipmentCommand(eqid,"31ffffff");
//                                Thread.sleep(1000);
//                                if(myApplication.isFaceback()){
//                                    Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                                    break;
//                                }else {
//                                    sd.sendEquipmentCommand(eqid,"31ffffff");
//                                    Toast.makeText(LampDetailActivity.this,"wre fighting",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            if(!myApplication.isFaceback()){
//                                Toast.makeText(LampDetailActivity.this,"please try again",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });
        blue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sentData("32ffffff");
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(eqid,"32ffffff");
//                if(myApplication.isWifiTag()){
//                    try {
//                        Thread.sleep(1000);
//                        if(myApplication.isFaceback()){
//                            Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                        }else {
//                            for (int i = 0; i< 3; i++){
//                                sd.sendEquipmentCommand(eqid,"32ffffff");
//                                Thread.sleep(1000);
//                                if(myApplication.isFaceback()){
//                                    Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                                    break;
//                                }else {
//                                    sd.sendEquipmentCommand(eqid,"32ffffff");
//                                    Toast.makeText(LampDetailActivity.this,"wre fighting",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            if(!myApplication.isFaceback()){
//                                Toast.makeText(LampDetailActivity.this,"please try again",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });
        rg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sentData("33ffffff");
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(eqid,"33ffffff");
//                if(myApplication.isWifiTag()){
//                    try {
//                        Thread.sleep(1000);
//                        if(myApplication.isFaceback()){
//                            Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                        }else {
//                            for (int i = 0; i< 3; i++){
//                                sd.sendEquipmentCommand(eqid,"33ffffff");
//                                Thread.sleep(1000);
//                                if(myApplication.isFaceback()){
//                                    Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                                    break;
//                                }else {
//                                    sd.sendEquipmentCommand(eqid,"33ffffff");
//                                    Toast.makeText(LampDetailActivity.this,"wre fighting",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            if(!myApplication.isFaceback()){
//                                Toast.makeText(LampDetailActivity.this,"please try again",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sentData("34ffffff");
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(eqid,"34ffffff");
//                if(myApplication.isWifiTag()){
//                    try {
//                        Thread.sleep(1000);
//                        if(myApplication.isFaceback()){
//                            Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                        }else {
//                            for (int i = 0; i< 3; i++){
//                                sd.sendEquipmentCommand(eqid,"34ffffff");
//                                Thread.sleep(1000);
//                                if(myApplication.isFaceback()){
//                                    Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                                    break;
//                                }else {
//                                    sd.sendEquipmentCommand(eqid,"34ffffff");
//                                    Toast.makeText(LampDetailActivity.this,"wre fighting",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            if(!myApplication.isFaceback()){
//                                Toast.makeText(LampDetailActivity.this,"please try again",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });
        gb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sentData("35ffffff");
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(eqid,"35ffffff");
//                if(myApplication.isWifiTag()){
//                    try {
//                        Thread.sleep(1000);
//                        if(myApplication.isFaceback()){
//                            Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                        }else {
//                            for (int i = 0; i< 3; i++){
//                                sd.sendEquipmentCommand(eqid,"35ffffff");
//                                Thread.sleep(1000);
//                                if(myApplication.isFaceback()){
//                                    Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                                    break;
//                                }else {
//                                    sd.sendEquipmentCommand(eqid,"35ffffff");
//                                    Toast.makeText(LampDetailActivity.this,"wre fighting",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            if(!myApplication.isFaceback()){
//                                Toast.makeText(LampDetailActivity.this,"please try again",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });
        open1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sentData("36ffffff");
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(eqid,"36ffffff");
//                if(myApplication.isWifiTag()){
//                    try {
//                        Thread.sleep(1000);
//                        if(myApplication.isFaceback()){
//                            Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                        }else {
//                            for (int i = 0; i< 3; i++){
//                                sd.sendEquipmentCommand(eqid,"36ffffff");
//                                Thread.sleep(1000);
//                                if(myApplication.isFaceback()){
//                                    Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                                    break;
//                                }else {
//                                    sd.sendEquipmentCommand(eqid,"36ffffff");
//                                    Toast.makeText(LampDetailActivity.this,"wre fighting",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            if(!myApplication.isFaceback()){
//                                Toast.makeText(LampDetailActivity.this,"please try again",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });
        close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sentData("37ffffff");
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(eqid,"37ffffff");
//                if(myApplication.isWifiTag()){
//                    try {
//                        Thread.sleep(1000);
//                        if(myApplication.isFaceback()){
//                            Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                        }else {
//                            for (int i = 0; i< 3; i++){
//                                sd.sendEquipmentCommand(eqid,"37ffffff");
//                                Thread.sleep(1000);
//                                if(myApplication.isFaceback()){
//                                    Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                                    break;
//                                }else {
//                                    sd.sendEquipmentCommand(eqid,"37ffffff");
//                                    Toast.makeText(LampDetailActivity.this,"wre fighting",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            if(!myApplication.isFaceback()){
//                                Toast.makeText(LampDetailActivity.this,"please try again",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });
        alarm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sentData("38ffffff");
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(eqid,"38ffffff");
//                if(myApplication.isWifiTag()){
//                    try {
//                        Thread.sleep(1000);
//                        if(myApplication.isFaceback()){
//                            Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                        }else {
//                            for (int i = 0; i< 3; i++){
//                                sd.sendEquipmentCommand(eqid,"38ffffff");
//                                Thread.sleep(1000);
//                                if(myApplication.isFaceback()){
//                                    Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                                    break;
//                                }else {
//                                    sd.sendEquipmentCommand(eqid,"38ffffff");
//                                    Toast.makeText(LampDetailActivity.this,"wre fighting",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            if(!myApplication.isFaceback()){
//                                Toast.makeText(LampDetailActivity.this,"please try again",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });
        colorful1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sentData("39ffffff");
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(eqid,"39ffffff");
//                if(myApplication.isWifiTag()){
//                    try {
//                        Thread.sleep(1000);
//                        if(myApplication.isFaceback()){
//                            Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                        }else {
//                            for (int i = 0; i< 3; i++){
//                                sd.sendEquipmentCommand(eqid,"39ffffff");
//                                Thread.sleep(1000);
//                                if(myApplication.isFaceback()){
//                                    Toast.makeText(LampDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
//                                    break;
//                                }else {
//                                    sd.sendEquipmentCommand(eqid,"39ffffff");
//                                    Toast.makeText(LampDetailActivity.this,"wre fighting",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            if(!myApplication.isFaceback()){
//                                Toast.makeText(LampDetailActivity.this,"please try again",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });

        if(!TextUtils.isEmpty(device.getState()) && device.getState().length()==8 ){

            try {
                doStatusShow(device.getState());
            }catch (Exception e){
                Log.i(TAG,"data is illegal");
            }
        }
    }
    private void updateName(String edit) {
        if( !device.getEquipmentName().equals(edit)){

            device.setEquipmentName(edit);
            ED = new EquipDAO(this);
            try {
                ED.updateName(device);
            }catch (Exception e){
                Toast.makeText(this,getResources().getString(R.string.name_is_repeat),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doStatusShow(String aaaa) {
        String signal1 = aaaa.substring(0,2);
        String quantity1 = aaaa.substring(2,4);
        String draw = aaaa.substring(4,6);
        String socketStatus = aaaa.substring(6,8);
//        drawBack.setBackgroundResource(R.drawable.green);
        showStatus.setText(getResources().getString(R.string.normal));
        String showMsg1 = "";

        String shiwei = quantity1.substring(0,1);
        if(signal1 != null){
            signal.setImageResource(ShowBascInfor.choseSPic(signal1));
        }
        if(draw != null ){
            drawBack.setBackgroundResource(R.drawable.green);
            for(int i=0;i<tag.length;i++) {
                tag[i].setTextColor(Color.BLACK);
//                tag[i].setBackground(getResources().getDrawable(R.drawable.button));
            }
            if ("30".equals(draw)) { //red
                tag[0].setTextColor(getResources().getColor(R.color.red));
            } else if ("31".equals(draw)) {//green
                tag[1].setTextColor(getResources().getColor(R.color.green));
            } else if ("32".equals(draw)) {//blue
                tag[2].setTextColor(getResources().getColor(R.color.blue));
            } else if ("33".equals(draw)) {//redgreen
                tag[3].setTextColor(getResources().getColor(R.color.redgreen));
            } else if ("34".equals(draw)) {//redblue
                tag[4].setTextColor(getResources().getColor(R.color.redblue));
            } else if ("35".equals(draw)) {//greenblue
                tag[5].setTextColor(getResources().getColor(R.color.greenblue));
            } else if ("36".equals(draw)) {//
                tag[6].setTextColor(Color.RED);
            } else if ("37".equals(draw)) {
                tag[7].setTextColor(Color.RED);
            }else if ("38".equals(draw)) {
                tag[8].setTextColor(Color.RED);
                drawBack.setBackgroundResource(R.drawable.green);
            }else if ("39".equals(draw)) {
                tag[9].setTextColor(Color.RED);
            }
        }
//            if ("30".equals(socketStatus)) {
//                red1.setTextColor(getResources().getColor(R.color.red));
//                red1.setBackgroundColor(Color.GRAY);
//            } else if ("31".equals(socketStatus)) {
//                green1.setTextColor(getResources().getColor(R.color.green));
//            } else if ("32".equals(socketStatus)) {
//                blue1.setTextColor(getResources().getColor(R.color.blue));
//            } else if ("33".equals(socketStatus)) {
//                rg1.setTextColor(getResources().getColor(R.color.redgreen));
//            } else if ("34".equals(socketStatus)) {
//                rb1.setTextColor(getResources().getColor(R.color.redblue));
//            } else if ("35".equals(socketStatus)) {
//                gb1.setTextColor(getResources().getColor(R.color.greenblue));
//            } else if ("36".equals(socketStatus)) {
//                open1.setTextColor(Color.RED);
//            } else if ("37".equals(socketStatus)) {
//                close1.setTextColor(Color.RED);
//            }else if ("38".equals(socketStatus)) {
//                alarm1.setTextColor(Color.RED);
//                drawBack.setBackgroundResource(R.drawable.green);
//            }else if ("39".equals(socketStatus)) {
//                colorful1.setTextColor(Color.RED);
//            }
        showStatus.setText(getResources().getString(R.string.normal));
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent() == SendCommand.MODIFY_EQUIPMENT_NAME){
            SendCommand.clearCommnad();
            Toast.makeText(LampDetailActivity.this,getResources().getString(R.string.update_name_success),Toast.LENGTH_SHORT).show();
        }else if(event.getEvent() == SendCommand.EQUIPMENT_CONTROL){

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
}
