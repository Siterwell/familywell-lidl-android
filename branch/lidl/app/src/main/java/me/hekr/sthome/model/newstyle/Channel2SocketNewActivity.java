package me.hekr.sthome.model.newstyle;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by ST-020111 on 2017/7/6.
 */

public class Channel2SocketNewActivity extends SceneBaseActivity {

    private EquipmentBean device;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private WheelView method,style;
    private ArrayList<String> item_channel = new ArrayList<String>(),item_style= new ArrayList<>();
    @Override
    protected void onCreateInit() {
        try {
            initData();
            initViewGuider();
            initView();
        }catch (Exception e){
            LOG.I("ceshi","data is null");
        }
    }

    private void initView(){
        method = (WheelView) findViewById(R.id.item);
        style = (WheelView) findViewById(R.id.item2);
        method.setAdapter(new NumberAdapter(item_channel,210));
        style.setAdapter(new NumberAdapter(item_style,210));
        if(mcp.position!=-1) {

            if (device.getState() != null && device.getState().length() == 8) {

                if ("01010000".equals(device.getState()) ) {
                   method.setCurrentItem(0);
                   style.setCurrentItem(0);
                }else if("01000000".equals(device.getState())) {
                    method.setCurrentItem(0);
                    style.setCurrentItem(1);
                }else if("0100FF00".equals(device.getState())) {
                    method.setCurrentItem(0);
                    style.setCurrentItem(2);
                }else if("02020000".equals(device.getState())) {
                    method.setCurrentItem(1);
                    style.setCurrentItem(0);
                }else if("02000000".equals(device.getState())) {
                    method.setCurrentItem(1);
                    style.setCurrentItem(1);
                }else if("0200FF00".equals(device.getState())) {
                    method.setCurrentItem(1);
                    style.setCurrentItem(2);
                }else{
                    method.setCurrentItem(0);
                    style.setCurrentItem(0);
                }


            }
        }else{
            method.setCurrentItem(0);
            style.setCurrentItem(0);
        }

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
                    Intent i = new Intent(Channel2SocketNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);
                }else {
                    mcp.position=-1;
                    Intent i = new Intent(Channel2SocketNewActivity.this, NewGroup2Activity.class);
                    startActivity(i);
                }
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int a = method.getCurrentItem();
                int b = style.getCurrentItem();

                if(a==0){
                    if(b==0){
                        device.setState("01010000");
                    }else if(b==1){
                        device.setState("01000000");
                    }else if(b==2){
                        device.setState("0100FF00");
                    }

                }else if(a==1){
                    if(b==0){
                        device.setState("02020000");
                    }else if(b==1){
                        device.setState("02000000");
                    }else if(b==2){
                        device.setState("0200FF00");
                    }
                }



                if(mcp.position==-1){
                    mcp.output.add(device);

                }else {
                    mcp.output.set(mcp.position,device);
                    mcp.position=-1;
                }
                Intent i = new Intent(Channel2SocketNewActivity.this, NewGroup2Activity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void initData() {
        if(mcp.position!=-1){
            if("input".equals(mcp.condition)){
                device = mcp.input.get(mcp.position);
            }
            else if("output".equals(mcp.condition)){
                device = mcp.output.get(mcp.position);
            }

        }else {
            device = mcp.device;
        }


        String[] strs = getResources().getStringArray(R.array.socket_channel);
        for(String ds:strs){
            item_channel.add(ds);
        }

        String[] strs2 = getResources().getStringArray(R.array.socket_actions);
        for(String ds:strs2){
            item_style.add(ds);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_2_channel_socket;
    }

    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items, int lengh) {
            super(items,lengh);
        }

    }
}
