package me.hekr.sthome.main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.litesuits.android.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import me.hekr.sdk.Constants;
import me.hekr.sthome.CarouselView.CycleViewPager;
import me.hekr.sthome.CarouselView.CycleViewWeatherPager;
import me.hekr.sthome.CarouselView.ViewFactory;
import me.hekr.sthome.DeviceListActivity;
import me.hekr.sthome.MyApplication;
import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.DeviceActivitys;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.MenuDialog;
import me.hekr.sthome.commonBaseView.MultiDirectionSlidingDrawer;
import me.hekr.sthome.commonBaseView.PullListView;
import me.hekr.sthome.equipment.ConfigActivity;
import me.hekr.sthome.event.LogoutEvent;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.event.ThcheckEvent;
import me.hekr.sthome.history.HistoryAdapter;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.model.modelbean.ClientUser;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.MonitorBean;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modelbean.WeatherInfoBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.NoticeDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.push.NoticeBean;
import me.hekr.sthome.push.NoticeResolve;
import me.hekr.sthome.tools.Config;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.ECPreferenceSettings;
import me.hekr.sthome.tools.ECPreferences;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendSceneGroupData;
import me.hekr.sthome.tools.SystemTintManager;
import me.hekr.sthome.tools.UnitTools;
import me.hekr.sthome.xmipc.ActivityGuideDeviceAdd;


/**
 * Created by xjj on 2016/12/6.
 */

@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment implements View.OnClickListener,MultiDirectionSlidingDrawer.OnDrawerOpenListener,MultiDirectionSlidingDrawer.OnDrawerCloseListener,CycleViewPager.ImageCycleViewListener,CycleViewWeatherPager.ImageCycleViewListener,MenuDialog.Dissmins,PullListView.IXListViewListener {
    private PullListView listView;
    private View view = null;
    private static final String TAG = "MyHomeFragment";
    private RadioButton title_view;
    private ImageButton setting_btn;
    private LinearLayout total_linearlayout;
    private LinearLayout topp;
    private LinearLayout alarm_content;
    private LinearLayout casaul;
    private LinearLayout nowmode;
    private ImageView btn_cancel;
    private Button btn_clear;
    private MultiDirectionSlidingDrawer drawer;
    private SystemTintManager tintManager;
    private List<FrameLayout> views = new ArrayList<FrameLayout>();
    private List<MonitorBean> infos = new ArrayList<MonitorBean>();

    private List<LinearLayout> views_weather = new ArrayList<LinearLayout>();
    private List<WeatherInfoBean> infos_weather = new ArrayList<WeatherInfoBean>();
    private CycleViewPager cycleViewPager;
    private CycleViewWeatherPager cycleViewWeatherPager;
    private SendSceneGroupData ssgd;
    private int nowmodeindex = -1;
    private MenuDialog menuDialog;
    private SysmodelDAO sysmodelDAO;
    private DeviceDAO deviceDAO;
    private EquipDAO equipDAO;
    private NoticeDAO noticeDAO;
    private List<NoticeBean> reslt;
    private HistoryAdapter historyAdapter;
    private View empty;
    private int page = 0;
    private UnitTools unitTools;
    private FusedLocationProviderClient mFusedLocationClient;


    private String gps_place="";
    private String gpsweather_ico="";
    private String weather_txt="";
    private String gpshum="";
    private String temp="";

    public HomeFragment()
    {
        super();

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, null);
            EventBus.getDefault().register(this);
            initGuider();
            initGps();
        }
        //缓存的rootView需要判断是否已经被加载过parent,如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
        ViewGroup viewparent = (ViewGroup) view.getParent();
        if (viewparent != null) {
            viewparent.removeView(view);
        }


        return view;
    }

    private void initGuider() {
        unitTools = new UnitTools(this.getActivity());
        sysmodelDAO = new SysmodelDAO(this.getActivity());
        deviceDAO = new DeviceDAO(this.getActivity());
        noticeDAO = new NoticeDAO(this.getActivity());
        equipDAO  = new EquipDAO(this.getActivity());
        ssgd = new SendSceneGroupData(this.getActivity()) {
            @Override
            protected void sendEquipmentDataFailed() {
                Log.i(TAG,"operation failed");
            }

            @Override
            protected void sendEquipmentDataSuccess() {
                Log.i(TAG,"operation success");
            }
        };
        cycleViewPager = new CycleViewPager(getActivity());
        nowmode      =  (LinearLayout)view.findViewById(R.id.nowmode);
        nowmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysmodelDAO dao = new SysmodelDAO(HomeFragment.this.getActivity());
                final List<SysModelBean> listnow = dao.findAllSys(ConnectionPojo.getInstance().deviceTid);
               MenuDialog.Builder builder = new  MenuDialog.Builder(HomeFragment.this.getActivity());
                builder.setSysModellist(listnow);
                builder.setDissmins(HomeFragment.this);
                menuDialog =  builder.create();
                menuDialog.show();
            }
        });
        tintManager = new SystemTintManager(this.getActivity());// 创建状态栏的管理实例
        drawer =(MultiDirectionSlidingDrawer)view.findViewById(R.id.drawer1);
        listView = (PullListView) drawer.findViewById(R.id.logs);
        empty = view.findViewById(R.id.empty);
        listView.setEmptyView(empty);
        empty.setBackgroundColor(getResources().getColor(R.color.white));
        reslt = new ArrayList<>();
        historyAdapter = new HistoryAdapter(HomeFragment.this.getActivity(),reslt);
        listView.setAdapter(historyAdapter);
        listView.setPullLoadEnable(true);
        listView.setXListViewListener(this);
        drawer.setOnDrawerOpenListener(this);
        drawer.setOnDrawerCloseListener(this);
        casaul = (LinearLayout)view.findViewById(R.id.casaul);
        topp = (LinearLayout)view.findViewById(R.id.topp);
        btn_cancel = (ImageView)view.findViewById(R.id.cancel);
        btn_clear  = (Button)view.findViewById(R.id.clear);
        btn_cancel.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        title_view = (RadioButton) view.findViewById(R.id.title_home);
        title_view.setOnClickListener(this);

        setting_btn = (ImageButton)view.findViewById(R.id.detailEdit_img);
        setting_btn.setOnClickListener(this);

        //沉浸式设置支持API19
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            total_linearlayout = (LinearLayout) view.findViewById(R.id.totl);
            int top = UnitTools.getStatusBarHeight(getActivity());
            total_linearlayout.setPadding(0,top,0,0);
            Log.i(TAG,"top="+top);
            alarm_content = (LinearLayout)view.findViewById(R.id.content);
            alarm_content.setPadding(0,top,0,0);
        }

        refreshSysmode();
        refreshTitle();
        reslt =  noticeDAO.findAllNotice();
        historyAdapter.refreshList(reslt);
        initialize();
        initializeWeather();

    }

    public int getNowmodeindex() {
        return nowmodeindex;
    }

    public void setNowmodeindex(int nowmodeindex) {
        this.nowmodeindex = nowmodeindex;
    }

    public void refreshSysmode(){
        SysModelBean bean = sysmodelDAO.findIdByChoice(ConnectionPojo.getInstance().deviceTid);

        ImageView mode = (ImageView)view.findViewById(R.id.mode);
        TextView mainHome = (TextView) view.findViewById(R.id.zouma);
        mainHome.setSelected(true);
        try {
            if("0".equals(bean.getSid())){
                mode.setImageResource(R.mipmap.home_mode2);
                mainHome.setText(getResources().getString(R.string.home_mode));
            }else if("1".equals(bean.getSid())){
                mode.setImageResource(R.mipmap.out_mode2);
                mainHome.setText(getResources().getString(R.string.out_mode));
            }else if("2".equals(bean.getSid())){
                mode.setImageResource(R.mipmap.sleep_mode2);
                mainHome.setText(getResources().getString(R.string.sleep_mode));
            }else{
                mode.setImageResource(R.mipmap.home_mode2);
                mainHome.setText(bean.getModleName());
            }
        }catch (Exception e){
             Log.i(TAG,"no choosed mode");
        }
    }


    public void closeTheAlarmList(){
        drawer.animateClose();
    }

    public void openTheAlarmList(){
        drawer.open();
    }

    public boolean isAlarmListOpened(){

        return drawer.isOpened();

    }

    @Override
    public void onResume(){
        super.onResume();


        if(!TextUtils.isEmpty(HekrUserAction.getInstance(this.getActivity()).getJWT_TOKEN())){
            HekrUserAction.getInstance(this.getActivity()).getProfile(new HekrUser.GetProfileListener() {
                @Override
                public void getProfileSuccess(Object object) {
                    try {
                        Log.i(TAG,"getSuccess"+object.toString());
                        JSONObject d = JSON.parseObject(object.toString());
                        ClientUser user = CCPAppManager.getClientUser();
                        user.setMonitor(d.getJSONObject("extraProperties").getString("monitor"));
                        CCPAppManager.setClientUser(user);
                        initialize();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void getProfileFail(int errorCode) {
                    Log.i(TAG,"getFail:"+errorCode);
                    if(errorCode==1){
                        LogoutEvent tokenTimeoutEvent = new LogoutEvent();
                        EventBus.getDefault().post(tokenTimeoutEvent);
                    }
                }
            });
        }

    }



    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.cancel:
                  drawer.animateClose();
                  break;
              case R.id.title_home:
                  Intent intent = new Intent(HomeFragment.this.getActivity(),DeviceListActivity.class);
                  startActivity(intent);
                  break;
              case R.id.clear:
                  ECAlertDialog ecAlertDialog = ECAlertDialog.buildAlert(this.getActivity(), getResources().getString(R.string.alert_delete_all_history_log), new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          clearNotices();
                      }
                  });
                  ecAlertDialog.show();

                  break;
              case R.id.detailEdit_img:
                  startActivity(new Intent(HomeFragment.this.getActivity(), ConfigActivity.class));
                  break;
              default:
                  break;
          }
    }


    @Override
    public void onDrawerOpened() {
        tintManager.setStatusBarDarkMode2(true,this.getActivity());
        page = 0;
        historyDataShow();

    }

    @Override
    public void onDrawerClosed() {
        tintManager.setStatusBarDarkMode2(false,this.getActivity());
    }


    private void historyDataShow() {

        final NoticeResolve noticeResolve = new NoticeResolve(this.getActivity());
        noticeResolve.getAlarmInforFromHrke(page,new HekrUserAction.GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {

                Log.i(TAG, "历史告警=============="+object.toString());
                JSONObject jsonObject = JSONObject.parseObject(object.toString());

                int pageload  = jsonObject.getIntValue("number");

                if(pageload == 0){
                    noticeDAO.deleteAllNotice();
                    reslt.clear();
                }

                boolean last = jsonObject.getBoolean("last");


                List<NoticeBean> res2 =  noticeResolve.getJsonArrayResolve(object.toString());

                if(!last&&res2.size()==20){
                    //防止以前的告警数据造成解析失效
                    page = pageload + 1;
                }

                reslt.addAll(res2);
                noticeDAO.insertNoticeList(res2);
                historyAdapter.refreshList(reslt);
                listView.stopLoadMore();
                if(!last&&res2.size()==20)        listView.setPullLoadEnable(true);
                else listView.setPullLoadEnable(false);
            }

            @Override
            public void getFail(int errorCode) {
                listView.stopLoadMore();
                if(reslt.size()>0)        listView.setPullLoadEnable(true);
                else listView.setPullLoadEnable(false);
            }
        });

    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void refreshTitle(){
        try {
            MyDeviceBean d = deviceDAO.findByChoice(1);
            String status = d.isOnline()?getResources().getString(R.string.on_line):getResources().getString(R.string.off_line);
            if("报警器".equals(d.getDeviceName())){
                title_view.setText(getResources().getString(R.string.my_home)+status);

            }else{
                title_view.setText(d.getDeviceName()+status);
            }
        }catch (Exception e){
            e.printStackTrace();
            title_view.setText("");
        }
    }




    @SuppressLint("NewApi")
    private void initialize() {

        try {
            infos.clear();
            views.clear();
            casaul.removeAllViews();
            if(cycleViewPager!=null){
                Log.i(TAG,"cycleViewPager.removeHandler();");
                cycleViewPager.removeHandler();
            }
            cycleViewPager = new CycleViewPager(getActivity());
            casaul.addView(cycleViewPager);
            List<MonitorBean> list = CCPAppManager.getClientUser().getMonitorList();

            if(list.size()>0){
                for(int i = 0; i < list.size(); i ++){

                    infos.add(list.get(i));
                }

                // 将最后一个ImageView添加进来
                views.add(ViewFactory.getImageView(getActivity(),list.get(list.size()-1).getDevid()));
                for (int i = 0; i < infos.size(); i++) {
                    views.add(ViewFactory.getImageView(getActivity(),infos.get(i).getDevid()));
                }
                // 将第一个ImageView添加进来
                views.add(ViewFactory.getImageView(getActivity(),list.get(0).getDevid()));

                // 设置循环，在调用setData方法前调用
                cycleViewPager.setCycle(true);

                // 在加载数据前设置是否循环
                cycleViewPager.setData(views, infos, this);
                //设置轮播
                cycleViewPager.setWheel(true);

                // 设置轮播时间，默认5000ms
                cycleViewPager.setTime(5000);
                //设置圆点指示图标组居中显示，默认靠右
                cycleViewPager.setIndicatorCenter();
            }else{
                MonitorBean monitorBean = new MonitorBean();
                monitorBean.setName(getResources().getString(R.string.no_monitor_hint));
                monitorBean.setDevid("");
                infos.add(monitorBean);

                // 将最后一个ImageView添加进来
                views.add(ViewFactory.getImageView2(getActivity()));


                // 设置循环，在调用setData方法前调用
                cycleViewPager.setCycle(false);

                // 在加载数据前设置是否循环
                cycleViewPager.setData(views, infos, this);
                //设置轮播
                cycleViewPager.setWheel(false);

                // 设置轮播时间，默认5000ms
                cycleViewPager.setTime(5000);
                //设置圆点指示图标组居中显示，默认靠右
                cycleViewPager.setIndicatorCenter();


            }
        }catch (NullPointerException e){
            Log.i(TAG,"tuichu");
        }


    }
    private String getGpsSetting(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_PGS_SETTING;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

    @SuppressLint("NewApi")
    private void initializeWeather() {

        try {
            infos_weather.clear();
            views_weather.clear();
            topp.removeAllViews();
            if(cycleViewWeatherPager!=null){
                Log.i(TAG,"cycleViewWeatherPager.removeHandler();");
                cycleViewWeatherPager.removeHandler();
            }
            cycleViewWeatherPager = new CycleViewWeatherPager(getActivity());
            topp.addView(cycleViewWeatherPager);



            if("yes".equals(getGpsSetting())){
                WeatherInfoBean weatherInfoBean = new WeatherInfoBean();
                weatherInfoBean.setFlag_first(true);
                weatherInfoBean.setWeather_ico_url(gpsweather_ico);
                weatherInfoBean.setWeather(weather_txt);
                weatherInfoBean.setHum(MyApplication.getAppResources().getString(R.string.hum)+gpshum+"%");
                weatherInfoBean.setName(gps_place);
                weatherInfoBean.setTemp(temp+"℃");
                infos_weather.add(weatherInfoBean);
            }


             if(ConnectionPojo.getInstance().deviceTid!=null){
                 List<EquipmentBean> equipmentBeans = equipDAO.findThChecks(ConnectionPojo.getInstance().deviceTid);
                 for(int i=0;i<equipmentBeans.size();i++){
                     WeatherInfoBean weatherInfoBean1 = new WeatherInfoBean();
                     weatherInfoBean1.setFlag_first(false);

                     String realT="";
                     String realH ="";
                     String temp = equipmentBeans.get(i).getState().substring(4,6);
                     String humidity = equipmentBeans.get(i).getState().substring(6,8);
                     String temp2 = Integer.toBinaryString(Integer.parseInt(temp,16));
                     if (temp2.length()==8){
                         realT = "-"+ (128 - Integer.parseInt(temp2.substring(1,temp2.length()),2));
                     }else{
                         realT = "" + Integer.parseInt(temp2,2);
                     }

                     if(Integer.parseInt(realT)>100 || Integer.parseInt(realT) < -40 || Integer.parseInt(humidity,16)<0 || Integer.parseInt(humidity,16)>100){
                         weatherInfoBean1.setHum("");
                         weatherInfoBean1.setTemp("");
                         weatherInfoBean1.setName((TextUtils.isEmpty(equipmentBeans.get(i).getEquipmentName())?(NameSolve.getDefaultName(this.getActivity(),equipmentBeans.get(i).getEquipmentDesc(),equipmentBeans.get(i).getEqid())):(equipmentBeans.get(i).getEquipmentName()))
                                 +(this.getActivity().getResources().getString(R.string.off_line)));
                     }else{
                         realH = "" +Integer.parseInt(humidity,16);
                         weatherInfoBean1.setHum(realH+"%");
                         weatherInfoBean1.setTemp(realT+"℃");
                         weatherInfoBean1.setName(TextUtils.isEmpty(equipmentBeans.get(i).getEquipmentName())?(NameSolve.getDefaultName(this.getActivity(),equipmentBeans.get(i).getEquipmentDesc(),equipmentBeans.get(i).getEqid())):(equipmentBeans.get(i).getEquipmentName()));
                     }


                     infos_weather.add(weatherInfoBean1);
                 }
             }



              if(infos_weather.size()>0){
                  // 将最后一个ImageView添加进来
                  views_weather.add(ViewFactory.getweatherLinearLayout(getActivity(),infos_weather.get(infos_weather.size()-1)));
                  for (int i = 0; i < infos_weather.size(); i++) {
                      views_weather.add(ViewFactory.getweatherLinearLayout(getActivity(),infos_weather.get(i)));
                  }
                  // 将第一个ImageView添加进来
                  views_weather.add(ViewFactory.getweatherLinearLayout(getActivity(),infos_weather.get(0)));

                  // 设置循环，在调用setData方法前调用
                  cycleViewWeatherPager.setCycle(true);

                  // 在加载数据前设置是否循环
                  cycleViewWeatherPager.setData(views_weather, infos_weather, this);
                  //设置轮播
                  cycleViewWeatherPager.setWheel(true);

                  // 设置轮播时间，默认5000ms
                  cycleViewWeatherPager.setTime(5000);
                  //设置圆点指示图标组居中显示，默认靠右
                  cycleViewWeatherPager.setIndicatorCenter();
              }


        }catch (NullPointerException e){
            Log.i(TAG,"tuichu");
        }


    }


    @Override
    public void onImageClick(MonitorBean info, int position, View imageView) {

        DeviceActivitys.startDeviceActivity(this.getActivity(),info.getDevid(),info.getName());
    }


    @Override
    public void onTesClick(WeatherInfoBean info, int position, View imageView) {

    }

    @Override
    public void onGpsContentSet() {

    }

    @Override
    public void onNoContentAlert() {
        Intent intent = new Intent(HomeFragment.this.getActivity(), ActivityGuideDeviceAdd.class);
        startActivity(intent);
    }
	
    @Override
    public void dmissListener() {
        menuDialog.dismiss();
    }

    @Override
    public void sendListener(int position) {
        final List<SysModelBean> listnow = sysmodelDAO.findAllSys(ConnectionPojo.getInstance().deviceTid);
        setNowmodeindex(Integer.parseInt(listnow.get(position).getSid()));
        SendCommand.Command = SendCommand.CHOOSE_SCENE_GROUP;
        ssgd.sceneGroupChose(nowmodeindex);
    }


    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){

        if(event.getRefreshevent()==1 || event.getRefreshevent() == 6){
            Log.i(TAG,"当前网关状态刷新refreshTitle();");
             refreshTitle();
        }else if(event.getRefreshevent()==5){
            initializeWeather();
        }
    }


    @Subscribe          //订阅更新温湿度界面
    public  void onEventMainThread(ThcheckEvent event){

         initializeWeather();
    }

    private void clearNotices(){
        String url = Constants.UrlUtil.BASE_USER_URL+"api/v1/notification?"
                          +"ctrlKey="+ ConnectionPojo.getInstance().ctrlKey;
        BasicHeader header = new BasicHeader("X-Hekr-ProdPubKey", ConnectionPojo.getInstance().propubkey);
        HekrUserAction.getInstance(this.getActivity()).deleteHekrData(url,new Header[]{header}, new HekrUserAction.GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {
                noticeDAO.deleteAllNotice();
                reslt.clear();
                historyAdapter.refreshList(reslt);
                Toast.makeText(HomeFragment.this.getActivity(),getResources().getString(R.string.delete_success),Toast.LENGTH_LONG).show();
            }

            @Override
            public void getFail(int errorCode) {
                if(errorCode==1){
                    LogoutEvent tokenTimeoutEvent = new LogoutEvent();
                    EventBus.getDefault().post(tokenTimeoutEvent);
                }else{
                    Toast.makeText(HomeFragment.this.getActivity(), UnitTools.errorCode2Msg(HomeFragment.this.getActivity(),errorCode),Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onLoadMore() {
        historyDataShow();
    }


    private void initGps(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            String address = "纬度：" + lat + "经度：" + lon;
                            Log.i("ceshi", "Google Location > " + address);

                            try {
                                if ("".equals(weather_txt)) {

                                    Config.getWeatherInfo(HomeFragment.this.getActivity(), new HekrUser.LoginListener() {
                                        @Override
                                        public void loginSuccess(String str) {
                                            Log.i("ceshi", "天气数据:" + str);
                                            try {
                                                JSONObject jsonObject = JSONObject.parseObject(str);
                                                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                                                String weather = jsonArray.getJSONObject(0).getString("description");
                                                String weather_ico = jsonArray.getJSONObject(0).getString("icon");
                                                int hum = jsonObject.getJSONObject("main").getInteger("humidity");
                                                double temp_hua = jsonObject.getJSONObject("main").getDouble("temp");

                                                weather_txt = weather;
                                                gpshum = ""+hum;
                                                temp = String.valueOf((int) ((temp_hua - 273)));
                                                gpsweather_ico = weather_ico;
                                                initializeWeather();


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        }

                                        @Override
                                        public void loginFail(int errorCode) {
                                            Log.i("ceshi", "errorCode:" + errorCode);
                                        }
                                    }, "http://api.openweathermap.org/data/2.5/weather?lat=" +
                                            lat + "&lon=" + lon
                                            + "&appid=b45eb4739891c226b7a36613ce3d1dbd&lang=" + unitTools.readLanguage());


                                    Config.getPlaceInfo(HomeFragment.this.getActivity(), new HekrUser.LoginListener() {
                                        @Override
                                        public void loginSuccess(String str) {
                                            try {
                                                JSONObject json = JSONArray.parseObject(str);
                                                JSONArray s = json.getJSONArray("results");
                                                JSONObject json2 = s.getJSONObject(0);
                                                JSONArray s2 = json2.getJSONArray("address_components");

                                                if(s2.size()>1){
                                                    JSONObject json3 = s2.getJSONObject(1);
                                                    String name = json3.getString("long_name");
                                                    gps_place = name;
                                                }else if(s2.size()==1){
                                                    JSONObject json3 = s2.getJSONObject(0);
                                                    String name = json3.getString("long_name");
                                                    gps_place = name;
                                                }
                                                Log.i(TAG,"定位到城市:"+gps_place);
                                                initializeWeather();
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void loginFail(int errorCode) {
                                            Log.i("ceshi","城市信息获取失败:"+errorCode);
                                        }
                                    },"http://maps.google.cn/maps/api/geocode/json?latlng="+lat+","+lon+"&sensor=true&language="+unitTools.readLanguage());


                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }






}
