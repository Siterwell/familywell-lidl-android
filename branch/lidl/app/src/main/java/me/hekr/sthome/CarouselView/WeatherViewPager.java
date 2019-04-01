package me.hekr.sthome.CarouselView;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.WeatherInfoBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.ECPreferenceSettings;
import me.hekr.sthome.tools.ECPreferences;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.NameSolve;

/**
 * Created by ryanhsueh on 2018/12/25
 */
public class WeatherViewPager extends RelativeLayout {
    private static final String TAG = WeatherViewPager.class.getSimpleName();

    private ViewPager weatherViewPager;
    private WeatherPagerAdapter weatherPagerAdapter;

    private List<LinearLayout> views_weather = new ArrayList<>();
    private List<WeatherInfoBean> infos_weather = new ArrayList<>();

    private EquipDAO equipDAO;

    public static class WeatherInfo {
        public String gps_place = "";
        public String gpsweather_ico = "";
        public String weather_txt = "";
        public String gpshum = "";
        public String temp = "";
    }


    public WeatherViewPager(Context context) {
        super(context);
    }

    public WeatherViewPager(Context context, View rootView, WeatherInfo weatherInfo, EquipDAO equipDAO) {
        super(context);

        this.equipDAO = equipDAO;

        initView(rootView, weatherInfo);
    }

    private void initView(View rootView, WeatherInfo weatherInfo) {
        LOG.D(TAG, "[RYAN] initView");

        updateWeather(weatherInfo);

        weatherViewPager = rootView.findViewById(R.id.viewpager_weather);
        weatherPagerAdapter = new WeatherPagerAdapter();
        weatherViewPager.setAdapter(weatherPagerAdapter);
    }

    public void update(WeatherInfo weatherInfo) {
        LOG.D(TAG, "[RYAN] update");

        updateWeather(weatherInfo);
    }

    public void updateWeather(WeatherInfo weatherInfo) {
        try {
            infos_weather.clear();
            views_weather.clear();

            if("yes".equals(getGpsSetting())){
                WeatherInfoBean weatherInfoBean = new WeatherInfoBean();
                weatherInfoBean.setFlag_first(true);
                weatherInfoBean.setWeather_ico_url(weatherInfo.gpsweather_ico);
                weatherInfoBean.setWeather(weatherInfo.weather_txt);
                weatherInfoBean.setHum(getResources().getString(R.string.hum) + " " + weatherInfo.gpshum+"%");
                weatherInfoBean.setName(weatherInfo.gps_place);
                weatherInfoBean.setTemp(weatherInfo.temp+"℃");
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
                        weatherInfoBean1.setName((TextUtils.isEmpty(equipmentBeans.get(i).getEquipmentName())?(NameSolve.getDefaultName(getContext(), equipmentBeans.get(i).getEquipmentDesc(),equipmentBeans.get(i).getEqid())):(equipmentBeans.get(i).getEquipmentName()))
                                +(getResources().getString(R.string.off_line)));
                    }else{
                        realH = "" +Integer.parseInt(humidity,16);
                        weatherInfoBean1.setHum(realH+"%");
                        weatherInfoBean1.setTemp(realT+"℃");
                        weatherInfoBean1.setName(TextUtils.isEmpty(equipmentBeans.get(i).getEquipmentName())?(NameSolve.getDefaultName(getContext(), equipmentBeans.get(i).getEquipmentDesc(),equipmentBeans.get(i).getEqid())):(equipmentBeans.get(i).getEquipmentName()));
                    }


                    infos_weather.add(weatherInfoBean1);
                }
            }


            for (int i = 0; i < infos_weather.size(); i++) {
                views_weather.add(ViewFactory.getweatherLinearLayout(getContext(), infos_weather.get(i)));
            }

            weatherPagerAdapter = new WeatherPagerAdapter();
            weatherViewPager.setAdapter(weatherPagerAdapter);
//            weatherPagerAdapter.notifyAll();

        }catch (NullPointerException e){
            LOG.I(TAG,"tuichu");
        }
    }

    private String getGpsSetting(){
        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_PGS_SETTING;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

    private class WeatherPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views_weather.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views_weather.get(position));
            return views_weather.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
