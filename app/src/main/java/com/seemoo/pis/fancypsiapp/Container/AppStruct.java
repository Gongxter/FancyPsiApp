package com.seemoo.pis.fancypsiapp.container;

import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TMZ_LToP on 16.11.2016.
 */
public class AppStruct implements Parcelable {


    public ApplicationInfo appInfo = null;

    public String category = null;

    public static final Parcelable.Creator<AppStruct> CREATOR
            = new Parcelable.Creator<AppStruct>() {
        public AppStruct createFromParcel(Parcel in) {
            return new AppStruct(in);
        }
        public AppStruct[] newArray(int size) {
            return new AppStruct[size];
        }
    };

    private AppStruct(Parcel in) {
        appInfo = ApplicationInfo.CREATOR.createFromParcel(in);
        category =  in.readString();
    }

    public AppStruct(ApplicationInfo i,String s){
        appInfo = i;
        category = s;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        appInfo.writeToParcel(parcel,i);
        parcel.writeString(category);

    }
}
