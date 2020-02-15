package me.hekr.sthome.model.modeldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.model.modelbean.TimerGatewayBean;
import me.hekr.sthome.tools.LOG;

/**
 * Created by jishu0001 on 2016/9/1.
 */
public class TimerDAO {
    private final String  TAG = "TimerDAO";
    private SysDB sys;
    Context context;
    public TimerDAO(Context context){

        try {
            this.context = context;
            this.sys = new SysDB(context);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
//add system modle
    public int add(TimerGatewayBean sy){
        int row = -1;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("timerid", sy.getTimerid());
            cv.put("enable", sy.getEnable());
            cv.put("modeid",sy.getModeid());
            cv.put("week",sy.getWeek());
            cv.put("hour",sy.getHour());
            cv.put("min",sy.getMin());
            cv.put("code",sy.getCode());
            cv.put("deviceid",sy.getDeviceid());
            row = (int) db.insert("timertable", null, cv);

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return row;
        }


    }

    /**
     * methodname:findAllTimer
     * 作者：Henry on 2017/5/15 15:24
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:获取定时列表
     */
    public List<TimerGatewayBean> findAllTimer(String deviceid){
        List<TimerGatewayBean> sys2 = new ArrayList<TimerGatewayBean>();
        SQLiteDatabase db = this.sys.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("select a.*,b.name from timertable a left join sysmodle b on a.modeid = b.sid where a.deviceid = '"+deviceid+"' and b.deviceid = '"+deviceid+"' order by a.timerid",null);
            while (cursor.moveToNext()){
                TimerGatewayBean sb = new TimerGatewayBean();
                sb.setTimerid(cursor.getString(cursor.getColumnIndex("timerid")));
                sb.setEnable(cursor.getInt(cursor.getColumnIndex("enable")));
                sb.setModeid(cursor.getString(cursor.getColumnIndex("modeid")));
                sb.setWeek(cursor.getString(cursor.getColumnIndex("week")));
                sb.setHour(cursor.getString(cursor.getColumnIndex("hour")));
                sb.setMin(cursor.getString(cursor.getColumnIndex("min")));
                sb.setCode(cursor.getString(cursor.getColumnIndex("code")));
                sb.setModename(cursor.getString(cursor.getColumnIndex("name")));
                sys2.add(sb);
            }

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }

    }


    /**
     * methodname:findAllTimerOrderByTime
     * 作者：Henry on 2017/5/15 15:24
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:获取定时列表(按照时间顺序)
     */
    public List<TimerGatewayBean> findAllTimerOrderByTime(String deviceid){
        List<TimerGatewayBean> sys2 = new ArrayList<TimerGatewayBean>();
        SQLiteDatabase db = this.sys.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("select a.*,b.name from timertable a left join sysmodle b on a.modeid = b.sid where a.deviceid = '"+deviceid+"' and b.deviceid = '"+deviceid+"' order by a.hour,a.min",null);
            while (cursor.moveToNext()){
                TimerGatewayBean sb = new TimerGatewayBean();
                sb.setTimerid(cursor.getString(cursor.getColumnIndex("timerid")));
                sb.setEnable(cursor.getInt(cursor.getColumnIndex("enable")));
                sb.setModeid(cursor.getString(cursor.getColumnIndex("modeid")));
                sb.setWeek(cursor.getString(cursor.getColumnIndex("week")));
                sb.setHour(cursor.getString(cursor.getColumnIndex("hour")));
                sb.setMin(cursor.getString(cursor.getColumnIndex("min")));
                sb.setCode(cursor.getString(cursor.getColumnIndex("code")));
                sb.setModename(cursor.getString(cursor.getColumnIndex("name")));
                sb.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                sys2.add(sb);
            }

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }

    }

    /**
     * methodname:findAllTimer
     * 作者：Henry on 2017/5/15 15:24
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:获取定时列表
     */
    public List<TimerGatewayBean> findAllTimerByTid(String tid, String deviceid){
        List<TimerGatewayBean> sys2 = new ArrayList<TimerGatewayBean>();
        SQLiteDatabase db = this.sys.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("select * from timertable where deviceid = '"+deviceid+"' and timerid = '"+tid+"' order by timerid",null);
            while (cursor.moveToNext()){
                TimerGatewayBean sb = new TimerGatewayBean();
                sb.setTimerid(cursor.getString(cursor.getColumnIndex("timerid")));
                sb.setEnable(cursor.getInt(cursor.getColumnIndex("enable")));
                sb.setModeid(cursor.getString(cursor.getColumnIndex("modeid")));
                sb.setWeek(cursor.getString(cursor.getColumnIndex("week")));
                sb.setHour(cursor.getString(cursor.getColumnIndex("hour")));
                sb.setMin(cursor.getString(cursor.getColumnIndex("min")));
                sb.setCode(cursor.getString(cursor.getColumnIndex("code")));
                sys2.add(sb);
            }

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }

    }


    /**
     * methodname:findAllTimerByTime
     * 作者：Henry on 2017/6/3 14:32
     * 邮箱：xuejunju_4595@qq.com
     * 参数:hour,min
     * 返回:获取特定时间的定时星期数据
     */
    public List<String> findAllTimerByTime(String hour,String min,String deviceid){
        List<String> sys2 = new ArrayList<String>();
        SQLiteDatabase db = this.sys.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("select * from timertable where deviceid = '"+deviceid+"' and hour = '"+hour+"' and min = '"+min+"' order by timerid",null);
            while (cursor.moveToNext()){
                sys2.add(cursor.getString(cursor.getColumnIndex("week")));
            }

        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return sys2;
        }

    }

    /**
     * methodname: findByTid
     * 作者：Henry on 2017/5/16 9:08
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     */
    public TimerGatewayBean findByTid(String tid, String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        TimerGatewayBean sb = new TimerGatewayBean();
        try {
            Cursor cursor = db.rawQuery("select * from timertable where timerid =? and deviceid = ?",new String[]{tid,deviceid});
            if(cursor.moveToFirst()) {
                sb.setTimerid(cursor.getString(cursor.getColumnIndex("timerid")));
                sb.setEnable(cursor.getInt(cursor.getColumnIndex("enable")));
                sb.setModeid(cursor.getString(cursor.getColumnIndex("modeid")));
                sb.setWeek(cursor.getString(cursor.getColumnIndex("week")));
                sb.setHour(cursor.getString(cursor.getColumnIndex("hour")));
                sb.setMin(cursor.getString(cursor.getColumnIndex("min")));
                sb.setCode(cursor.getString(cursor.getColumnIndex("code")));
                sb.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            }
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed gateway");
        }finally {
            db.close();
            return sb;
        }
    }

    /**
     * isHasTimer:判断是否有该tid的定时
     * 作者：Henry on 2017/3/7 13:02
     * 邮箱：xuejunju_4595@qq.com
     * 参数:sid
     * 返回:boolean
     */
    public boolean isHasTimer(String tid,String deviceid) {

        List<TimerGatewayBean> list = findAllTimerByTid(tid,deviceid);
        if (list == null || list.size() == 0) {
            return false;
        }else
            return true;
    }

    /**
     * 更新Timer到数据库
     * @param modelBean
     * @return
     */
    public long insertTimer(TimerGatewayBean modelBean) {

        if(modelBean == null || TextUtils.isEmpty(modelBean.getTimerid())) {
            return -1L;
        }
        SQLiteDatabase db = this.sys.getWritableDatabase();
        ContentValues values = null;
        try {

            values = new ContentValues();
            values.put("timerid", modelBean.getTimerid());
            values.put("enable", modelBean.getEnable());
            values.put("modeid",modelBean.getModeid());
            values.put("week",modelBean.getWeek());
            values.put("hour",modelBean.getHour());
            values.put("min",modelBean.getMin());
            values.put("code",modelBean.getCode());
            values.put("deviceid",modelBean.getDeviceid());
            if(!isHasTimer(modelBean.getTimerid(),modelBean.getDeviceid())) {
                db = this.sys.getWritableDatabase();
                return db.insert("timertable", null, values);
            } else {
                db = this.sys.getWritableDatabase();
                return db.update("timertable",values , "timerid ='" + modelBean.getTimerid()+"' and deviceid = '"+modelBean.getDeviceid()+"'",null);
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
     * @param tid
     */
    public void delete(String tid,String deviceid)
    {

        SQLiteDatabase db =this.sys.getWritableDatabase();
        try {
            String where = "timerid = ? and deviceid = ?";
            String[] whereValue ={ tid,deviceid };
            db.delete("timertable", where, whereValue);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
        }


    }


    //修改定时器使能
    public void updateEnable(TimerGatewayBean beana)
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "timerid = ? and deviceid = ?";
            String[] whereValue = {beana.getTimerid(),beana.getDeviceid()};
            ContentValues cv = new ContentValues();
            cv.put("enable",beana.getEnable());
            db.update("timertable", cv, where, whereValue);
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed gateway");
        }finally {
            db.close();
        }
    }


    /**
     * @return sid List
     */
    public List<String> findAllTimerTid(String deviceid){
        SQLiteDatabase db = this.sys.getWritableDatabase();
        List<String> list = new ArrayList<String>();
        try {
            Cursor cursor = db.rawQuery("select timerid from timertable where deviceid = '"+deviceid+"' and timerid is not null order by timerid,id",null);
            while (cursor.moveToNext()){
                list.add( cursor.getString(cursor.getColumnIndex("timerid")));
            }
        }catch (NullPointerException e){
            LOG.I(TAG,"no choosed device");
        }finally {
            db.close();
            return list;
        }



    }

}
