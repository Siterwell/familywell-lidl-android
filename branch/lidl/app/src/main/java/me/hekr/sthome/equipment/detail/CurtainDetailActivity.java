package me.hekr.sthome.equipment.detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.android.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
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
 * Created by jishu0001 on 2016/10/14.
 */
public class CurtainDetailActivity extends TopbarSuperActivity {
    private static final  int GETBACK_SUCCESS = 1;
    private static final int GETBACK_FAILED = 2;
    private MyInforHandler myInforHandler;

    private static final String TAG = "CurtainDetail";
    private EquipDAO ED;
    private LinearLayout drawBack;
    private ImageView deviceLogo,up1,down1;
    private TextView historyInfo,operation,emergencyCall,showStatus;
    private ImageView signal,quantity;
    private String status2="",eqid="";
    private JSONObject json;
    private EquipmentBean eq,device;
    private SendEquipmentData sd;
    private int num =0;

    @Override
    protected void onCreateInit() {
        initData();
        initViewGuider();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail_curtain;
    }


    private void initData() {
        EventBus.getDefault().register(this);
        myInforHandler = new MyInforHandler() {
            @Override
            protected void operationSuccess() {
               // Toast.makeText(CurtainDetailActivity.this,getResources().getString(R.string.operation_success),Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void operationFailed() {
                Toast.makeText(CurtainDetailActivity.this,getResources().getString(R.string.operation_failed),Toast.LENGTH_SHORT).show();
            }
        };
        try{
            device = (EquipmentBean) this.getIntent().getSerializableExtra("device");
        }catch(Exception e){
            Log.i(TAG,"device is null");
        }
        sd = new SendEquipmentData(this) {
            @Override
            protected void sendEquipmentDataFailed() {
//                Toast.makeText(CurtainDetailActivity.this,"operation failed",Toast.LENGTH_LONG).show();
                myInforHandler.sendEmptyMessage(GETBACK_FAILED);
            }

            @Override
            protected void sendEquipmentDataSuccess() {
//                Toast.makeText(CurtainDetailActivity.this,"operation success",Toast.LENGTH_LONG).show();
                myInforHandler.sendEmptyMessage(GETBACK_SUCCESS);
            }
        };
    }

    private void initViewGuider() {
        eqid= device.getEqid();
        String ds = "";
        if(TextUtils.isEmpty(device.getEquipmentName())){
            ds = getResources().getString(R.string.curtain)+device.getEqid();
        }else{
            ds = device.getEquipmentName();
        }
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
                                String dsCRC = ByteUtil.CRCmaker(ds);
                                SendCommand.Command = SendCommand.MODIFY_EQUIPMENT_NAME;
                                sd.modifyEquipmentName(device.getEqid(), ds + dsCRC);
                            }else {
                                Toast.makeText(CurtainDetailActivity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(CurtainDetailActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(CurtainDetailActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
                }

            }
        });


        showStatus = (TextView) findViewById(R.id.showStatus);
        drawBack = (LinearLayout) findViewById(R.id.drawBack);
        signal = (ImageView) findViewById(R.id.signalPosition);
        deviceLogo = (ImageView) findViewById(R.id.devicePosition);
        deviceLogo.setImageResource(R.drawable.detail13);
        up1 = (ImageView) findViewById(R.id.curtainUp);
        down1 = (ImageView) findViewById(R.id.curtainDown);

        historyInfo = (TextView) findViewById(R.id.deviceHistory);
        operation = (TextView) findViewById(R.id.operation);
        operation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(CurtainDetailActivity.this,getResources().getString(R.string.operation),Toast.LENGTH_SHORT).show();
            }
        });
        emergencyCall = (TextView) findViewById(R.id.emergencyCall);
        emergencyCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneAlert();
            }
        });

        newAction();
        doStatusShow(device.getState());
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

    private void newAction(){
        deviceLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num =1;
                chuli(num);
            }
        });
        up1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num =2;
                chuli(num);
            }
        });
        down1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num =3;
                chuli(num);
            }
        });
    }
    private void chuli(int num1){
        switch (num1){
            case 1://stop
                status2 = "22";
                break;
            case 2://close
                status2 = "11";
                break;
            case 3://open
                status2 = "33";
                break;
            default:
                break;
        }
//        valid = device.getState().substring(4,6);
        String aaa = status2 +"ff"+"ff"+"ff";
        SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
        sd.sendEquipmentCommand(eqid,aaa);
    }

    private void doStatusShow(String aaaa) {
        try {
            String signal1 = aaaa.substring(0,2);
            String quantity1 = aaaa.substring(2,4);
            String socketStatus = aaaa.substring(6,8);
            showStatus.setText(getResources().getString(R.string.normal));
            if(signal1 != null){

                signal.setImageResource(ShowBascInfor.choseSPic(signal1));

            }
            if(socketStatus != null ){
                drawBack.setBackgroundResource(R.drawable.green);
                if ("64".equals(socketStatus)) {
//                    holder.s.setText("闭合");
                    drawBack.setBackgroundResource(R.drawable.red);
                    showStatus.setText(getResources().getString(R.string.normal));
                } else if ("00".equals(socketStatus)) {
//                    holder.s.setText("断开");
                    showStatus.setText(getResources().getString(R.string.normal));
                }
            }
        }catch (Exception e){
              e.printStackTrace();
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
            Toast.makeText(CurtainDetailActivity.this,getResources().getString(R.string.update_name_success),Toast.LENGTH_SHORT).show();
        }else if(event.getEvent() == SendCommand.DELETE_EQUIPMENT_DETAIL){
            SendCommand.clearCommnad();
            Toast.makeText(CurtainDetailActivity.this,getResources().getString(R.string.delete_success),Toast.LENGTH_SHORT).show();
            ED = new EquipDAO(CurtainDetailActivity.this);
            ED.deleteByEqid(device);
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

    private void openPhoneAlert(){

        final  String phone = CCPAppManager.getClientUser().getDescription();

        if(!TextUtils.isEmpty(phone)){
            ECAlertDialog alertDialog = ECAlertDialog.buildAlert(this, phone, getResources().getString(R.string.cancel), getResources().getString(R.string.call), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }

                }
            });
            alertDialog.setTitleVisibility(View.GONE);
            alertDialog.show();
        }else {
            Toast.makeText(this,getResources().getString(R.string.please_set_emergencyphone),Toast.LENGTH_SHORT).show();
        }


    }


}
