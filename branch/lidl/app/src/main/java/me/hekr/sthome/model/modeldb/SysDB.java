package me.hekr.sthome.model.modeldb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.hekr.sthome.common.CCPAppManager;

/**
 * Created by jishu0001 on 2016/8/19.
 */
public class SysDB extends SQLiteOpenHelper {
    //    创建表格
    private String sysMTable = "create table if not exists sysmodle(id integer primary key autoincrement,name varchar(200) NOT NULL,modledesc varchar(200),choice varchar(20),sid varchar(20),deviceid varchar(30),color varchar(10))";
    private String equTalbe = "create table if not exists equipment(id integer primary key AUTOINCREMENT,name varchar(200) NOT NULL, eqid varchar(20),equipmenttype varchar(20),activitytime varchar(100) ,state varchar(20),equipmentdesc varchar(200),packageid integer default (0),namecum varchar(10),statuscum varchar(10),sort integer,deviceid varchar(30),autotemp varchar(10),handtemp varchar(10),fangtemp varchar(10))";
    private String pacTable = "create table if not exists pactable(id integer primary key autoincrement,name varchar(200) not null,packageid integer,desc varchar(200),sort integer,deviceid varchar(30))";
    private String scTable = "create table if not exists scenetable(id integer primary key autoincrement,name varchar(200) not null,code varchar(100),desc varchar(100),sid varchar(40),mid varchar(40),checksum varchar(40),deviceid varchar(30))";
    private String noticeTable = "create table if not exists noticetable(id integer primary key autoincrement,type varchar(5),mid varchar(40),eqid varchar(20),equipmenttype varchar(10),eqstatus varchar(10),activitytime var(40),desc varchar(100),deviceid varchar(30),name varchar(150))";
    private String deviceTable = "create table if not exists devicetable(id integer primary key autoincrement,deviceid varchar(30),bindkey varchar(100),ctrlkey varchar(100),devicename varchar(100),choice integer,status varchar(10),propubkey varchar(100),domain varchar(50),longtitude varchar(50),latitude varchar(50),reserve varchar(100))";
    private String timerTable = "create table if not exists timertable(id integer primary key autoincrement,timerid varchar(5),enable integer,modeid varchar(5),week varchar(5),hour varchar(5),min varchar(5),code varchar(20),deviceid varchar(30))";
    private String modeTable = "create table if not exists modetable(sid varchar(40),eqid varchar(20),deviceid varchar(30),content varchar(10),delay integer default (0),primary key(sid,eqid,deviceid,content))";
    //  防止重复
    private String sysmodletable ="drop table if exists sysmodle";
    private String actmodletable ="drop table if exists actmodle";
    private String equiptable ="drop table if exists equipment";
    private String activitytable ="drop table if exists actable";
    private String packageTable = "drop table if exists pactable";
    private String sceneTable = "drop table if exists scenetable";
    private String dcTable = "drop table if exists devicetable";
    private String noTable = "drop table if exists noticetable";
    private String droptimerTable = "drop table if exists timertable";

    private String eqaddcolumn = "alter table  equipment add column sort integer";
    private String eqmodelcolumn = "alter table equipment modify (packageid integer default 0)";

    private String sysMTableaddcolumn = "alter table sysmodle add column deviceid varchar(30)";
    private String equTableaddcolumn = "alter table equipment add column deviceid varchar(30)";
    private String scTableaddcolumn = "alter table scenetable add column deviceid varchar(30)";
    private String deviceTableaddcolumn1 = "alter table devicetable add  column domain varchar(50)";
    private String deviceTableaddcolumn2 = "alter table devicetable add  column longtitude varchar(50)";
    private String deviceTableaddcolumn3 = "alter table devicetable add  column latitude varchar(50)";
    private String deviceTableaddcolumn4 = "alter table devicetable add  column reserve varchar(100)";
    private String eqTableaddcolumn2 = "alter table equipment add  column autotemp varchar(10)";
    private String eqTableaddcolumn3 = "alter table equipment add  column handtemp varchar(10)";
    private String eqTableaddcolumn4 = "alter table equipment add  column fangtemp varchar(10)";


    public SysDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SysDB (Context context){
        super(context, CCPAppManager.getUserId()+"_sysdb.db",null,13);
//        this(context,"sysdb.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sysMTable);
        db.execSQL(equTalbe);
        db.execSQL(pacTable);
        db.execSQL(scTable);
        db.execSQL(noticeTable);
        db.execSQL(deviceTable);
        db.execSQL(timerTable);
        db.execSQL(modeTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(sysmodletable);
//        db.execSQL(actmodletable);
//        db.execSQL(equiptable);
//        db.execSQL(activitytable);
//        db.execSQL(packageTable);
//        db.execSQL(dcTable);
//        db.execSQL(sceneTable);
//        db.execSQL(noTable);
//        db.execSQL(droptimerTable);
        if(oldVersion == 10){
            db.execSQL(modeTable);
        }

        if(oldVersion == 11 || oldVersion == 10){
            db.execSQL(deviceTableaddcolumn1);
            db.execSQL(deviceTableaddcolumn2);
            db.execSQL(deviceTableaddcolumn3);
            db.execSQL(deviceTableaddcolumn4);
        }

        if(oldVersion<13){
            db.execSQL(eqTableaddcolumn2);
            db.execSQL(eqTableaddcolumn3);
            db.execSQL(eqTableaddcolumn4);
        }

//        if (newVersion > oldVersion) {
//            if(oldVersion<=2){
//                db.execSQL(equTableaddcolumn);
//                db.beginTransaction();
//             /*
//              * 下面我们修改name->newname
//              * 增加age
//              * str是为了为age字段设置默认数据
//              */
//                alterCloumn(
//                        db,
//                        "equipment",
//                        "create table equipment(id integer primary key AUTOINCREMENT,name varchar(200) NOT NULL unique , eqid varchar(20),equipmenttype varchar(20),activitytime varchar(100) ,state varchar(20),equipmentdesc varchar(200),packageid integer default (0),namecum varchar(10),statuscum varchar(10),sort integer,deviceid varchar(30))",
//                        "INSERT INTO equipment SELECT id, name , eqid,equipmenttype,activitytime,state,equipmentdesc,0,namecum,statuscum,eqid,id FROM tempTable");
//
//                db.execSQL(sysMTableaddcolumn);
//                db.execSQL(scTableaddcolumn);
//
//                db.setTransactionSuccessful();
//                db.endTransaction();
//            }
//
//        }
//        onCreate(db);


    }

    // 删除字段或则修改字段的方法
    public void alterCloumn(SQLiteDatabase db, String alterTableName,
                            String create_Table_Sql, String copy_Sql) {

        final String DROP_TEMP_TABLE = "drop table if exists tempTable";
        // 重新命名修改的表
        db.execSQL("alter table " + alterTableName + " rename to tempTable");
        // 重新创建修改的表
        db.execSQL(create_Table_Sql);
        // 将临时表里的数据copy到新的数据库中
        db.execSQL(copy_Sql);
        // 最后删掉临时表
        db.execSQL(DROP_TEMP_TABLE);
        Log.i("update", "--------");
    }



}
