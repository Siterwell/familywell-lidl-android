package me.hekr.sthome.model.modelbean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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
            jsonObject.put("firstName" , firstName);
            jsonObject.put("lastName" , lastName);
            jsonObject.put("updateDate" , updateDate);
            jsonObject.put("phoneNumber" , phoneNumber);
            jsonObject.put("gender" , gender);
            jsonObject.put("description" , description);
            jsonObject.put("email" , email);
            jsonObject.put("age" , age);
            jsonObject.put("monitor" , monitor);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "ClientUser{" +
                "id=" + id +
                "birthday=" + birthday +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", updateDate=" + updateDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", description='" + description + '\'' +
                ", email='" + email + '\'' +
                ", age='" + age + '\'' +
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
                this.firstName = object.getString("firstName");
            }
            if(object.has("lastName")) {
                this.lastName = object.getString("lastName");
            }
            if(object.has("updateDate")) {
                this.updateDate = object.getLong("updateDate");
            }
            if(object.has("phoneNumber")) {
                this.phoneNumber = object.getString("phoneNumber");
            }
            if(object.has("gender")) {
                this.gender = object.getString("gender");
            }
            if(object.has("description")) {
                this.description = object.getString("description");
            }
            if(object.has("email")) {
                this.email = object.getString("email");
            }
            if(object.has("age")) {
                this.age = object.getString("age");
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
