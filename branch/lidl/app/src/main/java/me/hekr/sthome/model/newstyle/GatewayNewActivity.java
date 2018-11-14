package me.hekr.sthome.model.newstyle;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.model.modeladapter.OptionAdapter;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by jishu0001 on 2016/11/30.
 */
public class GatewayNewActivity extends TopbarSuperActivity {
    private EquipmentBean device;
    private String a;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private WheelView wheelView;
    private ArrayList<String> itemslist = new ArrayList<String>();
    private SysmodelDAO sysmodelDAO = new SysmodelDAO(this);
    private List<SysModelBean> list2;
    @Override
    protected void onCreateInit() {
        try {
        initData();

        wheelView = (WheelView)findViewById(R.id.item);
        wheelView.setAdapter(new OptionAdapter(itemslist,30));
        wheelView.addChangingListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                switch (newValue){
                    case 0:
                        device.setState("33000000");//设置为开
                        break;
                    case 1:
                        device.setState("44000000");//设置为关
                        break;
                    case 2:
                        device.setState("00000000");//关闭报警
                        break;
                    case 3:
                        device.setState("22000000");//门铃报警
                        break;
                    default:
                        try {
                            String d = list2.get(newValue -4).getSid();
                            device.setState("110"+d+"FFFF");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        break;
                }
            }
        });


        a = device.getState();
        if( a != null){
            if("33000000".equals(a)){
                wheelView.setCurrentItem(0);
            }else if("44000000".equals(a)){
                wheelView.setCurrentItem(1);
            }else if("00000000".equals(a)){
                wheelView.setCurrentItem(2);
            }else if("22000000".equals(a)){
                wheelView.setCurrentItem(3);
            }else if("11".equals(a.substring(0,2))){
                String sids = a.substring(3,4);
                for(int i=0;i<list2.size();i++){
                    if(sids.equals(list2.get(i).getSid())){
                        wheelView.setCurrentItem(i+4);
                        break;
                    }
                }
            }
        }
        initViewGuider();
        }catch (Exception e){
            LOG.I("ceshi","data is null");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_gateway;
    }

    private void initViewGuider() {

        getTopBarView().setTopBarStatus(1, 2, device.getEquipmentName(), getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mcp.position==-1){
                    Intent i = new Intent(GatewayNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);
                }else {
                    mcp.position=-1;
                    Intent i = new Intent(GatewayNewActivity.this, NewGroup2Activity.class);
                    startActivity(i);
                }
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mcp.position==-1){
                    mcp.output.add(device);

                }else {
                    mcp.output.set(mcp.position,device);
                    mcp.position=-1;
                }
                Intent i = new Intent(GatewayNewActivity.this, NewGroup2Activity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void initData() {

        try {
            String[] strs = getResources().getStringArray(R.array.gateway_actions);
            for(String ds:strs){
                itemslist.add(ds);
            }
            boolean flag_has_mensuo = false;
            if(mcp.input.size()>0){
                for(EquipmentBean equipmentBean:mcp.input){
                    if(NameSolve.LOCK.equals(NameSolve.getEqType(equipmentBean.getEquipmentDesc()))){
                        flag_has_mensuo = true;
                        break;
                    }
                }
            }
            if(flag_has_mensuo){
                list2= sysmodelDAO.findAllSys(ConnectionPojo.getInstance().deviceTid);
                for(int i=0;i<list2.size();i++){
                    if("0".equals(list2.get(i).getSid())){
                        itemslist.add(getResources().getString(R.string.switch_to) + getResources().getString(R.string.home_mode));
                    }else if("1".equals(list2.get(i).getSid())){
                        itemslist.add(getResources().getString(R.string.switch_to) + getResources().getString(R.string.out_mode));
                    }else if("2".equals(list2.get(i).getSid())){
                        itemslist.add(getResources().getString(R.string.switch_to) + getResources().getString(R.string.sleep_mode));
                    }else {
                        itemslist.add(getResources().getString(R.string.switch_to) + list2.get(i).getModleName());
                    }

                }
            }
            if(mcp.position!=-1){
                device = mcp.output.get(mcp.position);
            }else {
                device = mcp.device;
                device.setState("33000000");

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
