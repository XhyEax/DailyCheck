package com.xhy.dailycheck.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.xhy.dailycheck.bean.Record;

import java.util.ArrayList;

public class SQLiteManager {

    static String TABLE_NAME = "record";
    static SQLiteDatabase DB;

    //初始化
    public static SQLiteDatabase initDB(Context ctx) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ctx, "my_db", null, 1);
        DB = databaseHelper.getWritableDatabase();
        return DB;
    }

    //插入数据
    public static long insert(Record record) {
        ContentValues values = new ContentValues();
        values.put("timestamp", record.getTimestamp());
        return DB.insert(TABLE_NAME, null, values);
    }

    //根据时间戳 降序排列
    public static ArrayList<Record> queryRecordList(ArrayList<Record> list) {
        list.clear();
        Cursor cursor = DB.query(TABLE_NAME, new String[]{"timestamp"}, null, null, null, null, "timestamp desc");
        while (cursor.moveToNext()) {
            Long timestamp  = cursor.getLong(cursor.getColumnIndex("timestamp"));
            Record record = new Record(timestamp);
            list.add(record);
        }
        return list;
    }

}
