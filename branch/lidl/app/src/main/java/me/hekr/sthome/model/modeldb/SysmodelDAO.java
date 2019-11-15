package me.hekr.sthome.model.modeldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.tools.LOG;

/**
 * Created by jishu0001 on 2016/9/1.
 */
public class SysmodelDAO {
    private final String  TAG = "SysmodelDAO";
    private SysDB sys;
    Context context;
    public SysmodelDAO(Context context){
        try {
            this.context = context;
            this.sys = new SysDB(context);
        }catch (NullPointerException e){
            e.printStackTrace();
        }


    }
//add system modle
    public int add(SysModelBean sy){
        int row = -1;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("color",sy.getColor());
            cv.put("name", sy.getModleName());
            cv.put("choice",sy.getChice());
            cv.put("deviceid",sy.getDeviceid());
            cv.put("sid",sy.getSid());
            row = (int) db.insert("sysmodle", null, cv);

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return row;
        }


    }

    /**
     * methodname:addinit
     * 作者：Henry on 2017/3/8 9:56
     * 邮箱：xuejunju_4595@qq.com
     * 参数:sy
     * 返回:int
     * 用来初始化系统组情景组数据(在家，离家，睡眠)
     */
    public int addinit (SysModelBean sy){
        int row = -1;
        int count =  findInitSysModelCount(sy);

        if(count > 0){
            return row;
        }

        SQLiteDatabase db = this.sys.getWritableDatabase();
    /* ContentValues */
            ContentValues cv = new ContentValues();
            cv.put("name", sy.getModleName());
            cv.put("choice",sy.getChice());
            cv.put("deviceid",sy.getDeviceid());
            cv.put("sid",sy.getSid());
            cv.put("color",sy.getColor());
            row = (int) db.insert("sysmodle", null, cv);

            db.close();
            return row;


    }


    //find by Choice
    public SysModelBean findIdByChoice(String deviceid){

        SQLiteDatabase db = this.sys.getWritableDatabase();
        SysModelBean sys2 = new SysModelBean();
        try {
            Cursor cursor = db.rawQuery("select * from sysmodle where choice =? and deviceid = ?",new String[]{"Y",deviceid});
            if(cursor.moveToFirst()) {
                sys2.setModleId(cursor.getInt(cursor.getColumnIndex("id")));
                sys2.setModleName(cursor.getString(cursor.getColumnIndex("name")));
                sys2.setSid(cursor.getString(cursor.getColumnIndex("sid")));
                sys2.setChice(cursor.getString(cursor.getColumnIndex("choice")));
                sys2.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                sys2.setColor(cursor.getString(cursor.getColumnIndex("color")));
            }
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }

    }

    //find by name
    public SysModelBean findIdByName(String name, String deviceid){

        SQLiteDatabase db = this.sys.getWritableDatabase();
        SysModelBean sys2 = new SysModelBean();
        try {
            Cursor cursor = db.rawQuery("select * from sysmodle where name =? and deviceid = ?",new String[]{name,deviceid});
            if(cursor.moveToFirst()) {
                sys2.setModleId(cursor.getInt(cursor.getColumnIndex("id")));
                sys2.setModleName(cursor.getString(cursor.getColumnIndex("name")));
                sys2.setSid(cursor.getString(cursor.getColumnIndex("sid")));
                sys2.setChice(cursor.getString(cursor.getColumnIndex("choice")));
                sys2.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                sys2.setColor(cursor.getString(cursor.getColumnIndex("color")));
            }
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }

    }



    //find by name
    public SysModelBean findById(String id, String deviceid){
        SysModelBean sys2 = new SysModelBean();
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        Cursor cursor = db.rawQuery("select * from sysmodle where id =? and deviceid = ?",new String[]{id,deviceid});
        if(cursor.moveToFirst()) {
            sys2.setModleId(cursor.getInt(cursor.getColumnIndex("id")));
            sys2.setModleName(cursor.getString(cursor.getColumnIndex("name")));
            sys2.setSid(cursor.getString(cursor.getColumnIndex("sid")));
            sys2.setChice(cursor.getString(cursor.getColumnIndex("choice")));
            sys2.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            sys2.setColor(cursor.getString(cursor.getColumnIndex("color")));
        }

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }
    }

    //find by name
    public SysModelBean findBySid(String sid, String deviceid){
        SysModelBean sys2 = new SysModelBean();
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from sysmodle where sid =? and deviceid = ?",new String[]{sid,deviceid});
            if(cursor.moveToFirst()) {
                sys2.setModleId(cursor.getInt(cursor.getColumnIndex("id")));
                sys2.setModleName(cursor.getString(cursor.getColumnIndex("name")));
                sys2.setSid(cursor.getString(cursor.getColumnIndex("sid")));
                sys2.setChice(cursor.getString(cursor.getColumnIndex("choice")));
                sys2.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                sys2.setColor(cursor.getString(cursor.getColumnIndex("color")));
            }

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }
    }

    //find all sysmodle
    public List<SysModelBean> findAllSys(String deviceid){
        List<SysModelBean> sys2 = new ArrayList<SysModelBean>();
        SQLiteDatabase db = this.sys.getWritableDatabase();

        try {
          Cursor cursor = db.rawQuery("select * from sysmodle where deviceid = '"+deviceid+"' order by sid",null);
          while (cursor.moveToNext()){
            SysModelBean sb = new SysModelBean();
            sb.setModleId(cursor.getInt(cursor.getColumnIndex("id")));
            sb.setModleName(cursor.getString(cursor.getColumnIndex("name")));
            sb.setSid(cursor.getString(cursor.getColumnIndex("sid")));
            sb.setChice(cursor.getString(cursor.getColumnIndex("choice")));
            sb.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            sb.setColor(cursor.getString(cursor.getColumnIndex("color")));
            sys2.add(sb);
        }

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }

    }

    //find all sysmodle
    public Map<String,String> findAllSysByHash(String deviceid){
        Map<String,String> sys2 = new HashMap<String,String>();
        SQLiteDatabase db = this.sys.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("select * from sysmodle where deviceid = '"+deviceid+"' order by sid",null);
            while (cursor.moveToNext()){
                sys2.put(cursor.getString(cursor.getColumnIndex("sid")),cursor.getString(cursor.getColumnIndex("name")));
            }

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }

    }

    //find all sysmodle
    public List<String> findAllSysName(String deviceid){
        List<String> sys2 = new ArrayList<String>();
        SQLiteDatabase db = this.sys.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("select * from sysmodle where deviceid = '"+deviceid+"' order by sid",null);
            while (cursor.moveToNext()){
                sys2.add(cursor.getString(cursor.getColumnIndex("name")));
            }

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }

    }

    //修改系统组名称操作
    public void updateName(int sid,String name,String deviceid) {
        SQLiteDatabase db =this.sys.getWritableDatabase();
        try {
            String where = "sid = ? and deviceid = ?";
            String[] whereValue = {String.valueOf(sid),deviceid};
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            db.update("sysmodle", cv, where, whereValue);
            LOG.I("update sysmodle over", "data " + name);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
        }

    }
    //修改颜色操作
    public void updateColor(String sid, String color,String deviceid) {
        SQLiteDatabase db =this.sys.getWritableDatabase();
        try {
            String where = "sid = ? and deviceid = ?";
            String[] whereValue = {String.valueOf(sid),deviceid};
            ContentValues cv = new ContentValues();
            cv.put("color", color);
            db.update("sysmodle", cv, where, whereValue);
            LOG.I("update sysmodle over", "data " + color);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
        }

    }


    /**
     * updateChoice:选择该情景组为当前模式
     * 作者：Henry on 2017/3/8 10:26
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     */
    public void updateChoice(String sid,String deviceid){


        SQLiteDatabase db =this.sys.getWritableDatabase();
        try {
            String where = "choice = ? and deviceid = ?";
            String[] whereValue = {"Y",deviceid};
            ContentValues cv = new ContentValues();
            cv.put("choice","N");
            db.update("sysmodle", cv, where, whereValue);

            String where2 = "sid = ? and deviceid = ?";
            String[] whereValue2 = {sid,deviceid};
            ContentValues cv2 = new ContentValues();
            cv2.put("choice","Y");
            db.update("sysmodle", cv2, where2, whereValue2);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
        }

    }

    /**
     * @param sid
     */
    public void delete(String sid,String deviceid)
    {

        SQLiteDatabase db =this.sys.getWritableDatabase();
        try {
            String where = "sid = ? and deviceid = ?";
            String[] whereValue ={ sid,deviceid };
            db.delete("sysmodle", where, whereValue);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
        }


    }

    /**
     * 删除所有场景组
     */
    public void deleteAll(String deviceid)
    {

        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {

            String where = "deviceid = '"+deviceid+"'";
            db.delete("sysmodle", where, null);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
        }

    }

    /**
     * @return sid List
     */
    public List<String> findAllSysmodelSid(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<String> list = new ArrayList<String>();
        try {
            Cursor cursor = db.rawQuery("select sid from sysmodle where deviceid = '"+deviceid+"' and sid is not null group by sid order by sid,id",null);
            while (cursor.moveToNext()){
                list.add( cursor.getString(cursor.getColumnIndex("sid")));
            }
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return list;
        }



    }


    /**
     * 更新情景组到数据库
     * @param modelBean
     * @return
     */
    public long insertSysmodel(SysModelBean modelBean) {

        if(modelBean == null || TextUtils.isEmpty(modelBean.getSid())) {
            return -1L;
        }
        SQLiteDatabase db = this.sys.getWritableDatabase();
        ContentValues values = null;
        try {

            values = new ContentValues();
            values.put("name", modelBean.getModleName());
            values.put("sid", modelBean.getSid());
            values.put("deviceid",modelBean.getDeviceid());
            values.put("color",modelBean.getColor());
            if(!isHasSysmodel(modelBean.getSid(),modelBean.getDeviceid())) {
                values.put("choice",modelBean.getChice());
                db = this.sys.getWritableDatabase();
                return db.insert("sysmodle", null, values);
            } else {
                db = this.sys.getWritableDatabase();
                return db.update("sysmodle",values , "sid ='" + modelBean.getSid()+"' and deviceid = '"+modelBean.getDeviceid()+"'",null);
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

    /**
     * isHasSysmodel:判断是否有该sid的情景组
     * 作者：Henry on 2017/3/7 13:02
     * 邮箱：xuejunju_4595@qq.com
     * 参数:sid
     * 返回:boolean
     */
    public boolean isHasSysmodel(String sid,String deviceid) {

        List<SysModelBean> list = findAllBeanSysmodelSid(sid,deviceid);
        if (list == null || list.size() == 0) {
            return false;
        }else
            return true;
    }
    /**
     * @return sid List
     */
    public List<SysModelBean> findAllBeanSysmodelSid(String sid, String deviceid){
        List<SysModelBean> list = new ArrayList<SysModelBean>();
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        Cursor cursor = db.rawQuery("select * from sysmodle where deviceid = '"+deviceid+"' and sid = '"+sid+"' group by sid order by id,sid",null);
        while (cursor.moveToNext()){
            SysModelBean sb = new SysModelBean();
            sb.setModleId(cursor.getInt(cursor.getColumnIndex("id")));
            sb.setModleName(cursor.getString(cursor.getColumnIndex("name")));
            sb.setSid(cursor.getString(cursor.getColumnIndex("sid")));
            sb.setChice(cursor.getString(cursor.getColumnIndex("choice")));
            sb.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            sb.setColor(cursor.getString(cursor.getColumnIndex("color")));
            list.add(sb);
        }


        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return list;
        }
    }


    /**
     * @return 所有的情景组数量
     */
    public int findSysModelCount(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        int a = 0;
        try {
            Cursor cursor = db.rawQuery("select count(id) from sysmodle where deviceid = '"+deviceid+"'",null);
            if(cursor.moveToFirst()) {
                a = cursor.getInt(cursor.getColumnIndex("count(id)"));
            }
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return a;
        }

    }

    /**
     * @return 所有的情景组数量
     */
    public int findInitSysModelCount(SysModelBean bean){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        int a = 0;
        try {
            db = this.sys.getWritableDatabase();
            Cursor cursor = db.rawQuery("select count(id) from sysmodle where deviceid = '"+bean.getDeviceid()+"' and sid = '"+bean.getSid()+"' ",null);
            if(cursor.moveToFirst()) {
                a = cursor.getInt(cursor.getColumnIndex("count(id)"));
            }
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return a;
        }

    }
}
