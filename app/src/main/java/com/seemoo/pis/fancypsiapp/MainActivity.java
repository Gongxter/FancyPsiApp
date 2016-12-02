package com.seemoo.pis.fancypsiapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;



import com.seemoo.pis.fancypsiapp.Controller.DataController;
import com.seemoo.pis.fancypsiapp.Listener.MyPermissionCallback;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DataController controller;
    private Button buttonApps;
    private CheckBox checkApps;
    private CheckBox checkContacts;
    private Button buttonContacts;
    private List<MyPermissionCallback> callbacks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        controller = new DataController(this);
        checkApps = (CheckBox) findViewById(R.id.check_apps);
        buttonApps = (Button) findViewById(R.id.button_apps);


        checkContacts = (CheckBox) findViewById(R.id.check_contacts);
        buttonContacts = (Button) findViewById(R.id.button_contacts);


    }

    public void setContactsActive(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkContacts.setChecked(true);
                buttonContacts.setEnabled(true);

            }
        });
    }

    public void setAppsActive(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkApps.setChecked(true);
                buttonApps.setEnabled(true);

            }
        });
        //TODO: maybe Progressbar or something that turning shit


    }

    public void addCallback(MyPermissionCallback callback){
        callbacks.add(callback);
    }

    public void changeToApps(View view){
        Intent intent = new Intent(this,Applications.class);
        intent.putExtra("Apps",controller.getApps());
        //intent.putExtra("Apps",controller.getApps());
        startActivity(intent);
    }

    public DataController getControler(){
        return controller;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (MyPermissionCallback c : callbacks){
            c.permission(requestCode,permissions,grantResults);
        }
    }
}

