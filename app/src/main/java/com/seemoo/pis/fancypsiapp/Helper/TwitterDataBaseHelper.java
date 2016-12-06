package com.seemoo.pis.fancypsiapp.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by TMZ_LToP on 29.11.2016.
 */

public class TwitterDataBaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "TWITTER_DB";
    private static final int VERSION = 1;
    private static final String TABLE_NAME_F = "FOLLOWEES";
    private static final String TABLE_NAME_D = "DATE";
    private static final String DATE = "DATE";
    private static final String FOLLOWEE= "FOLLOWEE";
    private static final String SCREEN_NAME = "SCREENNAME";
    private static final String USERNAME = "USER";
    private static final String TABLE_CREATE_F = "CREATE TABLE "+ TABLE_NAME_F+"(id integer primary key,"+USERNAME+" text,"+FOLLOWEE+" text,"+SCREEN_NAME+" text)";
    private static final String TABLE_CREATE_D = "CREATE TABLE "+ TABLE_NAME_D+"(id integer primary key,"+DATE+" integer,"+USERNAME+" text)";


    public TwitterDataBaseHelper(Context c){
        super(c,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE_F);
        sqLiteDatabase.execSQL(TABLE_CREATE_D);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_F);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_D);
        onCreate(db);
    }

    public int getDate(String username){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select "+"*"+" from "+TABLE_NAME_D+" where "+USERNAME+" = "+"'"+username+"'",null);
        //Date should always only have one value never more when new one is set the old one will ne delted
        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            return c.getInt(c.getColumnIndex(DATE));
        }
        db.close();
        //if no date exist for user then return -1
    return -1;
    }

    public void deleteUser(String user){
        getWritableDatabase().delete(TABLE_NAME_F,USERNAME+" = "+"'"+user+"'",null);
    }


    public void deleteTables(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_F,"",null);
        db.delete(TABLE_NAME_D,"",null);
        db.close();
    }
    public void setDate(String username){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_D,USERNAME+" = "+"'"+username+"'",null);
        ContentValues values = new ContentValues();
        int date = DateFormat.getInstance().getCalendar().get(Calendar.WEEK_OF_YEAR);
        values.put(DATE,date);
        values.put(USERNAME,username);
        db.insert(TABLE_NAME_D,null,values);
        db.close();
    }

    public void insert(String username, String followee, String followeeScreenName){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(FOLLOWEE,followee);
        v.put(USERNAME,username);
        v.put(SCREEN_NAME,followeeScreenName);
        db.insert(TABLE_NAME_F,null,v);
    }
    public List<String[]> getFollowees(String username) {
        List<String[]> list = new ArrayList<>();
        String[] s;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from "+TABLE_NAME_F+" where "+USERNAME+" = "+"'"+username+"'",null);
        for (c.moveToFirst(); !c.isAfterLast();c.moveToNext()){
            s =  new String[2];
            s[0]=c.getString(c.getColumnIndex(FOLLOWEE));
            s[1]=c.getString(c.getColumnIndex(SCREEN_NAME));
            list.add(s);
        }
        db.close();
        return list;
    }
}
