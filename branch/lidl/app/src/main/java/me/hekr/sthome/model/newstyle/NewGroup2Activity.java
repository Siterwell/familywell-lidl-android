package me.hekr.sthome.model.newstyle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.jock.lib.HighLight;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.SettingItem;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.ResolveScene;
import me.hekr.sthome.model.SceneModelPojo;
import me.hekr.sthome.model.modeladapter.GridAdapter;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.MyInforHandler;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendSceneData;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by jishu0001 on 2016/10/18.
 */
public class NewGroup2Activity extends TopbarSuperActivity {
    private static final  int GETBACK_SUCCESS = 1;
    private static final int GETBACK_FAILED = 2;
    private MyInforHandler myInforHandler;
    private static final String TAG = "NewGroup2Activity";
    private GridView input,output;
    private SettingItem choseCdt;
    private SceneBean amodle;
    private SceneModelPojo smp;
    private List<EquipmentBean> inputData,outputData;
    private List<EquipmentBean> inputShow,outputShow;
    private GridAdapter inAdapter,outAdapter;
    private SceneDAO SED;
    private int confirmNum =0;
    private String messageCode = "";
    private SendSceneData ssd;
    private HighLight mHightLight;
    private HighLight mHightLight2;
    private HighLight mHightLight3;
    private boolean flag_first;
    private String lan;
    private String init_code;
    private DeviceDAO deviceDAO;
    private ECAlertDialog ecAlertDialog;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private final int CHOOSE_CONDITION = 1;

    @Override
    protected void onCreateInit() {
        initViewGuider();
        initData();
        initView();
        initInput();
        initOutput();
        handler.sendEmptyMessageDelayed(1,1000);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_n_group;
    }


    private void initData() {

       String classname =  UnitTools.getRunningActivityName(this);
        flag_first = UnitTools.readFirstOpen(this,classname);

        UnitTools tools = new UnitTools(this);
        lan = tools.readLanguage();

        myInforHandler = new MyInforHandler() {
            @Override
            protected void operationSuccess() {
                Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.operation_success),Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void operationFailed() {
                Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.operation_failed),Toast.LENGTH_SHORT).show();
            }
        };
        ssd = new SendSceneData(this) {
            @Override
            protected void sendEquipmentDataFailed() {
                LOG.I(TAG,"operation failed");
                myInforHandler.sendEmptyMessage(GETBACK_FAILED);
            }

            @Override
            protected void sendEquipmentDataSuccess() {
                LOG.I(TAG,"operation success");
                myInforHandler.sendEmptyMessage(GETBACK_SUCCESS);
            }
        };

        amodle = mcp.sb;
        if(amodle!=null) init_code = amodle.getCode();
        LOG.I(TAG,"init_code:"+init_code);
        inputData = new ArrayList<>();
        outputData = new ArrayList<>();
        if(!mcp.modify) {

                if(mcp.input == null){
                    inputShow = new ArrayList<>();
                    mcp.input = new ArrayList<>();
                }else {
                    inputShow =mcp.input;
                }

                if(mcp.output == null){
                    outputShow = new ArrayList<>();
                    mcp.output = new ArrayList<>();
                }else {
                    outputShow =mcp.output;
                }


            if(mcp.trigger==null){
                mcp.trigger = "00";
            }



        }else {
            ResolveScene resolveScene = new ResolveScene(this,amodle.getCode());

                if(resolveScene.isTarget()){
                    smp = resolveScene.getSp();
                    mcp.trigger =smp.getCondition();

                        inputShow = mcp.input;

                        if(inputShow == null){
                            inputShow = new ArrayList<>();
                            try {
                                List<EquipmentBean> in = resolveScene.getInput();
                                for(EquipmentBean e : in){
                                    if("IN".equals(e.getEqid())){
                                        e.setEquipmentName(getResources().getString(R.string.timer));
                                        e.setEquipmentDesc("TIMER");
                                        inputShow.add(e);
                                    }else if("CLICK".equals(e.getEqid())){
                                        e.setEquipmentName(getResources().getString(R.string.clicktoaction));
                                        e.setEquipmentDesc("CLICK");
                                        inputShow.add(e);
                                    }else {
                                        EquipDAO ED = new EquipDAO(this);
                                        EquipmentBean fromEqid = ED.findByeqid(e.getEqid(), ConnectionPojo.getInstance().deviceTid);
                                        if(fromEqid!=null){
                                            e.setEquipmentDesc(fromEqid.getEquipmentDesc());
                                            e.setEquipmentName(fromEqid.getEquipmentName());
                                        }
                                        inputShow.add(e);
                                    }
                                }
                            }catch (Exception e){
                                LOG.I(TAG,"no input data");
                                inputShow = new ArrayList<>();
                                mcp.input = inputShow;
                            }
                            mcp.input = inputShow;
                        }







                        outputShow = mcp.output;
                        if(outputShow == null){
                            outputShow = new ArrayList<>();
                            try {
                                List<EquipmentBean> out = resolveScene.getOutput();
                                for(EquipmentBean e : out){
                                    if("PHONE".equals(e.getEqid())){
                                        e.setEquipmentName(getResources().getString(R.string.phone));
                                        e.setEquipmentDesc("PHONE");
                                        outputShow.add(e);
                                    }else if("OUT".equals(e.getEqid())){
                                        e.setEquipmentName(getResources().getString(R.string.delay));
                                        e.setEquipmentDesc("DELAY");
                                        outputShow.add(e);
                                    }else if("0".equals(e.getEqid())){
                                        e.setEquipmentDesc("GATEWAY");
                                        e.setEquipmentName(getResources().getString(R.string.gateway));
                                        outputShow.add(e);
                                    }else {
                                        EquipDAO ED = new EquipDAO(this);
                                        EquipmentBean fromEqid = ED.findByeqid(e.getEqid(), ConnectionPojo.getInstance().deviceTid);
                                        if(fromEqid!=null){
                                            e.setEquipmentDesc(fromEqid.getEquipmentDesc());
                                            e.setEquipmentName(fromEqid.getEquipmentName());
                                        }
                                        outputShow.add(e);
                                    }
                                }
                            }catch (Exception e){
                                LOG.I(TAG,"no data output");
                                outputShow = new ArrayList<>();
                                mcp.output = outputShow;
                            }

                            mcp.output = outputShow;
                        }

                }else {
                    LOG.I(TAG,"code is illegal");
                    if(mcp.input == null){
                        inputShow = new ArrayList<>();
                        mcp.input = new ArrayList<>();
                    }else {
                        inputShow =mcp.input;
                    }

                    if(mcp.output == null){
                        outputShow = new ArrayList<>();
                        mcp.output = new ArrayList<>();
                    }else {
                        outputShow =mcp.output;
                    }


                    if(mcp.trigger==null){
                        mcp.trigger = "00";
                    }
                }


        }
    }


    private void initInput() {
        inputData.clear();
        if(inputShow != null){
            LOG.I(TAG,inputShow.toString());
            for(EquipmentBean bean:inputShow){
                if( TextUtils.isEmpty(bean.getEquipmentDesc())){
                    mcp.cleanModleCondition();
                    Toast.makeText(this,getResources().getString(R.string.device_is_not_at),Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }

            inputData.addAll(inputShow);
        }
        EquipmentBean theLastTag = new EquipmentBean();
        theLastTag.setEqid(String.valueOf(-1));
        theLastTag.setEquipmentDesc("END");
        theLastTag.setEquipmentName(getResources().getString(R.string.increase_input));
        inputData.add(theLastTag);

        inAdapter = new GridAdapter(this,inputData);
        input.setAdapter(inAdapter);
        input.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(getTopBarView()!=null) mcp.name = getTopBarView().getEditTitleText();

                EquipmentBean eq = inputData.get(position);
                if("END".equals(eq.getEquipmentDesc())){
                    Intent addItem = new Intent(NewGroup2Activity.this, ModelCellListActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "input";
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.DOOR_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, DoorCheckNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "input";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.PIR_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, PirCheckNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "input";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.SOS_KEY.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, SOSNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "input";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.SM_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.no_need_to_check), Toast.LENGTH_LONG).show();
                }else if(NameSolve.CO_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.no_need_to_check), Toast.LENGTH_LONG).show();
                }else if(NameSolve.GAS_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.no_need_to_check), Toast.LENGTH_LONG).show();
                }else if(NameSolve.THERMAL_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.no_need_to_check), Toast.LENGTH_LONG).show();
                }else if (NameSolve.WT_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {
                    Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.no_need_to_check), Toast.LENGTH_LONG).show();
                }else if(NameSolve.TH_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
//                    Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.no_need_to_check), Toast.LENGTH_LONG).show();

                    Intent addItem = new Intent(NewGroup2Activity.this, ThcheckNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "input";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.BUTTON.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.no_need_to_check), Toast.LENGTH_LONG).show();
                }else if("CLICK".equals(eq.getEquipmentDesc())){
                    Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.no_need_to_check), Toast.LENGTH_LONG).show();
                }else if(NameSolve.LOCK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, LockNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "input";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, CXSMALARMNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "input";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }
                else if("TIMER".equals(eq.getEquipmentDesc())){
                    Intent addItem = new Intent(NewGroup2Activity.this, TimerNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "input";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, MODEBUTTONNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "input";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }
            }
        });
        input.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                EquipmentBean eq = inputData.get(position);
                if(!"END".equals(eq.getEquipmentDesc())){


                    ECAlertDialog buildAlert = ECAlertDialog.buildAlert(NewGroup2Activity.this,getResources().getString(R.string.delete_condition),getResources().getString(R.string.cancel),getResources().getString(R.string.ok),null,new Dialog.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inputShow.remove(position);
                            inputData.remove(position);
                            inAdapter.notifyDataSetChanged();
                        }
                    });
                    buildAlert.setTitle(getResources().getString(R.string.dialog_title_alert));
                    buildAlert.show();

                }
                return true;
            }
        });
    }

    private void initOutput() {
        outputData.clear();
        if(outputShow != null){
            LOG.I(TAG,outputShow.toString());
            for(EquipmentBean bean:outputShow){
                if(TextUtils.isEmpty(bean.getEquipmentDesc())){
                    mcp.cleanModleCondition();
                    Toast.makeText(this,getResources().getString(R.string.device_is_not_at),Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }

            outputData.addAll(outputShow);
        }
        EquipmentBean theLastTag = new EquipmentBean();
        theLastTag.setEqid(String.valueOf(-1));
        theLastTag.setEquipmentDesc("END");
        theLastTag.setEquipmentName(getResources().getString(R.string.increase_output));
        outputData.add(theLastTag);
        outAdapter = new GridAdapter(this,outputData);
        output.setAdapter(outAdapter);
        output.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(getTopBarView()!=null) mcp.name = getTopBarView().getEditTitleText();

                EquipmentBean eq = outputData.get(position);
                if("END".equals(eq.getEquipmentDesc())){
                    Intent addItem = new Intent(NewGroup2Activity.this, ModelCellListActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.SOCKET.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, SocketNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.DIMMING_MODULE.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, DimmingModuleNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.LAMP.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, LampNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.GUARD.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {
                    Intent addItem = new Intent(NewGroup2Activity.this, GuardNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.VALVE.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, ValveNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.CURTAIN.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, CurtainNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if("DELAY".equals(eq.getEquipmentDesc())){
                    Intent addItem = new Intent(NewGroup2Activity.this, TimerNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if("GATEWAY".equals(eq.getEquipmentDesc())){
                    Intent addItem = new Intent(NewGroup2Activity.this, GatewayNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.SOCKET.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, Channel2SocketNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.TWO_SOCKET.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, Channel2SocketNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }else if(NameSolve.TEMP_CONTROL.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    Intent addItem = new Intent(NewGroup2Activity.this, TempControlNewActivity.class);
                    ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                    mcp.condition = "output";
                    mcp.position = position;
                    mcp.input = inputShow;
                    mcp.output = outputShow;
                    startActivity(addItem);
                    finish();
                }
            }
        });
        output.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                EquipmentBean eq = outputData.get(position);
                if(!"END".equals(eq.getEquipmentDesc())){

                    if("DELAY".equals(eq.getEquipmentDesc())){

                        if(position>0&&position<outputData.size()-1){

                            EquipmentBean eq1 = outputData.get(position-1);
                            EquipmentBean eq2 = outputData.get(position+1);

                            if(eq1.getEqid().equals(eq2.getEqid())){
                                ECAlertDialog buildAlert = ECAlertDialog.buildAlert(NewGroup2Activity.this,getResources().getString(R.string.eq_repeat),getResources().getString(R.string.cancel),getResources().getString(R.string.ok),null,new Dialog.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        outputShow.remove(position);
                                        outputData.remove(position);
                                        outputShow.remove(position);
                                        outputData.remove(position);
                                        outAdapter.notifyDataSetChanged();
                                    }
                                });
                                buildAlert.setTitle(getResources().getString(R.string.dialog_title_alert));
                                buildAlert.show();
                            }else{
                                ECAlertDialog buildAlert = ECAlertDialog.buildAlert(NewGroup2Activity.this,getResources().getString(R.string.delete_delay),getResources().getString(R.string.cancel),getResources().getString(R.string.ok),null,new Dialog.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        outputShow.remove(position);
                                        outputData.remove(position);
                                        outAdapter.notifyDataSetChanged();
                                    }
                                });
                                buildAlert.setTitle(getResources().getString(R.string.dialog_title_alert));
                                buildAlert.show();
                            }


                        }else{
                            ECAlertDialog buildAlert = ECAlertDialog.buildAlert(NewGroup2Activity.this,getResources().getString(R.string.delete_delay),getResources().getString(R.string.cancel),getResources().getString(R.string.ok),null,new Dialog.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    outputShow.remove(position);
                                    outputData.remove(position);
                                    outAdapter.notifyDataSetChanged();
                                }
                            });
                            buildAlert.setTitle(getResources().getString(R.string.dialog_title_alert));
                            buildAlert.show();
                        }

                    }
                    else{

                        if(!"PHONE".equals(eq.getEquipmentDesc())){

                            if(position>0&&position<outputData.size()-1){

                                EquipmentBean eq1 = outputData.get(position-1);
                                EquipmentBean eq2 = outputData.get(position+1);
                                if("DELAY".equals(eq1.getEquipmentDesc())&&"DELAY".equals(eq2.getEquipmentDesc())){
                                    ECAlertDialog buildAlert = ECAlertDialog.buildAlert(NewGroup2Activity.this,getResources().getString(R.string.delete_last_delay),getResources().getString(R.string.cancel),getResources().getString(R.string.ok),null,new Dialog.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            outputShow.remove(position-1);
                                            outputData.remove(position-1);
                                            outputShow.remove(position-1);
                                            outputData.remove(position-1);
                                            outAdapter.notifyDataSetChanged();
                                        }
                                    });
                                    buildAlert.setTitle(getResources().getString(R.string.dialog_title_alert));
                                    buildAlert.show();
                                }else{
                                    ECAlertDialog buildAlert = ECAlertDialog.buildAlert(NewGroup2Activity.this,getResources().getString(R.string.delete_action),getResources().getString(R.string.cancel),getResources().getString(R.string.ok),null,new Dialog.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            outputShow.remove(position);
                                            outputData.remove(position);
                                            outAdapter.notifyDataSetChanged();
                                        }
                                    });
                                    buildAlert.setTitle(getResources().getString(R.string.dialog_title_alert));
                                    buildAlert.show();
                                }

                            }else{
                                ECAlertDialog buildAlert = ECAlertDialog.buildAlert(NewGroup2Activity.this,getResources().getString(R.string.delete_action),getResources().getString(R.string.cancel),getResources().getString(R.string.ok),null,new Dialog.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        outputShow.remove(position);
                                        outputData.remove(position);
                                        outAdapter.notifyDataSetChanged();
                                    }
                                });
                                buildAlert.setTitle(getResources().getString(R.string.dialog_title_alert));
                                buildAlert.show();
                            }


                        }else{
                            ECAlertDialog buildAlert = ECAlertDialog.buildAlert(NewGroup2Activity.this,getResources().getString(R.string.delete_action),getResources().getString(R.string.cancel),getResources().getString(R.string.ok),null,new Dialog.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    outputShow.remove(position);
                                    outputData.remove(position);
                                    outAdapter.notifyDataSetChanged();
                                }
                            });
                            buildAlert.setTitle(getResources().getString(R.string.dialog_title_alert));
                            buildAlert.show();
                        }


                    }




                }
                return true;
            }
        });
    }

    private void initView() {
        input = (GridView) findViewById(R.id.gridInput);
        output = (GridView) findViewById(R.id.gridOutput);
        choseCdt = (SettingItem) findViewById(R.id.chooseCdtion);

        if("00".equals(mcp.trigger)){
            choseCdt.setTitleText(getResources().getString(R.string.one_condition));
        }else if("ff".equals(mcp.trigger.toLowerCase())){
            choseCdt.setTitleText(getResources().getString(R.string.all_condition));
        }

        choseCdt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                choseCdt1();
            }
        });

        if(mcp.name !=null){
            getTopBarView().setEditTitle(mcp.name);
        }else{
            if(amodle != null){
                getTopBarView().setEditTitle(amodle.getName());
            }else {
                StringBuilder sb = new StringBuilder();
                sb.append(getString(R.string.my_action)).append(" ").append(getmid());
                getTopBarView().setEditTitle(sb.toString());
            }
        }

        if(!flag_first&&!mcp.modify){
            UnitTools.writeFirstOpen(this, UnitTools.getRunningActivityName(this),true);
            input.post(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showTipMask();
                        }
                    }
            );
        }

    }

    private void choseCdt1() {

        if("00".equals(mcp.trigger)){
            Intent intent = new Intent(this, ChooseConditionActivity.class);
            intent.putExtra("condition",false);
            startActivityForResult(intent,  CHOOSE_CONDITION);
        }else if("ff".equals(mcp.trigger.toLowerCase())){
            Intent intent = new Intent(this, ChooseConditionActivity.class);
            intent.putExtra("condition",true);
            startActivityForResult(intent,  CHOOSE_CONDITION);
        }

//        if(clicknum%2==0){
//            choseCdt.setTitleText(getResources().getString(R.string.one_condition));
//            mcp.trigger = "00";
//            clicknum=0;
//        }else{
//            choseCdt.setTitleText(getResources().getString(R.string.all_condition));
//            mcp.trigger = "ff";
//        }
    }

    private void initViewGuider() {

        EventBus.getDefault().register(this);
        getTopBarView().setTopBarStatus(2, 2, "", getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(mcp.modify){
                        String newcode =  getcode();
                        if(!init_code.toUpperCase().equals(newcode.toUpperCase())){
                            ECAlertDialog elc = ECAlertDialog.buildAlert(NewGroup2Activity.this, getResources().getString(R.string.scene_has_modify), getResources().getString(R.string.no_save), getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mcp.cleanModleCondition();
                                    finish();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    confirmNum++;
                                    verfication();
                                    if (confirmNum == 1){
                                        addModles();
                                    }else if(confirmNum > 1){
                                        sendDecode(messageCode);
                                    }
                                }
                            });
                            elc.show();

                        }else{
                            mcp.cleanModleCondition();
                            finish();
                        }

                    }else{

                       if( mcp.input ==null || mcp.output ==null || mcp.input.size()==0 || mcp.output.size()==0){
                           mcp.cleanModleCondition();
                           finish();
                       }else{
                           ECAlertDialog elc = ECAlertDialog.buildAlert(NewGroup2Activity.this, getResources().getString(R.string.scene_has_modify), getResources().getString(R.string.no_save), getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   mcp.cleanModleCondition();
                                   finish();
                               }
                           }, new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   confirmNum++;
                                   verfication();
                                   if (confirmNum == 1){
                                       addModles();
                                   }else if(confirmNum > 1){
                                       sendDecode(messageCode);
                                   }
                               }
                           });
                           elc.show();
                       }

                    }

                }catch (NullPointerException e){
                    mcp.cleanModleCondition();
                    finish();
                }

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNum++;
                verfication();
                if (confirmNum == 1){
                    addModles();
                }else if(confirmNum > 1){
                    sendDecode(messageCode);
                }
            }
        });

    }

    private void verfication() {

            deviceDAO = new DeviceDAO(this);
            List<MyDeviceBean> myDeviceBeanlist = deviceDAO.findAllDevice();
        if(myDeviceBeanlist.size()==0){
            confirmNum = 0;
            Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.please_choose_device),Toast.LENGTH_LONG).show();
            return;
        }

        SED = new SceneDAO(this);
        if(outputShow.size()>0 && inputShow.size()>0 ){

            boolean flag = false;
            EquipmentBean dv = outputShow.get(outputShow.size()-1);
                if(dv.getEquipmentDesc() != null && !"".equals(dv.getEquipmentDesc())){
                    if(!"DELAY".equals(dv.getEquipmentDesc())){
                        flag = true;
                    }
                }

            if(!flag){
                Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.output_must_has_eq),Toast.LENGTH_LONG).show();
                confirmNum = 0;
            }


        }else{

            if(inputShow.size()==0){
                Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.intput_must_has_eq),Toast.LENGTH_LONG).show();
            }else if(outputShow.size()==0){
                Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.output_must_has_eq),Toast.LENGTH_LONG).show();
            }

            confirmNum = 0;
        }



        try {
        if(TextUtils.isEmpty(getTopBarView().getEditTitleText())){
            Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_LONG).show();
            confirmNum = 0;
        }
        else if(getTopBarView().getEditTitleText().indexOf("@")!=-1||getTopBarView().getEditTitleText().indexOf("$")!=-1){
            confirmNum = 0;
            Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.name_is_illegal),Toast.LENGTH_LONG).show();
        }
        else
            if(getTopBarView().getEditTitleText().getBytes("UTF-8").length>15){
                Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_LONG).show();
                confirmNum = 0;
            }
            else {

                if(!mcp.modify){
                    if( SED.findScenceByNameCount(getTopBarView().getEditTitleText(), ConnectionPojo.getInstance().deviceTid)>0){
                        Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.name_is_repeat),Toast.LENGTH_LONG).show();
                        confirmNum = 0;
                    }
                }else{
                    if( SED.findScenceByNameCountWithMid(getTopBarView().getEditTitleText(), ConnectionPojo.getInstance().deviceTid,mcp.sb.getMid())>0){
                        Toast.makeText(NewGroup2Activity.this,getResources().getString(R.string.name_is_repeat),Toast.LENGTH_LONG).show();
                        confirmNum = 0;
                    }
                }


            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    /**
     * create a new sence modle
     */
    private void addModles() {
        int length = 2;//情景长度
        length += 1;//系统情景编号

        if(!mcp.modify){
            amodle = new SceneBean();
            amodle.setName(getTopBarView().getEditTitleText());
            amodle.setSid("-1");
            amodle.setDeviceid(ConnectionPojo.getInstance().deviceTid);
            int amid = getmid();
            LOG.I(TAG,"    ==="+amid);
            amodle.setMid(String.valueOf(amid));
        }


        String cm3 =mcp.trigger;
        length +=1;//条件选择

        String settime4 ="000000";
        length +=3;//定时

        String cl = "FF";
        for(EquipmentBean de : mcp.input){
            if( "CLICK".equals(de.getEquipmentDesc())){
                cl = "ab";
            }

        }

        String click5 = cl;
        length += 1;//点击执行

        String ph = "FF";
        for(EquipmentBean de : mcp.output){
            if( "PHONE".equals(de.getEquipmentDesc())){
                ph = "AC";
            }

        }

        String inf6 =ph;
        length +=1;//通知手机

        int in = 0;
        length +=1;//输入设备个数

        int out = 0;
        length +=1;//输出设备个数


        String inCode="";
        for(int i = 0; i < inputShow.size(); i++ ){
            EquipmentBean eq = inputShow.get(i);
            if(!"TIMER".equals(eq.getEquipmentDesc())&&!"CLICK".equals(eq.getEquipmentDesc())){
                in ++;
                length += 6;
                String ei1 ="0";
                if(Integer.toHexString(Integer.parseInt(eq.getEqid())).length() < 4){
                    for (int j = 0; j<4- Integer.toHexString(Integer.parseInt(eq.getEqid())).length()-1; j++ ){
                        ei1 += 0;
                    }
                    ei1 += Integer.toHexString(Integer.parseInt(eq.getEqid()));
                }else{
                    ei1 = Integer.toHexString(Integer.parseInt(eq.getEqid()));
                }
                inCode += (  ei1 + eq.getState());
            }else{
                settime4 = eq.getState();
            }
        }
        String outCode ="";
        for(int i = 0; i < outputShow.size(); i++ ){
            EquipmentBean eq = outputShow.get(i);
            if(!"DELAY".equals(eq.getEquipmentDesc())&&!"PHONE".equals(eq.getEquipmentDesc())){

                String beforeVelue = "ok";
                if(i >0 && outputShow.get(i-1) != null){
                    EquipmentBean before = outputShow.get(i-1);
                    beforeVelue = before.getEquipmentDesc();
                }

                if(i==0 || (i-1 >= 0 && !"DELAY".equals(beforeVelue)&&!"PHONE".equals(beforeVelue))){
                    out ++;  //use for get number
                    length += 8;
                    String ei1 ="0";
                    if(Integer.toHexString(Integer.parseInt(eq.getEqid())).length() < 4){
                        for (int j = 0; j<4- Integer.toHexString(Integer.parseInt(eq.getEqid())).length()-1; j++ ){
                            ei1 += 0;
                        }
                        ei1 += Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }else{
                        ei1 = Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }
                    outCode += ( "0000"+ei1 + eq.getState());
                }else if("DELAY".equals(beforeVelue)){
                    out ++;  //use for get number
                    length += 6;
                    String ei1 ="0";
                    if(Integer.toHexString(Integer.parseInt(eq.getEqid())).length() < 4){
                        for (int j = 0; j<4- Integer.toHexString(Integer.parseInt(eq.getEqid())).length()-1; j++ ){
                            ei1 += 0;
                        }
                        ei1 += Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }else{
                        ei1 = Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }
                    outCode += ( ei1 + eq.getState());
                }else if("PHONE".equals(beforeVelue)){
                    out ++;  //use for get number
                    length += 8;
                    String ei1 ="0";
                    if(Integer.toHexString(Integer.parseInt(eq.getEqid())).length() < 4){
                        for (int j = 0; j<4- Integer.toHexString(Integer.parseInt(eq.getEqid())).length()-1; j++ ){
                            ei1 += 0;
                        }
                        ei1 += Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }else{
                        ei1 = Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }
                    outCode += ("0000"+ ei1 + eq.getState());
                }
            }else if("DELAY".equals(eq.getEquipmentDesc())){
                length += 2;
                //settime4 = eq.getState();
                outCode += UnitTools.timeDecode(eq.getState(),4);
            }
        }
        String oooo ="0";
        if(Integer.toHexString(length+32).length() < 4){
            for (int i = 0; i<4-Integer.toHexString(length+32).length()-1; i++ ){
                oooo += 0;
            }
            oooo += Integer.toHexString(length+32);
        }else{
            oooo = Integer.toHexString(length+32);
        }

        String ooo = "";
        int amid2 = Integer.parseInt(amodle.getMid());
        String amid1 = Integer.toHexString(amid2);
        if(Integer.toHexString(Integer.parseInt(amodle.getMid())).length()<2){
            for(int i =0; i<2 - amid1.length();i++){
                ooo += 0;
            }
            ooo +=amid1;
        }else{
            ooo = amid1;
        }

        String oo = "0";
        if(Integer.toHexString(in).length()<2){
            oo = oo + Integer.toHexString(in);
        }else{
            oo = Integer.toHexString(in);
        }

        String o ="0";
        if(Integer.toHexString(out).length()<2){
            o += Integer.toHexString(out);
        }


        String name = getTopBarView().getEditTitleText();
        String ds = CoderUtils.getAscii(name);


        String deCode =  oooo + ooo + ds + cm3 + UnitTools.timeDecode(settime4,6) + click5 + inf6 + oo + o +inCode +outCode;
        LOG.I(TAG,deCode);
        ByteUtil byteUtil = new ByteUtil();
        String crc = byteUtil.CRCmakerCharAndCode(deCode);

        String abc = deCode + crc;

        messageCode = deCode;
        sendDecode(abc);
    }

    /**
     * create a new sence modle
     */
    private String  getcode() {
        int length = 2;//情景长度
        length += 1;//系统情景编号

        String cm3 =mcp.trigger;
        length +=1;//条件选择

        String settime4 ="000000";
        length +=3;//定时

        String cl = "FF";
        for(EquipmentBean de : mcp.input){
            if( "CLICK".equals(de.getEquipmentDesc())){
                cl = "ab";
            }

        }

        String click5 = cl;
        length += 1;//点击执行

        String ph = "FF";
        for(EquipmentBean de : mcp.output){
            if( "PHONE".equals(de.getEquipmentDesc())){
                ph = "AC";
            }

        }

        String inf6 =ph;
        length +=1;//通知手机

        int in = 0;
        length +=1;//输入设备个数

        int out = 0;
        length +=1;//输出设备个数


        String inCode="";
        for(int i = 0; i < inputShow.size(); i++ ){
            EquipmentBean eq = inputShow.get(i);
            if(!"TIMER".equals(eq.getEquipmentDesc())&&!"CLICK".equals(eq.getEquipmentDesc())){
                in ++;
                length += 6;
                String ei1 ="0";
                if(Integer.toHexString(Integer.parseInt(eq.getEqid())).length() < 4){
                    for (int j = 0; j<4- Integer.toHexString(Integer.parseInt(eq.getEqid())).length()-1; j++ ){
                        ei1 += 0;
                    }
                    ei1 += Integer.toHexString(Integer.parseInt(eq.getEqid()));
                }else{
                    ei1 = Integer.toHexString(Integer.parseInt(eq.getEqid()));
                }
                inCode += (  ei1 + eq.getState());
            }else{
                settime4 = eq.getState();
            }
        }
        String outCode ="";
        for(int i = 0; i < outputShow.size(); i++ ){
            EquipmentBean eq = outputShow.get(i);
            if(!"DELAY".equals(eq.getEquipmentDesc())&&!"PHONE".equals(eq.getEquipmentDesc())){

                String beforeVelue = "ok";
                if(i >0 && outputShow.get(i-1) != null){
                    EquipmentBean before = outputShow.get(i-1);
                    beforeVelue = before.getEquipmentDesc();
                }

                if(i==0 || (i-1 >= 0 && !"DELAY".equals(beforeVelue)&&!"PHONE".equals(beforeVelue))){
                    out ++;  //use for get number
                    length += 8;
                    String ei1 ="0";
                    if(Integer.toHexString(Integer.parseInt(eq.getEqid())).length() < 4){
                        for (int j = 0; j<4- Integer.toHexString(Integer.parseInt(eq.getEqid())).length()-1; j++ ){
                            ei1 += 0;
                        }
                        ei1 += Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }else{
                        ei1 = Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }
                    outCode += ( "0000"+ei1 + eq.getState());
                }else  if("DELAY".equals(beforeVelue)){
                    out ++;  //use for get number
                    length += 6;
                    String ei1 ="0";
                    if(Integer.toHexString(Integer.parseInt(eq.getEqid())).length() < 4){
                        for (int j = 0; j<4- Integer.toHexString(Integer.parseInt(eq.getEqid())).length()-1; j++ ){
                            ei1 += 0;
                        }
                        ei1 += Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }else{
                        ei1 = Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }
                    outCode += ( ei1 + eq.getState());
                }else  if("PHONE".equals(beforeVelue)){
                    out ++;  //use for get number
                    length += 8;
                    String ei1 ="0";
                    if(Integer.toHexString(Integer.parseInt(eq.getEqid())).length() < 4){
                        for (int j = 0; j<4- Integer.toHexString(Integer.parseInt(eq.getEqid())).length()-1; j++ ){
                            ei1 += 0;
                        }
                        ei1 += Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }else{
                        ei1 = Integer.toHexString(Integer.parseInt(eq.getEqid()));
                    }
                    outCode += ( "0000"+ei1 + eq.getState());
                }
            }else if("DELAY".equals(eq.getEquipmentDesc())){
                length += 2;
                //settime4 = eq.getState();
                outCode += UnitTools.timeDecode(eq.getState(),4);
            }
        }
        String oooo ="0";
        if(Integer.toHexString(length+32).length() < 4){
            for (int i = 0; i<4-Integer.toHexString(length+32).length()-1; i++ ){
                oooo += 0;
            }
            oooo += Integer.toHexString(length+32);
        }else{
            oooo = Integer.toHexString(length+32);
        }

        String ooo = "";
        int amid2 = Integer.parseInt(amodle.getMid());
        String amid1 = Integer.toHexString(amid2);
        if(Integer.toHexString(Integer.parseInt(amodle.getMid())).length()<2){
            for(int i =0; i<2 - amid1.length();i++){
                ooo += 0;
            }
            ooo +=amid1;
        }else{
            ooo = amid1;
        }

        String oo = "0";
        if(Integer.toHexString(in).length()<2){
            oo = oo + Integer.toHexString(in);
        }else{
            oo = Integer.toHexString(in);
        }

        String o ="0";
        if(Integer.toHexString(out).length()<2){
            o += Integer.toHexString(out);
        }


        String name = getTopBarView().getEditTitleText();
        String ds = CoderUtils.getAscii(name);


        String deCode =  oooo + ooo + ds + cm3 + UnitTools.timeDecode(settime4,6) + click5 + inf6 + oo + o +inCode +outCode;
        LOG.I(TAG,deCode);
        LOG.I(TAG,"edit code :"+deCode);
        return(deCode);
    }



    private void sendDecode(String deCode) {

        if(mcp.modify){
            SendCommand.Command = SendCommand.MODIFY_SCENE;
            ssd.modifyScene(deCode);

        }else {
            SendCommand.Command = SendCommand.INCREACE_SCENE;
            ssd.increaceScene(deCode);

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            try {
                if(mcp.modify){
                    String newcode =  getcode();
                    if(!init_code.toUpperCase().equals(newcode.toUpperCase())){
                        ECAlertDialog elc = ECAlertDialog.buildAlert(NewGroup2Activity.this, getResources().getString(R.string.scene_has_modify), getResources().getString(R.string.no_save), getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mcp.cleanModleCondition();
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confirmNum++;
                                verfication();
                                if (confirmNum == 1){
                                    addModles();
                                }else if(confirmNum > 1){
                                    sendDecode(messageCode);
                                }
                            }
                        });
                        elc.show();

                    }else{
                        mcp.cleanModleCondition();
                        finish();
                    }

                }else{
                    if( mcp.input ==null || mcp.output ==null || mcp.input.size()==0 || mcp.output.size()==0){
                        mcp.cleanModleCondition();
                        finish();
                    }else{
                        ECAlertDialog elc = ECAlertDialog.buildAlert(NewGroup2Activity.this, getResources().getString(R.string.scene_has_modify), getResources().getString(R.string.no_save), getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mcp.cleanModleCondition();
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confirmNum++;
                                verfication();
                                if (confirmNum == 1){
                                    addModles();
                                }else if(confirmNum > 1){
                                    sendDecode(messageCode);
                                }
                            }
                        });
                        elc.show();
                    }
                }

            }catch (NullPointerException e){
                mcp.cleanModleCondition();
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void onDestroy() {

        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }

    private int getmid(){
        SED = new SceneDAO(this);
        List<String> list = SED.findAllMid(ConnectionPojo.getInstance().deviceTid);

        //mid排序，非常重要，varchar数据库本身排序达不到数字排序的效果
        Collections.sort(list, new Comparator<String>() {
            public int compare(String o1, String o2) {

                if(Integer.parseInt(o1)<Integer.parseInt(o2)){
                    return  -1;
                }else if(Integer.parseInt(o1)>Integer.parseInt(o2)){
                    return 1;
                }else{
                    return 0;
                }

            }
        });

        if(list.size()==0){
            return 1;
        }else if(list.size()==1){
            if(Integer.parseInt(list.get(0))==1){
                return 2;
            }else{
                return 1;
            }
        }else{
            int m = 0;
            for(int i=0;i<list.size()-1;i++){


                if(i==0){

                    int d = Integer.parseInt(list.get(i));
                    if(d!=1){
                        m = 1;
                        break;
                    }
                    else {
                        if( (Integer.parseInt(list.get(i))+1) < Integer.parseInt(list.get(i+1))){
                            m = Integer.parseInt(list.get(i))+1;
                            break;
                        }else{
                            m = Integer.parseInt(list.get(i))+2;
                        }
                    }


                }else{
                    if( (Integer.parseInt(list.get(i))+1) < Integer.parseInt(list.get(i+1))){
                        m = Integer.parseInt(list.get(i))+1;
                        break;
                    }else{
                        m = Integer.parseInt(list.get(i))+2;
                    }

                }


            }
            return m;
        }

    }


    private void showTipMask()
    {
        int dr;

        if("zh".equals(lan)){
            dr = R.layout.highlight_info_up;
        }else if("de".equals(lan))
        {
            dr = R.layout.highlight_info_up_de;
        }else if("fr".equals(lan))
        {
            dr = R.layout.highlight_info_up_fr;
        }else if("es".equals(lan))
        {
            dr = R.layout.highlight_info_up_es;
        }else if("fi".equals(lan))
        {
            dr = R.layout.highlight_info_up_fi;
        }else {
            dr = R.layout.highlight_info_up_en;
        }
        mHightLight = new HighLight(this)//
               // 如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(input.getChildAt(input.getChildCount()-1), dr, new HighLight.OnPosCallback()
                {
                    /**
                     * @param rightMargin
                     *            高亮view在anchor中的右边距
                     * @param bottomMargin
                     *            高亮view在anchor中的下边距
                     * @param rectF
                     *            高亮view的l,t,r,b,w,h都有
                     * @param marginInfo
                     *            设置你的布局的位置，一般设置l,t或者r,b
                     */
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo)
                    {

                        System.out.println("rightMargin" + rightMargin);
                        System.out.println("rectF.width()" + rectF.width());
                        System.out.println("rectF.height()" + rectF.height());
                        System.out.println("bottomMargin" + bottomMargin);
                        System.out.println("--------------------------------------------------------------------");
                        marginInfo.leftMargin = rectF.right - rectF.width() / 2;
                        marginInfo.topMargin = rectF.bottom;
                    }

                }).setClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        mHightLight.remove();
                        showTipMask2();
                    }
                });

        mHightLight.show();
    }

    private void showTipMask2()
    {
        int dr;

        if("zh".equals(lan)){
            dr = R.layout.highlight_info_down;
        }else if("de".equals(lan))
        {
            dr = R.layout.highlight_info_down_de;
        }else if("fr".equals(lan))
        {
            dr = R.layout.highlight_info_down_fr;
        }else if("es".equals(lan))
        {
            dr = R.layout.highlight_info_down_es;
        }else {
            dr = R.layout.highlight_info_down_en;
        }
        mHightLight2 = new HighLight(this)//
                // 如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(output.getChildAt(output.getChildCount()-1), dr, new HighLight.OnPosCallback()
                {
                    /**
                     * @param rightMargin
                     *            高亮view在anchor中的右边距
                     * @param bottomMargin
                     *            高亮view在anchor中的下边距
                     * @param rectF
                     *            高亮view的l,t,r,b,w,h都有
                     * @param marginInfo
                     *            设置你的布局的位置，一般设置l,t或者r,b
                     */
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo)
                    {

                        System.out.println("rightMargin" + rightMargin);
                        System.out.println("rectF.width()" + rectF.width());
                        System.out.println("rectF.height()" + rectF.height());
                        System.out.println("bottomMargin" + bottomMargin);
                        System.out.println("--------------------------------------------------------------------");
                        marginInfo.leftMargin = rectF.width() / 2;
                        marginInfo.bottomMargin = bottomMargin + rectF.height();
                    }

                }).setClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        mHightLight2.remove();
                        showTipMask3();
                    }
                });

        mHightLight2.show();

    }

    private void showTipMask3()
    {
        int dr;
        if("zh".equals(lan)){
            dr = R.layout.highlight_info_name;
        }else if("de".equals(lan))
        {
            dr = R.layout.highlight_info_name_de;
        }else if("fr".equals(lan))
        {
            dr = R.layout.highlight_info_name_fr;
        }else if("es".equals(lan))
        {
            dr = R.layout.highlight_info_name_es;
        }else {
            dr = R.layout.highlight_info_name_en;
        }

        mHightLight3 = new HighLight(this)//
                // 如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(getTopBarView().getEditTitle(), dr, new HighLight.OnPosCallback()
                {
                    /**
                     * @param rightMargin
                     *            高亮view在anchor中的右边距
                     * @param bottomMargin
                     *            高亮view在anchor中的下边距
                     * @param rectF
                     *            高亮view的l,t,r,b,w,h都有
                     * @param marginInfo
                     *            设置你的布局的位置，一般设置l,t或者r,b
                     */
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo)
                    {

                        System.out.println("rightMargin" + rightMargin);
                        System.out.println("rectF.width()" + rectF.width());
                        System.out.println("rectF.height()" + rectF.height());
                        System.out.println("bottomMargin" + bottomMargin);
                        System.out.println("--------------------------------------------------------------------");
                        marginInfo.leftMargin = rectF.width() / 2;
                        marginInfo.topMargin = rectF.bottom;
                    }

                });

        mHightLight3.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case CHOOSE_CONDITION:
                      String result = data.getStringExtra("condition");
                      mcp.trigger = result;
                    if("00".equals(mcp.trigger)){
                        choseCdt.setTitleText(getResources().getString(R.string.one_condition));
                    }else if("ff".equals(mcp.trigger.toLowerCase())){
                        choseCdt.setTitleText(getResources().getString(R.string.all_condition));
                    }
                    break;
                default:

                    break;
            }

        }

    }


    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent()== SendCommand.INCREACE_SCENE || event.getEvent() == SendCommand.MODIFY_SCENE) {
            try{
                SED = new SceneDAO(this);
                amodle.setName(getTopBarView().getEditTitleText());
                amodle.setCode(messageCode);
                LOG.I(TAG,"amodle.getCode()"+amodle.getCode());

                if(!mcp.modify){




                    try {
                        LOG.I(TAG,"添加前amodle："+amodle.toString());
                        SED.addScence(amodle);
                        SysmodelDAO sysmodelDAO = new SysmodelDAO(this);
                        String sid =  sysmodelDAO.findIdByChoice(ConnectionPojo.getInstance().deviceTid).getSid();
                        amodle.setSid(sid);
                        List<SceneBean> e = SED.findScenceListBySidAndMid(amodle);
                        if(e != null && e.size()>0){
                            SED.deleteFromsceneGroup(amodle);
                        }
                        LOG.I(TAG,"添加后amodle："+amodle.toString());
                        SED.addScence(amodle);
                        SceneCopyPojo.getInstance().code = messageCode;
                    }catch (Exception e){
                        LOG.I("ceshi","无选中的情景组");
                    }

                }else{

                    try{
                        SED.updateByMid(amodle);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                confirmNum=0;
                mcp.cleanModleCondition();
                finish();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            SendCommand.clearCommnad();
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(!mcp.modify && !TextUtils.isEmpty(SceneCopyPojo.getInstance().code) && SceneCopyPojo.getInstance().first){
                        SceneCopyPojo.getInstance().first = false;
                        final ResolveScene resolveScene = new ResolveScene(NewGroup2Activity.this,SceneCopyPojo.getInstance().code);

                        if(resolveScene.isTarget()) {

                            if (resolveScene.getInput() != null && resolveScene.getInput()!=null && resolveScene.getInput().size()>0 && resolveScene.getOutput().size()>0) {
                                boolean flag = false;
                                try {
                                    List<EquipmentBean> in = resolveScene.getInput();
                                    for (EquipmentBean e : in) {

                                            EquipDAO ED = new EquipDAO(NewGroup2Activity.this);
                                            EquipmentBean fromEqid = ED.findByeqid(e.getEqid(), ConnectionPojo.getInstance().deviceTid);
                                            if (!"IN".equals(e.getEqid()) && !"CLICK".equals(e.getEqid()) && fromEqid == null) {
                                                flag = true;
                                            }
                                    }
                                } catch (Exception e) {
                                    LOG.I(TAG, "no input data");
                                }

                                try {
                                    List<EquipmentBean> in = resolveScene.getOutput();
                                    for (EquipmentBean e : in) {

                                        EquipDAO ED = new EquipDAO(NewGroup2Activity.this);
                                        EquipmentBean fromEqid = ED.findByeqid(e.getEqid(), ConnectionPojo.getInstance().deviceTid);
                                        if (!"0".equals(e.getEqid()) && !"OUT".equals(e.getEqid()) && !"PHONE".equals(e.getEqid()) && fromEqid == null) {
                                            flag = true;
                                        }
                                    }
                                } catch (Exception e) {
                                    LOG.I(TAG, "no input data");
                                }
                                if(flag == false){
                                    ecAlertDialog = ECAlertDialog.buildAlert(NewGroup2Activity.this, getResources().getString(R.string.copy_scene_alert), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SceneCopyPojo.getInstance().code = null;
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            smp = resolveScene.getSp();
                                            mcp.trigger =smp.getCondition();

                                                inputShow = new ArrayList<>();
                                                try {
                                                    List<EquipmentBean> in = resolveScene.getInput();
                                                    for(EquipmentBean e : in){
                                                        if("IN".equals(e.getEqid())){
                                                            e.setEquipmentName(getResources().getString(R.string.timer));
                                                            e.setEquipmentDesc("TIMER");
                                                            inputShow.add(e);
                                                        }else if("CLICK".equals(e.getEqid())){
                                                            e.setEquipmentName(getResources().getString(R.string.clicktoaction));
                                                            e.setEquipmentDesc("CLICK");
                                                            inputShow.add(e);
                                                        }else {
                                                            EquipDAO ED = new EquipDAO(NewGroup2Activity.this);
                                                            EquipmentBean fromEqid = ED.findByeqid(e.getEqid(), ConnectionPojo.getInstance().deviceTid);
                                                            if(fromEqid!=null){
                                                                e.setEquipmentDesc(fromEqid.getEquipmentDesc());
                                                                e.setEquipmentName(fromEqid.getEquipmentName());
                                                            }
                                                            inputShow.add(e);
                                                        }
                                                    }
                                                }catch (Exception e){
                                                    LOG.I(TAG,"no input data");
                                                    inputShow = new ArrayList<>();
                                                    mcp.input = inputShow;
                                                }
                                                mcp.input = inputShow;







                                                outputShow = new ArrayList<>();
                                                try {
                                                    List<EquipmentBean> out = resolveScene.getOutput();
                                                    for(EquipmentBean e : out){
                                                        if("PHONE".equals(e.getEqid())){
                                                            e.setEquipmentName(getResources().getString(R.string.phone));
                                                            e.setEquipmentDesc("PHONE");
                                                            outputShow.add(e);
                                                        }else if("OUT".equals(e.getEqid())){
                                                            e.setEquipmentName(getResources().getString(R.string.delay));
                                                            e.setEquipmentDesc("DELAY");
                                                            outputShow.add(e);
                                                        }else if("0".equals(e.getEqid())){
                                                            e.setEquipmentDesc("GATEWAY");
                                                            e.setEquipmentName(getResources().getString(R.string.gateway));
                                                            outputShow.add(e);
                                                        }else {
                                                            EquipDAO ED = new EquipDAO(NewGroup2Activity.this);
                                                            EquipmentBean fromEqid = ED.findByeqid(e.getEqid(), ConnectionPojo.getInstance().deviceTid);
                                                            if(fromEqid!=null){
                                                                e.setEquipmentDesc(fromEqid.getEquipmentDesc());
                                                                e.setEquipmentName(fromEqid.getEquipmentName());
                                                            }
                                                            outputShow.add(e);
                                                        }
                                                    }
                                                }catch (Exception e){
                                                    LOG.I(TAG,"no data output");
                                                    outputShow = new ArrayList<>();
                                                    mcp.output = outputShow;
                                                }

                                                mcp.output = outputShow;


                                            initView();
                                            initInput();
                                            initOutput();

                                        }
                                    });
                                    ecAlertDialog.show();
                                }

                            }


                        }
                    }
                    break;
            }
        }
    };


}
