package me.hekr.sthome.model.newstyle;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.equipment.detail.TempControlSettingActivity;
import me.hekr.sthome.model.modeladapter.OptionAdapter;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by TracyHenry on 2018/8/31.
 */

public class TempControlNewActivity extends TopbarSuperActivity {
    private EquipmentBean device;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private ArrayList<String> set_xiaoshu = new ArrayList<>();
    private ArrayList<String> set_xiaoshu2 = new ArrayList<>();
    private ArrayList<String> items_temp = new ArrayList<>();
    private WheelView wheelView_set_temp,wheelView_set_temp_xiaoshu,wheelView_set_temp_xiaoshu2;
    @Override
    protected void onCreateInit() {
        try {
            initData();
            wheelView_set_temp = (WheelView)findViewById(R.id.set_temp);
            wheelView_set_temp_xiaoshu =(WheelView)findViewById(R.id.set_temp_xiaoshu);
            wheelView_set_temp_xiaoshu2 = (WheelView)findViewById(R.id.set_temp_xiaoshu2);

            wheelView_set_temp.setAdapter(new TempControlNewActivity.NumberAdapter(items_temp,30));
            wheelView_set_temp_xiaoshu.setAdapter(new TempControlNewActivity.NumberAdapter(set_xiaoshu,30));
            wheelView_set_temp_xiaoshu2.setAdapter(new TempControlNewActivity.NumberAdapter(set_xiaoshu2,30));

            wheelView_set_temp.addChangingListener(new WheelView.OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if(newValue == (items_temp.size()-1)){
                        wheelView_set_temp_xiaoshu.setVisibility(View.GONE);
                        wheelView_set_temp_xiaoshu2.setVisibility(View.VISIBLE);
                    }else{
                        wheelView_set_temp_xiaoshu.setVisibility(View.VISIBLE);
                        wheelView_set_temp_xiaoshu2.setVisibility(View.GONE);
                    }
                }
            });



            String status1 = device.getState().substring(0,2);
            byte ds = (byte)Integer.parseInt(status1,16);
            byte xiaoshu = (byte)((0x20) & ds);
            int sta =  ((0x1F) & ds);

            if(sta>=5&&sta<=29){
                wheelView_set_temp_xiaoshu.setVisibility(View.VISIBLE);
                wheelView_set_temp_xiaoshu2.setVisibility(View.GONE);
                wheelView_set_temp.setCurrentItem(sta - 5);
                if(xiaoshu==0){
                    wheelView_set_temp_xiaoshu.setCurrentItem(0);
                }else{
                    wheelView_set_temp_xiaoshu.setCurrentItem(1);
                }
            }else if(sta == 30){
                wheelView_set_temp_xiaoshu.setVisibility(View.GONE);
                wheelView_set_temp_xiaoshu2.setVisibility(View.VISIBLE);
                wheelView_set_temp.setCurrentItem(items_temp.size()-1);
                wheelView_set_temp_xiaoshu2.setCurrentItem(0);
            }else{
                wheelView_set_temp_xiaoshu.setVisibility(View.VISIBLE);
                wheelView_set_temp_xiaoshu2.setVisibility(View.GONE);
                wheelView_set_temp.setCurrentItem(0);
                wheelView_set_temp_xiaoshu.setCurrentItem(0);
            }


            initViewGuider();
        }catch (Exception e){

        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_temp_control;
    }


    private void initViewGuider() {
        String name = "";
        if (TextUtils.isEmpty(device.getEquipmentName())) {
            name = NameSolve.getDefaultName(this, device.getEquipmentDesc(), device.getEqid());
        } else {
            name = device.getEquipmentName();
        }
        getTopBarView().setTopBarStatus(1, 2, name, getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcp.position == -1) {
                    Intent i = new Intent(TempControlNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);
                } else {
                    mcp.position = -1;
                    Intent i = new Intent(TempControlNewActivity.this, NewGroup2Activity.class);
                    startActivity(i);
                }
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(wheelView_set_temp.getCurrentItem()<(items_temp.size()-1)){
                    byte ds = (byte)(wheelView_set_temp.getCurrentItem()+15);

                    if(wheelView_set_temp_xiaoshu.getCurrentItem()==1){
                        ds |=0x20;
                    }

                    String str1 = ByteUtil.convertByte2HexString(ds);
                    device.setState(str1+"800000");
                }else{
                    device.setState("1C800000");
                }


                if (mcp.position == -1) {
                        mcp.output.add(device);

                } else {
                    mcp.output.set(mcp.position, device);
                    mcp.position = -1;
                }
                Intent i = new Intent(TempControlNewActivity.this, NewGroup2Activity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items, int lengh) {
            super(items,lengh);
        }

    }

    private void initData() {
        String[] set_xiaoshu22 = {"0","5"};
        for(int i=5;i<31;i++){
            items_temp.add(String.valueOf(i));
        }

        for(int i=0;i<set_xiaoshu22.length;i++){
            set_xiaoshu.add(set_xiaoshu22[i]);
        }
        set_xiaoshu2.add("0");

        if(mcp.position!=-1){
            device = mcp.output.get(mcp.position);
        }else {
            device = mcp.device;
        }

    }
}
