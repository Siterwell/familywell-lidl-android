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
 * Created by jishu0001 on 2016/9/18.
 */
public class SocketNewActivity extends TopbarSuperActivity {
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
                       device.setState("01010000");//设置为开
                       break;
                   case 1:
                       device.setState("01000000");//设置为关
                       break;
                   case 2:
                       device.setState("0100ff00");//设置为切换
                       break;
                   default:break;
               }
            }
        });


        a = device.getState();
        if( a != null){
            if("01010000".equals(a)){
                wheelView.setCurrentItem(0);
            }else if("01000000".equals(a)){
                wheelView.setCurrentItem(1);
            }else if("0100ff00".equals(a) || "0100FF00".equals(a)){
                wheelView.setCurrentItem(2);
            }
        }
        initViewGuider();
    }catch (Exception e){
        LOG.I("ceshi","data is null");
    }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_socket;
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
                    Intent i = new Intent(SocketNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);
                }else {
                    mcp.position=-1;
                    Intent i = new Intent(SocketNewActivity.this, NewGroup2Activity.class);
                    startActivity(i);
                }
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mcp.position==-1){

                    if("input".equals(mcp.condition)){
                        mcp.input.add(device);
                    }
                    else if("output".equals(mcp.condition)){
                        mcp.output.add(device);
                    }


                }else {

                    if("input".equals(mcp.condition)){
                        mcp.input.set(mcp.position,device);
                    }
                    else if("output".equals(mcp.condition)){
                        mcp.output.set(mcp.position,device);
                    }
                    mcp.position=-1;
                }
                Intent i = new Intent(SocketNewActivity.this, NewGroup2Activity.class);
                startActivity(i);
                finish();
            }
        });

    }


    private void initData() {

        String[] strs = getResources().getStringArray(R.array.socket_actions);
        for(String ds:strs){
            itemslist.add(ds);
        }

        if(mcp.position!=-1){
            if("input".equals(mcp.condition)){
                device = mcp.input.get(mcp.position);
            }
            else if("output".equals(mcp.condition)){
                device = mcp.output.get(mcp.position);
            }

        }else {
            device = mcp.device;
            device.setState("01010000");//设置为开
        }
    }



}
