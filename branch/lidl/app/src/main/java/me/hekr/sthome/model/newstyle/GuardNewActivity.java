package me.hekr.sthome.model.newstyle;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
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
 * Created by jishu0001 on 2016/10/15.
 */
public class GuardNewActivity extends TopbarSuperActivity {
    private EquipmentBean device;
    private String a;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private WheelView wheelView;
    private ArrayList<String> itemslist = new ArrayList<String>();

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
                        device.setState("55000000");//设置为开
                        break;
                    case 1:
                        device.setState("AA000000");//设置为关
                        break;
                    default:break;
                }
            }
        });


        a = device.getState();
        if(a != null){
            if("55000000".equals(a)){
                wheelView.setCurrentItem(0);
            }else if("AA000000".equals(a)){
                wheelView.setCurrentItem(1);
            }
        }
        initViewGuider();
    }catch (Exception e){
        LOG.I("ceshi","data is null");
    }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_guard;
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
                    Intent i = new Intent(GuardNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);
                }else {
                    mcp.position=-1;
                    Intent i = new Intent(GuardNewActivity.this, NewGroup2Activity.class);
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
                Intent i = new Intent(GuardNewActivity.this, NewGroup2Activity.class);
                startActivity(i);
                finish();
            }
        });
        
    }

    private void initData() {

        String[] strs = getResources().getStringArray(R.array.guard_actions);
        for(String ds:strs){
            itemslist.add(ds);
        }

        if(mcp.position!=-1){
            device = mcp.output.get(mcp.position);
        }else {
            device = mcp.device;
            device.setState("55000000");//设置为开
        }
    }

}
