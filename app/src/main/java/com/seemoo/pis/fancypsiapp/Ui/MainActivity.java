package com.seemoo.pis.fancypsiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.seemoo.pis.fancypsiapp.collector.GmailCollector;
import com.seemoo.pis.fancypsiapp.controller.DataController;
import com.seemoo.pis.fancypsiapp.listener.ActivityResultCallback;
import com.seemoo.pis.fancypsiapp.listener.Listener;
import com.seemoo.pis.fancypsiapp.listener.MyPermissionCallback;
import com.seemoo.pis.fancypsiapp.R;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private DataController controller;
    private Button buttonApps;
    private CheckBox checkApps;
    private CheckBox checkContacts;
    private Button buttonContacts;
    private ProgressBar progressContacts;
    private ProgressBar progressApps;


    private Context context = this;


    private List<MyPermissionCallback> callbacks = new ArrayList<>();
    private List<ActivityResultCallback> activityResults = new ArrayList<>();
    private EasyPermissions.PermissionCallbacks easyCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        checkApps = (CheckBox) findViewById(R.id.check_apps);
        buttonApps = (Button) findViewById(R.id.button_apps);
        progressApps = (ProgressBar)findViewById(R.id.progress_apps);

        checkContacts = (CheckBox) findViewById(R.id.check_contacts);
        buttonContacts = (Button) findViewById(R.id.button_contacts);
        progressContacts = (ProgressBar) findViewById(R.id.progress_contacts);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        controller = new DataController(this);

    }

    public void setContactsActive(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkContacts.setChecked(true);
                buttonContacts.setEnabled(true);
                progressContacts.setVisibility(View.INVISIBLE);


            }
        });
    }

    public void setAppsActive(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkApps.setChecked(true);
                buttonApps.setEnabled(true);
                progressApps.setVisibility(View.INVISIBLE);

            }
        });
        //TODO: maybe Progressbar or something that turning shit


    }

    public void addCallback(MyPermissionCallback callback){
        callbacks.add(callback);
    }
    public void addResultCallback(ActivityResultCallback callback){
        activityResults.add(callback);}

    public void changeToApps(View view){
        Intent intent = new Intent(this,Applications.class);
        intent.putExtra("Apps",controller.getApps());
        startActivity(intent);
    }

    public DataController getControler(){
        return controller;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (ActivityResultCallback c : activityResults){
            c.activityResultCallback(requestCode,resultCode,data);
        }

    }
    public void easyPermission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("onRequestPermission","here");
        for (MyPermissionCallback c : callbacks){
            c.permission(requestCode,permissions,grantResults);
        }
        if(easyCallback != null){
            easyCallback.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    public void changeToContacts(View view) {
        Intent intent = new Intent(this,ContactsActivity.class);
        intent.putExtra("twitter",controller.getFollowees());
        intent.putStringArrayListExtra("gmail",(ArrayList<String>) controller.getMails());
        //intent.putExtra("Apps",controller.getApps());
        startActivity(intent);
    }

    public void addEasyPermissionCallback(EasyPermissions.PermissionCallbacks callback) {
        easyCallback = callback;
    }
    
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i("EasyPErmissionGrant","here");
        easyCallback.onPermissionsGranted(requestCode,perms);
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i("EasyPermissionDen","here");
        easyCallback.onPermissionsDenied(requestCode,perms);
    }
}

