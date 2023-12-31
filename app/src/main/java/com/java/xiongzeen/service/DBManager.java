package com.java.xiongzeen.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.java.xiongzeen.MyApplication;
import com.java.xiongzeen.data.News;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DBManager {

    private static MySQLiteOpenHelper helper;
    private static SQLiteDatabase db;

    public DBManager() {
        helper = new MySQLiteOpenHelper(MyApplication.getContext());
        db = helper.getWritableDatabase();
        helper.onCreate(db);
    }

    public static void modify(News currentNews) {

        Log.d("DBManager", "Trying to modify a record.");

        String newsID = currentNews.getNewsID();

        db.beginTransaction();
        db.execSQL("REPLACE into myNews VALUES(?,?)", new Object[]{newsID.hashCode(), currentNews.toString()});
        db.setTransactionSuccessful();
        db.endTransaction();

        Log.d("DBManager", "Added(or Updated) a record successfully.");
    }

    public static List<News> query() {

        ArrayList<News> ans = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM myNews", null);
        try {
            while (c.moveToNext()) {
                Log.d("DBManager", "[Raw Data]" + c.getString(1));
                News item = new News(new JSONObject(c.getString(1)));
                ans.add(item);
            }
            Log.d("DBManager", "Read " + ans.toArray().length + " records from database.");
            return ans;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeDB() {
        db.close();
    }
}
