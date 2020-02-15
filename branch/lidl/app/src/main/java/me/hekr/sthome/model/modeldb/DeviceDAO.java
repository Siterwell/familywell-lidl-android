package me.hekr.sthome.model.modeldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.http.bean.DcInfo;
import me.hekr.sthome.model.modelbean.MyDeviceBean;

/**
 * ClassName:DeviceDAO
 * 作者：Henry on 2017/2/27 10:43
 * 邮箱：xuejunju_4595@qq.com
 * 描述:
 */
public class DeviceDAO {
    private SysDB sys;
    Context context;
    public DeviceDAO(Context context){
        try {
            this.context = context;
            this.sys = new SysDB(context);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }



    /**
     * 查找所有未属于任何分组的设备
     * @return
     */
    public List<MyDeviceBean> findAllDevice(){
        List<MyDeviceBean> list = new ArrayList<MyDeviceBean>();
        MyDeviceBean eq = null;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from devicetable where 1 = 1 order by id",null);
        while (cursor.moveToNext()){
            eq = new MyDeviceBean();
            DcInfo dcInfo = new DcInfo();
            eq.setDevTid(cursor.getString(cursor.getColumnIndex("deviceid")));
            eq.setBindKey(cursor.getString(cursor.getColumnIndex("bindkey")));
            eq.setCtrlKey(cursor.getString(cursor.getColumnIndex("ctrlkey")));
            eq.setChoice(cursor.getInt(cursor.getColumnIndex("choice")));
            eq.setDeviceName(cursor.getString(cursor.getColumnIndex("devicename")));
            eq.setOnline(cursor.getInt(cursor.getColumnIndex("status"))==1?true:false);
            eq.setProductPublicKey(cursor.getString(cursor.getColumnIndex("propubkey")));
            dcInfo.setConnectHost(cursor.getString(cursor.getColumnIndex("domain")));
            eq.setDcInfo(dcInfo);
            list.add(eq);
            eq.setBinVersion(cursor.getString(cursor.getColumnIndex("longtitude")));
            eq.setBinType(cursor.getString(cursor.getColumnIndex("latitude")));
        }
        db.close();
        return list;
    }


    public void deleteAllDeivceChoice(){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String where = "1 = 1";
        ContentValues cv = new ContentValues();
        cv.put("choice", 0);
        db.update("devicetable", cv, where, null);
        db.close();
    }

    //删除所有网关操作
    public void deleteAll()
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String where = "1 = 1";
        db.delete("devicetable", where, null);
        db.close();
    }

    public void deleteByDeviceId(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String where = "deviceid = ?";
        String[] whereValue ={ deviceid };
        db.delete("devicetable", where, whereValue);
        db.close();
    }

    public void updateDeivceChoice(String deviceid){
        deleteAllDeivceChoice();
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
        String[] whereValue = {deviceid};
        ContentValues cv = new ContentValues();
        cv.put("choice", 1);
        db.update("devicetable", cv, where, whereValue);
        db.close();
    }

    public void updateDeivceName(String deviceid,String name){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
        String[] whereValue = {deviceid};
        ContentValues cv = new ContentValues();
        cv.put("devicename", name);
        db.update("devicetable", cv, where, whereValue);
        db.close();
    }

    public void updateDeivceStatus(String deviceid,int status){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
        String[] whereValue = {deviceid};
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update("devicetable", cv, where, whereValue);
        db.close();
    }


    public void updateDeivce(MyDeviceBean deviceBean){
        deleteAllDeivceChoice();
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
        String[] whereValue = {deviceBean.getDevTid()};
        ContentValues cv = new ContentValues();
        cv.put("deviceid", deviceBean.getDevTid());
        cv.put("bindkey",deviceBean.getBindKey());
        cv.put("ctrlkey",deviceBean.getCtrlKey());
        cv.put("choice",deviceBean.getChoice());
        cv.put("devicename",deviceBean.getDeviceName());
        cv.put("status",deviceBean.isOnline()?1:0);
        cv.put("propubkey",deviceBean.getProductPublicKey());
        cv.put("domain",deviceBean.getDcInfo().getConnectHost());
        db.update("devicetable", cv, where, whereValue);
        db.close();
    }

    public void updateDeivceBinversion(String devTid,String binVer){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
        String[] whereValue = {devTid};
        ContentValues cv = new ContentValues();
        cv.put("longtitude",binVer);
        db.update("devicetable", cv, where, whereValue);
        db.close();
    }
    /**
     * 查找选中的网关设备
     * @return
     */
    public MyDeviceBean findByChoice(int choice){
        MyDeviceBean eq = null;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("select * from devicetable where choice =?",new String[]{String.valueOf(choice)});
        if(cursor.moveToFirst()) {
            eq = new MyDeviceBean();
            DcInfo dcInfo = new DcInfo();
            eq.setDevTid(cursor.getString(cursor.getColumnIndex("deviceid")));
            eq.setBindKey(cursor.getString(cursor.getColumnIndex("bindkey")));
            eq.setCtrlKey(cursor.getString(cursor.getColumnIndex("ctrlkey")));
            eq.setChoice(cursor.getInt(cursor.getColumnIndex("choice")));
            eq.setDeviceName(cursor.getString(cursor.getColumnIndex("devicename")));
            eq.setOnline(cursor.getInt(cursor.getColumnIndex("status"))==1?true:false);
            dcInfo.setConnectHost(cursor.getString(cursor.getColumnIndex("domain")));
            eq.setProductPublicKey(cursor.getString(cursor.getColumnIndex("propubkey")));
            eq.setDcInfo(dcInfo);
            eq.setBinVersion(cursor.getString(cursor.getColumnIndex("longtitude")));
            eq.setBinType(cursor.getString(cursor.getColumnIndex("latitude")));
        }
        db.endTransaction();
        db.close();
        return eq;
    }


    /**
     * 查找特定deviceid的网关设备
     * @return
     */
    public MyDeviceBean findByDeviceid(String deviceid){
        MyDeviceBean eq = null;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from devicetable where deviceid =?",new String[]{deviceid});
        if(cursor.moveToFirst()) {
            eq = new MyDeviceBean();
            DcInfo dcInfo = new DcInfo();
            eq.setDevTid(cursor.getString(cursor.getColumnIndex("deviceid")));
            eq.setBindKey(cursor.getString(cursor.getColumnIndex("bindkey")));
            eq.setCtrlKey(cursor.getString(cursor.getColumnIndex("ctrlkey")));
            eq.setChoice(cursor.getInt(cursor.getColumnIndex("choice")));
            eq.setDeviceName(cursor.getString(cursor.getColumnIndex("devicename")));
            eq.setOnline(cursor.getInt(cursor.getColumnIndex("status"))==1?true:false);
            eq.setProductPublicKey(cursor.getString(cursor.getColumnIndex("propubkey")));
            dcInfo.setConnectHost(cursor.getString(cursor.getColumnIndex("domain")));
            eq.setDcInfo(dcInfo);
            eq.setBinVersion(cursor.getString(cursor.getColumnIndex("longtitude")));
            eq.setBinType(cursor.getString(cursor.getColumnIndex("latitude")));
        }
        db.close();
        return eq;
    }

    //增加操作
    public int addDevice(MyDeviceBean device)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
    /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put("deviceid", device.getDevTid());
        cv.put("bindkey",device.getBindKey());
        cv.put("ctrlkey",device.getCtrlKey());
        cv.put("choice",device.getChoice());
        cv.put("devicename",device.getDeviceName());
        cv.put("status",device.isOnline()?1:0);
        cv.put("propubkey",device.getProductPublicKey());
        cv.put("domain",device.getDcInfo().getConnectHost());
        cv.put("longtitude",device.getBinVersion());
        cv.put("latitude",device.getBinType());
        int row = (int) db.insert("devicetable", null, cv);
        db.close();
        return row;
    }

    /**
     * @return 所有的组的数量
     */
    public int finddeviceCount(String deviceid){
        int a = 0;
        SQLiteDatabase db = this.sys.getWritableDatabase();
            Cursor cursor = db.rawQuery("select count(id) from devicetable where deviceid='"+deviceid+"'",null);
            if(cursor.moveToFirst()) {
                a = cursor.getInt(cursor.getColumnIndex("count(id)"));
            }
            db.close();
        return a;
    }

}
