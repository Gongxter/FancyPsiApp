package com.seemoo.pis.fancypsiapp.Collector;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.seemoo.pis.fancypsiapp.Container.AppStruct;
import com.seemoo.pis.fancypsiapp.Helper.AppDataBaseHelper;
import com.seemoo.pis.fancypsiapp.Listener.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TMZ_LToP on 16.11.2016.
 */

public class AppCollector extends AsyncTask<Void,Void,Void> implements Parcelable {

    public final static String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
    public final static String OTHER_URL = "http://api.wheredatapp.com/data";
    public final static String CATEGORY = "class=\"document-subtitle category\"";

    AppDataBaseHelper db;
    ArrayList<AppStruct> list = new ArrayList<>();
    ArrayList<ApplicationInfo> applist =  new ArrayList<>();
    HashMap<String,String> savedMap;
    PackageManager pm;

    List<Listener> listener = new ArrayList<>();


    boolean isFinished = false;

    public AppCollector(Context context){


        db = new AppDataBaseHelper(context);
        savedMap = db.getApps();
        pm = context.getPackageManager();
        execute();

        //hash applist before updating the list



    }

    private String getCategory(ApplicationInfo app) {
        String s = "";
        try {
            URL url = new URL(GOOGLE_URL+app.packageName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream in = conn.getInputStream();

            s = convert(in);
        } catch (MalformedURLException e) {
            Log.e("AppCollector",e.toString());
        } catch (IOException e){
            Log.e("AppCollector",e.toString());
        }

        return s;
    }

    private String convert(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line="";
        String[] a;
        try {
            while ((line = reader.readLine())!= null){
                if(line.contains(CATEGORY)){
                    line = line.split(CATEGORY)[1].split("\">")[0];
                    a = line.replaceAll("href=","").replaceAll("\"","").split("/");
                    return a[a.length-1];
                }
            }
        } catch (IOException e) {
            Log.e("AppCollector",e.toString());
        }
        return line;
    }

    private ArrayList<AppStruct> getInstalledApps(PackageManager pm) {
        String category;

        for (ApplicationInfo i : pm.getInstalledApplications(PackageManager.GET_META_DATA)){
            category = "";
            if(pm.getLaunchIntentForPackage(i.packageName)!= null){
                //app has lunch intentn -> must be an app
                if((i.flags & ApplicationInfo.FLAG_SYSTEM)!=1){
                    //system app we dont want
                    applist.add(i);
                    if(savedMap.containsKey(i.packageName)){
                        list.add(new AppStruct(i,savedMap.get(i.packageName)));
                    } else {
                        category = getCategory(i);
                        list.add(new AppStruct(i, category));
                        db.insert(i.packageName, category);
                    }
                }
            }

        }


        return list;
    }
    public ArrayList<AppStruct> getApps(){
        return list;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        list = getInstalledApps(pm);
        isFinished=true;

        for(Listener l : listener){
            l.onReady();
        }

        return null;
    }

    public boolean addListener(Listener l) {
        if(listener.contains(l)){
            return false;
        } else {
            listener.add(l);
            return true;
        }
    }

    public static final Parcelable.Creator<AppCollector> CREATOR
            = new Parcelable.Creator<AppCollector>() {

        @Override
        public AppCollector createFromParcel(Parcel parcel) {
            return new AppCollector(parcel);
        }

        @Override
        public AppCollector[] newArray(int i) {
            return new AppCollector[i];
        }
    };

    private AppCollector(Parcel in) {
        list = new ArrayList<AppStruct>();
        //list = in.createTypedArrayList(AppStruct.CREATOR);
        list = in.readArrayList(AppStruct.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(list);
    }
}
