package com.seemoo.pis.fancypsiapp.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by TMZ_LToP on 18.11.2016.
 */

public class AppDataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "APP_DB";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "Apps";
    private static final String PACKNAME = "package";
    private static final String CATEGORY = "category";
    private static final String TABLE_CREATE = "CREATE TABLE "+ TABLE_NAME+"(id integer primary key,package text,category text)";
                                                                                                    //package name and category
    public AppDataBaseHelper(Context context){
        super(context, DB_NAME,null,VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS"+ TABLE_NAME);
        onCreate(db);
    }

    public HashMap<String,String> getApps(){
        SQLiteDatabase db = getReadableDatabase();
        HashMap<String,String> retMap = new HashMap<>();
        Cursor c =db.rawQuery("select * from "+TABLE_NAME,null);
        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            String pname = c.getString(c.getColumnIndex(PACKNAME));
            String catname = c.getString(c.getColumnIndex(CATEGORY));
            retMap.put(c.getString(c.getColumnIndex(PACKNAME)),c.getString(c.getColumnIndex(CATEGORY)));

        }
        c.close();

        return retMap;
    }

    public void insert(String packname, String category){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        long i;
        values.put(PACKNAME,packname);
        values.put(CATEGORY,category);
        i = db.insert(TABLE_NAME,null,values);
    }
}
