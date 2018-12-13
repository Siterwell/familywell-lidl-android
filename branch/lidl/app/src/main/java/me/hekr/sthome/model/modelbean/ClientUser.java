package me.hekr.sthome.model.modelbean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.tools.EncryptUtil;

/**
 * Created by gc-0001 on 2017/2/7.
 */
public class ClientUser {

    private String id;
    private long birthday;
    private String firstName;
    private String lastName;
    private long updateDate;
    private String phoneNumber;
    private String gender;
    private String description;
    private String email;
    private String age;
    private String monitor;

    public String getMonitor() {
        return monitor;
    }
    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public long getBirthday() {
        return birthday;
    }
    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getFirstName() {
        return EncryptUtil.decrypt(firstName);
    }
    public void setFirstName(String firstName) {
        this.firstName = EncryptUtil.encrypt(firstName);
    }

    public String getLastName() {
        return EncryptUtil.decrypt(lastName);
    }
    public void setLastName(String lastName) {
        this.lastName = EncryptUtil.encrypt(lastName);
    }

    public long getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getPhoneNumber() {
        return EncryptUtil.decrypt(phoneNumber);
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = EncryptUtil.encrypt(phoneNumber);
    }

    public String getGender() {
        return EncryptUtil.decrypt(gender);
    }
    public void setGender(String gender) {
        this.gender = EncryptUtil.encrypt(gender);
    }

    public String getDescription() {
        return EncryptUtil.decrypt(description);
    }
    public void setDescription(String description) {
        this.description = EncryptUtil.encrypt(description);
    }

    public String getEmail() {
        return EncryptUtil.decrypt(email);
    }
    public void setEmail(String email) {
        this.email = EncryptUtil.encrypt(email);
    }

    public String getAge() {
        return EncryptUtil.decrypt(age);
    }
    public void setAge(String age) {
        this.age = EncryptUtil.encrypt(age);
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id" , id);
            jsonObject.put("birthday" , birthday);
            jsonObject.put("firstName" , EncryptUtil.decrypt(firstName));
            jsonObject.put("lastName" , EncryptUtil.decrypt(lastName));
            jsonObject.put("updateDate" , updateDate);
            jsonObject.put("phoneNumber" , EncryptUtil.decrypt(phoneNumber));
            jsonObject.put("gender" , EncryptUtil.decrypt(gender));
            jsonObject.put("description" , EncryptUtil.decrypt(description));
            jsonObject.put("email" , EncryptUtil.decrypt(email));
            jsonObject.put("age" , EncryptUtil.decrypt(age));
            jsonObject.put("monitor" , monitor);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "ClientUser{" +
                "id=" + id +
                "birthday=" + birthday +
                ", firstName='" + EncryptUtil.decrypt(firstName) + '\'' +
                ", lastName='" + EncryptUtil.decrypt(lastName) + '\'' +
                ", updateDate=" + updateDate +
                ", phoneNumber='" + EncryptUtil.decrypt(phoneNumber) + '\'' +
                ", gender='" + EncryptUtil.decrypt(gender) + '\'' +
                ", description='" + EncryptUtil.decrypt(description) + '\'' +
                ", email='" + EncryptUtil.decrypt(email) + '\'' +
                ", age='" + EncryptUtil.decrypt(age) + '\'' +
                ", monitor='" + monitor + '\'' +
                '}';
    }

    public ClientUser from(String input) {
        JSONObject object = null;
        try {
            object = new JSONObject(input);
            if(object.has("id")) {
                this.id = object.getString("id");
            }
            if(object.has("birthday")) {
                this.birthday = object.getLong("birthday");
            }
            if(object.has("firstName")) {
                this.firstName = EncryptUtil.encrypt(object.getString("firstName"));
            }
            if(object.has("lastName")) {
                this.lastName = EncryptUtil.encrypt(object.getString("lastName"));
            }
            if(object.has("updateDate")) {
                this.updateDate = object.getLong("updateDate");
            }
            if(object.has("phoneNumber")) {
                this.phoneNumber = EncryptUtil.encrypt(object.getString("phoneNumber"));
            }
            if(object.has("gender")) {
                this.gender = EncryptUtil.encrypt(object.getString("gender"));
            }
            if(object.has("description")) {
                this.description = EncryptUtil.encrypt(object.getString("description"));
            }
            if(object.has("email")) {
                this.email = EncryptUtil.encrypt(object.getString("email"));
            }
            if(object.has("age")) {
                this.age = EncryptUtil.encrypt(object.getString("age"));
            }
            if(object.has("monitor")){
                this.monitor = object.getString("monitor");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public List<MonitorBean> getMonitorList(){
        ArrayList<MonitorBean> arrayList = new ArrayList<MonitorBean>();
        if(TextUtils.isEmpty(monitor)){
            return  arrayList;
        }else{
            com.alibaba.fastjson.JSONArray jsonArray = JSON.parseArray(monitor);
            for(int i=0;i<jsonArray.size();i++){
                MonitorBean monitorBean = new MonitorBean();
                com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                monitorBean.setDevid(jsonObject.getString("devid"));
                monitorBean.setName(jsonObject.getString("name"));
                arrayList.add(monitorBean);
            }
        }



        return arrayList;
    }

}
