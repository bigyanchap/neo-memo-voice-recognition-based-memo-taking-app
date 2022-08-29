package com.iox_prime.neomemo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Bigyan Chapagain on 5/20/2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static String dbName="memoDb";
    static int version=1;
    public String createTableSql="CREATE TABLE if not exists `user` (\n" +
            "\t`id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
            "\t`title`\ttext,\n" +
            "\t`memo`\ttext\n" +
            ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        getWritableDatabase().execSQL(createTableSql);
    }

    public DatabaseHelper(Context context) {
        super(context, dbName, null, version);
        getWritableDatabase().execSQL(createTableSql);
    }
    //insert
    public void insertUserInfo(ContentValues cv){
        getWritableDatabase().insert("user","",cv);
    }
    //update
    public void updateUserInfo(ContentValues cv, int id) {
        getWritableDatabase().update("user", cv, "id=" + id, null);
    }

    //delete
    public void deleteUserInfo(String id) {
        getWritableDatabase().delete("user", "id=" + id, null);
    }
    public ArrayList<UserInfo> getUserList() {
        ArrayList<UserInfo> list = new ArrayList<>();
        String sql = "Select * from user";
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        //rawQuery() returns cursor
        while (c.moveToNext()) {
            UserInfo info = new UserInfo();
            info.id = c.getInt(c.getColumnIndex("id"));
            info.title = c.getString(c.getColumnIndex("title"));
            info.memo = c.getString(c.getColumnIndex("memo"));
            list.add(info);//everything is added to this list now
        }
        c.close();
        return list;
    }
    public UserInfo getUserInfo(String id) {
        String sql = "Select * from user where id=" + id;
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        //rawQuery() returns cursor
        UserInfo info = new UserInfo();
        while (c.moveToNext()) {
            info.id = c.getInt(c.getColumnIndex("id"));
            info.title = c.getString(c.getColumnIndex("title"));
            info.memo = c.getString(c.getColumnIndex("memo"));
        }
        c.close();
        return info;
    }
}
