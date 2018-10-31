package me.hekr.sthome.model.modelbean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by gc-0001 on 2017/4/10.
 */
public class MonitorBean implements Serializable{
    private String devid;
    private String name;


    public String getDevid() {
        return devid;
    }

    public void setDevid(String devid) {
        this.devid = devid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("devid" , devid);
            jsonObject.put("name" , name);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "MonitorBean{" +
                ", devid='" + devid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
