package me.hekr.sthome.model;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.modeladapter.ModifySceneAdapter;
import me.hekr.sthome.model.modeladapter.OptionAdapter;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.ShortcutBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.ShortcutDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.MyInforHandler;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendSceneGroupData;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by jishu0001 on 2016/9/1.
 */
public class SysDetaiActivity extends TopbarSuperActivity {
    private final String TAG = "SysDetaiActivity";
    private static final  int GETBACK_SUCCESS = 1;
    private static final int GETBACK_FAILED = 2;
    private MyInforHandler myInforHandler;
    private SendSceneGroupData ssgd;
    private SysmodelDAO SD;
    private SceneDAO SED;
    private SysModelBean sys;
    private List<Integer> beforeValue;
    private List<SceneBean> sceneLists;
    private ListView slist;
    private ModifySceneAdapter sAdapter;
    private String sid;
    private String name;
    private int sysid,length;
    private String deCode;
    private int clickNum = 0;
    private DeviceDAO deviceDAO;
    private String color;
    private WheelView wheelView;
    private ArrayList<String> itemslist = new ArrayList<String>();
    private ImageView colorShow;
    private String init_code;
    private ShortcutDAO shortcutDAO;

    private int[] colorGroup = new int[]{
            R.color.gateway_color_0,
            R.color.gateway_color_1,
            R.color.gateway_color_2,
            R.color.gateway_color_3,
            R.color.gateway_color_4,
            R.color.gateway_color_5,
            R.color.gateway_color_6,
            R.color.gateway_color_7,
            R.color.gateway_color_8
    };

    @Override
    protected void onCreateInit() {
        initdata();
        initGuiader();
        initView();
        init_code = getcode();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_detail;
    }


    private void initGuiader() {

        int flag = Integer.parseInt(sid)>2?2:1;
        getTopBarView().setTopBarStatus(flag, 2, name, getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(init_code.toUpperCase().equals(getcode().toUpperCase())){
                    finish();
                }else {
                    ECAlertDialog elc = ECAlertDialog.buildAlert(SysDetaiActivity.this, getResources().getString(R.string.save_modle_or_not), getResources().getString(R.string.no_save), getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clickNum ++;
                            verfication();
                            if(clickNum == 1){
                                String edit = getTopBarView().getEditTitle().getText().toString().trim();
                                updateName(edit);
                                conmmit();
                            }else if(clickNum > 1){
                                String edit = getTopBarView().getEditTitle().getText().toString().trim();
                                updateName(edit);
                                sendMsg(deCode);

                            }else {
                                clickNum = 0;
                            }
                        }
                    });
                    elc.show();
                }


            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNum ++;
                verfication();
                if(clickNum == 1){
                    String edit = getTopBarView().getEditTitle().getText().toString().trim();
                    updateName(edit);
                    conmmit();
                }else if(clickNum > 1){
                    String edit = getTopBarView().getEditTitle().getText().toString().trim();
                    updateName(edit);
                    sendMsg(deCode);

                }else {
                    clickNum = 0;
                }
            }
        });

    }
    private void updateName(String edit) {

        if( !name.equals(edit)){

            SD = new SysmodelDAO(this);
            try {
                SD.updateName(sysid,edit, ConnectionPojo.getInstance().deviceTid);
            }catch (Exception e){
                Toast.makeText(this,getResources().getString(R.string.this_name_has_been_used),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initdata() {
        EventBus.getDefault().register(this);
        String[] strs = getResources().getStringArray(R.array.gatewaycolor);
        for(String ds:strs){
            itemslist.add(ds);
        }

        SED = new SceneDAO(this);
        SD = new SysmodelDAO(this);

        myInforHandler = new MyInforHandler() {
            @Override
            protected void operationSuccess() {
                Toast.makeText(SysDetaiActivity.this,getResources().getString(R.string.operation_success),Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void operationFailed() {
                Toast.makeText(SysDetaiActivity.this,getResources().getString(R.string.operation_failed),Toast.LENGTH_SHORT).show();
            }
        };

        shortcutDAO = new ShortcutDAO(this);
        ssgd = new SendSceneGroupData(this) {
            @Override
            protected void sendEquipmentDataFailed() {
                myInforHandler.sendEmptyMessage(GETBACK_FAILED);
            }

            @Override
            protected void sendEquipmentDataSuccess() {
                myInforHandler.sendEmptyMessage(GETBACK_SUCCESS);
            }
        };
        sid = this.getIntent().getStringExtra("sid");
        sysid = Integer.parseInt(sid);

        SysModelBean sysModelBean = SD.findBySid(sid, ConnectionPojo.getInstance().deviceTid);
        name = sysModelBean.getModleName();
        color = sysModelBean.getColor();
        switch (Integer.parseInt(sid)){
            case 0:
                name = getResources().getString(R.string.home_mode);
                break;
            case 1:
                name = getResources().getString(R.string.out_mode);
                break;
            case 2:
                name = getResources().getString(R.string.sleep_mode);
                break;
            default:break;
        }

        sceneLists = SED.findAllAm(ConnectionPojo.getInstance().deviceTid);
        sys= new SysModelBean();
        sys.setModleName(name);
        sys.setSid(sid);

        beforeValue = SED.findAllSenceBySysId(sid, ConnectionPojo.getInstance().deviceTid);
    }

    private void initView() {
        slist = (ListView) findViewById(R.id.sDetailListView);
        colorShow = (ImageView)findViewById(R.id.colorshow);

        wheelView = (WheelView)findViewById(R.id.color);
        wheelView.colorfulflag = true;
        wheelView.setAdapter(new OptionAdapter(itemslist,30));
        wheelView.addChangingListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                switch (newValue){
                    case 0:
                        colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_0));
                        color = "F0";
                        break;
                    case 1:
                        colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_1));
                        color = "F1";
                        break;
                    case 2:
                        colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_2));
                        color = "F2";
                        break;
                    case 3:
                        colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_3));
                        color = "F3";
                        break;
                    case 4:
                        colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_4));
                        color = "F4";
                        break;
                    case 5:
                        colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_5));
                        color = "F5";
                        break;
                    case 6:
                        colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_6));
                        color = "F6";
                        break;
                    case 7:
                        colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_7));
                        color = "F7";
                        break;
                    case 8:
                        colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_8));
                        color = "F8";
                        break;
                }
            }
        });

        if(sceneLists!=null){
            sAdapter = new ModifySceneAdapter(this,sceneLists,beforeValue); //chuange a condition
            slist.setAdapter(sAdapter);
        }
        doAction();
        if( !TextUtils.isEmpty(color) ){
            if("F0".equals(color)){
                colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_0));
                wheelView.setCurrentItem(0);
            }else if("F1".equals(color)){
                colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_1));
                wheelView.setCurrentItem(1);
            }else if("F2".equals(color)){
                colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_2));
                wheelView.setCurrentItem(2);
            }else if("F3".equals(color)){
                colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_3));
                wheelView.setCurrentItem(3);
            }else if("F4".equals(color)){
                colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_4));
                wheelView.setCurrentItem(4);
            }else if("F5".equals(color)){
                colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_5));
                wheelView.setCurrentItem(5);
            }else if("F6".equals(color)){
                colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_6));
                wheelView.setCurrentItem(6);
            }else if("F7".equals(color)){
                colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_7));
                wheelView.setCurrentItem(7);
            }else if("F8".equals(color)){
                colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_8));
                wheelView.setCurrentItem(8);
            }else{
                colorShow.setBackgroundColor(getResources().getColor(colorGroup[sysid]));
                wheelView.setCurrentItem(sysid);
            }
        }else {
            colorShow.setBackgroundColor(getResources().getColor(colorGroup[sysid]));
            wheelView.setCurrentItem(sysid);
        }

    }

    private void doAction() {
        slist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sAdapter.getIsSelected().put(position,!sAdapter.getIsSelected().get(position));
                sAdapter.notifyDataSetChanged();
            }
        });
    }

    private void verfication(){

        deviceDAO = new DeviceDAO(this);
        List<MyDeviceBean> myDeviceBeanlist = deviceDAO.findAllDevice();
        if(myDeviceBeanlist.size()==0){
            clickNum = -4;
            Toast.makeText(SysDetaiActivity.this,getResources().getString(R.string.please_choose_device),Toast.LENGTH_LONG).show();
            return;
        }


        if(sysid>2){
            try {

                if(TextUtils.isEmpty(getTopBarView().getEditTitleText())){
                    clickNum = -1;
                }else if(getTopBarView().getEditTitleText().getBytes("GBK").length>15){
                    Toast.makeText(SysDetaiActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_LONG).show();
                    clickNum = -2;
                }  else if(getTopBarView().getEditTitleText().indexOf("@")!=-1||getTopBarView().getEditTitleText().indexOf("$")!=-1){
                    Toast.makeText(SysDetaiActivity.this,getResources().getString(R.string.name_is_illegal),Toast.LENGTH_LONG).show();
                    clickNum = -3;
                }

            } catch (UnsupportedEncodingException e) {

            }
        }



    }

    private void conmmit() {
        byte scene_default = 0;
        String name = null;
        if(sysid>2){
            name = getTopBarView().getEditTitleText();
        }else{
            switch (sysid){
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

        length = 0;
        length+=2;//the total num length

        String id2 = sid;
        length += 1;//the scene id

        String btnNum = "";
        List<ShortcutBean> shortcutBeans = shortcutDAO.findShorcutsBysid(ConnectionPojo.getInstance().deviceTid,String.valueOf(sysid));

        if (Integer.toHexString(shortcutBeans.size()).length()<2){  //new mid
            btnNum = "0"+Integer.toHexString(shortcutBeans.size());
        }else{
            btnNum =Integer.toHexString(shortcutBeans.size());
        }
        length+=1;//button num

        String shortcut = "";

        for (int i = 0;i<shortcutBeans.size();i++){



            String eqid = "";
            String dessid = "";
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


        //self-define scene num
        length+=1;
        int scene =0;
        //scene id
        String sceneCode ="";
        SED = new SceneDAO(this);
        SED.deleteBySid(sysid, ConnectionPojo.getInstance().deviceTid);
        if(sceneLists.size()>0){
            for(int i = 0; i<sceneLists.size();i++){
                if(sAdapter.getIsSelected().get(i)){   //chuange confirm condition

                    doAddToActable(sceneLists.get(i),sys);
                   if(Integer.parseInt(sceneLists.get(i).getMid())<=128){
                       scene++;
                       length++;
                       String singleCode ="";
                       if (Integer.toHexString(Integer.parseInt(sceneLists.get(i).getMid())).length()<2){  //new mid
                           singleCode = "0"+Integer.toHexString(Integer.parseInt(sceneLists.get(i).getMid()));
                       }else{
                           singleCode =Integer.toHexString(Integer.parseInt(sceneLists.get(i).getMid()));
                       }
                       sceneCode += singleCode;
                   }else{
                       if("129".equals(sceneLists.get(i).getMid())){
                           scene_default = (byte)(scene_default|0x81);
                       }else if("130".equals(sceneLists.get(i).getMid())){
                           scene_default = (byte)(scene_default|0x82);
                       }else if("131".equals(sceneLists.get(i).getMid())){
                           scene_default = (byte)(scene_default|0x84);
                       }
                   }

                }
            }
        }else {
            length+=0;
            sceneCode = "";
        }
        scene_default = (byte)(scene_default|0x80);
        //增加了颜色的定制
        length+=2;

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
        String str_default_scene = ByteUtil.convertByte2HexString(scene_default);
        String fullCode = oooo +"0"+id2 + ds + btnNum  + oo + shortcut + sceneCode + color+str_default_scene;
        deCode = fullCode;
        String crc = ByteUtil.CRCmakerCharAndCode(fullCode);
        Log.i(TAG,"fullCode:"+fullCode);
        sendMsg(fullCode + crc);
        Log.i(TAG, " sysdetail CRC +++++++++++++++"+ crc);
    }


    private String getcode() {
        byte scene_default = 0;
        String name = null;
        if(sysid>2){
            name = getTopBarView().getEditTitleText();
        }else{
            switch (sysid){
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

        String id2 = sid;
        length += 1;//the scene id

        String btnNum = "";
        List<ShortcutBean> shortcutBeans = shortcutDAO.findShorcutsBysid(ConnectionPojo.getInstance().deviceTid,String.valueOf(sysid));

        if (Integer.toHexString(shortcutBeans.size()).length()<2){  //new mid
            btnNum = "0"+Integer.toHexString(shortcutBeans.size());
        }else{
            btnNum =Integer.toHexString(shortcutBeans.size());
        }
        length+=1;//button num

        String shortcut = "";

        for (int i = 0;i<shortcutBeans.size();i++){



            String eqid = "";
            String dessid = "";
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

        //self-define scene num
        length+=1;
        int scene =0;
        //scene id
        String sceneCode ="";
        if(sceneLists.size()>0){
            for(int i = 0; i<sceneLists.size();i++){
                if(sAdapter.getIsSelected().get(i)){   //chuange confirm condition


                    if(Integer.parseInt(sceneLists.get(i).getMid())<=128){
                        scene++;
                        length++;
                        String singleCode ="";
                        if (Integer.toHexString(Integer.parseInt(sceneLists.get(i).getMid())).length()<2){  //new mid
                            singleCode = "0"+Integer.toHexString(Integer.parseInt(sceneLists.get(i).getMid()));
                        }else{
                            singleCode =Integer.toHexString(Integer.parseInt(sceneLists.get(i).getMid()));
                        }
                        sceneCode += singleCode;
                    }else{
                        if("129".equals(sceneLists.get(i).getMid())){
                            scene_default = (byte)(scene_default|0x81);
                        }else if("130".equals(sceneLists.get(i).getMid())){
                            scene_default = (byte)(scene_default|0x82);
                        }else if("131".equals(sceneLists.get(i).getMid())){
                            scene_default = (byte)(scene_default|0x84);
                        }
                    }

                }
            }
        }else {
            length+=0;
            sceneCode = "";
        }
        scene_default = (byte)(scene_default|0x80);
        //增加了颜色的定制
        length+=2;

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
        String str_default_scene = ByteUtil.convertByte2HexString(scene_default);
        String fullCode = oooo +"0"+id2 + ds + btnNum  + oo+ shortcut + sceneCode + color + str_default_scene;
        return fullCode;

    }


    private void sendMsg(String deCode) {
        SendCommand.Command = SendCommand.MODIFY_SCENE_GROUP;
        ssgd.modifySceneGroup(deCode);
    }



    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent()== SendCommand.MODIFY_SCENE_GROUP){
            SD = new SysmodelDAO(this);
            SD.updateColor(sys.getSid(),color, ConnectionPojo.getInstance().deviceTid);
            clickNum = 0;
            finish();
            SendCommand.clearCommnad();
        }
    }


    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    private void doAddToActable(SceneBean ab, SysModelBean sys2) {

        try {
            SED = new SceneDAO(this);
            ab.setSid(String.valueOf(sys2.getSid()));

            SED.addScence(ab);
        }catch (Exception e){
            Log.i(TAG,"ScenceBean is null or SysModleBean is null");
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if(init_code.toUpperCase().equals(getcode().toUpperCase())){
                finish();
            }else {
                ECAlertDialog elc = ECAlertDialog.buildAlert(SysDetaiActivity.this, getResources().getString(R.string.save_modle_or_not), getResources().getString(R.string.no_save), getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clickNum ++;
                        verfication();
                        if(clickNum == 1){
                            String edit = getTopBarView().getEditTitle().getText().toString().trim();
                            updateName(edit);
                            conmmit();
                        }else if(clickNum > 1){
                            String edit = getTopBarView().getEditTitle().getText().toString().trim();
                            updateName(edit);
                            sendMsg(deCode);

                        }else {
                            clickNum = 0;
                        }
                    }
                });
                elc.show();
            }
            return true;
          }
          return super.onKeyDown(keyCode,event);
        }

}
