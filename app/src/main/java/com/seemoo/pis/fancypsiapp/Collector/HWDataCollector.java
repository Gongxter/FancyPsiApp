package com.seemoo.pis.fancypsiapp.Collector;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.util.List;

/**
 * Created by TMZ_LToP on 16.11.2016.
 */

public class HWDataCollector {

    WifiManager manager;


    public HWDataCollector(WifiManager m){
        manager = m;
    }

    public String getDevice(){
        return Build.DEVICE;
    }
    public String getBrand(){
        return Build.BRAND;
    }
    public String getManufacturer(){
        return Build.MANUFACTURER;
    }

    public List<WifiConfiguration> getWifis(){
        return manager.getConfiguredNetworks();
    }



}
