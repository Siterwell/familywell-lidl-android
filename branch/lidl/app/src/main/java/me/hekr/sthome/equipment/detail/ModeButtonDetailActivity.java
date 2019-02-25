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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.android.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.List;

import me.hekr.sthome.AddDeviceActivity;
import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.modeladapter.ModeButtonAdapter;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.ShortcutBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.ShortcutDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.EmojiFilter;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.tools.SendSceneGroupData;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by jishu0001 on 2016/9/29.
 */
public class ModeButtonDetailActivity extends AppCompatActivity implements ModeButtonAdapter.ChangeDesMode{
    private static final String TAG = "ModeButtonDetail";
    private ImageView signal,quatity,deviceLogo;
    private TextView showStatus;
    private EquipmentBean device;
    private EquipDAO ED;
    private SysmodelDAO sysmodelDAO;
    private SceneDAO sceneDAO;
    private ShortcutDAO shortcutDAO;
    private ImageView back_img;
    private TextView  edt_txt,eq_name,battay_text;
    private LinearLayout root;
    private ECAlertDialog alertDialog;
    private SendEquipmentData sd;
    private ListView listView_sys;
    private ModeButtonAdapter modeButtonAdapter;
    private List<SysModelBean> sysModelBeans;
    private List<String> sysmodelnames;
    private ECListDialog ecListDialog;
    private ShortcutBean shortcutBean;
    private SendSceneGroupData ssgd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mode_button);
        initData();
        initViewGuider();
    }



    private void initData() {
        sysmodelDAO = new SysmodelDAO(this);
        shortcutDAO = new ShortcutDAO(this);
        sceneDAO    = new SceneDAO(this);
        sysModelBeans = sysmodelDAO.findAllSys(ConnectionPojo.getInstance().deviceTid);
        sysmodelnames = sysmodelDAO.findAllSysName(ConnectionPojo.getInstance().deviceTid);
        if(sysmodelnames.size()>=3){
            sysmodelnames.set(0,getResources().getString(R.string.home_mode));
            sysmodelnames.set(1,getResources().getString(R.string.out_mode));
            sysmodelnames.set(2,getResources().getString(R.string.sleep_mode));
        }

        EventBus.getDefault().register(this);
        sd = new SendEquipmentData(this) {
            @Override
            protected void sendEquipmentDataFailed() {

            }

            @Override
            protected void sendEquipmentDataSuccess() {

            }
        };
        ssgd = new SendSceneGroupData(this) {
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
        listView_sys = (ListView)findViewById(R.id.modelist);
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
                ECListDialog ecListDialog = new ECListDialog(ModeButtonDetailActivity.this,getResources().getStringArray(R.array.DeivceOperation));
                ecListDialog.setTitle(getResources().getString(R.string.manage));
                ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(Dialog d, int position) {

                        switch (position){
                            case 0:
                                SendCommand.Command = SendCommand.REPLACE_EQUIPMENT;
                                sd.replaceEquipment(device.getEqid());
                                Intent intent =new Intent(ModeButtonDetailActivity.this,AddDeviceActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("eqid",device.getEqid());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                break;
                            case 1:
                                ECAlertDialog elc = ECAlertDialog.buildAlert(ModeButtonDetailActivity.this,getResources().getString(R.string.delete_or_not), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                alertDialog = ECAlertDialog.buildAlert(ModeButtonDetailActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                                                        Toast.makeText(ModeButtonDetailActivity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    alertDialog.setDismissFalse(false);
                                                    Toast.makeText(ModeButtonDetailActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else{
                                            alertDialog.setDismissFalse(false);
                                            Toast.makeText(ModeButtonDetailActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
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
        deviceLogo.setImageResource(R.drawable.detail16);

        modeButtonAdapter = new ModeButtonAdapter(this,sysModelBeans,this,device);
        listView_sys.setAdapter(modeButtonAdapter);
        eq_name = (TextView)findViewById(R.id.eq_name);
        eq_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        eq_name.setSelected(true);
        eq_name.setFocusable(true);
        eq_name.setFocusableInTouchMode(true);
        eq_name.setMarqueeRepeatLimit(-1);
        if(TextUtils.isEmpty(device.getEquipmentName())){
            eq_name.setText(getResources().getString(R.string.mode_button)+device.getEqid());
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
                Toast.makeText(ModeButtonDetailActivity.this,getResources().getString(R.string.name_is_repeat),Toast.LENGTH_SHORT).show();
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
            if("55".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_error));
                showStatus.setText(getResources().getString(R.string.help));
            }else if("AA".equals(draw) || "01".equals(draw) || "02".equals(draw) || "04".equals(draw) || "08".equals(draw)){
                if( qqqq <= 15 ){
                    root.setBackgroundColor(getResources().getColor(R.color.device_warn));
                    showStatus.setText(getResources().getString(R.string.low_battery));
                }else{
                    root.setBackgroundColor(getResources().getColor(R.color.device_offine));
                    showStatus.setText(getResources().getString(R.string.normal));
                }

            }else if("66".equals(draw)){
                root.setBackgroundColor(getResources().getColor(R.color.device_offine));
                showStatus.setText(getResources().getString(R.string.offline));
            }else{
                root.setBackgroundColor(getResources().getColor(R.color.device_offine));
                showStatus.setText(getResources().getString(R.string.offline));
            }
        }catch (Exception e){
            root.setBackgroundColor(getResources().getColor(R.color.device_offine));
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
            Toast.makeText(ModeButtonDetailActivity.this,getResources().getString(R.string.update_name_success),Toast.LENGTH_SHORT).show();
        }else if(event.getEvent() == SendCommand.DELETE_EQUIPMENT_DETAIL){
            SendCommand.clearCommnad();
            Toast.makeText(ModeButtonDetailActivity.this,getResources().getString(R.string.delete_success),Toast.LENGTH_SHORT).show();
            ED = new EquipDAO(ModeButtonDetailActivity.this);
            if(NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(device.getEquipmentDesc()))){
                shortcutDAO.deleteShortcurtByEqid(ConnectionPojo.getInstance().deviceTid,device.getEqid());
            }
            ED.deleteByEqid(device);
            finish();
        }else if(event.getEvent()== SendCommand.MODIFY_SCENE_GROUP){
            SendCommand.clearCommnad();
            Toast.makeText(ModeButtonDetailActivity.this,getResources().getString(R.string.success_set),Toast.LENGTH_SHORT).show();
            if(TextUtils.isEmpty(shortcutBean.getDes_sid())){
                shortcutDAO.deleteShortcurt(shortcutBean.getSrc_sid(), ConnectionPojo.getInstance().deviceTid,shortcutBean.getEqid());
            }else{
                shortcutDAO.insertShortcut(shortcutBean);
            }
            modeButtonAdapter.notifyDataSetChanged();
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
    public void showDialogd(final int index) {
       ecListDialog = new ECListDialog(this,sysmodelnames);
       ecListDialog.setTitle(R.string.please_choose_mode);
       ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
           @Override
           public void onDialogItemClick(Dialog d, int position) {

                     shortcutBean = new ShortcutBean();
                     shortcutBean.setDes_sid(sysModelBeans.get(position).getSid());
                     shortcutBean.setSrc_sid(sysModelBeans.get(index).getSid());
                     shortcutBean.setDelay(0);
                     shortcutBean.setDeviceid(device.getDeviceid());
                     shortcutBean.setEqid(device.getEqid());
                     String code = getcode(sysModelBeans.get(index),shortcutBean.getEqid(),shortcutBean.getDes_sid());
                     String crc = ByteUtil.CRCmakerCharAndCode(code);
                     SendCommand.Command = SendCommand.MODIFY_SCENE_GROUP;
                     ssgd.modifySceneGroup(code+crc);
           }
       });
        ecListDialog.show();
    }


    private String getcode(SysModelBean sysModelBean, @NotNull String thiseqid, String des_sid) {

        String name = null;
        if(Integer.parseInt(sysModelBean.getSid())>2){
            name = sysModelBean.getModleName();
        }else{
            switch (Integer.parseInt(sysModelBean.getSid())){
                case 0:
                    name = "Home";
                    break;
                case 1:
                    name = "Away";
                    break;
                case 2:
                    name = "Sleep";
                    break;
                default:break;
            }
        }

        int  length = 0;
        length+=2;//the total num length

        String id2 = sysModelBean.getSid();
        length += 1;//the scene id

        String btnNum = "";
        List<ShortcutBean> shortcutBeans = shortcutDAO.findShorcutsBysid(ConnectionPojo.getInstance().deviceTid,sysModelBean.getSid());



        String shortcut = "";

        boolean flag_this_eqid = false;
        for (int i = 0;i<shortcutBeans.size();i++){
            String eqid = "";
            String dessid = "";
            if(thiseqid.equals(shortcutBeans.get(i).getEqid())){
                flag_this_eqid = true;

                if(!TextUtils.isEmpty(des_sid)){
                    if (Integer.toHexString(Integer.parseInt(thiseqid)).length()<2){  //new mid
                        eqid = "000"+Integer.toHexString(Integer.parseInt(thiseqid));
                    }else{
                        eqid ="00"+Integer.toHexString(Integer.parseInt(thiseqid));
                    }

                    if (Integer.toHexString(Integer.parseInt(des_sid)).length()<2){  //new mid
                        dessid = "0"+Integer.toHexString(Integer.parseInt(des_sid))+"000000";
                    }else{
                        dessid =Integer.toHexString(Integer.parseInt(des_sid))+"000000";
                    }

                    shortcut+=(eqid+dessid+"00");
                    length += 7;
                }

            }else{

                if (Integer.toHexString(Integer.parseInt(shortcutBeans.get(i).getEqid())).length()<2){  //new mid
                    eqid = "000"+Integer.toHexString(Integer.parseInt(shortcutBeans.get(i).getEqid()));
                }else{
                    eqid ="00"+Integer.toHexString(Integer.parseInt(shortcutBeans.get(i).getEqid()));
                }

                if (Integer.toHexString(Integer.parseInt(shortcutBeans.get(i).getDes_sid())).length()<2){  //new mid
                    dessid = "0"+Integer.toHexString(Integer.parseInt(shortcutBeans.get(i).getDes_sid()))+"000000";
                }else{
                    dessid =Integer.toHexString(Integer.parseInt(shortcutBeans.get(i).getDes_sid()))+"000000";
                }

                shortcut+=(eqid+dessid+"00");
                length += 7;
            }



        }

        if(!flag_this_eqid){
            String eqid = "";
            String dessid = "";
            if (Integer.toHexString(Integer.parseInt(thiseqid)).length()<2){  //new mid
                eqid = "000"+Integer.toHexString(Integer.parseInt(thiseqid));
            }else{
                eqid ="00"+Integer.toHexString(Integer.parseInt(thiseqid));
            }

            if (Integer.toHexString(Integer.parseInt(des_sid)).length()<2){  //new mid
                dessid = "0"+Integer.toHexString(Integer.parseInt(des_sid))+"000000";
            }else{
                dessid =Integer.toHexString(Integer.parseInt(des_sid))+"000000";
            }

            shortcut+=(eqid+dessid+"00");
            length += 7;


            if (Integer.toHexString(shortcutBeans.size()+1).length()<2){  //new mid
                btnNum = "0"+Integer.toHexString(shortcutBeans.size()+1);
            }else{
                btnNum =Integer.toHexString(shortcutBeans.size()+1);
            }
            length+=1;//button num

        }else{
            int ds = TextUtils.isEmpty(des_sid)? (shortcutBeans.size()-1):shortcutBeans.size();

            if (Integer.toHexString(ds).length()<2){  //new mid
                btnNum = "0"+Integer.toHexString(ds);
            }else{
                btnNum =Integer.toHexString(ds);
            }
            length+=1;//button num
        }

        //self-define scene num
        length+=1;
        int scene =0;
        //scene id
        String sceneCode ="";
        List<SceneBean> sceneBeanList = sceneDAO.findAllAmBySid(sysModelBean.getSid(), ConnectionPojo.getInstance().deviceTid);
        if(sceneBeanList.size()>0){
            for(int i = 0; i<sceneBeanList.size();i++){
                    scene++;
                    length++;
                    String singleCode ="";
                    //do count
//                if (Integer.toHexString(sceneLists.get(i).getActivityId()).length()<2){  defore
                    if (Integer.toHexString(Integer.parseInt(sceneBeanList.get(i).getMid())).length()<2){  //new mid
                        singleCode = "0"+Integer.toHexString(Integer.parseInt(sceneBeanList.get(i).getMid()));
                    }else{
                        singleCode =Integer.toHexString(Integer.parseInt(sceneBeanList.get(i).getMid()));
                    }
                    sceneCode += singleCode;
            }
        }else {
            length+=0;
            sceneCode = "";
        }

        //增加了颜色的定制
        length+=1;

        String oooo ="0";
        if(Integer.toHexString(length+32).length() < 4){
            for (int i = 0 ; i<4-Integer.toHexString(length+32).length()-1;i++ ){
                oooo += 0;
            }
            oooo += Integer.toHexString(length+32);
        }else{
            oooo = Integer.toHexString(length+32);
        }

        String oo = "0";
        if(Integer.toHexString(scene).length()<2){
            oo = oo + Integer.toHexString(scene);
        }else{
            oo = Integer.toHexString(scene);
        }
        String ds = CoderUtils.getAscii(name);
        String fullCode = oooo +"0"+id2 + ds + btnNum  + oo + shortcut + sceneCode + sysModelBean.getColor();
        return fullCode;

    }




}
