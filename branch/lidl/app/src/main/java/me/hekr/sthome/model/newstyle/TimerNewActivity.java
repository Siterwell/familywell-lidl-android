package me.hekr.sthome.model.newstyle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.wheelwidget.helper.TimerBean;
import me.hekr.sthome.wheelwidget.picker.SecondDelayPicker;
import me.hekr.sthome.wheelwidget.picker.TimerPicker;
import me.hekr.sthome.wheelwidget.picker.WheelPicker;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by jishu0001 on 2016/10/21.
 */
public class TimerNewActivity extends TopbarSuperActivity {
    private EquipmentBean device;
    private String fromAdd="";
    private SceneBean amodle;

    private String setting_status;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private WheelView wheelView_hour,wheelView_min;
    private WheelView wheelView_min_delay,wheelView_sec_delay;
    private GridView  grid_wek;
    private ArrayList<String> items_hour = new ArrayList<String>();
    private ArrayList<String> items_min = new ArrayList<String>();
    private MyweekAdapter myweekAdapter;
    private byte wekint=0x00;
    private LinearLayout timer_lay;
    private LinearLayout delay_lay;



    @Override
    protected void onCreateInit() {
        try {
        initData();
        initView();
        initViewGuider();
    }catch (Exception e){
        Log.i("ceshi","data is null");
    }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_timer;
    }


    private void initView() {
        timer_lay = (LinearLayout)findViewById(R.id.timer_mode);
        delay_lay = (LinearLayout)findViewById(R.id.delay_mode);
        grid_wek = (GridView)findViewById(R.id.weeklist);
        wheelView_hour = (WheelView)findViewById(R.id.hour);
        wheelView_min  = (WheelView)findViewById(R.id.min);
        wheelView_min_delay = (WheelView)findViewById(R.id.delay_min);
        wheelView_sec_delay = (WheelView)findViewById(R.id.delay_sec);
        wheelView_hour.setCyclic(true);
        wheelView_min.setCyclic(true);
        wheelView_min_delay.setCyclic(true);
        wheelView_sec_delay.setCyclic(true);
        wheelView_hour.setAdapter(new NumberAdapter(items_hour));
        wheelView_hour.setLabel(":");
        wheelView_min.setAdapter(new NumberAdapter(items_min));
        wheelView_min_delay.setAdapter(new NumberAdapter(items_min));
        wheelView_min_delay.setLabel(":");
        wheelView_sec_delay.setAdapter(new NumberAdapter(items_min));

        if (device.getState() != null && !"".equals(device.getState())){

            if("TIMER".equals(device.getEquipmentDesc())){
                timer_lay.setVisibility(View.VISIBLE);
                delay_lay.setVisibility(View.GONE);
                String initstate = device.getState();
                String initwek = initstate.substring(0,initstate.length()-4);
                byte ds = ByteUtil.hexStr2Bytes(initwek)[0];
                wekint = ds;
                String hour = initstate.substring(initstate.length()-4,initstate.length()-2);
                String min = initstate.substring(initstate.length()-2);
                wheelView_hour.setCurrentItem(Integer.valueOf(hour));
                wheelView_min.setCurrentItem(Integer.valueOf(min));

            }
            else if("DELAY".equals(device.getEquipmentDesc())){
                timer_lay.setVisibility(View.GONE);
                delay_lay.setVisibility(View.VISIBLE);
                String initstate = device.getState();
                String min = initstate.substring(initstate.length()-4,initstate.length()-2);
                String sec = initstate.substring(initstate.length()-2);
                wheelView_min_delay.setCurrentItem(Integer.valueOf(min));
                wheelView_sec_delay.setCurrentItem(Integer.valueOf(sec));
            }

        }else {
            if("TIMER".equals(device.getEquipmentDesc())){
                timer_lay.setVisibility(View.VISIBLE);
                delay_lay.setVisibility(View.GONE);
            }
            else if("DELAY".equals(device.getEquipmentDesc())){
                timer_lay.setVisibility(View.GONE);
                delay_lay.setVisibility(View.VISIBLE);
            }
            wheelView_hour.setCurrentItem(0);
            wheelView_min.setCurrentItem(0);
            wheelView_min_delay.setCurrentItem(0);
            wheelView_sec_delay.setCurrentItem(0);
        }

        myweekAdapter = new MyweekAdapter(this,wekint);
        grid_wek.setAdapter(myweekAdapter);
        grid_wek.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myweekAdapter.isSelected.put(position,!myweekAdapter.isSelected.get(position));
                myweekAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initViewGuider() {


        getTopBarView().setTopBarStatus(1, 2, device.getEquipmentName(), getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mcp.position==-1){
                    if("TIMER".equals(device.getEquipmentDesc())){
                        mcp.condition = "input";
                    }else {
                        mcp.condition = "output";
                    }

                    Intent i = new Intent(TimerNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);

                }else{
                    mcp.position=-1;
                    Intent i = new Intent(TimerNewActivity.this, NewGroup2Activity.class);
                    startActivity(i);
                }

                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("TIMER".equals(device.getEquipmentDesc()))
                {
                    setting_status = getTimerStringFromContent(myweekAdapter.isSelected,wheelView_hour.getCurrentItem(),wheelView_min.getCurrentItem());
                }
                else if("DELAY".equals(device.getEquipmentDesc())){
                    String ds = wheelView_min_delay.getCurrentItem()<10?"0":"";
                    String ds2 = wheelView_sec_delay.getCurrentItem()<10?"0":"";
                    setting_status = ds+String.valueOf(wheelView_min_delay.getCurrentItem())+ds2+ String.valueOf(wheelView_sec_delay.getCurrentItem());
                }

                if(TextUtils.isEmpty(setting_status)){
                    Toast.makeText(TimerNewActivity.this,getResources().getString(R.string.set_first), Toast.LENGTH_SHORT).show();
                    return;
                }else{

                    if(setting_status.startsWith("00")&&"TIMER".equals(device.getEquipmentDesc())){
                        Toast.makeText(TimerNewActivity.this,getResources().getString(R.string.set_weekday), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(setting_status.startsWith("0000")&&"DELAY".equals(device.getEquipmentDesc())){
                        Toast.makeText(TimerNewActivity.this,getResources().getString(R.string.set_delay), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    device.setState(setting_status);
                }

                if( mcp.position==-1){

                    if("TIMER".equals(device.getEquipmentDesc())){
                        mcp.input.add(device);
                    }else if("DELAY".equals(device.getEquipmentDesc())){
                        mcp.output.add(device);
                    }

                }else {
                    if("TIMER".equals(device.getEquipmentDesc())){
                        mcp.input.set(mcp.position,device);
                    }else if("DELAY".equals(device.getEquipmentDesc())){
                        mcp.output.set(mcp.position,device);
                    }
                    mcp.position=-1;
                }
                Intent i = new Intent(TimerNewActivity.this, NewGroup2Activity.class);
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

        for (int i = 0; i < 24; i ++) {

            String item = String.valueOf(i);

            if (item != null && item.length() == 1) {
                item = "0" + item;
            }

            items_hour.add(item);
        }

        for (int i = 0; i < 60; i ++) {
            String item = String.valueOf(i);

            if (item != null && item.length() == 1) {
                item = "0" + item;
            }

            items_min.add(item);
        }



    }


    /**
     * 时间选择器
     *
     */
    public void onDatePicker() {

        if("TIMER".equals(device.getEquipmentDesc())){
            final TimerPicker picker = new TimerPicker(this) ;
            picker.setRange();
            picker.setOnWheelListener(new WheelPicker.OnWheelListener<TimerBean>() {
                @Override
                public void onSubmit(TimerBean result) {
                    ArrayList<Integer> weeklist = result.getWeekdays();
                    int hour = result.getHour();
                    int min  = result.getMin();
                    if(weeklist.size()==0){
                        Toast.makeText(TimerNewActivity.this,"choose weekday please",Toast.LENGTH_LONG).show();
                    }
                    else{
                        setTimerShow(weeklist,hour,min);
                        String wek = ByteUtil.convertByte2HexString(getWeekbyte(weeklist));
                        String ds2 = hour<10?"0":"";
                        String ds3 = min<10?"0":"";
                        setting_status = wek+ds2+ String.valueOf(hour)+ds3+String.valueOf(min);
                        picker.dismiss();
                    }
                }
            });
            picker.showAtBottom() ;
        }else if("DELAY".equals(device.getEquipmentDesc())){
            final SecondDelayPicker picker2 = new SecondDelayPicker(this);
            picker2.setRange(0,59);
            picker2.setOnWheelListener(new WheelPicker.OnWheelListener<int[]>() {
                @Override
                public void onSubmit(int[] result) {
                    setDelayShow(result);
                    String ds = result[0]<10?"0":"";
                    String ds2 = result[1]<10?"0":"";
                    setting_status = ds+String.valueOf(result[0])+ds2+ String.valueOf(result[1]);
                    picker2.dismiss();
                }
            });
            picker2.showAtBottom();
        }


    }

    private void setTimerShow(@NotNull ArrayList<Integer> weeklist, int hour, int min){

        StringBuffer ds = new StringBuffer();
        ds.append("星期");
             for(int i=0;i<weeklist.size();i++){
                int day = weeklist.get(i);
                 switch (day){

                     case 0: ds.append("一");break;
                     case 1: ds.append("二");break;
                     case 2: ds.append("三");break;
                     case 3: ds.append("四");break;
                     case 4: ds.append("五");break;
                     case 5: ds.append("六");break;
                     case 6: ds.append("日");break;
                     default:break;
                 }
                 ds.append(",");
            }

        String ds2 = hour<10?"0":"";
        String ds3 = min<10?"0":"";

        ds.append(ds2+String.valueOf(hour)+":");
        ds.append(ds3+String.valueOf(min));

        //nowt.setText(ds.toString());
    }

    private void setDelayShow(int[] delay){

        String ds = delay[0]<10?"0":"";
        String ds2 = delay[1]<10?"0":"";
       // nowt.setText(ds +String.valueOf(delay[0])+"分"+ds2+String.valueOf(delay[1])+"秒");
    }

    private byte getWeekbyte (@NotNull ArrayList<Integer> weklist){
        byte f = 0x00;

        for(int ds:weklist){
           f =   (byte)((0x02 << ds) | f);
        }

        return f;
    }

    private String getTimerStringFrombyte (@NotNull byte devicestatus){
        StringBuffer ds = new StringBuffer();
        if(devicestatus!=0x00)
        ds.append("星期");
        for(int i=0;i<7;i++){
            byte d = (byte)((0x02 << i) & devicestatus);
            if(d!=0){
                switch (i){
                    case 0:ds.append("一,");break;
                    case 1:ds.append("二,");break;
                    case 2:ds.append("三,");break;
                    case 3:ds.append("四,");break;
                    case 4:ds.append("五,");break;
                    case 5:ds.append("六,");break;
                    case 6:ds.append("日,");break;
                }
            }
        }

        return ds.toString();
    }

    private String getTimerStringFromContent (@NotNull HashMap<Integer,Boolean> weeklist, int hour, int min){
        byte f = 0x00;
        for(int i=0;i<weeklist.size();i++){
            if(weeklist.get(i)){
                f =   (byte)((0x02 << i) | f);
            }
        }
        String wek = ByteUtil.convertByte2HexString(f);

        String ds2 = hour<10?"0":"";
        String ds3 = min<10?"0":"";
        String  setting_status = wek+ds2+ String.valueOf(hour)+ds3+String.valueOf(min);

        return setting_status;
    }


    private class MyweekAdapter extends BaseAdapter{

        private Context mcontext;
        private final String[] weekstr= getResources().getStringArray(R.array.week);
        private ViewHolder holder;
        public HashMap<Integer, Boolean> isSelected;

        public MyweekAdapter(Context mc,byte ds) {
            this.mcontext = mc;
            init(ds);
        }


        private void init(byte ds) {
            byte f;
            isSelected = new HashMap<Integer, Boolean>();

            for (int i = 0; i < weekstr.length; i++) {
                isSelected.put(i,false);
            }

            for(int i=0;i<weekstr.length;i++){
                f =   (byte)((0x02 << i) & ds);
                if(f!=0){
                    isSelected.put(i,true);
                }
            }

        }


        @Override
        public int getCount() {
            return weekstr.length;
        }

        @Override
        public String getItem(int position) {
            return weekstr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if( convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mcontext).inflate(R.layout.timer_week_item,null);
                holder.textView = (TextView) convertView.findViewById(R.id.wek_item);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(weekstr[position]);
            updataBackground(position,holder.textView);
            return convertView;
        }

        @SuppressLint("NewApi")
        protected void updataBackground(int position, TextView view){

            if(isSelected.get(position)){
                view.setTextColor(mcontext.getResources().getColor(R.color.item_is_sel));
            }else{
                view.setTextColor(mcontext.getResources().getColor(R.color.item_not));
            }

        };
        private class ViewHolder {
            TextView textView;
        }

    }

    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items) {
            super(items);
        }

    }

}
