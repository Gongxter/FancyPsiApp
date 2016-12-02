package com.seemoo.pis.fancypsiapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seemoo.pis.fancypsiapp.Adapter.ExpandAdapter;
import com.seemoo.pis.fancypsiapp.Collector.AppCollector;
import com.seemoo.pis.fancypsiapp.Container.AppStruct;

import java.util.ArrayList;

public class Applications extends AppCompatActivity {

    AppCollector apps;
    LinearLayout layout;
    ExpandableListAdapter adapter;
    ExpandableListView epView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        apps= getIntent().getParcelableExtra("Apps");
        adapter = new ExpandAdapter(this, apps.getApps());
        epView =(ExpandableListView) findViewById(R.id.expander);
        epView.setAdapter(adapter);

        //setApps(apps);
    }

    private void setApps(AppCollector apps) {
        PackageManager pm = getPackageManager();
        TextView name;
        TextView category;
        ImageView icon;
        LinearLayout container;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(125,125);

        for(AppStruct struc : apps.getApps()){
            name = new TextView(this);
            category = new TextView(this);
            icon = new ImageView(this);
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            icon.setLayoutParams(params);
            container = new LinearLayout(this);
            name.setText(pm.getApplicationLabel(struc.appInfo));
            category.setText(struc.category);
            icon.setImageDrawable(pm.getApplicationIcon(struc.appInfo));
            container.setOrientation(LinearLayout.HORIZONTAL);
            container.addView(icon);
            container.addView(name);
            container.addView(category);
            layout.addView(container);



        }
    }


}
