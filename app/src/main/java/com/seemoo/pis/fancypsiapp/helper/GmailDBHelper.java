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
 * Created by TMZ_LToP on 05.12.2016.
 */

public class GmailDBHelper extends SQLiteOpenHelper {

    private static final String DB_NMAE="GMAILDB";
    private static final String TABLE_NAME="MAILDB";
    private static final String MAILS ="MAILS";
    private static final String TABLE_NAME_D = "GMAILDATE";
    private static final String DATE = "DATE";

    private static final String TABLE_CREATE_M = "CREATE TABLE "+ TABLE_NAME+"(id integer primary key,"+ MAILS +" text)";
    private static final String TABLE_CREATE_D = "CREATE TABLE "+ TABLE_NAME_D+"(id integer primary key,"+DATE+" integer)";


    private Context context;

    public GmailDBHelper(Context context) {
        super(context,DB_NMAE,null,1);
        this.context = context;
    }
    public void setDate(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_D,"",null);
        ContentValues values = new ContentValues();
        int date = DateFormat.getInstance().getCalendar().get(Calendar.WEEK_OF_YEAR);
        values.put(DATE,date);
        db.insert(TABLE_NAME_D,null,values);
        db.close();
    }
    public void insert(String mail){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(MAILS,mail);
        db.insert(TABLE_NAME,null,v);
        db.close();
    }
    public List<String> getMails() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from "+TABLE_NAME,null);
        for (c.moveToFirst(); !c.isAfterLast();c.moveToNext()){
            list.add(c.getString(c.getColumnIndex(MAILS)));
        }
        db.close();
        return list;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE_M);
        sqLiteDatabase.execSQL(TABLE_CREATE_D);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
    public int getDate(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select "+"*"+" from "+TABLE_NAME_D,null);
        //Date should always only have one value never more when new one is set the old one will ne delted
        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            int ret = c.getInt(c.getColumnIndex(DATE));
            db.close();
            return ret;
        }

        //if no date exist for user then return -1
        db.delete(TABLE_NAME,"",null); // in case data got written but not complete
        db.close();
        return -1;
    }

    public void delete() {
        getWritableDatabase().delete(TABLE_NAME,"",null);
    }
}
