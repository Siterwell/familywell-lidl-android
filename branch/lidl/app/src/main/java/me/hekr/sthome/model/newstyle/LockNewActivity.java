package me.hekr.sthome.model.newstyle;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.model.modeladapter.OptionAdapter;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by jishu0001 on 2016/8/30.
 */
public class LockNewActivity extends TopbarSuperActivity {
    private EquipmentBean device;
    String a;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private ArrayList<String> itemslist = new ArrayList<String>();
    private WheelView wheelView;

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
                        device.setState("00005000");
                        break;
                    case 1:
                        device.setState("00005100");
                        break;
                    case 2:
                        device.setState("00005200");
                        break;
                    case 3:
                        device.setState("00005300");
                        break;
                    case 4:
                        device.setState("00001000");
                        break;
                    case 5:
                        device.setState("00002000");
                        break;
                    case 6:
                        device.setState("00003000");
                        break;

                    default:break;
                }
            }
        });


        a = device.getState();
        if(a != null){
            if("00005000".equals(a)){
                wheelView.setCurrentItem(0);
            }else if("00005100".equals(a)){
                wheelView.setCurrentItem(1);
            }else if("00005200".equals(a)){
                wheelView.setCurrentItem(2);
            }else if("00005300".equals(a)){
                wheelView.setCurrentItem(3);
            }else if("00001000".equals(a)){
                wheelView.setCurrentItem(4);
            }else if("00002000".equals(a)){
                wheelView.setCurrentItem(5);
            }else if("00003000".equals(a)){
                wheelView.setCurrentItem(6);
            }
        }
        initViewGuider();
    }catch (Exception e){
        LOG.I("ceshi","data is null");
    }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_sos;
    }


    private void initViewGuider() {
        String name = "";
        if(TextUtils.isEmpty(device.getEquipmentName())){
            name = NameSolve.getDefaultName(this,device.getEquipmentDesc(),device.getEqid());
        }else{
            name = device.getEquipmentName();
        }
        getTopBarView().setTopBarStatus(1, 2, name, getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mcp.position==-1){
                    Intent i = new Intent(LockNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);
                }else {
                    mcp.position=-1;
                    Intent i = new Intent(LockNewActivity.this, NewGroup2Activity.class);
                    startActivity(i);
                }
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mcp.position==-1){
                    mcp.input.add(device);

                }else {
                    mcp.input.set(mcp.position,device);
                    mcp.position=-1;
                }
                Intent i = new Intent(LockNewActivity.this, NewGroup2Activity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void initData() {

        String[] strs = getResources().getStringArray(R.array.lock_input);
        for(String ds:strs){
            itemslist.add(ds);
        }
        if(mcp.position!=-1){
            device = mcp.input.get(mcp.position);
        }else {
            device = mcp.device;
            device.setState("00005000");//设置为开
        }

    }


}
