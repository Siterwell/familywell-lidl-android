package me.hekr.sthome.equipment.detail;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.io.UnsupportedEncodingException;

import me.hekr.sthome.AddDeviceActivity;
import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.EmojiFilter;
import me.hekr.sthome.tools.MyInforHandler;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by jishu0001 on 2016/9/13.
 */
public class Channel2SocketDetailActivity extends AbstractDetailActivity {
    private static final  int GETBACK_SUCCESS = 1;
    private static final int GETBACK_FAILED = 2;
    private MyInforHandler myInforHandler;
    private static  final  String TAG = "Channel2SocketDetail";
    private EquipDAO ED;
    private ImageView deviceLogo;
    private TextView showStatus;
    private TextView showStatus2;
    private ImageView signal;
    private String eqid="";
    private EquipmentBean device;
    private SendEquipmentData sd;
    private ImageView back_img;
    private TextView  edt_txt,eq_name;
    private LinearLayout root;
    private ImageView operation_img;
    private ImageView operation_img2;
    private ECAlertDialog alertDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_2_channel_socket);
        initData();
        initViewGuider();
    }



    private void initData() {
        EventBus.getDefault().register(this);
        myInforHandler = new MyInforHandler() {
            @Override
            protected void operationSuccess() {
//                Toast.makeText(SocketDetailActivity.this,"operartion Success",Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void operationFailed() {
                Toast.makeText(Channel2SocketDetailActivity.this,getResources().getString(R.string.operation_failed),Toast.LENGTH_SHORT).show();
            }
        };
        try{
            device = (EquipmentBean) this.getIntent().getSerializableExtra("device");
        }catch(Exception e){
            Log.i("Detail socket","device is null");
        }
        sd = new SendEquipmentData(this) {
            @Override
            protected void sendEquipmentDataFailed() {
                Log.i(TAG,"opreration failed");
//                sendFailed();
//                Toast.makeText(SocketDetailActivity.this,"operation failed",Toast.LENGTH_LONG).show();
                myInforHandler.sendEmptyMessage(GETBACK_FAILED);
            }

            @Override
            protected void sendEquipmentDataSuccess() {
                Log.i(TAG,"opreration success");
//                sendSuccess();
//                Toast.makeText(SocketDetailActivity.this,"operation success",Toast.LENGTH_LONG).show();
                myInforHandler.sendEmptyMessage(GETBACK_SUCCESS);
            }
        };

    }



    private void initViewGuider() {
        eqid= device.getEqid();
        back_img   = (ImageView)findViewById(R.id.goBack);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edt_txt    = (TextView)findViewById(R.id.detailEdit);
        edt_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ECListDialog ecListDialog = new ECListDialog(Channel2SocketDetailActivity.this,getResources().getStringArray(R.array.DeivceOperation));
                ecListDialog.setTitle(getResources().getString(R.string.manage));
                ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(Dialog d, int position) {

                        switch (position){
                            case 0:
                                SendCommand.Command = SendCommand.REPLACE_EQUIPMENT;
                                sd.replaceEquipment(device.getEqid());
                                Intent intent =new Intent(Channel2SocketDetailActivity.this,AddDeviceActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("eqid",device.getEqid());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                break;
                            case 1:
                                ECAlertDialog elc = ECAlertDialog.buildAlert(Channel2SocketDetailActivity.this,getResources().getString(R.string.delete_or_not), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                alertDialog = ECAlertDialog.buildAlert(Channel2SocketDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                                        Toast.makeText(Channel2SocketDetailActivity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    alertDialog.setDismissFalse(false);
                                                    Toast.makeText(Channel2SocketDetailActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else{
                                            alertDialog.setDismissFalse(false);
                                            Toast.makeText(Channel2SocketDetailActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
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
        root       = (LinearLayout)findViewById(R.id.root);
        //沉浸式设置支持API19
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = UnitTools.getStatusBarHeight(this);
            root.setPadding(0,top,0,0);
        }
        operation_img = (ImageView)findViewById(R.id.operation);
        operation_img2 = (ImageView)findViewById(R.id.operation2);
        showStatus = (TextView) findViewById(R.id.showStatus);
        showStatus2 = (TextView)findViewById(R.id.showStatus2);
        signal = (ImageView) findViewById(R.id.signalPosition);
        deviceLogo = (ImageView) findViewById(R.id.devicePosition);
        deviceLogo.setImageResource(R.drawable.detail20);
        newAction();
        eq_name = (TextView)findViewById(R.id.eq_name);
        eq_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        eq_name.setSelected(true);
        eq_name.setFocusable(true);
        eq_name.setFocusableInTouchMode(true);
        eq_name.setMarqueeRepeatLimit(-1);
        if(TextUtils.isEmpty(device.getEquipmentName())){
            eq_name.setText(getResources().getString(R.string.two_channel_socket)+device.getEqid());
        }else{
            eq_name.setText(device.getEquipmentName());
        }

        initLogHistoryDrawer();

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
        operation_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    chuli();
            }
        });
        operation_img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chuli2();
            }
        });
    }
    private void chuli(){

        if(device.getState() == null || "".equals(device.getState()) || device.getState().length()!=8){
            sd.sendEquipmentCommand(eqid,"01010000");
        }else {
            String status2 = device.getState().substring(6,8);
            String status_to = "00";
            if("00".equals(status2)){
                status_to = "01";
            }else if("01".equals(status2)){
                status_to = "00";
            }else if("02".equals(status2)){
                status_to = "01";
            }else if("03".equals(status2)){
                status_to = "00";
            }

            String aaa = "01" + status_to +"00"+"00";//目前固定使用01
            sd.sendEquipmentCommand(eqid,aaa);
        }
    }

    private void chuli2(){

        if(device.getState() == null || "".equals(device.getState()) || device.getState().length()!=8){
            sd.sendEquipmentCommand(eqid,"02020000");
        }else {
            String status2 = device.getState().substring(6,8);
            String status_to = "00";
            if("00".equals(status2)){
                status_to = "02";
            }else if("02".equals(status2)){
                status_to = "00";
            }else if("01".equals(status2)){
                status_to = "02";
            }else if("03".equals(status2)){
                status_to = "00";
            }

            String aaa = "02" + status_to +"00"+"00";//目前固定使用01
            sd.sendEquipmentCommand(eqid,aaa);
        }
    }


    @Override
    protected void doStatusShow(String aaaa) {
        try {
        String signal1 = aaaa.substring(0,2);
        String socketStatus = aaaa.substring(6,8);

            signal.setImageResource(ShowBascInfor.choseSPic(signal1));
            if ("01".equals(socketStatus)) {
//                    holder.s.setText("闭合");
                showStatus2.setVisibility(View.VISIBLE);
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus2.setTextColor(getResources().getColor(R.color.device_offine));
                operation_img.setImageResource(R.drawable.detail_switch_on);
                operation_img2.setImageResource(R.drawable.detail_switch_off);
                showStatus.setText(getResources().getStringArray(R.array.socket_channel)[0]+getResources().getStringArray(R.array.socket_actions)[0]);
                showStatus2.setText(getResources().getStringArray(R.array.socket_channel)[1]+getResources().getStringArray(R.array.socket_actions)[1]);
            } else if ("00".equals(socketStatus)) {
//                    holder.s.setText("断开");
                showStatus2.setVisibility(View.VISIBLE);
                root.setBackgroundColor(getResources().getColor(R.color.device_offine));
                showStatus.setTextColor(getResources().getColor(R.color.device_offine));
                showStatus2.setTextColor(getResources().getColor(R.color.device_offine));
                operation_img.setImageResource(R.drawable.detail_switch_off);
                operation_img2.setImageResource(R.drawable.detail_switch_off);
                showStatus.setText(getResources().getStringArray(R.array.socket_channel)[0]+getResources().getStringArray(R.array.socket_actions)[1]);
                showStatus2.setText(getResources().getStringArray(R.array.socket_channel)[1]+getResources().getStringArray(R.array.socket_actions)[1]);
            }else if ("02".equals(socketStatus)) {
//                    holder.s.setText("断开");
                showStatus2.setVisibility(View.VISIBLE);
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_offine));
                showStatus2.setTextColor(getResources().getColor(R.color.device_error));
                operation_img.setImageResource(R.drawable.detail_switch_off);
                operation_img2.setImageResource(R.drawable.detail_switch_on);
                showStatus.setText(getResources().getStringArray(R.array.socket_channel)[0]+getResources().getStringArray(R.array.socket_actions)[1]);
                showStatus2.setText(getResources().getStringArray(R.array.socket_channel)[1]+getResources().getStringArray(R.array.socket_actions)[0]);
            }else if ("03".equals(socketStatus)) {
//                    holder.s.setText("断开");
                showStatus2.setVisibility(View.VISIBLE);
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus2.setTextColor(getResources().getColor(R.color.device_error));
                operation_img.setImageResource(R.drawable.detail_switch_on);
                operation_img2.setImageResource(R.drawable.detail_switch_on);
                showStatus.setText(getResources().getStringArray(R.array.socket_channel)[0]+getResources().getStringArray(R.array.socket_actions)[0]);
                showStatus2.setText(getResources().getStringArray(R.array.socket_channel)[1]+getResources().getStringArray(R.array.socket_actions)[0]);
            }else {
                root.setBackgroundColor(getResources().getColor(R.color.device_offine));
                showStatus.setTextColor(getResources().getColor(R.color.device_offine));
                showStatus2.setVisibility(View.GONE);
                operation_img.setImageResource(R.drawable.detail_switch_off);
                operation_img2.setImageResource(R.drawable.detail_switch_off);
                showStatus.setText(getResources().getString(R.string.offline));
            }
    }catch (Exception e){
        root.setBackgroundColor(getResources().getColor(R.color.device_offine));
        showStatus.setTextColor(getResources().getColor(R.color.device_offine));
        showStatus2.setVisibility(View.GONE);
        operation_img.setImageResource(R.drawable.detail_switch_off);
        operation_img2.setImageResource(R.drawable.detail_switch_off);
        showStatus.setText(getResources().getString(R.string.off_line));
        signal.setImageResource(ShowBascInfor.choseSPic("04"));
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
            Toast.makeText(Channel2SocketDetailActivity.this,getResources().getString(R.string.update_name_success),Toast.LENGTH_SHORT).show();
        }else if(event.getEvent() == SendCommand.DELETE_EQUIPMENT_DETAIL){
            SendCommand.clearCommnad();
            Toast.makeText(Channel2SocketDetailActivity.this,getResources().getString(R.string.delete_success),Toast.LENGTH_SHORT).show();
            ED = new EquipDAO(Channel2SocketDetailActivity.this);
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


}
