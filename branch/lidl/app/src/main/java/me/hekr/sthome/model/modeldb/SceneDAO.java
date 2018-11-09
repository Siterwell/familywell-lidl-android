package me.hekr.sthome.model.modeldb;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.tools.LOG;


/**
 * Created by xjj on 2016/12/14.
 */
public class SceneDAO {
    private final String TAG = "ScenceDAO";
    private SysDB sys;
    Context context;
    public SceneDAO(Context context){

        try {
            this.context = context;
            this.sys = new SysDB(context);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     * 修改Bymid
     * @param am
     */
    public void updateByMid(SceneBean am){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        String where = "mid = ? and deviceid = ?";
        String[] whereValue = {am.getMid(),am.getDeviceid()};
        ContentValues cv = new ContentValues();
        cv.put("name",am.getName());
        cv.put("code",am.getCode());
        db.update("scenetable", cv, where, whereValue);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }


    /**
     * @param scenebean
     * @return 生成的数量
     */
    public int addScence(SceneBean scenebean)
    {
        int row = 0;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
    /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put("name", scenebean.getName());
        cv.put("code",scenebean.getCode());
        cv.put("sid",scenebean.getSid());
        cv.put("mid",scenebean.getMid());
        cv.put("checksum",scenebean.getChecksum());
        cv.put("deviceid",scenebean.getDeviceid());
        row = (int) db.insert("scenetable", null, cv);

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed gateway");
        }finally {
            db.close();
            return row;
        }
    }

   /*
   @method addinit
   @autor TracyHenry
   @time 2018/8/6 上午10:20
   @email xuejunju_4595@qq.com
   用来初始化默认情景
   */
    public int addinit (SceneBean sy){
        int row = -1;
        int count =  findInitSceneCount(sy.getMid(),sy.getDeviceid(),sy.getSid());

        if(count > 0){
            return row;
        }

        SQLiteDatabase db = this.sys.getWritableDatabase();
    /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put("name", sy.getName());
        cv.put("code",sy.getCode());
        cv.put("sid",sy.getSid());
        cv.put("mid",sy.getMid());
        cv.put("checksum",sy.getChecksum());
        cv.put("deviceid",sy.getDeviceid());
        row = (int) db.insert("scenetable", null, cv);

        db.close();
        return row;


    }

    /**
     * @param sid
     * 根据系统组sid删除响应的情景
     */
    public void deleteBySid(int sid,String deviceid)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        String where = "sid = ? and deviceid = ?";
        String[] whereValue ={ String.valueOf(sid),deviceid };
        db.delete("scenetable", where, whereValue);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    /**
     * @param eq
     */
    public void delete(SceneBean eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        String where = "mid = ? and deviceid = ?";
        String[] whereValue ={ eq.getMid(),eq.getDeviceid()};
        int row = db.delete("scenetable", where, whereValue);
        LOG.I("delete sence list",eq.getMid()+" ============delete"+ row );
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }


    /**
     * @param eq
     */
    public void deleteBySidAndMid(SceneBean eq, String deviceid)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        String where = "mid = ? and sid = ? and deviceid = ?";
        String[] whereValue ={ eq.getMid(), eq.getSid(),deviceid };
        int row = db.delete("scenetable", where, whereValue);
        LOG.I("delete sence list",eq.getMid()+" ============delete"+ row );
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }

    /**
     * @param mid
     */
    public void deleteByMid(String mid,String deviceid)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "mid = ? and deviceid = ?";
            String[] whereValue ={ mid,deviceid};
            db.delete("scenetable", where, whereValue);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed gateway");
        }finally {
            db.close();
        }


    }

    /**
     * @param eq
     */
    public void deleteFromsceneGroup(SceneBean eq)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "mid = ? and sid = ? and deviceid = ?";
            String[] whereValue ={ eq.getMid(),eq.getSid(),eq.getDeviceid()};
            db.delete("scenetable", where, whereValue);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed gateway");
        }finally {
            db.close();
        }

    }

    public void deleteAll(String deviceid)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "deviceid = '"+deviceid+"'";
            db.delete("scenetable", where, null);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed gateway");
        }finally {
            db.close();
        }

    }

    public List<SceneBean> findScenceListBySid(String sid, String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<SceneBean> list = new ArrayList<SceneBean>();
        try {
        String [] whereValue = {sid,deviceid};
        Cursor cursor = db.rawQuery("select * from scenetable where sid = ? and deviceid = ?",whereValue);
        while (cursor.moveToNext()){
            SceneBean eq = new SceneBean();
            eq.setId(cursor.getInt(cursor.getColumnIndex("id")));
            eq.setName(cursor.getString(cursor.getColumnIndex("name")));
            eq.setChecksum(cursor.getString(cursor.getColumnIndex("checksum")));
            eq.setCode(cursor.getString(cursor.getColumnIndex("code")));
            eq.setMid(cursor.getString(cursor.getColumnIndex("mid")));
            eq.setSid(cursor.getString(cursor.getColumnIndex("sid")));
            eq.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            list.add(eq);
        }
        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return list;
        }

    }

    /**
     * 查找在该情景下是否有该情景，要是有则查看长度
     * @param sb
     * @return
     */
    public List<SceneBean> findScenceListBySidAndMid(SceneBean sb){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<SceneBean> list = new ArrayList<SceneBean>();
        try {
        String [] whereValue = {sb.getSid(),sb.getMid(),sb.getDeviceid()};
        Cursor cursor = db.rawQuery("select * from scenetable where sid = ? and mid = ? and deviceid = ?",whereValue);
        while (cursor.moveToNext()){
            SceneBean eq = new SceneBean();
            eq.setId(cursor.getInt(cursor.getColumnIndex("id")));
            eq.setName(cursor.getString(cursor.getColumnIndex("name")));
            eq.setChecksum(cursor.getString(cursor.getColumnIndex("checksum")));
            eq.setCode(cursor.getString(cursor.getColumnIndex("code")));
            eq.setMid(cursor.getString(cursor.getColumnIndex("mid")));
            eq.setSid(cursor.getString(cursor.getColumnIndex("sid")));
            eq.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            list.add(eq);
        }
        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return list;
        }

    }


    /**
     * @param mid
     * @return ScenceBean
     */
    public SceneBean findScenceBymid(String mid, String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        SceneBean eq = null;
        try {
        Cursor cursor = db.rawQuery("select * from scenetable where mid =? and sid = '-1' and deviceid = ?",new String[]{mid,deviceid});
        if(cursor.moveToFirst()) {
            eq = new SceneBean();
            eq.setId(cursor.getInt(cursor.getColumnIndex("id")));
            eq.setName(cursor.getString(cursor.getColumnIndex("name")));
            eq.setChecksum(cursor.getString(cursor.getColumnIndex("checksum")));
            eq.setCode(cursor.getString(cursor.getColumnIndex("code")));
            eq.setMid(cursor.getString(cursor.getColumnIndex("mid")));
            eq.setSid(cursor.getString(cursor.getColumnIndex("sid")));
            eq.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
        }
        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return eq;
        }
    }

    /**
     * @param mid
     * @return ScenceBean
     */
    public SceneBean findScenceBymidByOtherGateway(String mid, String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        SceneBean eq = null;
        try {
            Cursor cursor = db.rawQuery("select * from scenetable where mid =? and sid = '-1' and deviceid = ?",new String[]{mid,deviceid});
            if(cursor.moveToFirst()) {
                eq = new SceneBean();
                eq.setId(cursor.getInt(cursor.getColumnIndex("id")));
                eq.setName(cursor.getString(cursor.getColumnIndex("name")));
                eq.setChecksum(cursor.getString(cursor.getColumnIndex("checksum")));
                eq.setCode(cursor.getString(cursor.getColumnIndex("code")));
                eq.setMid(cursor.getString(cursor.getColumnIndex("mid")));
                eq.setSid(cursor.getString(cursor.getColumnIndex("sid")));
                eq.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            }
        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return eq;
        }
    }

    /**
     * 按照名称进行查找ID
     * @return
     */
    public SceneBean findByName(String name, String deviceid){
        SceneBean eq = null;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        Cursor cursor = db.rawQuery("select * from scenetable where name =? and deviceid = ?",new String[]{name,deviceid});
        if(cursor.moveToFirst()) {
            eq = new SceneBean();
            eq.setId(cursor.getInt(cursor.getColumnIndex("id")));
            eq.setName(cursor.getString(cursor.getColumnIndex("name")));
            eq.setChecksum(cursor.getString(cursor.getColumnIndex("checksum")));
            eq.setCode(cursor.getString(cursor.getColumnIndex("code")));
            eq.setMid(cursor.getString(cursor.getColumnIndex("mid")));
            eq.setSid(cursor.getString(cursor.getColumnIndex("sid")));
            eq.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
        }

        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return eq;
        }
    }

    /**
     * @return 所有的情景数量
     */
    public int findScenceCount(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        int a = 0;
        try {
        Cursor cursor = db.rawQuery("select count(id) from scenetable where sid ='-1' and deviceid= '"+deviceid+"'",null);
        if(cursor.moveToFirst()) {
            a = cursor.getInt(cursor.getColumnIndex("count(id)"));
        }
        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return a;
        }
    }



    /**
     * @return mid List
     */
    public List<String> findAllScenceMid(String deviceid){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
        Cursor cursor = db.rawQuery("select mid from scenetable where deviceid = '"+deviceid+"' and mid is not null group by mid order by id,mid",null);
        while (cursor.moveToNext()){
            list.add( cursor.getString(cursor.getColumnIndex("mid")));
        }
        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return list;
        }
    }

    /**
     * update Mid
     * @param id ,mid
     */
    public void updateScenceMid(int id,String mid,String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        String where = "id = ? and deviceid = ?";
        String[] whereValue = {Integer.toString(id),deviceid};
        ContentValues cv = new ContentValues();
        cv.put("mid",mid);
        db.update("scenetable", cv, where, whereValue);
        db.close();
    }


    /**
     * find all scene from scenetable;
     * @return
     */
    public List<SceneBean> findAllAm(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<SceneBean> list = new ArrayList<SceneBean>();
        try {
        Cursor cursor = db.rawQuery("select * from scenetable where sid = '-1' and deviceid = '"+deviceid+"' and code is not null",null);
        while (cursor.moveToNext()){
            SceneBean am = new SceneBean();
            am.setId(cursor.getInt(cursor.getColumnIndex("id")));
            am.setName(cursor.getString(cursor.getColumnIndex("name")));
            am.setChecksum(cursor.getString(cursor.getColumnIndex("checksum")));
            am.setCode(cursor.getString(cursor.getColumnIndex("code")));
            am.setMid(cursor.getString(cursor.getColumnIndex("mid")));
            am.setSid(cursor.getString(cursor.getColumnIndex("sid")));
            am.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            list.add(am);
        }

        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return list;
        }
    }


    /**
     * find all scene from scenetable;
     * @return
     */
    public List<SceneBean> findAllAmWithoutDefault(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<SceneBean> list = new ArrayList<SceneBean>();
        try {
            Cursor cursor = db.rawQuery("select * from scenetable where sid = '-1' and deviceid = '"+deviceid+"' and code is not null",null);
            while (cursor.moveToNext()){
                String mid = cursor.getString(cursor.getColumnIndex("mid"));
                if(Integer.parseInt(mid)<=128) {
                    SceneBean am = new SceneBean();
                    am.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    am.setName(cursor.getString(cursor.getColumnIndex("name")));
                    am.setChecksum(cursor.getString(cursor.getColumnIndex("checksum")));
                    am.setCode(cursor.getString(cursor.getColumnIndex("code")));
                    am.setMid(cursor.getString(cursor.getColumnIndex("mid")));
                    am.setSid(cursor.getString(cursor.getColumnIndex("sid")));
                    am.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                    list.add(am);
                }
            }

        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return list;
        }
    }

    /**
     * find all scene from scenetable;
     * @return
     */
    public List<SceneBean> findAllAmBySid(String sid, String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<SceneBean> list = new ArrayList<SceneBean>();
        try {
            Cursor cursor = db.rawQuery("select * from scenetable where sid = '"+sid+"' and deviceid = '"+deviceid+"' and code is not null",null);
            while (cursor.moveToNext()){
                String mid = cursor.getString(cursor.getColumnIndex("mid"));
                if(Integer.parseInt(mid)<=128){
                    SceneBean am = new SceneBean();
                    am.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    am.setName(cursor.getString(cursor.getColumnIndex("name")));
                    am.setChecksum(cursor.getString(cursor.getColumnIndex("checksum")));
                    am.setCode(cursor.getString(cursor.getColumnIndex("code")));
                    am.setMid(cursor.getString(cursor.getColumnIndex("mid")));
                    am.setSid(cursor.getString(cursor.getColumnIndex("sid")));
                    am.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                    list.add(am);
                }
            }

        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return list;
        }
    }

    /**
     * find all scene from scenetable;
     * @return
     */
    public List<SceneBean> findAllAmBySidWithDefualt(String sid, String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<SceneBean> list = new ArrayList<SceneBean>();
        try {
            Cursor cursor = db.rawQuery("select * from scenetable where sid = '"+sid+"' and deviceid = '"+deviceid+"' and code is not null",null);
            while (cursor.moveToNext()){
                    SceneBean am = new SceneBean();
                    am.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    am.setName(cursor.getString(cursor.getColumnIndex("name")));
                    am.setChecksum(cursor.getString(cursor.getColumnIndex("checksum")));
                    am.setCode(cursor.getString(cursor.getColumnIndex("code")));
                    am.setMid(cursor.getString(cursor.getColumnIndex("mid")));
                    am.setSid(cursor.getString(cursor.getColumnIndex("sid")));
                    am.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                    list.add(am);
            }

        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return list;
        }
    }

    /**
     * find all scene from scenetable;
     * @return
     */
    public List<String> findAllMid(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<String> list = new ArrayList<String>();
        try {
            Cursor cursor = db.rawQuery("select mid from scenetable where deviceid = '"+deviceid+"' and mid is not null and sid = '-1'  group by mid order by mid,id",null);
            while (cursor.moveToNext()){
                list.add( cursor.getString(cursor.getColumnIndex("mid")));
            }
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return list;
        }
    }


    //find distinct id feom actable and the actmodle id is you want
    public List<Integer> findAllSenceBySysId(String sid,String deviceid){
        List<Integer> in = new ArrayList<Integer>();
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select distinct(mid) from scenetable a1 where a1.sid= ? and a1.deviceid = ?",new String[]{sid,deviceid});
            while (cursor.moveToNext()){
                in.add(new Integer(cursor.getString(cursor.getColumnIndex("mid"))));
            }
        }catch (NullPointerException e){
            LOG.I(TAG, "no choosed device");
        }finally {
            db.close();
            return in;
        }



    }

    /**
     * @return 查找含有相应字符串的情景数量
     */
    public int findScenceByNameCount(String name,String deviceid) {
        int a = 0;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select count(id) from scenetable where deviceid = '"+deviceid+"' and name ='" + name + "' and code is not null", null);
            if (cursor.moveToFirst()) {
                a = cursor.getInt(cursor.getColumnIndex("count(id)"));
            }
        } catch (NullPointerException e) {
            LOG.I(TAG, "no choosed device");
        } finally {
            db.close();
            return a;

        }
    }


    /**
     * @return 查找含有相应字符串的情景数量,出去相应的mid
     */
    public int findScenceByNameCountWithMid(String name,String deviceid,String mid) {
        int a = 0;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select count(id) from scenetable where deviceid = '"+deviceid+"' and name ='" + name + "' and code is not null and mid !='"+mid+"'", null);
            if (cursor.moveToFirst()) {
                a = cursor.getInt(cursor.getColumnIndex("count(id)"));
            }
        } catch (NullPointerException e) {
            LOG.I(TAG, "no choosed device");
        } finally {
            db.close();
            return a;

        }
    }

    /**
     * @return 所有的情景组数量
     */
    public int findInitSceneCount(String mid,String deviceid,String sid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        int a = 0;
        try {
            db = this.sys.getWritableDatabase();
            Cursor cursor = db.rawQuery("select count(id) from scenetable where deviceid = '"+deviceid+"' and mid = '"+mid+"' and sid = '"+sid+"'",null);
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
