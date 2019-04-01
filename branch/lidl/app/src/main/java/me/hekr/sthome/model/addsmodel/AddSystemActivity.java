package me.hekr.sthome.model.addsmodel;

import android.text.TextUtils;
import android.util.Log;
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
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.modeladapter.AddSceneAdapter;
import me.hekr.sthome.model.modeladapter.OptionAdapter;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendSceneGroupData;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by jishu0001 on 2016/8/31.
 */
public class AddSystemActivity extends TopbarSuperActivity {
    private final String TAG = "AddSystemActivity";
    private SysmodelDAO SD;
    private List<SceneBean> listA,addLists;
    private AddSceneAdapter sAdapter;
    private ListView lv;
    private SysModelBean sys1;
    private int length = 0;
    private SceneDAO SED;
    private String messageCode="";
    private int confirmNum = 0;
    private SendSceneGroupData sgd;
    private DeviceDAO deviceDAO;
    private String color;
    private WheelView wheelView;
    private ArrayList<String> itemslist = new ArrayList<String>();
    private ImageView colorShow;


    @Override
    protected void onCreateInit() {
        initView();
        initViewGuider();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_sysmodle;
    }

    private void initViewGuider() {

        sgd = new SendSceneGroupData(this) {
            @Override
            protected void sendEquipmentDataFailed() {

            }

            @Override
            protected void sendEquipmentDataSuccess() {

            }
        };

        getTopBarView().setTopBarStatus(2, 2, "", getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNum ++;
                verfication();
                if (confirmNum==1){
                    confirmToSys();
                }
                else if(confirmNum==-1){
                    Toast.makeText(AddSystemActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_LONG).show();
                    confirmNum = 0;
                }
                else if(confirmNum==-2){
                    Toast.makeText(AddSystemActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_LONG).show();
                    confirmNum = 0;
                }
                else if(confirmNum==-3){
                    Toast.makeText(AddSystemActivity.this,getResources().getString(R.string.name_is_illegal),Toast.LENGTH_LONG).show();
                    confirmNum = 0;
                }
                else if(confirmNum==-5){
                    Toast.makeText(AddSystemActivity.this,getResources().getString(R.string.please_choose_device),Toast.LENGTH_LONG).show();
                    confirmNum = 0;
                }
                else{
                    sendDoData(messageCode);
                }
            }
        });
        getTopBarView().setEditTitle(getResources().getString(R.string.system_scene)+getsid());
    }
    private void initView() {
        EventBus.getDefault().register(this);
        String[] strs = getResources().getStringArray(R.array.gatewaycolor);
        for(String ds:strs){
            itemslist.add(ds);
        }

        addLists = new ArrayList<SceneBean>();
        SED = new SceneDAO(this);
        colorShow = (ImageView)findViewById(R.id.colorshow);
        color = "F0";
        colorShow.setBackgroundColor(getResources().getColor(R.color.gateway_color_0));
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
        lv = (ListView) findViewById(R.id.sysListView);
        listA = SED.findAllAm(ConnectionPojo.getInstance().deviceTid);
        if(listA !=null){
            sAdapter = new AddSceneAdapter(this,lv,listA);
        }
        lv.setAdapter(sAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sAdapter.notifyDataSetChanged();
            }
        });

    }

    private void verfication(){
        try {
            deviceDAO = new DeviceDAO(this);
            List<MyDeviceBean> myDeviceBeanlist = deviceDAO.findAllDevice();
            if(myDeviceBeanlist.size()==0){
                confirmNum = -5;
                return;
            }


            if(TextUtils.isEmpty(getTopBarView().getEditTitleText())){
                confirmNum = -1;
            }
            else if(getTopBarView().getEditTitleText().getBytes("UTF-8").length >= 25){
                confirmNum = -2;
            }   else if(getTopBarView().getEditTitleText().indexOf("@")!=-1||getTopBarView().getEditTitleText().indexOf("$")!=-1){
                confirmNum = -3;
            }

        } catch (UnsupportedEncodingException e) {

        }
    }

    private void confirmToSys() {
            byte scene_default = 0;
            int id2 = getsid();
            sys1 = new SysModelBean();
            sys1.setModleName(getTopBarView().getEditTitleText());
            sys1.setChice("N");
            sys1.setSid(String.valueOf(id2));
            sys1.setDeviceid(ConnectionPojo.getInstance().deviceTid);
            length += 2;

            length += 1;

            //button num
            length += 1;
            String button4 = "00";
            //self-define scene num
            length += 1;
            int scene = 0;
            //scene id
            String sceneCode = "";
            if (addLists != null) {
                for (int i = 0; i < listA.size(); i++) {
                    if (lv.isItemChecked(i)) {

                        SceneBean ab = sAdapter.getItem(i);
                        if(Integer.parseInt(ab.getMid())<=128){
                            scene++;
                            length++;
                            String singleCode = "";
                            if (Integer.toHexString(Integer.parseInt(ab.getMid())).length() < 2) {  // new  mid

                                singleCode = "0" + Integer.toHexString(Integer.parseInt(ab.getMid()));
                            } else {
                                singleCode = Integer.toHexString(Integer.parseInt(ab.getMid()));
                            }
                            sceneCode += singleCode;
                        }else{
                            if("129".equals(ab.getMid())){
                                scene_default = (byte)(scene_default|0x81);
                            }else if("130".equals(ab.getMid())){
                                scene_default = (byte)(scene_default|0x82);
                            }else if("131".equals(ab.getMid())){
                                scene_default = (byte)(scene_default|0x84);
                            }
                        }

                    }
                }
            }
        scene_default = (byte)(scene_default|0x80);
        //增加了颜色的定制
        length+=2;
        sys1.setColor(color);


            String oooo = "0";
            if (Integer.toHexString(length+32).length() < 4) {
                for (int i = 0; i < 4 - Integer.toHexString(length+32).length() - 1; i++) {
                    oooo += 0;
                }
                oooo += Integer.toHexString(length+32);
            } else {
                oooo = Integer.toHexString(length+32);
            }

            String oo = "0";
            if (Integer.toHexString(scene).length() < 2) {
                oo = oo + Integer.toHexString(scene);
            } else {
                oo = Integer.toHexString(scene);
            }
            String name = getTopBarView().getEditTitleText();
            String ds = CoderUtils.getAscii(name);
            String str_default_scene = ByteUtil.convertByte2HexString(scene_default);
            String fullCode = oooo + "0" + id2 + ds + button4 + oo + sceneCode + color+str_default_scene;
            messageCode = fullCode;
            String crc = ByteUtil.CRCmakerCharAndCode(fullCode);
        Log.i(TAG,"fullCode=="+fullCode+crc);
            sendDoData(fullCode + crc);
          confirmNum = 0;
    }
    private void sendDoData( String fullCode) {
         SendCommand.Command = SendCommand.INCREACE_SCENE_GROUP;
         sgd.increaceSceneGroup(fullCode);
    }

    private void doAddToScenceTable(SceneBean ab, SysModelBean sys2) {

        try {
            SED = new SceneDAO(this);
            ab.setSid(String.valueOf(sys2.getSid()));

            SED.addScence(ab);
        }catch (Exception e){
            Log.i(TAG,"ScenceBean is null or SysModleBean is null");
        }

    }


    private int getsid(){
        SD = new SysmodelDAO(this);
       List<String> list = SD.findAllSysmodelSid(ConnectionPojo.getInstance().deviceTid);

        if(list.size()<3){
            return -1;
        }else{
            int m = 0;
            for(int i=0;i<list.size()-1;i++){
                if( (Integer.parseInt(list.get(i))+1) < Integer.parseInt(list.get(i+1))){
                    m = Integer.parseInt(list.get(i))+1;
                    break;
                }else{
                    m = i+2;
                }
            }



            return m;
        }

    }
    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent()== SendCommand.INCREACE_SCENE_GROUP){
            try {
                SD = new SysmodelDAO(this);
                if(!SD.isHasSysmodel(sys1.getSid(), ConnectionPojo.getInstance().deviceTid)){
                    SD.add(sys1);
                    if (addLists != null) {
                        for (int i = 0; i < listA.size(); i++) {
                            if (lv.isItemChecked(i)) {
                                SceneBean ab = sAdapter.getItem(i);
                                doAddToScenceTable(ab, sys1);
                            }
                        }
                    }
                }
                finish();

            }catch (Exception e){
                Log.i(TAG,"sys1 is null");
            }
          SendCommand.clearCommnad();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
