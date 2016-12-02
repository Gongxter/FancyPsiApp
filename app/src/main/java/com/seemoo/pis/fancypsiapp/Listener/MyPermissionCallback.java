package com.seemoo.pis.fancypsiapp.Listener;

import android.support.annotation.NonNull;

/**
 * Created by TMZ_LToP on 30.11.2016.
 */

public interface MyPermissionCallback {
    public void permission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
