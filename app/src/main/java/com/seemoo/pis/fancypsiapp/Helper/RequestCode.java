package com.seemoo.pis.fancypsiapp.Helper;

/**
 * Created by TMZ_LToP on 30.11.2016.
 */

public enum RequestCode {
    ACCOUNTS(0);

    private int requestCode;
    private RequestCode(int i){
        requestCode = i;
    }
    public int id(){
        return this.requestCode;
    }
}
