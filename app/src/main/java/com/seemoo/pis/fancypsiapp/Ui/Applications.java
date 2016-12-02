package com.seemoo.pis.fancypsiapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.seemoo.pis.fancypsiapp.adapter.ExpandAdapterApps;
import com.seemoo.pis.fancypsiapp.collector.AppCollector;
import com.seemoo.pis.fancypsiapp.R;

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
        adapter = new ExpandAdapterApps(this, apps.getApps());
        epView =(ExpandableListView) findViewById(R.id.expander);
        epView.setAdapter(adapter);

        //setApps(apps);
    }

}
