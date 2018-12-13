package me.hekr.sthome.model.modeldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.push.NoticeBean;

/**
 * Created by jishu0001 on 2017/3/1.
 */

public class NoticeDAO  {
    private static final String TAG = NoticeDAO.class.getName();
    private SysDB sys;
    private Context context;
    public NoticeDAO(Context context){

        try {
            this.context = context;
            this.sys = new SysDB(context);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     *increace a message
     * @param nb
     * @return
     */
    public int addNotice(NoticeBean nb) {
        SQLiteDatabase db = this.sys.getWritableDatabase();
    /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put("type", nb.getType());
        cv.put("mid", nb.getMid());
//        cv.put("activitytime",Integer.toString(eq.getActivityTime()));
        cv.put("eqid", nb.getEqid());
        cv.put("equipmenttype", nb.getEquipmenttype());
        cv.put("eqstatus", nb.getEqstatus());
        cv.put("activitytime", nb.getActivitytime());
        cv.put("desc", nb.getDesc());
        cv.put("deviceid",nb.getDeviceid());
        int row = (int) db.insert("noticetable", null, cv);
        Log.i(TAG,"add finished ");
        db.close();
        return row;
    }



    /**
     * 插入历史告警列表到数据库
     *
     * @param noticeBeanList
     * @return
     */
    public ArrayList<Long> insertNoticeList(List<NoticeBean> noticeBeanList) {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        ArrayList<Long> rows = new ArrayList<Long>();
        try {

            db.beginTransaction();
            for (NoticeBean c : noticeBeanList) {
                long rowId = insertNotice(c,db);
                if (rowId != -1L) {
                    rows.add(rowId);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return rows;
    }

    /**
     * 查找所有报警信息
     * @return
     */
    public List<NoticeBean> findAllNotice(){
        List<NoticeBean> list = new ArrayList<NoticeBean>();
        NoticeBean nb = null;
        SQLiteDatabase db = this.sys.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from noticetable order by id ",null);
        while (cursor.moveToNext()){
            nb = new NoticeBean();
            nb.setType(cursor.getString(cursor.getColumnIndex("type")));
            nb.setMid(cursor.getString(cursor.getColumnIndex("mid")));
            nb.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
            nb.setEquipmenttype(cursor.getString(cursor.getColumnIndex("equipmenttype")));
            nb.setEqstatus(cursor.getString(cursor.getColumnIndex("eqstatus")));
            nb.setActivitytime(cursor.getString(cursor.getColumnIndex("activitytime")));
            nb.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
            nb.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
            nb.setName(cursor.getString(cursor.getColumnIndex("name")));
            list.add(nb);
        }
        db.close();
        return list;
    }

    //判断是否有该设备
    public boolean isHasNotice(String id,SQLiteDatabase db){

        NoticeBean deviceBean = findNoticeById(id,db);
        try {
            if(deviceBean == null){
                return false;
            }else{
                return true;
            }

        }catch (NullPointerException e){
            return false;
        }

    }

    public long insertNotice(NoticeBean noticeBean, SQLiteDatabase db) {

        long result = -1;
        if (noticeBean == null || TextUtils.isEmpty(noticeBean.getDeviceid())) {
            return -1;
        }
        try {
            ContentValues values = new ContentValues();

            values.put("name", noticeBean.getName());
            values.put("deviceid",noticeBean.getDeviceid());
            values.put("desc",noticeBean.getDesc());
            values.put("activitytime",noticeBean.getActivitytime());
            values.put("type",noticeBean.getType());
            values.put("eqid",noticeBean.getEqid());
            values.put("equipmenttype",noticeBean.getEquipmenttype());
            values.put("eqstatus",noticeBean.getEqstatus());
            values.put("mid",noticeBean.getMid());
            if (!isHasNotice(noticeBean.getDesc(),db)) {
                result = db.insert("noticetable", null, values);
            }else{
                result = db.update("noticetable", values, "deviceid = '" + noticeBean.getDeviceid() + "' and desc = '"+noticeBean.getDesc()+"' ", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return result;
        }

    }


    /**
     * @return DeviceBean
     */
    public NoticeBean findNoticeById(String id, SQLiteDatabase db){
        NoticeBean noticeBean = null;
        try {

            Cursor cursor = db.rawQuery("select * from noticetable where desc = '"+id+"'",null);
            if(cursor.moveToFirst()) {
                noticeBean = new NoticeBean();
                noticeBean.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                noticeBean.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
                noticeBean.setActivitytime(cursor.getString(cursor.getColumnIndex("activitytime")));
                noticeBean.setType(cursor.getString(cursor.getColumnIndex("type")));
                noticeBean.setEqid(cursor.getString(cursor.getColumnIndex("eqid")));
                noticeBean.setEquipmenttype(cursor.getString(cursor.getColumnIndex("equipmenttype")));
                noticeBean.setEqstatus(cursor.getString(cursor.getColumnIndex("eqstatus")));
                noticeBean.setMid(cursor.getString(cursor.getColumnIndex("mid")));
                noticeBean.setName(cursor.getString(cursor.getColumnIndex("name")));
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no folder");
        }finally {
            return noticeBean;
        }
    }


    /**
     * @param
     */
    public void deleteAllNotice()
    {
        SQLiteDatabase db = this.sys.getWritableDatabase();
        try {
            String where = "1 = 1";
            int row = db.delete("noticetable", where, null);
        }catch (NullPointerException e){
            e.printStackTrace();
        }finally {
            db.close();
        }
    }

}
