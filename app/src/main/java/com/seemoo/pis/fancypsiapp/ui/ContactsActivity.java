package com.seemoo.pis.fancypsiapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.seemoo.pis.fancypsiapp.R;
import com.seemoo.pis.fancypsiapp.adapter.ExpandAdapterContacts;
import com.seemoo.pis.fancypsiapp.collector.TwitterCollector;

public class ContactsActivity extends AppCompatActivity {

    TwitterCollector twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        twitter = getIntent().getParcelableExtra("twitter");
        ExpandableListAdapter adapter = new ExpandAdapterContacts(this,twitter.getFollowees(),null);

        ExpandableListView epView =(ExpandableListView) findViewById(R.id.expander);
        epView.setAdapter(adapter);



    }
}
