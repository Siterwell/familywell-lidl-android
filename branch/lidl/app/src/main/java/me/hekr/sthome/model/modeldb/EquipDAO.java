package me.hekr.sthome.model.modeldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.DragFolderwidget.ApplicationInfo;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.tools.NameSolve;

/**
 * Created by jishu0001 on 2016/8/22.
 */
public class EquipDAO {
    private final String TAG = "EquipDAO";
    private SysDB sys;
    Context context;
    public EquipDAO(Context context){

        try {
            this.context = context;
            this.sys = new SysDB(context);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     * 判断Id是否存在
     * @param eqid
     * @return
     */
    public int isIdExists(String eqid,String deviceid){
        int a = 0;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String Sql = "select count(*) from equipment where eqid = ? and deviceid = ?";
            Cursor localCursor = db.rawQuery(Sql,new String[]{eqid,deviceid});
            localCursor.moveToFirst();
            a = localCursor.getInt(localCursor.getColumnIndex("count(*)"));
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return a;
        }
    }


    //删除操作
    public void deleteByEqid(EquipmentBean eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ?";
            String[] whereValue ={ eq.getEqid(),eq.getDeviceid() };
            db.delete("equipment", where, whereValue);
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //删除操作
    public void deleteByEqidByOtherGateway(EquipmentBean eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ?";
            String[] whereValue ={ eq.getEqid(),eq.getDeviceid() };
            db.delete("equipment", where, whereValue);
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }


    //删除所有设备操作
    public void deleteAll(String deviceid)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "deviceid = '"+deviceid+"'";
            db.delete("equipment", where, null);
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //增加操作
    public int addEq(ApplicationInfo eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        int row = 0;
        try {
            /* ContentValues */
            ContentValues cv = new ContentValues();
            cv.put("name", eq.getEquipmentName());
            cv.put("eqid",eq.getEqid());
            cv.put("activitytime",eq.getActivityTime());
            cv.put("state",eq.getState());
            cv.put("equipmentdesc",eq.getEquipmentDesc());
            cv.put("packageid",eq.getPackId());
            cv.put("sort",eq.getOrder());
            cv.put("deviceid",eq.getDeviceid());
            row = (int) db.insert("equipment", null, cv);

        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return row;
        }
    }
    //修改操作
    public void update(EquipmentBean eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ?";
            String[] whereValue = {eq.getEqid(),eq.getDeviceid()};
            ContentValues cv = new ContentValues();
            cv.put("eqid", eq.getEqid());
            cv.put("activitytime",eq.getActivityTime());
            cv.put("state",eq.getState());
            cv.put("equipmentdesc",eq.getEquipmentDesc());
            db.update("equipment", cv, where, whereValue);
            Log.i(TAG,"data "+eq.toString());
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //修改操作
    public void updateByOtherGateway(EquipmentBean eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ?";
            String[] whereValue = {eq.getEqid(),eq.getDeviceid()};
            ContentValues cv = new ContentValues();
            cv.put("eqid", eq.getEqid());
            cv.put("activitytime",eq.getActivityTime());
            cv.put("state",eq.getState());
            cv.put("equipmentdesc",eq.getEquipmentDesc());
            db.update("equipment", cv, where, whereValue);
            Log.i(TAG,"data "+eq.toString());
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }


    //修改操作用于替换设备
    public void updateWithName(EquipmentBean eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ?";
            String[] whereValue = {eq.getEqid(),eq.getDeviceid()};
            ContentValues cv = new ContentValues();
            cv.put("name", eq.getEquipmentName());
            cv.put("eqid", eq.getEqid());
            cv.put("activitytime",eq.getActivityTime());
            cv.put("state",eq.getState());
            cv.put("equipmentdesc",eq.getEquipmentDesc());
            db.update("equipment", cv, where, whereValue);
            Log.i(TAG,"data "+eq.toString());
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //修改操作
    public void updateName(EquipmentBean eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ? ";
            String[] whereValue = {eq.getEqid(),eq.getDeviceid()};
            ContentValues cv = new ContentValues();
            cv.put("name", eq.getEquipmentName());
            cv.put("deviceid",eq.getDeviceid());
            db.update("equipment", cv, where, whereValue);
            Log.i(TAG," update equipment over data "+eq.toString());
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //修改顺序操作
    public void updateOrder(ApplicationInfo eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ?";
            String[] whereValue = {eq.getEqid(),eq.getDeviceid()};
            ContentValues cv = new ContentValues();
            cv.put("sort", eq.getOrder());
            cv.put("deviceid",eq.getDeviceid());
            db.update("equipment", cv, where, whereValue);
            Log.i( TAG,"update equipment over data "+eq.toString());
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //修改所在包名操作
    public void updatePack(ApplicationInfo eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ?";
            String[] whereValue = {eq.getEqid(),eq.getDeviceid()};
            ContentValues cv = new ContentValues();
            cv.put("packageid", eq.getPackId());
            cv.put("deviceid",eq.getDeviceid());
            db.update("equipment", cv, where, whereValue);
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //修改温控器自动温度操作
    public void updateAutoTemp(String eq,String deviceid,String value)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ?";
            String[] whereValue = {eq,deviceid};
            ContentValues cv = new ContentValues();
            cv.put("autotemp", value);
            db.update("equipment", cv, where, whereValue);
            Log.i(TAG,"data "+eq.toString());
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //修改温控器手动温度操作
    public void updateHandTemp(String eq,String deviceid,String value)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ?";
            String[] whereValue = {eq,deviceid};
            ContentValues cv = new ContentValues();
            cv.put("handtemp", value);
            db.update("equipment", cv, where, whereValue);
            Log.i(TAG,"data "+eq.toString());
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    //修改温控器防冻温度操作
    public void updateFangTemp(String eq,String deviceid,String value)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "eqid = ? and deviceid = ?";
            String[] whereValue = {eq,deviceid};
            ContentValues cv = new ContentValues();
            cv.put("fangtemp", value);
            db.update("equipment", cv, where, whereValue);
            Log.i(TAG,"data "+eq.toString());
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    /**
     * 查找所有未属于任何分组的设备
     * @return
     */
    public List<ApplicationInfo> findAllEqByNoPack(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
        try {

            Cursor cursor = db.rawQuery("select * from equipment where packageid = 0 and deviceid = '"+deviceid+"' group by eqid order by sort,eqid",null);
            while (cursor.moveToNext()){
                ApplicationInfo  eq = new ApplicationInfo();
                eq.setEquipmentId(cursor.getInt(cursor.getColumnIndex("id")));
                eq.setEquipmentName(cursor.getString(cursor.getColumnIndex("name")));
                eq.setTitle(cursor.getString(cursor.getColumnIndex("name")));
                eq.setActivityTime(cursor.getString(cursor.getColumnIndex("activitytime")));
                eq.setState(cursor.getString(cursor.getColumnIndex("state")));
                eq.setEquipmentDesc(cursor.getString(cursor.getColumnIndex("equipmentdesc")));
                eq.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
                eq.setOrder(cursor.getInt(cursor.getColumnIndex("sort")));
                eq.setPackId(cursor.getInt(cursor.getColumnIndex("packageid")));
                eq.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                list.add(eq);
            }

        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return list;
        }
    }

    /**
     * 查找所有未属于任何分组的设备
     * @return
     */
    public List<ApplicationInfo> findAllEq(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
        try {

            Cursor cursor = db.rawQuery("select * from equipment where deviceid = '"+deviceid+"' order by eqid",null);
            while (cursor.moveToNext()){
                ApplicationInfo eq = new ApplicationInfo();
                eq.setEquipmentId(cursor.getInt(cursor.getColumnIndex("id")));
                eq.setEquipmentName(cursor.getString(cursor.getColumnIndex("name")));
                eq.setTitle(cursor.getString(cursor.getColumnIndex("name")));
//            eq.setActivityTime(cursor.getInt(cursor.getColumnIndex("activitytime")));
                eq.setActivityTime(cursor.getString(cursor.getColumnIndex("activitytime")));
                eq.setState(cursor.getString(cursor.getColumnIndex("state")));
                eq.setEquipmentDesc(cursor.getString(cursor.getColumnIndex("equipmentdesc")));
                eq.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
                eq.setOrder(cursor.getInt(cursor.getColumnIndex("sort")));
                eq.setPackId(cursor.getInt(cursor.getColumnIndex("packageid")));
                eq.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                list.add(eq);
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return list;
        }
    }

    /**
     * 查找输入设备
     * @return
     */
    public List<EquipmentBean> findInput(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<EquipmentBean> list = new ArrayList<EquipmentBean>();
        try {
            Cursor cursor = db.rawQuery("select * from equipment where deviceid = '"+deviceid+"' and (equipmentdesc GLOB '*0??' or equipmentdesc GLOB '*1??' or equipmentdesc GLOB '*3??' or equipmentdesc GLOB '*6??' or equipmentdesc = '1213') ",null);
            while (cursor.moveToNext()){
                EquipmentBean eq = new EquipmentBean();
                eq.setEquipmentId(cursor.getInt(cursor.getColumnIndex("id")));
                eq.setEquipmentName(cursor.getString(cursor.getColumnIndex("name")));
                eq.setActivityTime(cursor.getString(cursor.getColumnIndex("activitytime")));
                eq.setState(cursor.getString(cursor.getColumnIndex("state")));
                eq.setEquipmentDesc(cursor.getString(cursor.getColumnIndex("equipmentdesc")));
                eq.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
                list.add(eq);
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return list;
        }
    }

    /**
     * 查找温湿度传感器设备
     * @return
     */
    public List<EquipmentBean> findThChecks(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<EquipmentBean> list = new ArrayList<EquipmentBean>();
        try {
            Cursor cursor = db.rawQuery("select * from equipment where deviceid = '"+deviceid+"' and (equipmentdesc GLOB '?102' ) ",null);
            while (cursor.moveToNext()){
                EquipmentBean eq = new EquipmentBean();
                eq.setEquipmentId(cursor.getInt(cursor.getColumnIndex("id")));
                eq.setEquipmentName(cursor.getString(cursor.getColumnIndex("name")));
                eq.setActivityTime(cursor.getString(cursor.getColumnIndex("activitytime")));
                eq.setState(cursor.getString(cursor.getColumnIndex("state")));
                eq.setEquipmentDesc(cursor.getString(cursor.getColumnIndex("equipmentdesc")));
                eq.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
                list.add(eq);
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return list;
        }
    }

    /**
     * 除去361部分
     * @param deviceid
     * @return
     */
    public List<EquipmentBean> findOutput(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<EquipmentBean> list = new ArrayList<EquipmentBean>();
        try {
            Cursor cursor = db.rawQuery("select * from equipment where deviceid = '"+deviceid+"' and equipmentdesc GLOB '*2??' and equipmentdesc !='1213'",null);
            while (cursor.moveToNext()){
                String type = cursor.getString(cursor.getColumnIndex("equipmentdesc"));
                if(!NameSolve.getEqType(type).equals(NameSolve.TEMP_CONTROL)){
                    EquipmentBean eq = new EquipmentBean();
                    eq.setEquipmentId(cursor.getInt(cursor.getColumnIndex("id")));
                    eq.setEquipmentName(cursor.getString(cursor.getColumnIndex("name")));
                    eq.setActivityTime(cursor.getString(cursor.getColumnIndex("activitytime")));
                    eq.setState(cursor.getString(cursor.getColumnIndex("state")));
                    eq.setEquipmentDesc(cursor.getString(cursor.getColumnIndex("equipmentdesc")));
                    eq.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
                    list.add(eq);
                }

            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return list;
        }
    }


    //find equipment by eqid for scene, no need to get status, the status has been saved before
    public EquipmentBean findByeqid(String eqid, String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        EquipmentBean eq = null;
        try {
            Cursor cursor = db.rawQuery("select name,equipmenttype,equipmentdesc from equipment where eqid =? and deviceid = ?",new String[]{eqid,deviceid});
            if(cursor.moveToFirst()) {
                eq = new EquipmentBean();
                eq.setEquipmentName(cursor.getString(cursor.getColumnIndex("name")));
                eq.setEquipmentDesc(cursor.getString(cursor.getColumnIndex("equipmentdesc")));
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return eq;
        }
    }

    /**
     * 查找温控器自动设置温度缓存值
     * @return
     */
    public String findTempByAutoModel(String deviceid,String eqid){

        SQLiteDatabase db = this.sys.getWritableDatabase();
        String eq = null;
        try {
            Cursor cursor = db.rawQuery("select autotemp from equipment where eqid =? and deviceid = ?",new String[]{eqid,deviceid});
            if(cursor.moveToFirst()) {
                eq =(cursor.getString(cursor.getColumnIndex("autotemp")));
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return eq;
        }

    }

    /**
     * 查找温控器手动设置温度缓存值
     * @return
     */
    public String findTempByHandModel(String deviceid,String eqid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String eq = null;
        try {
            Cursor cursor = db.rawQuery("select handtemp from equipment where eqid =? and deviceid = ?",new String[]{eqid,deviceid});
            if(cursor.moveToFirst()) {
                eq =(cursor.getString(cursor.getColumnIndex("handtemp")));
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return eq;
        }
    }

    /**
     * 查找温控器防冻设置温度缓存值
     * @return
     */
    public String findTempByFangModel(String deviceid,String eqid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String eq = null;
        try {
            Cursor cursor = db.rawQuery("select fangtemp from equipment where eqid =? and deviceid = ?",new String[]{eqid,deviceid});
            if(cursor.moveToFirst()) {
                eq =(cursor.getString(cursor.getColumnIndex("fangtemp")));
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed gateway");
        }finally {
            db.close();
            return eq;
        }
    }
}
