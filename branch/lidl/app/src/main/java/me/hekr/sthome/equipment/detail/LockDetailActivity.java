package me.hekr.sthome.equipment.detail;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.android.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.InvalidClassException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

import me.hekr.sthome.AddDeviceActivity;
import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.commonBaseView.CodeEdit;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ECPreferenceSettings;
import me.hekr.sthome.tools.ECPreferences;
import me.hekr.sthome.tools.EmojiFilter;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by jishu0001 on 2016/9/26.
 */
public class LockDetailActivity extends AbstractDetailActivity {
    private static final String TAG = "LockDetailActivity";
    private AtomicBoolean flag_active = new AtomicBoolean(false);
    private ECAlertDialog alertDialog2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_smalarm);
        initData();
        initViewGuider();
    }



    private void initData() {
        EventBus.getDefault().register(this);
        try{
            device = (EquipmentBean) this.getIntent().getSerializableExtra("device");
        }catch(Exception e){
            Log.i(TAG,"device is null");
        }
        sd = new SendEquipmentData(this) {
            @Override
            protected void sendEquipmentDataFailed() {
//                operationFailed();
//                Toast.makeText(CoDetailActivity.this,"operation failed",Toast.LENGTH_LONG).show();
            }

            @Override
            protected void sendEquipmentDataSuccess() {
//                Toast.makeText(CoDetailActivity.this,"operation success",Toast.LENGTH_LONG).show();
            }
        };
    }

    private void initViewGuider() {
        battay_text =(TextView)findViewById(R.id.quantitydesc);
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
                ECListDialog ecListDialog = new ECListDialog(LockDetailActivity.this,getResources().getStringArray(R.array.DeivceOperation));
                ecListDialog.setTitle(getResources().getString(R.string.manage));
                ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(Dialog d, int position) {

                        switch (position){
                            case 0:
                                SendCommand.Command = SendCommand.REPLACE_EQUIPMENT;
                                sd.replaceEquipment(device.getEqid());
                                Intent intent =new Intent(LockDetailActivity.this,AddDeviceActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("eqid",device.getEqid());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                break;
                            case 1:
                                ECAlertDialog elc = ECAlertDialog.buildAlert(LockDetailActivity.this,getResources().getString(R.string.delete_or_not), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                alertDialog = ECAlertDialog.buildAlert(LockDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                                        Toast.makeText(LockDetailActivity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    alertDialog.setDismissFalse(false);
                                                    Toast.makeText(LockDetailActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else{
                                            alertDialog.setDismissFalse(false);
                                            Toast.makeText(LockDetailActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
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
        root       = findViewById(R.id.root);
        //沉浸式设置支持API19
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = UnitTools.getStatusBarHeight(this);
            root.setPadding(0,top,0,0);
        }
        eq_name    =  (TextView)findViewById(R.id.eq_name);
        eq_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        eq_name.setSelected(true);
        eq_name.setFocusable(true);
        eq_name.setFocusableInTouchMode(true);
        eq_name.setMarqueeRepeatLimit(-1);
        if(TextUtils.isEmpty(device.getEquipmentName())){
            eq_name.setText(getResources().getString(R.string.lock)+device.getEqid());
        }else{
            eq_name.setText(device.getEquipmentName());
        }
        showStatus = (TextView) findViewById(R.id.showStatus);
        signal = (ImageView) findViewById(R.id.signalPosition);
        quatity = (ImageView) findViewById(R.id.quantityPosition);
        deviceLogo = (ImageView) findViewById(R.id.devicePosition);
        deviceLogo.setImageResource(R.drawable.detail19);
        operation = (TextView) findViewById(R.id.operation);
        operation.setVisibility(View.VISIBLE);
        operation.setText(getResources().getString(R.string.unlock));
        operation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                alertDialog = ECAlertDialog.buildAlert(LockDetailActivity.this, getResources().getString(R.string.please_input_unlock_password),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.setDismissFalse(true);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CodeEdit text = (CodeEdit) alertDialog.getContent().findViewById(R.id.tet);
                        String psw = text.getCodeEdit().getText().toString().trim();

                        if(!TextUtils.isEmpty(psw)){

                            if(flag_active.get()){
                                alertDialog.setDismissFalse(true);
                                SendCommand.Command = SendCommand.EQUIPMENT_CONTROL;
                                sd.sendEquipmentCommand(device.getEqid(), psw);

                            }
                            else{
                                alertDialog.setDismissFalse(false);
                                Toast.makeText(LockDetailActivity.this,getResources().getString(R.string.no_actived),Toast.LENGTH_SHORT).show();
                            }

                            if(isRememberLockPassword()){
                                try {
                                    ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_LOCK_PASSWORD, psw, true);
                                } catch (InvalidClassException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                try {
                                    ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_LOCK_PASSWORD, "", true);
                                } catch (InvalidClassException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else{
                            alertDialog.setDismissFalse(false);
                            Toast.makeText(LockDetailActivity.this,getResources().getString(R.string.please_input_complete),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                alertDialog.setContentView(R.layout.password_edit_alert);
                alertDialog.setTitle(getResources().getString(R.string.please_input_unlock_password));
                CodeEdit text = (CodeEdit) alertDialog.getContent().findViewById(R.id.tet);
                RadioButton radioButton = (RadioButton) alertDialog.getContent().findViewById(R.id.rem);
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       if(isRememberLockPassword()){
                           try {
                               ((RadioButton)view).setButtonDrawable(R.drawable.save_pass_2);
                               ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_CONFIG_REMEMBER_LOCK_PASSWORD, false, true);
                           } catch (InvalidClassException e) {
                               e.printStackTrace();
                           }

                       }else{
                           try {
                               ((RadioButton)view).setButtonDrawable(R.drawable.save_pass_3);
                               ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_CONFIG_REMEMBER_LOCK_PASSWORD, true, true);
                           } catch (InvalidClassException e) {
                               e.printStackTrace();
                           }
                       }
                    }
                });
                text.getCodeEdit().setRawInputType(InputType.TYPE_CLASS_NUMBER);
                text.getCodeEdit().setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                if(isRememberLockPassword()){
                    radioButton.setButtonDrawable(R.drawable.save_pass_3);
                    text.getCodeEdit().setText(RememberLockPassword());
                    text.getCodeEdit().setSelection(RememberLockPassword().length());
                }else {
                    radioButton.setButtonDrawable(R.drawable.save_pass_2);
                }

                alertDialog.show();
            }
        });
        emergencyCall = (TextView) findViewById(R.id.emergencyCall);
        emergencyCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneAlert();
            }
        });

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
                Toast.makeText(this, getResources().getString(R.string.name_is_repeat),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void doStatusShow(String aaaa) {


        try {
            String signal1 = aaaa.substring(0,2);
            String quantity1 = aaaa.substring(2,4);
            String draw = aaaa.substring(4,6);
            String num = aaaa.substring(6,8);
            int qqqq = Integer.parseInt(quantity1,16);
            int numberr = Integer.parseInt(num,16);
            quatity.setImageResource(ShowBascInfor.choseQPic(qqqq));
            battay_text.setText(ShowBascInfor.choseLNum(qqqq));
            if(signal1 != null){
                signal.setImageResource(ShowBascInfor.choseSPic(signal1));

            }
            if("AA".equals(draw)){

                if( qqqq <= 15 ){
                    root.setBackgroundColor(getResources().getColor(R.color.device_warn));
                    showStatus.setTextColor(getResources().getColor(R.color.device_warn));
                    showStatus.setText(getResources().getString(R.string.low_battery));
                }else{
                    showStatus.setText(getResources().getString(R.string.normal));
                    root.setBackgroundColor(getResources().getColor(R.color.device_offine));
                    showStatus.setTextColor(getResources().getColor(R.color.device_offine));
                }
                flag_active.set(false);
            }else if("AB".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_offine));
                showStatus.setTextColor(getResources().getColor(R.color.device_offine));
                showStatus.setText(getResources().getString(R.string.actived));
                flag_active.set(true);
            }else if("51".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText("#" +numberr + "\n" + getResources().getString(R.string.code_open));
                flag_active.set(false);
            }else if("52".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText("#" +numberr + "\n" +getResources().getString(R.string.card_open));
                flag_active.set(false);
            }else if("53".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText("#" +numberr + "\n" +getResources().getString(R.string.footprint_open));
                flag_active.set(false);
            }else if("10".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText(getResources().getString(R.string.illegal_operation));
                flag_active.set(false);
            }else if("20".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText(getResources().getString(R.string.dismantle));
                flag_active.set(false);
            }else if("30".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText(getResources().getString(R.string.coercion));
                flag_active.set(false);
            }else if("40".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_warn));
                showStatus.setTextColor(getResources().getColor(R.color.device_warn));
                showStatus.setText(getResources().getString(R.string.low_battery));
                flag_active.set(false);
            }else if("55".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText(getResources().getString(R.string.remote_unlock_success));
                flag_active.set(true);
            }else if("56".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setTextColor(getResources().getColor(R.color.device_error));
                showStatus.setText(getResources().getString(R.string.code_fault));
                flag_active.set(true);
            }else if("60".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_offine));
                showStatus.setTextColor(getResources().getColor(R.color.device_offine));
                showStatus.setText(getResources().getString(R.string.alarm_remove));
                flag_active.set(false);
            }else{
                root.setBackgroundColor(getResources().getColor(R.color.device_offine));
                showStatus.setTextColor(getResources().getColor(R.color.device_offine));
                showStatus.setText(getResources().getString(R.string.offline));
                flag_active.set(false);
            }
        }catch (Exception e){
            root.setBackgroundColor(getResources().getColor(R.color.device_offine));
            showStatus.setTextColor(getResources().getColor(R.color.device_offine));
            showStatus.setText(getResources().getString(R.string.offline));
            quatity.setImageResource(ShowBascInfor.choseQPic(100));
            battay_text.setText(ShowBascInfor.choseLNum(100));
            operation.setVisibility(View.VISIBLE);
            flag_active.set(false);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent() == SendCommand.MODIFY_EQUIPMENT_NAME){
            SendCommand.clearCommnad();
            Toast.makeText(LockDetailActivity.this,getResources().getString(R.string.update_name_success),Toast.LENGTH_SHORT).show();
        }else if(event.getEvent() == SendCommand.DELETE_EQUIPMENT_DETAIL){
            SendCommand.clearCommnad();
            Toast.makeText(LockDetailActivity.this,getResources().getString(R.string.delete_success),Toast.LENGTH_SHORT).show();
            ED = new EquipDAO(LockDetailActivity.this);
            ED.deleteByEqid(device);
            finish();
        }else if(event.getEvent()== SendCommand.EQUIPMENT_CONTROL){
            SendCommand.clearCommnad();
            Toast.makeText(LockDetailActivity.this,getResources().getString(R.string.success_test),Toast.LENGTH_SHORT).show();
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



    private boolean isRememberLockPassword(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_CONFIG_REMEMBER_LOCK_PASSWORD;
        boolean autoflag = sharedPreferences.getBoolean(flag.getId(), (boolean) flag.getDefaultValue());
        return autoflag;
    }

    private String RememberLockPassword(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_LOCK_PASSWORD;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }
}
