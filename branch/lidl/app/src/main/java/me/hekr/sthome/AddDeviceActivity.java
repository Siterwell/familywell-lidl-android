package me.hekr.sthome;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by gc-0001 on 2017/2/11.
 */
public class AddDeviceActivity extends TopbarSuperActivity {


    private final String TAG = "AddDeviceActivity";

    private Timer timer = null;
    private Handler handler;
    private TextView textView;
    private String eqid;
    private String title;
    private SendEquipmentData sda;
    private ImageView icon;
    private int count = 0;
    private ECAlertDialog alertDialog;
    private EquipDAO equipDAO;

    private TimerTask task = new TimerTask() {

        @Override
        public void run() {
            count ++;
            Log.i(TAG,"count"+count);
            if(count>=60){
                count = 0;
                handler.sendEmptyMessage(2);
            }
        }
    };

    @Override
    protected void onCreateInit() {
        initdata();
        initview();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device;
    }

    private  void initdata(){
        equipDAO = new EquipDAO(this);
        EventBus.getDefault().register(this);
        eqid = getIntent().getStringExtra("eqid");
        ConnectionPojo.getInstance().folderid = getIntent().getIntExtra("folderid",0);
        if(TextUtils.isEmpty(eqid)){
            title = getResources().getString(R.string.add_equipment);
        }else{
            title = getResources().getString(R.string.replace_equipment);
        }
        sda = new SendEquipmentData(this) {
            @Override
            protected void sendEquipmentDataFailed() {

            }

            @Override
            protected void sendEquipmentDataSuccess() {

            }
        };

    }

    private void initview(){
        textView =(TextView)findViewById(R.id.tishi);
        icon = (ImageView)findViewById(R.id.icon);
        getTopBarView().setTopBarStatus(1, 1, 1, null, title, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sda.cancelIncreaseEq();
              finish();
              overridePendingTransition(0, R.anim.closefromtop);
            }
        },null);
        getTopBarView().getBackView().setImageResource(R.drawable.down);
        timer = new Timer();
        timer.schedule(task,1000,1000);
        handler = new Handler() {

            public void handleMessage(Message msg){
                super.handleMessage(msg);
//        Log.i(TAG, (String)msg.obj);
                switch (msg.what){
                    case 1:
                        finish();
                        overridePendingTransition(0, R.anim.closefromtop);
                        break;
                    case 2:
                        finish();
                        overridePendingTransition(0, R.anim.closefromtop);
                        Toast.makeText(AddDeviceActivity.this,getResources().getString(R.string.cancel),Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };

        UnitTools tools = new UnitTools(this);
        String lan = tools.readLanguage();
        textView.setText(getResources().getString(R.string.add_device_hint));

        if("zh".equals(lan)){
            icon.setImageResource(R.drawable.adevice_zh);
        }else if("de".equals(lan)){
            icon.setImageResource(R.drawable.adevice_de);
        }else if("fr".equals(lan)){
            icon.setImageResource(R.drawable.adevice_fr);
        }else if("es".equals(lan)){
            icon.setImageResource(R.drawable.adevice_es);
        }else {
            icon.setImageResource(R.drawable.adevice_en);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            sda.cancelIncreaseEq();
            finish();
            overridePendingTransition(0, R.anim.closefromtop);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SendCommand.clearCommnad();
        timer.cancel();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
           if(event.getEvent()== SendCommand.INCREACE_EQUIPMENT){
               Toast.makeText(AddDeviceActivity.this,getResources().getString(R.string.add_success),Toast.LENGTH_SHORT).show();
               SendCommand.clearCommnad();
               setEqipmentName(event.getEq_type(),event.getEq_id());

           }else if(event.getEvent()== SendCommand.REPLACE_EQUIPMENT){
               handler.sendEmptyMessage(1);
               SendCommand.clearCommnad();
               Toast.makeText(AddDeviceActivity.this,getResources().getString(R.string.replace_success),Toast.LENGTH_SHORT).show();
           }else if(event.getEvent() == SendCommand.MODIFY_EQUIPMENT_NAME){
               Toast.makeText(AddDeviceActivity.this,getResources().getString(R.string.update_name_success),Toast.LENGTH_SHORT).show();
            SendCommand.clearCommnad();
            handler.sendEmptyMessageDelayed(1,500l);

        }
    }


    private void setEqipmentName(final String eqtype,final String eqid){
         if (!TextUtils.isEmpty(eqtype) && !TextUtils.isEmpty(eqid)){
             String ds2 = null;
             EquipmentBean equipmentBean =  equipDAO.findByeqid(eqid, ConnectionPojo.getInstance().deviceTid);
             if(equipmentBean==null){
                 ds2 = String.format(getResources().getString(R.string.set_eq_name), NameSolve.getDefaultName(this,eqtype,eqid));
             }else {
                 if(TextUtils.isEmpty(equipmentBean.getEquipmentName())){
                     ds2 = String.format(getResources().getString(R.string.already_in_gateway), NameSolve.getDefaultName(this,eqtype,eqid));
                 }else{
                     ds2 = String.format(getResources().getString(R.string.already_in_gateway),equipmentBean.getEquipmentName());
                 }

             }

             alertDialog = ECAlertDialog.buildAlert(AddDeviceActivity.this, ds2,getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     alertDialog.setDismissFalse(true);
                 }
             }, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     hideSoftKeyboard();
                     EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                     String newname = text.getText().toString().trim();

                     if(!TextUtils.isEmpty(newname)){

                         try {
                             if(newname.getBytes("GBK").length<=15){
                                 alertDialog.setDismissFalse(true);
                                 updateName(newname,eqid);
                                 String ds = CoderUtils.getAscii(newname);
                                 String dsCRC= ByteUtil.CRCmaker(ds);
                                 SendCommand.Command = SendCommand.MODIFY_EQUIPMENT_NAME;
                                 sda.modifyEquipmentName(eqid,ds + dsCRC);
                             }else{
                                 alertDialog.setDismissFalse(false);
                                 Toast.makeText(AddDeviceActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                             }
                         } catch (UnsupportedEncodingException e) {
                             e.printStackTrace();
                         }

                     }
                     else{
                         alertDialog.setDismissFalse(false);
                         Toast.makeText(AddDeviceActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
                     }

                 }
             });
             alertDialog.setContentView(R.layout.edit_alert);
             alertDialog.setTitle(ds2);
             alertDialog.setSuperTitle(getResources().getString(R.string.success_add));
             alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                 @Override
                 public void onDismiss(DialogInterface dialogInterface) {
                     handler.sendEmptyMessageDelayed(1,500l);
                     hideSoftKeyboard();
                 }
             });
             alertDialog.show();
         }
    }


    private void updateName(String edit,String eqid) {
        EquipmentBean equipmentBean = new EquipmentBean();
        equipmentBean.setEquipmentName(edit);
        equipmentBean.setDeviceid(ConnectionPojo.getInstance().deviceTid);
        equipmentBean.setEqid(eqid);
           EquipDAO ED = new EquipDAO(this);
            try {
                ED.updateName(equipmentBean);
            }catch (Exception e){
                Toast.makeText(this, getResources().getString(R.string.name_is_repeat),Toast.LENGTH_SHORT).show();
            }
            }
}
