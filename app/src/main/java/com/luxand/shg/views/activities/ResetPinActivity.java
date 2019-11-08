package com.luxand.shg.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.model.Registration;
import com.luxand.shg.util.ProgressBarDialog;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ResetPinActivity extends BaseActivity {

    private static final String TAG = "ResetPinActivity";
    MySharedPreference preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reser_pin);
        EditText etPin = findViewById(R.id.et_pin_original);
        EditText etReEnteredPin = findViewById(R.id.et_pin_re_enter);
        preference = new MySharedPreference(this);
        findViewById(R.id.bt_reset).setOnClickListener(view -> {
         String pin = etPin.getText().toString();

         String confirmPin = etReEnteredPin.getText().toString();
            if(pin.length() == confirmPin.length() && pin.equalsIgnoreCase(confirmPin)){
                JsonObject object = new JsonObject();
                object.addProperty("pin",etPin.getText().toString().trim());
                object.addProperty("mac_address",RetrofitInstance.getMacAddress());
                Log.d(TAG, "onClick: object"+object);
                generatePIN(object);

            }else{
                Toast.makeText(ResetPinActivity.this, "Entered Pin MisMatches", Toast.LENGTH_SHORT).show();
            }


        });

    }

    public void generatePIN(JsonObject object) {

        Log.d(TAG, "generatePIN: "+object);
        ProgressBarDialog.showLoadingDialog(ResetPinActivity.this);
        RetrofitInstance.getInstance(ResetPinActivity.this).
                getRestAdapter()
                .updatePin(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Registration>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.e(TAG, "onError: getMessage"+e.getMessage() );
                    }

                    @Override
                    public void onNext(Registration registration) {

                        Log.d(TAG, "onNext: "+registration.getData());
                        ProgressBarDialog.cancelLoading();
                        Toast.makeText(ResetPinActivity.this, "Pin Created Successfully!", Toast.LENGTH_SHORT).show();
                        startActivityBase(ResetPinActivity.this,LoginActivity.class);
                    }
                });

    }
}
