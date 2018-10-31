package me.hekr.sthome.model.modelbean;

import java.io.Serializable;

/**
 * Created by TracyHenry on 2018/3/12.
 */

public class WeatherInfoBean implements Serializable {

    boolean flag_first; //true代表首页当前手机定位，false为温湿度页面；

    String weather;

    String hum;

    String temp;

    String name;

    String weather_ico_url;


    public boolean isFlag_first() {
        return flag_first;
    }

    public void setFlag_first(boolean flag_first) {
        this.flag_first = flag_first;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getHum() {
        return hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeather_ico_url() {
        return weather_ico_url;
    }

    public void setWeather_ico_url(String weather_ico_url) {
        this.weather_ico_url = weather_ico_url;
    }

    @Override
    public String toString() {
        return "WeatherInfoBean{" +
                "flag_first=" + flag_first +
                ", weather='" + weather + '\'' +
                ", hum='" + hum + '\'' +
                ", temp='" + temp + '\'' +
                ", name='" + name + '\'' +
                ", weather_ico_url='" + weather_ico_url + '\'' +
                '}';
    }
}
