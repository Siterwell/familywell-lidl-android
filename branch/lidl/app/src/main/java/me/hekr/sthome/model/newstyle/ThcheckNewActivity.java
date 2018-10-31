package me.hekr.sthome.model.newstyle;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by ST-020111 on 2017/7/6.
 */

public class ThcheckNewActivity extends TopbarSuperActivity {

    private EquipmentBean device;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private WheelView method,style,number_temp,number_hum;
    private ArrayList<String> item_w1 = new ArrayList<String>(),item_h1 = new ArrayList<>();
    private ArrayList<String> item_method = new ArrayList<String>(),item_style = new ArrayList<>();
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
        method = (WheelView) findViewById(R.id.method);
        style = (WheelView) findViewById(R.id.style);
        number_temp = (WheelView) findViewById(R.id.num_temp);
        number_hum = (WheelView)findViewById(R.id.num_hum);
        number_temp.setCyclic(true);
        number_hum.setCyclic(true);
        method.setAdapter(new NumberAdapter(item_method,210));
        style.setAdapter(new NumberAdapter(item_style,210));
        number_temp.setAdapter(new NumberAdapter(item_w1,210));
        number_hum.setAdapter(new NumberAdapter(item_h1,210));
        method.addChangingListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if(newValue == 0){
                    number_temp.setVisibility(View.VISIBLE);
                    number_hum.setVisibility(View.GONE);
                }else {
                    number_temp.setVisibility(View.GONE);
                    number_hum.setVisibility(View.VISIBLE);
                }
            }
        });
        if(mcp.position!=-1) {

            if (device.getState() != null && device.getState().length() == 8) {
                String wd1 = device.getState().substring(0,2);
                String wd2 = device.getState().substring(2,4);
                String sd1 = device.getState().substring(4,6);
                String sd2 = device.getState().substring(6);

                if ("7F00FF".equals(device.getState().substring(2)) ) {
                    number_temp.setVisibility(View.VISIBLE);
                    number_hum.setVisibility(View.GONE);

                    //number_temp.setCurrentItem(Integer.parseInt(wd1,16)-1);
                    if(Integer.parseInt(wd1,16)>215){
                        int ds = 40 - (256 - Integer.parseInt(wd1,16));
                        number_temp.setCurrentItem(ds);
                    }else if(Integer.parseInt(wd1,16)<91){
                        number_temp.setCurrentItem(Integer.parseInt(wd1,16) + 40);
                    }else{
                        number_temp.setCurrentItem(0);
                    }
                    number_hum.setCurrentItem(0);
                    method.setCurrentItem(0);
                    style.setCurrentItem(0);
                }else if("D8".equals(device.getState().substring(0,2)) && "00FF".equals(device.getState().substring(4))){

                    number_temp.setVisibility(View.VISIBLE);
                    number_hum.setVisibility(View.GONE);

//                    number_temp.setCurrentItem(Integer.parseInt(wd2,16)-1);
                    if(Integer.parseInt(wd2,16)>215){
                        int ds = 40 - (256 - Integer.parseInt(wd2,16));
                        number_temp.setCurrentItem(ds);
                    }else if(Integer.parseInt(wd2,16)<91){
                        number_temp.setCurrentItem(Integer.parseInt(wd2,16) + 40);
                    }else{
                        number_temp.setCurrentItem(0);
                    }
                    number_hum.setCurrentItem(0);
                    method.setCurrentItem(0);
                    style.setCurrentItem(1);

                }else if("D87F".equals(device.getState().substring(0,4)) && "FF".equals(device.getState().substring(6)) ){

                    number_temp.setVisibility(View.GONE);
                    number_hum.setVisibility(View.VISIBLE);
                    number_temp.setCurrentItem(0);
                    number_hum.setCurrentItem(Integer.parseInt(sd1,16)-1);
                    method.setCurrentItem(1);
                    style.setCurrentItem(0);

                }else if("D87F00".equals(device.getState().substring(0,6))){

                    number_temp.setVisibility(View.GONE);
                    number_hum.setVisibility(View.VISIBLE);
                    number_temp.setCurrentItem(0);
                    number_hum.setCurrentItem(Integer.parseInt(sd2,16)-1);
                    method.setCurrentItem(1);
                    style.setCurrentItem(1);

                }
//                if (!"FF".equals(wd2)) {
//                    String wdu2 = String.valueOf(Integer.parseInt(wd2,16));
////                    w2.setHint(""+wdu2);
//                    w2.setCurrentItem(Integer.parseInt(wdu2));
//                }
//                if (!"FF".equals(sd1)) {
//                    String sdu1 = String.valueOf(Integer.parseInt(sd1,16));
////                    h1.setHint(""+sdu1);
//                    h1.setCurrentItem(Integer.parseInt(sdu1));
//                }
//                if (!"FF".equals(device.getState().substring(6, 8))) {
//                    String sdu2 = String.valueOf(Integer.parseInt(sd2,16));
////                    h2.setHint(""+sdu2);
//                    h2.setCurrentItem(Integer.parseInt(sdu2));
//                }
            }
        }else{
            number_temp.setVisibility(View.VISIBLE);
            number_hum.setVisibility(View.GONE);
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
                    Intent i = new Intent(ThcheckNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);
                }else {
                    mcp.position=-1;
                    Intent i = new Intent(ThcheckNewActivity.this, NewGroup2Activity.class);
                    startActivity(i);
                }
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  int a = number_temp.getCurrentItem();
                String temp = null;
//                temp = Integer.toHexString(a).toUpperCase();
//                if(temp.length()==1){
//                    temp = "0"+temp;
//                }


                if(a<40){
                    temp = Integer.toHexString(256+( a-40 )).toUpperCase();
                }
                else {
                    temp = Integer.toHexString(a-40).toUpperCase();
                    if(temp.length()==1){
                        temp = "0"+temp;
                    }
                }
                  String hum =Integer.toHexString(number_hum.getCurrentItem()+1).toUpperCase();
                if(hum.length()==1){
                    hum = "0"+hum;
                }
                String dsd = null;
                if(method.getCurrentItem()==0){
                    switch (style.getCurrentItem()){
                        case 0:
                            dsd =   temp  +"7F" + "00" + "FF";
                            break;
                        case 1:
                            dsd =  "D8" + temp + "00"  +  "FF" ;
                            break;

                    }
                }else{
                    switch (style.getCurrentItem()){
                        case 0:
                            dsd = "D8" + "7F"  + hum + "FF";
                            break;
                        case 1:
                            dsd = "D8" + "7F" +  "00"  + hum;
                            break;

                    }
                }
                //showToast(dsd);
                device.setState(dsd);
                if(mcp.position==-1){
                    mcp.input.add(device);

                }else {
                    mcp.input.set(mcp.position,device);
                    mcp.position=-1;
                }
                Intent i = new Intent(ThcheckNewActivity.this, NewGroup2Activity.class);
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


        String[] strs = getResources().getStringArray(R.array.thtrigger_method);
        for(String ds:strs){
            item_method.add(ds);
        }

        String[] strs2 = getResources().getStringArray(R.array.thtrigger_style);
        for(String ds:strs2){
            item_style.add(ds);
        }


        for (int i = -40; i <= 90; i ++) {

            String item = String.valueOf(i);

            if ( i>-10 && i<0) {
                item =  item.substring(0,1)+"0"+item.substring(1);
            }else if(i>=0 && i< 10){
                item = "0" + item;
            }

            item_w1.add(item+"℃");
        }

        for (int i = 1; i <= 100; i ++) {
            String item = String.valueOf(i);

            if (item != null && item.length() == 1 && i>=0 && i<10) {
                item = "0" + item;
            }

            item_h1.add(item+"%");
        }


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_thcheck;
    }

    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items, int lengh) {
            super(items,lengh);
        }

    }

}
