package me.hekr.sthome.model.modeldb;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.litesuits.android.log.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


import me.hekr.sthome.LoginActivity;
import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.model.modelbean.ShortcutBean;

/**
 * Created by jishu0001 on 2016/8/22.
 */
public class ShortcutDAO {
    private final String TAG = "ShortcutDAO";
    private SysDB sys;
    Context context;
    public ShortcutDAO(Context context){

        try {
            this.context = context;
            this.sys = new SysDB(context);
        }catch (NullPointerException e){
             e.printStackTrace();
        }
    }


    public List<ShortcutBean> findShorcutsByeqid(@NotNull String deviceid, String eqid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<ShortcutBean> list = new ArrayList<ShortcutBean>();
        try {
            Cursor cursor = db.rawQuery("select * from modetable where deviceid = '"+deviceid+"' and eqid ='"+eqid+"'",null);
            while (cursor.moveToNext()){
                ShortcutBean eq = new ShortcutBean();
                eq.setDelay(cursor.getInt(cursor.getColumnIndex("delay")));
                eq.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
                eq.setDes_sid(cursor.getString(cursor.getColumnIndex("content")));
                eq.setSrc_sid(cursor.getString(cursor.getColumnIndex("sid")));
                eq.setDeviceid(deviceid);
                list.add(eq);
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return list;
        }
    }

    public List<ShortcutBean> findShorcutsBysid(@NotNull String deviceid, String sid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<ShortcutBean> list = new ArrayList<ShortcutBean>();
        try {
            Cursor cursor = db.rawQuery("select * from modetable where deviceid = '"+deviceid+"' and sid ='"+sid+"'",null);
            while (cursor.moveToNext()){
                ShortcutBean eq = new ShortcutBean();
                eq.setDelay(cursor.getInt(cursor.getColumnIndex("delay")));
                eq.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
                eq.setDes_sid(cursor.getString(cursor.getColumnIndex("content")));
                eq.setSrc_sid(cursor.getString(cursor.getColumnIndex("sid")));
                eq.setDeviceid(deviceid);
                list.add(eq);
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return list;
        }
    }


    public ShortcutBean findShortcutByeqid(String sid, String eqid, String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        ShortcutBean eq = null;
        try {
            Cursor cursor = db.rawQuery("select a.* from modetable a where a.sid='"+sid+"' and a.eqid ='"+eqid+"' and a.deviceid = '"+deviceid+"'",null);
            if(cursor.moveToFirst()) {
                eq = new ShortcutBean();
                eq.setDelay(cursor.getInt(cursor.getColumnIndex("delay")));
                eq.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
                eq.setDes_sid(cursor.getString(cursor.getColumnIndex("content")));
                eq.setSrc_sid(cursor.getString(cursor.getColumnIndex("sid")));
                eq.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return eq;
        }
    }
    //删除某情景模式下的某条快捷关联
    public void deleteShortcurtByEqid(String deviceid,String eqid)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "deviceid = '"+deviceid+"' and  eqid = '"+eqid+"'";
            db.delete("modetable", where, null);
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //删除某情景模式下的某条快捷关联
    public void deleteShortcurt(String sid,String deviceid,String eqid)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "deviceid = '"+deviceid+"' and sid = '"+sid+"' and eqid = '"+eqid+"'";
            db.delete("modetable", where, null);
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //删除某情景模式下的所有快捷关联
    public void deleteAllShortcurt(String sid,String deviceid)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "deviceid = '"+deviceid+"' and sid = '"+sid+"'";
            db.delete("modetable", where, null);
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }


        /**
     * isHasShorcut:判断是否有该sid的情景组
     * 作者：Henry on 2017/3/7 13:02
     * 邮箱：xuejunju_4595@qq.com
     * 参数:sid
     * 返回:boolean
     */
    public boolean isHasShorcut(String sid,String eqid,String deviceid) {

        ShortcutBean shortcutBean = findShortcutByeqid(sid,eqid,deviceid);
        if (shortcutBean == null ) {
            return false;
        }else
            return true;
    }


    /**
     * 更新情景关联到数据库
     * @param shortcutBean
     * @return
     */
    public long insertShortcut(ShortcutBean shortcutBean) {

        if(shortcutBean == null || TextUtils.isEmpty(shortcutBean.getEqid())) {
            return -1L;
        }
        SQLiteDatabase db = this.sys.getWritableDatabase();
        ContentValues values = null;
        try {

            values = new ContentValues();
            values.put("eqid", shortcutBean.getEqid());
            values.put("sid", shortcutBean.getSrc_sid());
            values.put("deviceid",shortcutBean.getDeviceid());
            values.put("content", shortcutBean.getDes_sid());
            if(!isHasShorcut(shortcutBean.getSrc_sid(), shortcutBean.getEqid(),shortcutBean.getDeviceid())) {
                db = this.sys.getWritableDatabase();
                return db.insert("modetable", null, values);
            } else {
                db = this.sys.getWritableDatabase();
                return db.update("modetable",values , "sid ='" + shortcutBean.getSrc_sid()+"' and deviceid = '"+shortcutBean.getDeviceid()+"' and eqid = '"+ shortcutBean.getEqid()+"'",null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
            }
            db.close();
        }
        return -1L;
    }

}
