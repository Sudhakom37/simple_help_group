package com.luxand.shg.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreference {

    SharedPreferences mainSharedPreference;

    public MySharedPreference(Context context){

        mainSharedPreference = context.getSharedPreferences("main_activity", Context.MODE_PRIVATE);
    }

    public String getPref(String key){
        return  mainSharedPreference.getString(key, key);
    }

    public void setPref(String key, String value){
        SharedPreferences.Editor e = mainSharedPreference.edit();
        e.putString(key, value);
        e.commit();
    }

    public void clearAllPref(){
        SharedPreferences.Editor e = mainSharedPreference.edit();
        e.clear().commit();
    }

}
