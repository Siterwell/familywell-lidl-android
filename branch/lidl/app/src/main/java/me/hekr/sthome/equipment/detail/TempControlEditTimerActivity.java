package me.hekr.sthome.equipment.detail;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.history.DataFromSceneGroup;
import me.hekr.sthome.model.ResolveScene;
import me.hekr.sthome.model.addsmodel.AddTimerAcitivity;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendSceneData;
import me.hekr.sthome.tools.SendSceneGroupData;
import me.hekr.sthome.tools.UnitTools;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by tracyhenry on 2018/11/2.
 */

public class TempControlEditTimerActivity extends TopbarSuperActivity {
    private final String TAG = "TempControlEditTimer";
    private WheelView temp_wheel,xiaoshu_wheel,xiaoshu_wheel2,hour_wheel,min_wheel;
    private GridView grid_wek;
    private TempControlEditTimerActivity.MyweekAdapter myweekAdapter;

    private ArrayList<String> items_hour = new ArrayList<String>();
    private ArrayList<String> items_min = new ArrayList<String>();
    private ArrayList<String> templist = new ArrayList<>();
    private ArrayList<String> xiaoshulist = new ArrayList<>();
    private ArrayList<String> xiaoshulist2 = new ArrayList<>();
    private String mid;
    private String eqid;
    private byte wekint;
    private String init_code;
    private SceneBean sceneBean_init;
    private SceneDAO sceneDAO;
    private SysmodelDAO sysmodelDAO;
    private SendSceneData sendSceneData;
    private SendSceneGroupData sendSceneGroupData;
    private ECAlertDialog ecAlertDialog;
    @Override
    protected void onCreateInit() {
        initData();
        initView();
    }

    private void initView() {
        sendSceneData = new SendSceneData(this) {
            @Override
            protected void sendEquipmentDataFailed() {

            }

            @Override
            protected void sendEquipmentDataSuccess() {

            }
        };
        sendSceneGroupData = new SendSceneGroupData(this) {
            @Override
            protected void sendEquipmentDataFailed() {

            }

            @Override
            protected void sendEquipmentDataSuccess() {

            }
        };
        EventBus.getDefault().register(this);
        sceneDAO = new SceneDAO(this);
        eqid = getIntent().getStringExtra("eqid");
        mid = getIntent().getStringExtra("mid");
        grid_wek = (GridView) findViewById(R.id.weeklist);
        hour_wheel = (WheelView) findViewById(R.id.hour);
        min_wheel = (WheelView) findViewById(R.id.min);
        temp_wheel = (WheelView) findViewById(R.id.temp);
        xiaoshu_wheel = (WheelView) findViewById(R.id.xiaoshu);
        xiaoshu_wheel2 = (WheelView)findViewById(R.id.xiaoshu2);
        hour_wheel.setCyclic(true);
        min_wheel.setCyclic(true);
        temp_wheel.setCyclic(true);
        hour_wheel.setAdapter(new NumberAdapter(items_hour, 30));
        min_wheel.setAdapter(new NumberAdapter(items_min, 30));
        temp_wheel.setAdapter(new NumberAdapter(templist, 30));
        xiaoshu_wheel.setAdapter(new NumberAdapter(xiaoshulist,30));
        xiaoshu_wheel2.setAdapter(new NumberAdapter(xiaoshulist2,30));
        temp_wheel.addChangingListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if(newValue==(templist.size()-1)){
                    xiaoshu_wheel2.setVisibility(View.VISIBLE);
                    xiaoshu_wheel.setVisibility(View.GONE);
                }else {
                    xiaoshu_wheel2.setVisibility(View.GONE);
                    xiaoshu_wheel.setVisibility(View.VISIBLE);
                }
            }
        });
        getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.edit_timer), getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               back();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });
        if(TextUtils.isEmpty(mid)){
            wekint = 0x00;
        }else{
            try {
                sceneBean_init = sceneDAO.findScenceBymid(mid,ConnectionPojo.getInstance().deviceTid);
                ResolveScene resolveScene = new ResolveScene(this,sceneBean_init.getCode());
                if(resolveScene.isTarget()){
                    String timer = resolveScene.getSp().getTimer();
                    String hour = timer.substring(timer.length()-4,timer.length()-2);
                    String min = timer.substring(timer.length()-2);
                    String initwek = timer.substring(0,timer.length()-4);
                    wekint = ByteUtil.hexStr2Bytes(initwek)[0];
                    hour_wheel.setCurrentItem(Integer.parseInt(hour));
                    min_wheel.setCurrentItem(Integer.parseInt(min));
                    init_code = sceneBean_init.getCode();
                    String status1 = resolveScene.getOutput().get(0).getState().substring(0,2);
                    byte ds = (byte)Integer.parseInt(status1,16);
                    byte xiaoshu = (byte)((0x20) & ds);
                    int sta =  ((0x1F) & ds);
                    if(sta>=5&&sta<=30){
                        if(sta==30){
                            xiaoshu_wheel2.setVisibility(View.VISIBLE);
                            xiaoshu_wheel.setVisibility(View.GONE);
                        }else {
                            xiaoshu_wheel2.setVisibility(View.GONE);
                            xiaoshu_wheel.setVisibility(View.VISIBLE);
                            if(xiaoshu==0){
                                xiaoshu_wheel.setCurrentItem(0);
                            }else {
                                xiaoshu_wheel.setCurrentItem(1);
                            }
                        }
                        temp_wheel.setCurrentItem(sta-5);
                    }else {
                        temp_wheel.setCurrentItem(0);
                        xiaoshu_wheel.setVisibility(View.VISIBLE);
                        xiaoshu_wheel2.setVisibility(View.GONE);
                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        myweekAdapter = new TempControlEditTimerActivity.MyweekAdapter(this,wekint);
        grid_wek.setAdapter(myweekAdapter);
        grid_wek.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myweekAdapter.isSelected.put(position,!myweekAdapter.isSelected.get(position));
                myweekAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
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

        for (int i = 5; i < 31; i ++) {
            String item = String.valueOf(i);


            templist.add(item);
        }

        xiaoshulist.add("0");
        xiaoshulist.add("5");
        xiaoshulist2.add("0");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_gs361timer;
    }


    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent()== SendCommand.MODIFY_SCENE){
            SendCommand.clearCommnad();
            sceneDAO = new SceneDAO(this);
            String code = getcode().toUpperCase();
            sceneDAO.updateCodeByMid(code,mid,ConnectionPojo.getInstance().deviceTid);

            sysmodelDAO = new SysmodelDAO(this);
            List<SysModelBean> ds = sysmodelDAO.findAllSys(ConnectionPojo.getInstance().deviceTid);
            for (SysModelBean cb:ds){
               String sid = cb.getSid();
               SceneBean sceneBean = new SceneBean();
               sceneBean.setName(sceneBean_init.getName());
               sceneBean.setMid(mid);
               sceneBean.setSid(sid);
               sceneBean.setDeviceid(ConnectionPojo.getInstance().deviceTid);
               sceneBean.setCode(code);
               sceneDAO.addinit(sceneBean);
            }

            DataFromSceneGroup dataFromSceneGroup = new DataFromSceneGroup(this);
            dataFromSceneGroup.doSendSynCode();
            finish();
        }else if(event.getEvent()== SendCommand.INCREACE_SCENE){
            SendCommand.clearCommnad();
            String code = getcode().toUpperCase();
            String mids = String.valueOf(getmid());
            sceneDAO = new SceneDAO(this);
            SceneBean sceneBean1 = new SceneBean();
            sceneBean1.setName("");
            sceneBean1.setMid(mids);
            sceneBean1.setSid("-1");
            sceneBean1.setDeviceid(ConnectionPojo.getInstance().deviceTid);
            sceneBean1.setCode(code);
            sceneDAO.addinit(sceneBean1);

            sysmodelDAO = new SysmodelDAO(this);
            List<SysModelBean> ds = sysmodelDAO.findAllSys(ConnectionPojo.getInstance().deviceTid);
            for (SysModelBean cb:ds){
                String sid = cb.getSid();
                SceneBean sceneBean = new SceneBean();
                sceneBean.setName("");
                sceneBean.setMid(mids);
                sceneBean.setSid(sid);
                sceneBean.setDeviceid(ConnectionPojo.getInstance().deviceTid);
                sceneBean.setCode(code);
                sceneDAO.addinit(sceneBean);
            }

            DataFromSceneGroup dataFromSceneGroup = new DataFromSceneGroup(this);
            dataFromSceneGroup.doSendSynCode();
            finish();
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            back();

            return true;
        }
        return  super.onKeyDown(keyCode, event);
    }

    private void back(){
       if(TextUtils.isEmpty(mid)){
           String  settime4 = getTimerStringFromContent(myweekAdapter.isSelected,hour_wheel.getCurrentItem(),min_wheel.getCurrentItem());
           if(!settime4.startsWith("00")){
               ecAlertDialog = ECAlertDialog.buildAlert(this, R.string.save_or_not, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       finish();
                   }
               }, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       confirm();
                   }
               });
               ecAlertDialog.show();
           }else {
               finish();
           }
       }else{
           if(!TextUtils.isEmpty(init_code) && !init_code.toUpperCase().equals(getcode().toUpperCase())){
               ecAlertDialog = ECAlertDialog.buildAlert(this, R.string.save_or_not, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       finish();
                   }
               }, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       confirm();
                   }
               });
               ecAlertDialog.show();
           }else {
               finish();
           }
       }

    }


    private void confirm(){
        String  setting_status = getTimerStringFromContent(myweekAdapter.isSelected,hour_wheel.getCurrentItem(),min_wheel.getCurrentItem());
        if(TextUtils.isEmpty(setting_status)){
            Toast.makeText(TempControlEditTimerActivity.this,getResources().getString(R.string.set_first), Toast.LENGTH_SHORT).show();
            return;
        }else{

            if(setting_status.startsWith("00")){
                Toast.makeText(TempControlEditTimerActivity.this,getResources().getString(R.string.set_weekday), Toast.LENGTH_SHORT).show();
                return;
            }

            sendDecode(getcode().toUpperCase());

        }
    }

    /**
     * create a new sence modle
     */
    private String  getcode() {
        int length = 2;//情景长度
        length += 1;//系统情景编号

        String cm3 ="00";
        length +=1;//条件选择
        String  settime4 = getTimerStringFromContent(myweekAdapter.isSelected,hour_wheel.getCurrentItem(),min_wheel.getCurrentItem());
        length +=3;//定时

        String cl = "FF";

        String click5 = cl;
        length += 1;//点击执行

        String ph = "FF";

        String inf6 =ph;
        length +=1;//通知手机

        int in = 0;
        length +=1;//输入设备个数

        int out = 1;
        length +=1;//输出设备个数


        String inCode="";


        length += 8;
        String ei1 ="0";
        if(Integer.toHexString(Integer.parseInt(eqid)).length() < 4){
            for (int j = 0; j<4- Integer.toHexString(Integer.parseInt(eqid)).length()-1; j++ ){
                ei1 += 0;
            }
            ei1 += Integer.toHexString(Integer.parseInt(eqid));
        }else{
            ei1 = Integer.toHexString(Integer.parseInt(eqid));
        }

        String status = "";
        if(temp_wheel.getCurrentItem()<(templist.size()-1)){
            byte ds = (byte)(temp_wheel.getCurrentItem()+5);

            if(xiaoshu_wheel.getCurrentItem()==1){
                ds |=0x20;
            }

            String str1 = ByteUtil.convertByte2HexString(ds);
            status = (str1+"800000");
        }else{
            status = "1E800000";
        }

        String outCode = ( "0000"+ei1 + status);

        String oooo ="0";
        if(Integer.toHexString(length+32).length() < 4){
            for (int i = 0; i<4-Integer.toHexString(length+32).length()-1; i++ ){
                oooo += 0;
            }
            oooo += Integer.toHexString(length+32);
        }else{
            oooo = Integer.toHexString(length+32);
        }

        String ooo = "";
        String midbuff =(TextUtils.isEmpty(mid)?String.valueOf(getmid()):mid);
        String amid1 = Integer.toHexString(Integer.parseInt(midbuff));
        if(amid1.length()<2){
            for(int i =0; i<2 - amid1.length();i++){
                ooo += 0;
            }
            ooo +=amid1;
        }else{
            ooo = amid1;
        }

        String oo = "0";
        if(Integer.toHexString(in).length()<2){
            oo = oo + Integer.toHexString(in);
        }else{
            oo = Integer.toHexString(in);
        }

        String o ="0";
        if(Integer.toHexString(out).length()<2){
            o += Integer.toHexString(out);
        }

        String nameinit = (sceneBean_init==null?"":sceneBean_init.getName());

        String name = CoderUtils.getAscii(nameinit);


        String deCode =  oooo + ooo + name + cm3 + UnitTools.timeDecode(settime4,6) + click5 + inf6 + oo + o +inCode +outCode;
        Log.i(TAG,"edit code :"+deCode);
        return(deCode);
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

    private int getmid(){
        sceneDAO = new SceneDAO(this);
        List<String> list = sceneDAO.findAllMid(ConnectionPojo.getInstance().deviceTid);

        //mid排序，非常重要，varchar数据库本身排序达不到数字排序的效果
        Collections.sort(list, new Comparator<String>() {
            public int compare(String o1, String o2) {

                if(Integer.parseInt(o1)<Integer.parseInt(o2)){
                    return  -1;
                }else if(Integer.parseInt(o1)>Integer.parseInt(o2)){
                    return 1;
                }else{
                    return 0;
                }

            }
        });

        if(list.size()==0){
            return 1;
        }else if(list.size()==1){
            if(Integer.parseInt(list.get(0))==1){
                return 2;
            }else{
                return 1;
            }
        }else{
            int m = 0;
            for(int i=0;i<list.size()-1;i++){


                if(i==0){

                    int d = Integer.parseInt(list.get(i));
                    if(d!=1){
                        m = 1;
                        break;
                    }
                    else {
                        if( (Integer.parseInt(list.get(i))+1) < Integer.parseInt(list.get(i+1))){
                            m = Integer.parseInt(list.get(i))+1;
                            break;
                        }else{
                            m = Integer.parseInt(list.get(i))+2;
                        }
                    }


                }else{
                    if( (Integer.parseInt(list.get(i))+1) < Integer.parseInt(list.get(i+1))){
                        m = Integer.parseInt(list.get(i))+1;
                        break;
                    }else{
                        m = Integer.parseInt(list.get(i))+2;
                    }

                }


            }
            return m;
        }

    }

    private void sendDecode(String deCode) {

        if(TextUtils.isEmpty(mid)){
            SendCommand.Command = SendCommand.INCREACE_SCENE;
            sendSceneData.modifyScene(deCode);

        }else {
            SendCommand.Command = SendCommand.MODIFY_SCENE;
            sendSceneData.increaceScene(deCode);
        }
    }
}
