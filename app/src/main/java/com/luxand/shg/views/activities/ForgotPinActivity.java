package com.luxand.shg.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.luxand.shg.R;
import com.luxand.shg.helper.PrefKeys;


public class ForgotPinActivity extends BaseActivity {
    private static final String TAG = "ForgotPinActivity";
    EditText etMobile;
    Button btGenerateOtp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin);
        etMobile = findViewById(R.id.user_mobile);
        btGenerateOtp = findViewById(R.id.bt_generate_otp);

        btGenerateOtp.setOnClickListener(view -> verifyOtp(etMobile.getText().toString().trim()));
    }
    private void verifyOtp(String mobile){

        Bundle b = new Bundle();
        b.putString(PrefKeys.MOBILE,mobile);
        if(mobile!= null){
            Log.d(TAG, "verifyOtp: "+mobile);
        }
    }

}
