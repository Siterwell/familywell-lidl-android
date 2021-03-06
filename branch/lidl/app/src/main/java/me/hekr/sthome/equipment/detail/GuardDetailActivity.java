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
 * Created by jishu0001 on 2016/10/14.
 */
public class GuardDetailActivity extends AppCompatActivity {
    private static final  int GETBACK_SUCCESS = 1;
    private static final int GETBACK_FAILED = 2;
    private MyInforHandler myInforHandler;

    private static final String TAG = "GuardDetail";
    private EquipDAO ED;
    private ImageView deviceLogo;
    private TextView showStatus;
    private ImageView signal,quantity;
    private String status2="",eqid="";
    //    private SwitchCompat switcher;
    private JSONObject json;
    private EquipmentBean eq,device;
    private SendEquipmentData sd;
    private int num =0;

    private int xianzhuang = 0; //condition
    private ImageView back_img;
    private TextView  edt_txt,eq_name;
    private LinearLayout root;
    private ImageView operation_img;
    private ECAlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_socket);
        initData();
        initViewGuider();
    }


    private void initData() {
        EventBus.getDefault().register(this);
        myInforHandler = new MyInforHandler() {
            @Override
            protected void operationSuccess() {
              //  Toast.makeText(GuardDetailActivity.this,getResources().getString(R.string.operation_success),Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void operationFailed() {
                Toast.makeText(GuardDetailActivity.this,getResources().getString(R.string.operation_failed),Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(GuardDetailActivity.this,"operation failed",Toast.LENGTH_LONG).show();
                myInforHandler.sendEmptyMessage(GETBACK_FAILED);
            }

            @Override
            protected void sendEquipmentDataSuccess() {
//                Toast.makeText(GuardDetailActivity.this,"operation success",Toast.LENGTH_LONG).show();
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
                ECListDialog ecListDialog = new ECListDialog(GuardDetailActivity.this,getResources().getStringArray(R.array.DeivceOperation));
                ecListDialog.setTitle(getResources().getString(R.string.manage));
                ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(Dialog d, int position) {

                        switch (position){
                            case 0:
                                SendCommand.Command = SendCommand.REPLACE_EQUIPMENT;
                                sd.replaceEquipment(device.getEqid());
                                Intent intent =new Intent(GuardDetailActivity.this,AddDeviceActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("eqid",device.getEqid());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                break;
                            case 1:
                                ECAlertDialog elc = ECAlertDialog.buildAlert(GuardDetailActivity.this,getResources().getString(R.string.delete_or_not), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                alertDialog = ECAlertDialog.buildAlert(GuardDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                                        Toast.makeText(GuardDetailActivity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    alertDialog.setDismissFalse(false);
                                                    Toast.makeText(GuardDetailActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else{
                                            alertDialog.setDismissFalse(false);
                                            Toast.makeText(GuardDetailActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
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
        showStatus = (TextView) findViewById(R.id.showStatus);
        signal = (ImageView) findViewById(R.id.signalPosition);
        deviceLogo = (ImageView) findViewById(R.id.devicePosition);
        deviceLogo.setImageResource(R.drawable.detail14);
        eq_name = (TextView)findViewById(R.id.eq_name);
        eq_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        eq_name.setSelected(true);
        eq_name.setFocusable(true);
        eq_name.setFocusableInTouchMode(true);
        eq_name.setMarqueeRepeatLimit(-1);
        if(TextUtils.isEmpty(device.getEquipmentName())){
            eq_name.setText(getResources().getString(R.string.guardor)+device.getEqid());
        }else{
            eq_name.setText(device.getEquipmentName());
        }
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
        operation_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num ++;
                chuli(num);
            }
        });
    }
    private void chuli(int num1){
        if(num1 == 1){
            if(xianzhuang ==1){
                status2="AA";
                num=0;
            }else {
                status2="55";
                num=0;
            }
        }
        String aaa = status2 +"ff"+"ff"+"ff";
        sd.sendEquipmentCommand(eqid,aaa);
    }

    private void doStatusShow(String aaaa) {

        try {
            String signal1 = aaaa.substring(0,2);
            String socketStatus = aaaa.substring(6,8);

            signal.setImageResource(ShowBascInfor.choseSPic(signal1));

            if ("55".equals(socketStatus)) {
//                    holder.s.setText("闭合");
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                operation_img.setImageResource(R.drawable.detail_switch_on);
                showStatus.setText(getResources().getString(R.string.normal));
            } else if ("AA".equals(socketStatus)) {
//                    holder.s.setText("断开");
                root.setBackgroundColor(getResources().getColor(R.color.device_normal));
                showStatus.setTextColor(getResources().getColor(R.color.device_normal));
                operation_img.setImageResource(R.drawable.detail_switch_off);
                showStatus.setText(getResources().getString(R.string.normal));
            }else{
                root.setBackgroundColor(getResources().getColor(R.color.device_offine));
                showStatus.setTextColor(getResources().getColor(R.color.device_offine));
                operation_img.setImageResource(R.drawable.detail_switch_off);
                showStatus.setText(getResources().getString(R.string.off_line));
            }
        }catch (Exception e){
            root.setBackgroundColor(getResources().getColor(R.color.device_offine));
            showStatus.setTextColor(getResources().getColor(R.color.device_offine));
            operation_img.setImageResource(R.drawable.detail_switch_off);
            showStatus.setText(getResources().getString(R.string.off_line));
            signal.setImageResource(ShowBascInfor.choseSPic("04"));
        }


    }

    public void onDestroy() {
        super.onDestroy();
       EventBus.getDefault().unregister(this);
    }

    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event) {
        if (event.getEvent() == SendCommand.MODIFY_EQUIPMENT_NAME) {
            SendCommand.clearCommnad();
            Toast.makeText(GuardDetailActivity.this, getResources().getString(R.string.update_name_success), Toast.LENGTH_SHORT).show();
        } else if (event.getEvent() == SendCommand.DELETE_EQUIPMENT_DETAIL) {
            SendCommand.clearCommnad();
            Toast.makeText(GuardDetailActivity.this, getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
            ED = new EquipDAO(GuardDetailActivity.this);
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
