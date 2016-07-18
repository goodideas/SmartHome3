package com.zuobiao.smarthome.smarthome3.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhuangbinbin
 * on 2016/1/5.
 */
public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBconstant.CREATEE_QUIPMENT_DB_SQL);
        db.execSQL(DBconstant.DB_CREATEE_SCENE_SQL);
        db.execSQL(DBconstant.DB_CREATEE_SCENE_NAME_SQL);
        db.execSQL(DBconstant.DB_ALTER_SCENE_SQL);
        db.execSQL(DBconstant.DB_CREATEE_SCENE_ALL_SQL);
        db.execSQL(DBconstant.DB_CREATEE_RFID_INFO_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion){
                case 1:
                    db.execSQL(DBconstant.DB_CREATEE_SCENE_SQL);
                case 2:
                    db.execSQL(DBconstant.DB_CREATEE_SCENE_NAME_SQL);
                case 3:
                    db.execSQL(DBconstant.DB_ALTER_SCENE_SQL);
                case 4:
                    db.execSQL(DBconstant.DB_CREATEE_SCENE_ALL_SQL);
                case 5:
                    db.execSQL(DBconstant.DB_CREATEE_RFID_INFO_SQL);
                default:
            }
    }




}
