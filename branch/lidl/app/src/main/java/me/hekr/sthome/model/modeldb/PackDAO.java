package me.hekr.sthome.model.modeldb;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.DragFolderwidget.ApplicationInfo;
import me.hekr.sthome.DragFolderwidget.FolderInfo;

/**
 * Created by gc-0001 on 2017/2/13.
 */
public class PackDAO {

    private final String TAG = "PackDAO";
    private SysDB sys;
    Context context;
    public PackDAO(Context context){

        try {
            this.context = context;
            this.sys = new SysDB(context);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    /**
     * @return 所有的组的数量
     */
    public int findPackCount(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        int a = 0;
        try {
        Cursor cursor = db.rawQuery("select count(id) from pactable where deviceid='"+deviceid+"'",null);
        if(cursor.moveToFirst()) {
            a = cursor.getInt(cursor.getColumnIndex("count(id)"));
        }
        }catch (NullPointerException e){
            com.litesuits.android.log.Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return a;
        }
    }

    /**
     * 获取分组列表
     * @return
     */
        public List<FolderInfo> findPackList(String deviceid){
            SQLiteDatabase db = this.sys.getWritableDatabase();
            List<FolderInfo> list = new ArrayList<FolderInfo>();
            try {
                Cursor cursor = db.rawQuery("select * from pactable where deviceid = '"+deviceid+"' order by sort asc ",null);
                while (cursor.moveToNext()){
                    FolderInfo eq = new FolderInfo();
                    eq.setTitle(cursor.getString(cursor.getColumnIndex("name")));
                    eq.setOrder(cursor.getInt(cursor.getColumnIndex("sort")));
                    eq.setPackId(cursor.getInt(cursor.getColumnIndex("packageid")));
                    eq.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                    list.add(eq);
                }
            }catch (NullPointerException e){
                com.litesuits.android.log.Log.i(TAG,"no choosed gateway");
            }finally {
                db.close();
                return list;
            }
    }

    public List<ApplicationInfo> findAppInfoList(int packid,String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
        try {
        Cursor cursor = db.rawQuery("select * from equipment where packageid = "+packid+" and deviceid = '"+deviceid+"' group by eqid order by eqid asc ",null);
        while (cursor.moveToNext()){
            ApplicationInfo eq = new ApplicationInfo();
            eq.setEquipmentName(cursor.getString(cursor.getColumnIndex("name")));
            eq.setTitle(cursor.getString(cursor.getColumnIndex("name")));
            eq.setState(cursor.getString(cursor.getColumnIndex("state")));
            eq.setEquipmentDesc(cursor.getString(cursor.getColumnIndex("equipmentdesc")));
            eq.setOrder(Integer.parseInt(cursor.getString(cursor.getColumnIndex("eqid"))));
            eq.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
            eq.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            list.add(eq);
        }
        }catch (NullPointerException e){
            com.litesuits.android.log.Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return list;
        }
    }

    //删除所有设备操作
    public void deleteAll(String deviceid)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        String where = "deviceid = '"+deviceid+"'";
        db.delete("pactable", where, null);
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //删除所有设备操作
    public void deletePack(int packageid,String deviceid)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        String where = "packageid = "+packageid+" and deviceid ='"+deviceid+"' ";
        db.delete("pactable", where, null);
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //增加操作
    public int addPack(FolderInfo eq)
    {
        int row = 0;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
    /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put("name", eq.getText());
        cv.put("packageid",eq.getPackId());
        cv.put("sort",eq.getOrder());
        cv.put("deviceid",eq.getDeviceid());
        row = (int) db.insert("pactable", null, cv);
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return row;
        }
    }

    //修改顺序操作
    public void updateOrder(FolderInfo eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        String where = "packageid = ? and deviceid = ?";
        String[] whereValue = {String.valueOf(eq.getPackId()),eq.getDeviceid()};
        ContentValues cv = new ContentValues();
        cv.put("sort", eq.getOrder());
        db.update("pactable", cv, where, whereValue);
        Log.i("update pactable over","data "+eq.toString());
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //修改组名称操作
    public void updateName(FolderInfo eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        String where = "packageid = ? and deviceid = ?";
        String[] whereValue = {String.valueOf(eq.getPackId()),eq.getDeviceid()};
        ContentValues cv = new ContentValues();
        cv.put("name", eq.getEquipmentName());
        db.update("pactable", cv, where, whereValue);
        Log.i("update pactable over","data "+eq.toString());
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

}
