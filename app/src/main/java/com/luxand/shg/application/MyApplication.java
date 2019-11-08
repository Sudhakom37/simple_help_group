package com.luxand.shg.application;

import android.app.Application;
import android.content.Context;

import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.util.LogoutInterface;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.Timer;
import java.util.TimerTask;

import in.myinnos.customfontlibrary.TypefaceUtil;


public class MyApplication extends Application {
    LogoutInterface logoutInterface;
    Timer timer;
    MySharedPreference mySharedPreference;
    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        FlowManager.init(getApplicationContext());
        mySharedPreference=new MySharedPreference(getApplicationContext());
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Poppins-Regular.ttf");
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(getBaseContext());
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        FlowManager.destroy();
    }
    public void  startSession(){

        cancelTimer();
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logoutInterface.LogoutSession();
            }
        },Long.parseLong(mySharedPreference.getPref(PrefKeys.DELAY_TIME)));
    }

    void cancelTimer(){
        if(timer!=null){
            timer.cancel();
        }
    }

    public void UserInteracted() {

        startSession();
    }

    public void registerLogoutListener(LogoutInterface logoutInterface) {
        this.logoutInterface=logoutInterface;
    }
}