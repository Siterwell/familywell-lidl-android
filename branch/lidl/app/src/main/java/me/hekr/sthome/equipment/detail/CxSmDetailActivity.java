package me.hekr.sthome.equipment.detail;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.EmojiFilter;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by jishu0001 on 2016/9/26.
 */
public class CxSmDetailActivity extends AbstractDetailActivity {
    private static final String TAG = "CxSmDetailActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_smalarm);
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
    }

    private void initViewGuider() {
        battay_text = (TextView)findViewById(R.id.quantitydesc);
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
                ECListDialog ecListDialog = new ECListDialog(CxSmDetailActivity.this,getResources().getStringArray(R.array.DeivceOperation));
                ecListDialog.setTitle(getResources().getString(R.string.manage));
                ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(Dialog d, int position) {

                        switch (position){
                            case 0:
                                SendCommand.Command = SendCommand.REPLACE_EQUIPMENT;
                                sd.replaceEquipment(device.getEqid());
                                Intent intent =new Intent(CxSmDetailActivity.this,AddDeviceActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("eqid",device.getEqid());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                break;
                            case 1:
                                ECAlertDialog elc = ECAlertDialog.buildAlert(CxSmDetailActivity.this,getResources().getString(R.string.delete_or_not), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                alertDialog = ECAlertDialog.buildAlert(CxSmDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                                        Toast.makeText(CxSmDetailActivity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    alertDialog.setDismissFalse(false);
                                                    Toast.makeText(CxSmDetailActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else{
                                            alertDialog.setDismissFalse(false);
                                            Toast.makeText(CxSmDetailActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
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
        deviceLogo.setImageResource(R.drawable.detail8);
        operation = (TextView) findViewById(R.id.operation);
        try {
            int ds = Integer.parseInt(device.getEquipmentDesc().substring(device.getEquipmentDesc().length()-1),16);
            if(ds<=7||ds>=14){
                operation.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        operation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                sendSlienceSign(operation.getText().toString().trim());
            }
        });
        emergencyCall = (TextView) findViewById(R.id.emergencyCall);
        emergencyCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneAlert();
            }
        });
        eq_name = (TextView)findViewById(R.id.eq_name);
        eq_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        eq_name.setSelected(true);
        eq_name.setFocusable(true);
        eq_name.setFocusableInTouchMode(true);
        eq_name.setMarqueeRepeatLimit(-1);
        if(TextUtils.isEmpty(device.getEquipmentName())){
            eq_name.setText(getResources().getString(R.string.cxsmalarm)+device.getEqid());
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
                Toast.makeText(CxSmDetailActivity.this,getResources().getString(R.string.name_is_repeat),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doStatusShow(String aaaa) {

        try {
            String signal1 = aaaa.substring(0,2);
            String quantity1 = aaaa.substring(2,4);
            String draw = aaaa.substring(4,6);
            int qqqq = Integer.parseInt(quantity1,16);
            quatity.setImageResource(ShowBascInfor.choseQPic(qqqq));
            battay_text.setText(ShowBascInfor.choseLNum(qqqq));
            if(signal1 != null){
                signal.setImageResource(ShowBascInfor.choseSPic(signal1));
            }
            if("17".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText(getResources().getString(R.string.cxalert7));
            }else if("19".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText(getResources().getString(R.string.cxalert9));
                operation.setText(getResources().getString(R.string.cxalert11));
            }else if("12".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_warn));
                showStatus.setTextColor(getResources().getColor(R.color.device_warn));
                showStatus.setText(getResources().getString(R.string.cxalert2));
                operation.setText(getResources().getString(R.string.cxalert5));
            }else if("15".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_warn));
                showStatus.setTextColor(getResources().getColor(R.color.device_warn));
                showStatus.setText(getResources().getString(R.string.cxalert5));
            }else if("1B".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText(getResources().getString(R.string.cxalert11));
            }else if("AA".equals(draw)){
                if(qqqq<=15){
                    root.setBackgroundColor(getResources().getColor(R.color.device_warn));
                    showStatus.setTextColor(getResources().getColor(R.color.device_warn));
                    operation.setText(getResources().getString(R.string.cxalert6));
                    showStatus.setText(getResources().getString(R.string.low_battery));
                }else{
                    root.setBackgroundColor(getResources().getColor(R.color.device_normal));
                    showStatus.setTextColor(getResources().getColor(R.color.device_normal));
                    operation.setText(getResources().getString(R.string.cxalert7));
                    showStatus.setText(getResources().getString(R.string.normal));
                }


            }
            else{
                root.setBackgroundColor(getResources().getColor(R.color.devices_offline));
                showStatus.setTextColor(getResources().getColor(R.color.devices_offline));
                showStatus.setText(getResources().getString(R.string.offline));
            }
        }catch (Exception e){
            root.setBackgroundColor(getResources().getColor(R.color.devices_offline));
            showStatus.setTextColor(getResources().getColor(R.color.devices_offline));
            showStatus.setText(getResources().getString(R.string.offline));
            quatity.setImageResource(ShowBascInfor.choseQPic(100));
            battay_text.setText(ShowBascInfor.choseLNum(100));
        }


    }


    private void sendSlienceSign(String ds){

        try {

            if(getResources().getString(R.string.cxalert7).equals(ds)){
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                 sd.sendEquipmentCommand(device.getEqid(),"17000000");
            }else if(getResources().getString(R.string.cxalert11).equals(ds)){
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(device.getEqid(),"1B000000");
            }else if(getResources().getString(R.string.cxalert5).equals(ds)){
                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                sd.sendEquipmentCommand(device.getEqid(),"15000000");
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
            Toast.makeText(CxSmDetailActivity.this,getResources().getString(R.string.update_name_success),Toast.LENGTH_SHORT).show();
        }else if(event.getEvent() == SendCommand.DELETE_EQUIPMENT_DETAIL){
            SendCommand.clearCommnad();
            Toast.makeText(CxSmDetailActivity.this,getResources().getString(R.string.delete_success),Toast.LENGTH_SHORT).show();
            ED = new EquipDAO(CxSmDetailActivity.this);
            ED.deleteByEqid(device);
            finish();
        }else if(event.getEvent()== SendCommand.EQUIPMENT_CONTROL){
            SendCommand.clearCommnad();
            Toast.makeText(CxSmDetailActivity.this,getResources().getString(R.string.success_test),Toast.LENGTH_SHORT).show();
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
}
