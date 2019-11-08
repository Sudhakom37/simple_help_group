package com.luxand.shg.views.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.google.gson.JsonObject;
import com.luxand.shg.BuildConfig;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.Auth;
import com.luxand.shg.model.FrKey;
import com.luxand.shg.model.GroupDatfile;
import com.luxand.shg.services.DownloadGroupDatFile;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.FrKeyViewModel;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;


import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class SplashScreen extends BaseActivity implements FrKeyViewModel.ViewListener {
    private static final String TAG = "SplashScreen";
    MySharedPreference mySharedPreference;
    String userType = "";
    private static final int REQUEST_CAMERA = 4;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    Thread splashThread;
    Handler mHandler;
    ProgressBar loadingProgressBar;
    FrKeyViewModel viewModel;
    JsonObject object = new JsonObject();
    MySharedPreference preference;
    ResponseReceiver receiver =new ResponseReceiver();
    ProgressBar progressBar_per;
    Dialog d_per_download;
    TextView tv_id;
    String group_id;
    private List<GroupDatfile.DatUrl> datUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preference =new MySharedPreference(this);
        setOAuthJson();
        viewModel = ViewModelProviders.of(this).get(FrKeyViewModel.class);
        viewModel.setViewListener(this);
        loadingProgressBar = findViewById(R.id.loading);
        mySharedPreference = new MySharedPreference(SplashScreen.this);
        userType = mySharedPreference.getPref(PrefKeys.USER_TYPE);
        mHandler = new Handler();
        //mySharedPreference.setPref(PrefKeys.CAM_CONFIG,"front");
        loadingProgressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "onCreate: getMacAddress "+RetrofitInstance.getMacAddress());
        requestCameraAndStorage();
        //getBearerKey();

    }

    void getBearerKey(){
        RetrofitInstance.getInstance(this).getRestAdapterAuth()
        .getBearerKey(object).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Auth>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: getMessage"+e.getMessage() );
                        Toast.makeText(SplashScreen.this, "Could not Connect to App ,Internet Error ", Toast.LENGTH_SHORT).show();
                        //finish();
                    }

                    @Override
                    public void onNext(Auth auth) {

                        if(auth!=null) {
                            mySharedPreference.setPref(PrefKeys.ACCESS_TOKEN, auth.getAccess_token());
                            mySharedPreference.setPref(PrefKeys.REFRESH_TOKEN, auth.getRefresh_token());
                            viewModel.login(RetrofitInstance.getMacAddress());
                        }else{
                            Log.d(TAG, "onNext: auth"+auth);
                        }

                    }
                });
    }


    public void requestCameraAndStorage() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                if(isNetWorkAvailable()){
                    String role_id=mySharedPreference.getPref(PrefKeys.ROLEID);
                    if(role_id!=null&&role_id.length()!=0){
                        if(role_id.equalsIgnoreCase("4")){
                            Intent intent=new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(role_id.equalsIgnoreCase("2")){
                            Intent intent=new Intent(SplashScreen.this, RegistrationActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(role_id.equalsIgnoreCase("1")){
                            Intent intent=new Intent(SplashScreen.this, ForgotPinActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent=new Intent(SplashScreen.this, Face_SignIn.class);
                            intent.putExtra(PrefKeys.VERIFY_MODE,"login");
                            startActivity(intent);
                            finish();
                        }
                    }else{
                       getBearerKey();
                    }
                }else{
                    //NavigateActivity();
                }

            }
        });
    }
private void setOAuthJson(){
    object.addProperty("username","anjaneyuluganipisetty@gmail.com");
    object.addProperty("password","123456");
    object.addProperty("grant_type","password");
    object.addProperty("client_id","3");
    object.addProperty("client_secret","ReJvTJy5KS1teVoPReO45ciWREfLjFUL1l8nSI7Q");
    Log.d(TAG, "setOAuthJson: object "+object);

}


    @Override
    public void onLoginResponse(FrKey key) {

        if(key != null) {
            Log.d(TAG, "onLoginResponse: "+key.getData().getFr_key());
            mySharedPreference.setPref(PrefKeys.FACEURL, key.getData().getFr_key());
            finish();
            startActivityBase(SplashScreen.this, Face_SignIn.class);
        }
    }

    public void getClassDatFile() {

        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this).getRestAdapter()
                .download_staff_dat(preference.getPref(PrefKeys.GROUP_ID),RetrofitInstance.getMacAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GroupDatfile>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.d(TAG, "onError: "+e.getMessage());

                    }

                    @Override
                    public void onNext(GroupDatfile groupDatfile) {
                        Log.d(TAG, "onNext: groupDatFile "+groupDatfile.getData().getIsDownloadDat());
                        ProgressBarDialog.cancelLoading();
                        datUrl=groupDatfile.getData().getDatUrl();

                        if (groupDatfile.getData().getIsDownloadDat()==1) {

                            Intent intent = new Intent(getApplicationContext(), DownloadGroupDatFile.class);
                            startService(intent);


                        }

                    }
                });
    }
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = BuildConfig.APPLICATION_ID;

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive: "+intent.getAction());
            int value = intent.getIntExtra("per", -1);
            if (value <= 0) {
                if (d_per_download == null) {
                    d_per_download = new Dialog(SplashScreen.this);
                    if (!d_per_download.isShowing()) {
                        d_per_download.setContentView(R.layout.download_progress_dialog);
                        progressBar_per =  d_per_download.findViewById(R.id.pb_id);
                        progressBar_per.setMax(100);
                        d_per_download.setCancelable(false);
                        d_per_download.setCanceledOnTouchOutside(false);
                        tv_id =  d_per_download.findViewById(R.id.tv_id);
                        d_per_download.show();
                    }
                } else {
                    if (!(d_per_download.isShowing())) {
                        d_per_download.show();// or alert.dismiss() it
                    }

                }
            }
            try {
                progressBar_per.setProgress(value);
                tv_id.setText(String.valueOf(value));
            } catch (Exception e) {
                e.printStackTrace();
            }

            d_per_download.cancel();
            d_per_download.dismiss();

            //updateDownloadDatStatus();



        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver,new IntentFilter(
                GroupMemberEnrollmentList_Activity.ResponseReceiver.ACTION_RESP));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
