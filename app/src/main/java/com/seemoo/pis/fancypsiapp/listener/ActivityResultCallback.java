package com.seemoo.pis.fancypsiapp.listener;

import android.content.Intent;

/**
 * Created by TMZ_LToP on 05.12.2016.
 */

public interface ActivityResultCallback {
    public void activityResultCallback(int requestCode, int resultCode, Intent data);
}
