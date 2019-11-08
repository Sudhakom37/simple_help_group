package com.luxand.shg.views.activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.JsonObject;
import com.luxand.shg.R;
import com.luxand.shg.views.activities.division_offcer.DivisionOfficerMainActivity;
import com.luxand.shg.views.activities.state_officer.StateOfficerMainActivity;
import com.luxand.shg.views.activities.village_officer.VillageOfficerMainActivity;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.Login;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.LoginVewModel;
import com.luxand.shg.views.fragments.department.DepartmentHomeFragment;
import com.luxand.shg.views.fragments.samuha_sakhi.DivisionStartMeetingMapActivity;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class LoginActivity extends BaseActivity {


    private static final String TAG = "LoginActivity";
    String macAddress;
    EditText usernameEditText;
    LoginVewModel viewModel;
    String action;
    MySharedPreference preference ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.user_pin);
        final TextView loginButton = findViewById(R.id.forgot_pin);
        TextView sign_up = findViewById(R.id.sign_up);
        String verify = getIntent().getStringExtra(PrefKeys.VERIFY_MODE);
        action = getIntent().getStringExtra("Action");
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        macAddress = RetrofitInstance.getMacAddress();
        viewModel = ViewModelProviders.of(this).get(LoginVewModel.class);
        preference = new MySharedPreference(this);
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()==4){
                    JsonObject object = new JsonObject();
                    object.addProperty("pin",usernameEditText.getText().toString());
                    object.addProperty("type","pin");
                    object.addProperty("verify_mode",verify);
                    object.addProperty("mac_address",RetrofitInstance.getMacAddress());
                    //object.addProperty("role_id","4");


                    login(object);


                }

            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);


        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,ResetPinActivity.class);
            startActivity(intent);

        });
        sign_up.setOnClickListener(v -> {
           //Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivityBase(LoginActivity.this, RegistrationActivity.class);

        });
    }


    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void login(JsonObject object){

        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this).getRestAdapter()
                .loginWithPin(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Login>() {
                    @Override
                    public void onCompleted() {
                        usernameEditText.setText("");
                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.e(TAG, "onError: "+e.getMessage());
                    }

                    @Override
                    public void onNext(Login login) {

                        ProgressBarDialog.cancelLoading();
                        Log.d(TAG, "onNext: getRoleId"+ login.getData().getRoleId());

                        preference.setPref(PrefKeys.GROUP_NAME,login.getData().getGroupName());
                        preference.setPref(PrefKeys.USER_IMAGE,login.getData().getImage());
                        preference.setPref(PrefKeys.NAME,login.getData().getName());
                        preference.setPref(PrefKeys.USER_ID,String.valueOf(login.getData().getUserId()));
                        preference.setPref(PrefKeys.GROUP_ID,String.valueOf(login.getData().getGroupId()));
                        preference.setPref(PrefKeys.ROLE_ID,String.valueOf(login.getData().getRoleId()));
                        if(login.getData().getRoleId()== 2){
                            startActivityBase(LoginActivity.this, StateOfficerMainActivity.class);
                        }else if(login.getData().getRoleId()== 3){
                            startActivityBase(LoginActivity.this, VillageOfficerMainActivity.class);
                        }else if(login.getData().getRoleId()== 4){
                            startActivityBase(LoginActivity.this, MainActivity.class);
                        }else if(login.getData().getRoleId() == 6&& login.getData().getVerify_mode().equalsIgnoreCase("login")){
                            startActivityBase(LoginActivity.this, DivisionOfficerMainActivity.class);
                        } else if(login.getData().getRoleId() == 6&& login.getData().getVerify_mode().equalsIgnoreCase("division")){
                            finish();
                            Intent intent=new Intent(LoginActivity.this, DivisionStartMeetingMapActivity.class);
                            //intent.putExtra(PrefKeys.VERIFY_MODE,"division");
                            intent.putExtra("Action",action);

                            startActivity(intent);
                        }


                    }
                });


    }
}
