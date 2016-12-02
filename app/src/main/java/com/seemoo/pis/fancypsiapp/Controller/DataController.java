package com.seemoo.pis.fancypsiapp.controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;

import com.seemoo.pis.fancypsiapp.collector.AppCollector;
import com.seemoo.pis.fancypsiapp.collector.GmailCollector;
import com.seemoo.pis.fancypsiapp.collector.HWDataCollector;
import com.seemoo.pis.fancypsiapp.collector.TwitterCollector;
import com.seemoo.pis.fancypsiapp.container.AppStruct;
import com.seemoo.pis.fancypsiapp.listener.Listener;
import com.seemoo.pis.fancypsiapp.ui.MainActivity;

import java.util.List;

/**
 * Created by TMZ_LToP on 16.11.2016.
 */

public class DataController {
    //TODO: set up all the data read existing data and only get everything according to the Settings

        AppCollector apps;
        HWDataCollector local;
        Context context;
        TwitterCollector twitter;
        SQLiteDatabase db;
        List<String[]> followees;
        boolean isTwitterReady = false;
        GmailCollector gmailCollector;


    public DataController(final Context context){
        this.context= context;
        apps = new AppCollector(context);
        twitter = new TwitterCollector(context);
        gmailCollector = new GmailCollector(context);


        initApps();
        initTwitter();
        initGmail();

        local = new HWDataCollector((WifiManager) context.getSystemService(Context.WIFI_SERVICE));


    }

    private void initGmail() {

    }

    private void initTwitter() {
        twitter.addListener(new Listener() {
            @Override
            public void onReady() {
                followees = twitter.getFollowees();
                if (followees != null ){
                    isTwitterReady = true;
                    ((MainActivity)context).setContactsActive();
                }
            }
        });
        twitter.execute();
    }

    private void initApps(){
        apps.addListener(new Listener() {
            @Override
            public void onReady() {
                for(AppStruct i : apps.getApps())
                    if(context instanceof MainActivity){
                        ((MainActivity)context).setAppsActive();
                    }
            }
        });
    }

    public HWDataCollector getLocalData(){
        return local;
    }

    public AppCollector getApps(){
        return apps;
    }
    public TwitterCollector getFollowees(){return twitter;}
}
