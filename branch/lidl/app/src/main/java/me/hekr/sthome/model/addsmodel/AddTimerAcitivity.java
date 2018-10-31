package me.hekr.sthome.model.addsmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.ResolveTimer;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modelbean.TimerGatewayBean;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.model.modeldb.TimerDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendOtherData;
import me.hekr.sthome.tools.UnitTools;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by gc-0001 on 2017/5/15.
 */

public class AddTimerAcitivity extends TopbarSuperActivity {
    private final String TAG = "AddTimerAcitivity";
    private WheelView mode_wheel,hour_wheel,min_wheel;
    private GridView grid_wek;
    private MyweekAdapter myweekAdapter;

    private ArrayList<String> items_hour = new ArrayList<String>();
    private ArrayList<String> items_min = new ArrayList<String>();
    private ArrayList<String> sysnamelist = new ArrayList<>();
    private List<SysModelBean> syslist;
    private SysmodelDAO sysmodelDAO;
    private TimerDAO timerDAO;
    private String timerid;
    private TimerGatewayBean timerGatewayBean;
    private byte wekint=0x00;
    private SendOtherData sendOtherData;
    private int confirmNum = 0;
    private String messagecode = "";
    private String repeatInfo = "";
    private String init_code = "";

    @Override
    protected void onCreateInit() {
        initData();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_timer;
    }

    private void initView(){
        EventBus.getDefault().register(this);
        sendOtherData = new SendOtherData(this);
        grid_wek = (GridView)findViewById(R.id.weeklist);
        hour_wheel = (WheelView)findViewById(R.id.hour);
        min_wheel = (WheelView)findViewById(R.id.min);
        mode_wheel = (WheelView)findViewById(R.id.mode);
        hour_wheel.setCyclic(true);
        min_wheel.setCyclic(true);
        mode_wheel.setCyclic(true);
        hour_wheel.setAdapter(new NumberAdapter(items_hour,30));
        min_wheel.setAdapter(new NumberAdapter(items_min,30));
        mode_wheel.setAdapter(new NumberAdapter(sysnamelist,30));
        getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.edit_timer), getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(timerid)){
                    String ds = getTimerStringFromContent(myweekAdapter.isSelected);
                    if("00".equals(ds)){
                        finish();
                    }else {
                        ECAlertDialog elc = ECAlertDialog.buildAlert(AddTimerAcitivity.this, getResources().getString(R.string.save_or_not), getResources().getString(R.string.no_save), getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confirmNum ++;
                                verfication();
                                if (confirmNum==1){
                                    confirmToSys();
                                }
                                else if(confirmNum==-1){
                                    Toast.makeText(AddTimerAcitivity.this,getResources().getString(R.string.set_weekday),Toast.LENGTH_LONG).show();
                                    confirmNum = 0;
                                }else if(confirmNum == -2){
                                    Toast.makeText(AddTimerAcitivity.this,repeatInfo,Toast.LENGTH_LONG).show();
                                    confirmNum = 0;
                                }
                                else{
                                    SendCommand.Command = SendCommand.MODEL_SWITCH_TIMER;
                                    sendOtherData.addTimerModel(messagecode);
                                }
                            }
                        });
                        elc.show();
                    }

                }else{
                   if(init_code.toUpperCase().equals(getcode().toUpperCase())){
                       finish();
                   }else{
                       ECAlertDialog elc = ECAlertDialog.buildAlert(AddTimerAcitivity.this, getResources().getString(R.string.save_or_not), getResources().getString(R.string.no_save), getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               finish();
                           }
                       }, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               confirmNum ++;
                               verfication();
                               if (confirmNum==1){
                                   confirmToSys();
                               }
                               else if(confirmNum==-1){
                                   Toast.makeText(AddTimerAcitivity.this,getResources().getString(R.string.set_weekday),Toast.LENGTH_LONG).show();
                                   confirmNum = 0;
                               }else if(confirmNum == -2){
                                   Toast.makeText(AddTimerAcitivity.this,repeatInfo,Toast.LENGTH_LONG).show();
                                   confirmNum = 0;
                               }
                               else{
                                   SendCommand.Command = SendCommand.MODEL_SWITCH_TIMER;
                                   sendOtherData.addTimerModel(messagecode);
                               }
                           }
                       });
                       elc.show();
                   }

                }



            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNum ++;
                verfication();
                if (confirmNum==1){
                    confirmToSys();
                }
                else if(confirmNum==-1){
                    Toast.makeText(AddTimerAcitivity.this,getResources().getString(R.string.set_weekday),Toast.LENGTH_LONG).show();
                    confirmNum = 0;
                }else if(confirmNum == -2){
                    Toast.makeText(AddTimerAcitivity.this,repeatInfo,Toast.LENGTH_LONG).show();
                    confirmNum = 0;
                }
                else{
                    SendCommand.Command = SendCommand.MODEL_SWITCH_TIMER;
                    sendOtherData.addTimerModel(messagecode);
                }
            }
        });

        if(TextUtils.isEmpty(timerGatewayBean.getWeek())){
            wekint = 0x00;
            myweekAdapter = new MyweekAdapter(this,wekint);
        }else{
            wekint = ByteUtil.hexStr2Bytes(timerGatewayBean.getWeek())[0];
            myweekAdapter = new MyweekAdapter(this,wekint);
        }

        grid_wek.setAdapter(myweekAdapter);
        grid_wek.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myweekAdapter.isSelected.put(position,!myweekAdapter.isSelected.get(position));
                myweekAdapter.notifyDataSetChanged();
            }
        });
        if(TextUtils.isEmpty(timerGatewayBean.getHour())){
            hour_wheel.setCurrentItem(0);
        }else{
            hour_wheel.setCurrentItem(Integer.valueOf(timerGatewayBean.getHour()));
        }

        if(TextUtils.isEmpty(timerGatewayBean.getMin())){
            min_wheel.setCurrentItem(0);
        }else{
            min_wheel.setCurrentItem(Integer.valueOf(timerGatewayBean.getMin()));
        }

        if(TextUtils.isEmpty(timerGatewayBean.getModeid())){
            mode_wheel.setCurrentItem(0);
        }else{
            for(int i=0;i<syslist.size();i++){
                if(syslist.get(i).getSid().equals(timerGatewayBean.getModeid())){
                    mode_wheel.setCurrentItem(i);
                    break;
                }
            }

        }
    }


    private void initData() {
        sysmodelDAO = new SysmodelDAO(this);
        timerDAO    = new TimerDAO(this);
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
        sysnamelist.clear();
        syslist = sysmodelDAO.findAllSys(ConnectionPojo.getInstance().deviceTid);
        for(SysModelBean sysModelBean:syslist){
            if("0".equals(sysModelBean.getSid())){
                sysnamelist.add(getResources().getString(R.string.home_mode));
            }else if("1".equals(sysModelBean.getSid())){
                sysnamelist.add(getResources().getString(R.string.out_mode));
            }
            else if("2".equals(sysModelBean.getSid())){
                sysnamelist.add(getResources().getString(R.string.sleep_mode));
            }else{
                sysnamelist.add(sysModelBean.getModleName());
            }

        }
        timerid = getIntent().getStringExtra("timerid");

        if(TextUtils.isEmpty(timerid)){
            timerGatewayBean = new TimerGatewayBean();
        }else{
            timerGatewayBean = timerDAO.findByTid(timerid, ConnectionPojo.getInstance().deviceTid);
            init_code = ResolveTimer.getCode(timerGatewayBean);
        }
    }

    private void verfication(){
        String ds = getTimerStringFromContent(myweekAdapter.isSelected);
        if("00".equals(ds)){

            confirmNum = -1;
            return;
        }


        String hour = hour_wheel.getCurrentItem()<10?("0"+hour_wheel.getCurrentItem()):String.valueOf(hour_wheel.getCurrentItem());
        String min  = min_wheel.getCurrentItem()<10?("0"+min_wheel.getCurrentItem()):String.valueOf(min_wheel.getCurrentItem());

        List<String> weekList = timerDAO.findAllTimerByTime(hour,min, ConnectionPojo.getInstance().deviceTid);

        HashMap<Integer,Boolean> result = CheckRepeat(weekList);
        Log.i(TAG,"result:"+result.toString());
        boolean flagrepeat = false;
        HashMap<Integer,Boolean> result2 = new HashMap<Integer,Boolean>();
        for(int i=0;i<7;i++){
            if(myweekAdapter.isSelected.get(i) && result.get(i)&&TextUtils.isEmpty(timerid)){
                flagrepeat = true;
                result2.put(i,true);
            }else {
                result2.put(i,false);
            }
        }
        if(flagrepeat){
            UnitTools unitTools = new UnitTools(this);
            repeatInfo = ResolveTimer.getWeekinfoHash(result2,this,unitTools);
            confirmNum = -2;

            return;
        }



        if(TextUtils.isEmpty(timerid)){
            timerGatewayBean.setTimerid(String.valueOf(gettid()));
            timerGatewayBean.setEnable(1);
        }

        timerGatewayBean.setModeid(syslist.get(mode_wheel.getCurrentItem()).getSid());
        timerGatewayBean.setWeek(ds);
        timerGatewayBean.setHour(hour);
        timerGatewayBean.setMin(min);
        timerGatewayBean.setDeviceid(ConnectionPojo.getInstance().deviceTid);

    }


    private String getcode(){
        String ds = getTimerStringFromContent(myweekAdapter.isSelected);

        String hour = hour_wheel.getCurrentItem()<10?("0"+hour_wheel.getCurrentItem()):String.valueOf(hour_wheel.getCurrentItem());
        String min  = min_wheel.getCurrentItem()<10?("0"+min_wheel.getCurrentItem()):String.valueOf(min_wheel.getCurrentItem());


        timerGatewayBean.setModeid(syslist.get(mode_wheel.getCurrentItem()).getSid());
        timerGatewayBean.setWeek(ds);
        timerGatewayBean.setHour(hour);
        timerGatewayBean.setMin(min);
        return ResolveTimer.getCode(timerGatewayBean);
    }

    private void confirmToSys() {
        messagecode = ResolveTimer.getCode(timerGatewayBean);
        Log.i(TAG,"code++++++++++:"+messagecode);
        timerGatewayBean.setCode(messagecode);
        SendCommand.Command = SendCommand.MODEL_SWITCH_TIMER;
        sendOtherData.addTimerModel(messagecode);
        confirmNum = 0;
    }



    private String getTimerStringFromContent (@NotNull HashMap<Integer,Boolean> weeklist){
        byte f = 0x00;
        for(int i=0;i<weeklist.size();i++){
            if(weeklist.get(i)){
                f =   (byte)((0x02 << i) | f);
            }
        }
        String wek = ByteUtil.convertByte2HexString(f);

        return wek;
    }


    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items,int lengh) {
            super(items,lengh);
        }

    }

    private class MyweekAdapter extends BaseAdapter {

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

    private int gettid(){

        List<String> list = timerDAO.findAllTimerTid(ConnectionPojo.getInstance().deviceTid);
        if(list.size()==0){
            return 0;
        }else{

            if(list.size()==1){
                if("0".equals(list.get(0))){
                    return 1;
                }else {
                    return 0;
                }

            }else{
                int m = 0;
                for(int i=0;i<list.size()-1;i++){

                    if(i==0){
                       int d = Integer.parseInt(list.get(i));
                        if(d!=0){
                            m = 0;
                            break;
                        }
                        else {
                            if( (Integer.parseInt(list.get(i))+1) < Integer.parseInt(list.get(i+1))){
                                m = Integer.parseInt(list.get(i))+1;
                                break;
                            }else{
                                m = i+2;
                            }
                        }
                    }else{
                        if( (Integer.parseInt(list.get(i))+1) < Integer.parseInt(list.get(i+1))){
                            m = Integer.parseInt(list.get(i))+1;
                            break;
                        }else{
                            m = i+2;
                        }
                    }


                }
                return m;
            }


        }


    }


    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent() == SendCommand.MODEL_SWITCH_TIMER){
            confirmNum = 0;
            timerDAO.insertTimer(timerGatewayBean);
            finish();
            SendCommand.clearCommnad();
        }
    }


    private HashMap<Integer, Boolean> CheckRepeat(List<String> weeklist){
        HashMap<Integer, Boolean>  isSelected = new HashMap<Integer, Boolean>();
        for (int j = 0; j < 7; j++) {
            isSelected.put(j,false);
        }

        for(int i=0;i<weeklist.size();i++){

            byte weekbtye= ByteUtil.hexStr2Bytes(weeklist.get(i))[0];

            byte f;
            for(int j=0;j<7;j++){
                f =   (byte)((0x02 << j) & weekbtye);
                if(f!=0){
                    isSelected.put(j,true);
                }
            }

        }

        return isSelected;

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            if(TextUtils.isEmpty(timerid)){
                String ds = getTimerStringFromContent(myweekAdapter.isSelected);
                if("00".equals(ds)){
                    finish();
                }else {
                    ECAlertDialog elc = ECAlertDialog.buildAlert(AddTimerAcitivity.this, getResources().getString(R.string.save_or_not), getResources().getString(R.string.no_save), getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmNum ++;
                            verfication();
                            if (confirmNum==1){
                                confirmToSys();
                            }
                            else if(confirmNum==-1){
                                Toast.makeText(AddTimerAcitivity.this,getResources().getString(R.string.set_weekday),Toast.LENGTH_LONG).show();
                                confirmNum = 0;
                            }else if(confirmNum == -2){
                                Toast.makeText(AddTimerAcitivity.this,repeatInfo,Toast.LENGTH_LONG).show();
                                confirmNum = 0;
                            }
                            else{
                                SendCommand.Command = SendCommand.MODEL_SWITCH_TIMER;
                                sendOtherData.addTimerModel(messagecode);
                            }
                        }
                    });
                    elc.show();
                }

            }else{
                if(init_code.toUpperCase().equals(getcode().toUpperCase())){
                    finish();
                }else{
                    ECAlertDialog elc = ECAlertDialog.buildAlert(AddTimerAcitivity.this, getResources().getString(R.string.save_or_not), getResources().getString(R.string.no_save), getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmNum ++;
                            verfication();
                            if (confirmNum==1){
                                confirmToSys();
                            }
                            else if(confirmNum==-1){
                                Toast.makeText(AddTimerAcitivity.this,getResources().getString(R.string.set_weekday),Toast.LENGTH_LONG).show();
                                confirmNum = 0;
                            }else if(confirmNum == -2){
                                Toast.makeText(AddTimerAcitivity.this,repeatInfo,Toast.LENGTH_LONG).show();
                                confirmNum = 0;
                            }
                            else{
                                SendCommand.Command = SendCommand.MODEL_SWITCH_TIMER;
                                sendOtherData.addTimerModel(messagecode);
                            }
                        }
                    });
                    elc.show();
                }

            }

            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

}
