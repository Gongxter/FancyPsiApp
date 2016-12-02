package com.seemoo.pis.fancypsiapp.Adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seemoo.pis.fancypsiapp.Container.AppStruct;
import com.seemoo.pis.fancypsiapp.Helper.AppCategory;
import com.seemoo.pis.fancypsiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TMZ_LToP on 22.11.2016.
 */

public class ExpandAdapter extends BaseExpandableListAdapter {

    Context context;
    AppCategory category[] = AppCategory.getCategorys();
    ArrayList<AppStruct>[] appsByCategory;
    PackageManager pm;

    public ExpandAdapter(Context context, ArrayList<AppStruct> appList){
        this.context = context;
        pm = context.getPackageManager();
        AppCategory e;
        appsByCategory = new ArrayList[category.length];
        for (int i = 0; i< category.length; i++){
            appsByCategory[i] = new ArrayList<>();
        }
        for(AppStruct s : appList){
            e = AppCategory.getCategory(s.category);
            appsByCategory[e.getPos()].add(s);
            if(e == AppCategory.UNDEFINED)Log.i("AppCategory",s.category);
        }
    }

    @Override
    public int getGroupCount() {
        return category.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return appsByCategory[i].size();
    }

    @Override
    public Object getGroup(int i) {
        return category[i];
    }

    @Override
    public Object getChild(int i, int i1) {
        return appsByCategory[i].get(i1);
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
        String title = category[i].toString();

        LinearLayout layout = (LinearLayout) fl.inflate(R.layout.group_layout,null);
        TextView titleView = (TextView) layout.findViewById(R.id.group_title);
        titleView.setText(title);
        return layout;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        LayoutInflater fl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AppStruct app = appsByCategory[i].get(i1);
        LinearLayout layout = (LinearLayout) fl.inflate(R.layout.item_layout,null);
        TextView name = (TextView) layout.findViewById(R.id.item_name);
        ImageView icon = (ImageView) layout.findViewById(R.id.item_icon);

        icon.setImageDrawable(pm.getApplicationIcon(app.appInfo));
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        name.setText(pm.getApplicationLabel(app.appInfo));

        layout.requestLayout();

        return layout;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
