package me.hekr.sthome.model.newstyle;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.model.modeladapter.GridAdapter;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.NameSolve;

/**
 * Created by jishu0001 on 2016/10/21.
 */
public class ModelCellListActivity extends TopbarSuperActivity {
    private List<EquipmentBean> listDate;
    private EquipDAO ED;
    private GridAdapter listAdapter;
    private GridView gv;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private ECAlertDialog ecAlertDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_modle_cell_list;
    }

    @Override
    protected void onCreateInit() {

        initData();
        initViewGuider();
        initView();
    }


    private void initView() {
        gv = (GridView) findViewById(R.id.equipShow);
        LOG.I("ceshi","listDate"+listDate.toString());
        listAdapter = new GridAdapter(this,listDate);
        gv.setAdapter(listAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EquipmentBean eq = listDate.get(position);
                if(NameSolve.DOOR_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, DoorCheckNewActivity.class);
                            mcp.device = eq;

                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, DoorCheckNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }


                }else if(NameSolve.SOCKET.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){

                  if("output".equals(mcp.condition)){
                      if(mcp.output!=null && mcp.output.size()>0){
                          EquipmentBean de = mcp.output.get(mcp.output.size()-1);
                          if( !eq.getEqid().equals(de.getEqid())){
                              Intent device = new Intent(ModelCellListActivity.this, SocketNewActivity.class);
                              mcp.device = eq;
                              startActivity(device);
                              finish();
                          }else {
                              ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.output_exist,null);
                              ecAlertDialog.show();
                          }
                      }else {
                          Intent device = new Intent(ModelCellListActivity.this, SocketNewActivity.class);
                          mcp.device = eq;
                          startActivity(device);
                          finish();
                      }
                  }
                    else if("input".equals(mcp.condition)){
                      if(mcp.input!=null && mcp.input.size()>0){
                          EquipmentBean de = mcp.input.get(mcp.input.size()-1);
                          if( !eq.getEqid().equals(de.getEqid())){
                              Intent device = new Intent(ModelCellListActivity.this, SocketNewActivity.class);
                              mcp.device = eq;
                              startActivity(device);
                              finish();
                          }else {
                              ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                              ecAlertDialog.show();
                          }
                      }else {
                          Intent device = new Intent(ModelCellListActivity.this, SocketNewActivity.class);
                          mcp.device = eq;
                          startActivity(device);
                          finish();
                      }
                  }
                }else if(NameSolve.PIR_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, PirCheckNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, PirCheckNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.SOS_KEY.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, SOSNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, SOSNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.SM_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, AlarmNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, AlarmNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.CO_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, AlarmNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, AlarmNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.GAS_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, AlarmNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, AlarmNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if (NameSolve.WT_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, AlarmNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, AlarmNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.TH_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
						 if(mcp.input != null){
						                        try{
						                            for(EquipmentBean de : mcp.input){
						                                if( eq.getEqid().equals(de.getEqid())){
						                                    throw new Exception("the same");
						                                }
						                            }
						                            Intent device = new Intent(ModelCellListActivity.this, ThcheckNewActivity.class);
						                            mcp.device = eq;

						                            startActivity(device);
						                            finish();
						                        }catch (Exception e){

                                                    ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                                                    ecAlertDialog.show();

						                        }
						                    }else {
						                        Intent device = new Intent(ModelCellListActivity.this, ThcheckNewActivity.class);
						                        mcp.device = eq;
						                        startActivity(device);
						                        finish();
						                    }
                }else if(NameSolve.LAMP.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.output!=null && mcp.output.size()>0){
                        EquipmentBean de = mcp.output.get(mcp.output.size()-1);
                        if( !eq.getEqid().equals(de.getEqid())){
                            Intent device = new Intent(ModelCellListActivity.this, LampNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }else {
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.output_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, LampNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }

                }else if(NameSolve.GUARD.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {
                    if(mcp.output!=null && mcp.output.size()>0){
                        EquipmentBean de = mcp.output.get(mcp.output.size()-1);
                        if( !eq.getEqid().equals(de.getEqid())){
                            Intent device = new Intent(ModelCellListActivity.this, GuardNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }else {
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.output_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, GuardNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.VALVE.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.output!=null && mcp.output.size()>0){
                        EquipmentBean de = mcp.output.get(mcp.output.size()-1);
                        if( !eq.getEqid().equals(de.getEqid())){
                            Intent device = new Intent(ModelCellListActivity.this, ValveNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }else {
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.output_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, ValveNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }

                }else if(NameSolve.CURTAIN.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.output!=null && mcp.output.size()>0){
                        EquipmentBean de = mcp.output.get(mcp.output.size()-1);
                        if( !eq.getEqid().equals(de.getEqid())){
                            Intent device = new Intent(ModelCellListActivity.this, CurtainNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }else {
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.output_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, CurtainNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.BUTTON.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, NewGroup2Activity.class);
                            eq.setState("00000100");
                            mcp.input.add(eq);
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, NewGroup2Activity.class);
                        eq.setState("00000100");
                        mcp.input.add(eq);
                        startActivity(device);
                        finish();
                    }

                }else if("TIMER".equals(eq.getEquipmentDesc())){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, TimerNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, TimerNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if("DELAY".equals(eq.getEquipmentDesc())){
                    if(mcp.output!=null && mcp.output.size()>0){
                        EquipmentBean de = mcp.output.get(mcp.output.size()-1);
                        if( !eq.getEqid().equals(de.getEqid())){
                            Intent device = new Intent(ModelCellListActivity.this, TimerNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }else {
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.delay_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, TimerNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if("GATEWAY".equals(eq.getEquipmentDesc())){
                    if(mcp.output!=null && mcp.output.size()>0){
                        EquipmentBean de = mcp.output.get(mcp.output.size()-1);
                        if( !eq.getEqid().equals(de.getEqid())){
                            Intent device = new Intent(ModelCellListActivity.this, GatewayNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }else {
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.output_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, GatewayNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }

                }else if("CLICK".equals(eq.getEquipmentDesc())){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, NewGroup2Activity.class);
                            eq.setState("00005500");
                            mcp.input.add(eq);
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.click_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, NewGroup2Activity.class);
                        eq.setState("00005500");
                        mcp.input.add(eq);
                        startActivity(device);
                        finish();
                    }
                }else if("PHONE".equals(eq.getEquipmentDesc())){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.output){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, NewGroup2Activity.class);
                            eq.setState("00005500");
                            mcp.output.add(0,eq);
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.phone_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, NewGroup2Activity.class);
                        eq.setState("00005500");
                        mcp.output.add(0,eq);
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, CXSMALARMNewActivity.class);
//                            eq.setState("00005500");
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, CXSMALARMNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.THERMAL_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, AlarmNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, AlarmNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, MODEBUTTONNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, MODEBUTTONNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.LOCK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
                    if(mcp.input != null){
                        try{
                            for(EquipmentBean de : mcp.input){
                                if( eq.getEqid().equals(de.getEqid())){
                                    throw new Exception("the same");
                                }
                            }
                            Intent device = new Intent(ModelCellListActivity.this, LockNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }catch (Exception e){
                            ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                            ecAlertDialog.show();
                        }
                    }else {
                        Intent device = new Intent(ModelCellListActivity.this, LockNewActivity.class);
                        mcp.device = eq;
                        startActivity(device);
                        finish();
                    }
                }else if(NameSolve.TWO_SOCKET.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){

                    if("output".equals(mcp.condition)){
                        if(mcp.output!=null && mcp.output.size()>0){
                            EquipmentBean de = mcp.output.get(mcp.output.size()-1);
                            if( !eq.getEqid().equals(de.getEqid())){
                                Intent device = new Intent(ModelCellListActivity.this, Channel2SocketNewActivity.class);
                                mcp.device = eq;
                                startActivity(device);
                                finish();
                            }else {
                                ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.output_exist,null);
                                ecAlertDialog.show();
                            }
                        }else {
                            Intent device = new Intent(ModelCellListActivity.this, Channel2SocketNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }
                    }
                    else if("input".equals(mcp.condition)){
                        if(mcp.input!=null && mcp.input.size()>0){
                            EquipmentBean de = mcp.input.get(mcp.input.size()-1);
                            if( !eq.getEqid().equals(de.getEqid())){
                                Intent device = new Intent(ModelCellListActivity.this, SocketNewActivity.class);
                                mcp.device = eq;
                                startActivity(device);
                                finish();
                            }else {
                                ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.input_exist,null);
                                ecAlertDialog.show();
                            }
                        }else {
                            Intent device = new Intent(ModelCellListActivity.this, SocketNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }
                    }



                }else if(NameSolve.DIMMING_MODULE.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){

                    if("output".equals(mcp.condition)){
                        if(mcp.output!=null && mcp.output.size()>0){
                            EquipmentBean de = mcp.output.get(mcp.output.size()-1);
                            if( !eq.getEqid().equals(de.getEqid())){
                                Intent device = new Intent(ModelCellListActivity.this, DimmingModuleNewActivity.class);
                                mcp.device = eq;
                                startActivity(device);
                                finish();
                            }else {
                                ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.output_exist,null);
                                ecAlertDialog.show();
                            }
                        }else {
                            Intent device = new Intent(ModelCellListActivity.this, DimmingModuleNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }
                    }
                }else if(NameSolve.TEMP_CONTROL.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){

                    if("output".equals(mcp.condition)){
                        if(mcp.output!=null && mcp.output.size()>0){
                            EquipmentBean de = mcp.output.get(mcp.output.size()-1);
                            if( !eq.getEqid().equals(de.getEqid())){
                                Intent device = new Intent(ModelCellListActivity.this, TempControlNewActivity.class);
                                mcp.device = eq;
                                startActivity(device);
                                finish();
                            }else {
                                ecAlertDialog = ECAlertDialog.buildPositiveAlert(ModelCellListActivity.this, R.string.output_exist,null);
                                ecAlertDialog.show();
                            }
                        }else {
                            Intent device = new Intent(ModelCellListActivity.this, TempControlNewActivity.class);
                            mcp.device = eq;
                            startActivity(device);
                            finish();
                        }
                    }




                }
            }
        });
    }

    private void initData() {


        if(mcp.sb != null){
            Log.i("Modle",mcp.sb.getName());
        }
        ED = new EquipDAO(this);
        try{
            if("input".equals(mcp.condition)){
                listDate = new ArrayList<EquipmentBean>();
                EquipmentBean theFirstTag = new EquipmentBean();
                theFirstTag.setEqid(String.valueOf("IN"));
                theFirstTag.setEquipmentDesc("TIMER");
                theFirstTag.setEquipmentName(getResources().getString(R.string.timer));
                EquipmentBean theSecondTag = new EquipmentBean();
                theSecondTag.setEqid("CLICK");
                theSecondTag.setEquipmentDesc("CLICK");
                theSecondTag.setEquipmentName(getResources().getString(R.string.clicktoaction));
                listDate.add(theFirstTag);
                listDate.add(theSecondTag);
                if(ED.findInput(ConnectionPojo.getInstance().deviceTid)!=null){
                    listDate.addAll(ED.findInput(ConnectionPojo.getInstance().deviceTid));
                }
            }else if("output".equals(mcp.condition)){
                listDate = new ArrayList<EquipmentBean>();
                EquipmentBean theSecondTag = new EquipmentBean();
                theSecondTag.setEqid("PHONE");
                theSecondTag.setEquipmentDesc("PHONE");
                theSecondTag.setEquipmentName(getResources().getString(R.string.phone));
                listDate.add(theSecondTag);
                EquipmentBean theFirstTag = new EquipmentBean();
                theFirstTag.setEqid(String.valueOf("OUT"));
                theFirstTag.setEquipmentDesc("DELAY");
                theFirstTag.setEquipmentName(getResources().getString(R.string.delay));
                listDate.add(theFirstTag);
                EquipmentBean gateway = new EquipmentBean();
                gateway.setEqid("0");
                gateway.setEquipmentDesc("GATEWAY");
                gateway.setEquipmentName(getResources().getString(R.string.gateway));
                listDate.add(gateway);
                if(ED.findOutput(ConnectionPojo.getInstance().deviceTid)!=null){
                    listDate.addAll(ED.findOutput(ConnectionPojo.getInstance().deviceTid));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(ModelCellListActivity.this,"no data", Toast.LENGTH_LONG).show();
        }

    }

    private void initViewGuider() {

       String ds = getResources().getString(R.string.increase_input);


        if("input".equals(mcp.condition)){
            ds = getResources().getString(R.string.increase_input);
        }else if("output".equals(mcp.condition)){
            ds = getResources().getString(R.string.increase_output);
        }

        getTopBarView().setTopBarStatus(1, 1, ds, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(CoDetailActivity.this,"go back",Toast.LENGTH_SHORT).show();
                Intent addItem = new Intent(ModelCellListActivity.this,NewGroup2Activity.class);

                startActivity(addItem);
                finish();
            }
        },null);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
