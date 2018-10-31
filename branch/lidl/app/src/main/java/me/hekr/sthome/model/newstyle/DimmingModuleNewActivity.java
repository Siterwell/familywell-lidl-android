package me.hekr.sthome.model.newstyle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by ST-020111 on 2017/7/6.
 */

public class DimmingModuleNewActivity extends TopbarSuperActivity {

    private EquipmentBean device;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private WheelView open,brightness;
    private ArrayList<String> item_open = new ArrayList<String>(),item_brightness= new ArrayList<>();
    @Override
    protected void onCreateInit() {
        try {
            initData();
            initViewGuider();
            initView();
        }catch (Exception e){
            Log.i("ceshi","data is null");
        }
    }

    private void initView(){
        open = (WheelView) findViewById(R.id.item);
        brightness = (WheelView) findViewById(R.id.item2);
        open.setAdapter(new NumberAdapter(item_open,210));
        brightness.setAdapter(new NumberAdapter(item_brightness,210));
        if(mcp.position!=-1) {

            if (device.getState() != null && device.getState().length() == 8) {

                int liangdu = Integer.parseInt(device.getState().substring(2,4),16);
                int open2    = Integer.parseInt(device.getState().substring(0,2),16);

                if(open2>=0&&open2<=1){
                    if(open2==0)
                    open.setCurrentItem(1);
                    else
                    open.setCurrentItem(0);
                }else open.setCurrentItem(0);

                if(liangdu>=0&&liangdu<=100){
                    brightness.setCurrentItem(liangdu);
                }else brightness.setCurrentItem(0);

            }
        }else{
            open.setCurrentItem(0);
            brightness.setCurrentItem(0);
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
                    Intent i = new Intent(DimmingModuleNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);
                }else {
                    mcp.position=-1;
                    Intent i = new Intent(DimmingModuleNewActivity.this, NewGroup2Activity.class);
                    startActivity(i);
                }
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int a = open.getCurrentItem();
                int b = brightness.getCurrentItem();

                if(a==0){

                    String ds2 = ByteUtil.convertByte2HexString((byte)b);
                    device.setState("01"+ds2+"0000");

                }else if(a==1){
                    String ds2 = ByteUtil.convertByte2HexString((byte)b);
                    device.setState("00"+ds2+"0000");
                }



                if(mcp.position==-1){
                    mcp.output.add(device);

                }else {
                    mcp.output.set(mcp.position,device);
                    mcp.position=-1;
                }
                Intent i = new Intent(DimmingModuleNewActivity.this, NewGroup2Activity.class);
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


        String[] strs = getResources().getStringArray(R.array.dimming_modlue_action);
        for(String ds:strs){
            item_open.add(ds);
        }


        for(int i=0;i<=100;i++){
            item_brightness.add(String.valueOf(i)+"%");
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_dimming_module;
    }

    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items, int lengh) {
            super(items,lengh);
        }

    }
}
