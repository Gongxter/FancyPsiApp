package com.seemoo.pis.fancypsiapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seemoo.pis.fancypsiapp.R;

import java.util.List;

/**
 * Created by TMZ_LToP on 02.12.2016.
 */

public class ExpandAdapterContacts extends BaseExpandableListAdapter {

    List<String[]> twitter;
    List<String> gmail;
    Context context;

    public ExpandAdapterContacts(Context c, List<String[]> twitterList, List<String>gmail){
        context = c;
        this.twitter = twitterList;
        this.gmail = gmail;
    }

    @Override
    public int getGroupCount() {
        return (gmail == null)? 1:2;
    }

    @Override
    public int getChildrenCount(int i) {
        if(i == 0){
            return twitter.size();
        } else return (gmail==null) ? 0 :gmail.size();
    }

    @Override
    public Object getGroup(int i) {
        return (i == 0) ? twitter:gmail;
    }

    @Override
    public Object getChild(int i, int i1) {
        if(i == 1){
            return twitter.get(i1);
        } else {
            return gmail.get(i1);
        }
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        LayoutInflater fl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String title = (i == 0)? "Twitter Followees":"Gmail Adresses";

        LinearLayout layout = (LinearLayout) fl.inflate(R.layout.group_layout,null);
        TextView titleView = (TextView) layout.findViewById(R.id.group_title);
        titleView.setText(title);
        return layout;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        return (i == 0) ? makeTwitter(i1,b,view,viewGroup):makeGmail(i1,b,view,viewGroup);
    }

    private View makeTwitter(int i1, boolean b, View view, ViewGroup viewGroup){
        LayoutInflater fl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String [] followee = twitter.get(i1);
        LinearLayout layout = (LinearLayout) fl.inflate(R.layout.item_layout,null);
        TextView name = (TextView) layout.findViewById(R.id.item_name);
        ImageView icon = (ImageView) layout.findViewById(R.id.item_icon);

        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        name.setText(followee[1]);

        layout.requestLayout();

        return layout;
    }
    private View makeGmail(int i1, boolean b, View view, ViewGroup viewGroup){
        LayoutInflater fl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String mail = gmail.get(i1);
        LinearLayout layout = (LinearLayout) fl.inflate(R.layout.item_layout,null);
        TextView name = (TextView) layout.findViewById(R.id.item_name);
        ImageView icon = (ImageView) layout.findViewById(R.id.item_icon);

        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        name.setText(mail);

        layout.requestLayout();

        return layout;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
