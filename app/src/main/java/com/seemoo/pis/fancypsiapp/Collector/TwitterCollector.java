package com.seemoo.pis.fancypsiapp.collector;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.seemoo.pis.fancypsiapp.helper.TwitterDataBaseHelper;
import com.seemoo.pis.fancypsiapp.helper.TwitterHelper;
import com.seemoo.pis.fancypsiapp.listener.Listener;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


/**
 * Created by TMZ_LToP on 23.11.2016.
 */

public class TwitterCollector extends AsyncTask<Void,Void,Void> implements Parcelable{


    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.



    private Twitter twitter;
    private List<User> followees;
    private List<String[]> listOfFollowees;
    private List<Listener> listeners = new ArrayList<>();
    String username = "timmantom";
    TwitterDataBaseHelper db;
    //TODO: get User_id for twitter from SharedPrefer...

    public TwitterCollector(Context c) {

        db = new TwitterDataBaseHelper(c);
        //new User so get followees or update followees
    }

    @Override
    protected Void doInBackground(Void... voids) {

        int currentDate = DateFormat.getInstance().getCalendar().get(Calendar.WEEK_OF_YEAR);
        if(db.getDate(username) == -1 || db.getDate(username)!= currentDate ){
            load();
        }else {
            //load existing followees;
            listOfFollowees = db.getFollowees(username);
            callListener();
        }
        return null;
    }

    private void load(){
        try {
            twitter = TwitterHelper.appAuth();
            //TODO: change the name to the one from SharedSettings
            followees = TwitterHelper.getFollowees(twitter,"timmantom");
            listOfFollowees = new ArrayList<>();
            db.setDate(username);
            String[]s;
            for (User u : followees){
                db.insert(username,u.getName(),u.getScreenName());
                s = new String[2];
                s[0] = u.getName();
                s[1] = u.getScreenName();
                listOfFollowees.add(s);
            }
            callListener();

        } catch (TwitterException e) {
            Log.e("",e.getErrorMessage());
        }

    }

    private void callListener() {
        for(Listener l :listeners){
            l.onReady();
        }
    }
    public List<String[]> getFollowees(){
        return listOfFollowees;
    }
    public void addListener(Listener l ){
        listeners.add(l);
    }

    public static final Parcelable.Creator<TwitterCollector> CREATOR
            = new Creator<TwitterCollector>() {
        @Override
        public TwitterCollector createFromParcel(Parcel parcel) {
            return new TwitterCollector(parcel);
        }

        @Override
        public TwitterCollector[] newArray(int i) {
            return new TwitterCollector[0];
        }
    };

    private TwitterCollector(Parcel in){
        listOfFollowees = in.readArrayList(String[].class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(listOfFollowees);
    }
}
