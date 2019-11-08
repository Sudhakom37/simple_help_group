package com.luxand.shg.views.fragments.registration;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.OtpModel;
import com.luxand.shg.model.Registration;
import com.luxand.shg.views.signup.Face_Signup;


import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegistrationFragment2 extends Fragment {
    private static final String TAG = "RegistrationFragment2";

    Button bt_verify_otp;
    EditText user_otp;
    MySharedPreference preference;
    TextView tv_mobile_for_otp,tvTimer,tv_resend_otp;
    CountDownTimer timer;
    long timeConsumed = 0, totalTime = 0, timeRequired = 45, displayTime = 0;
    long tStart, tEnd, tDelta;
    Double timeConsumed1 = 0.0;

    String mobile;
    String otp,sno;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registration2,container,false);

        bt_verify_otp = view.findViewById(R.id.bt_verify_otp);
        user_otp = view.findViewById(R.id.user_otp);
        tv_mobile_for_otp = view.findViewById(R.id.tv_mobile_for_otp);
        tvTimer = view.findViewById(R.id.tv_timer);
        tv_resend_otp = view.findViewById(R.id.tv_resend_otp);
         preference = new MySharedPreference(getActivity());
         mobile = getArguments().getString(PrefKeys.MOBILE);
         otp = getArguments().getString(PrefKeys.OTP);
         sno = getArguments().getString(PrefKeys.SNO);

         initTimer();
        tv_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initTimer();
                tv_resend_otp.setEnabled(false);
                JsonObject object = new JsonObject();
                object.addProperty("mobile",mobile);// Change this to get from runtime
                object.addProperty("mac_address",RetrofitInstance.getMacAddress());
                resendOtp(object);
            }
        });
        tv_mobile_for_otp.setText(mobile);
        user_otp.setText(otp);
        //((MainActivity)getActivity()).tv_title.setText("Registration");
        bt_verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JsonObject object = new JsonObject();
                object.addProperty("otp",user_otp.getText().toString().trim());// Change this to get from runtime
                object.addProperty("sno",sno);
                registerWithMobile(object);
            }
        });
        return view;

    }

    private void registerWithMobile(JsonObject object){


        Log.d(TAG, "registerWithMobile: "+object);
        RetrofitInstance.getInstance(getActivity())
                .getRestAdapter().verifyOtp(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OtpModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(OtpModel otpModel) {
                        preference.setPref(PrefKeys.ROLE_ID,otpModel.getData().getRole_id());
                        Intent intent = new Intent(getActivity(), Face_Signup.class);
                        startActivity(intent);
                        //((RegistrationActivity)getActivity()).setRegistrationFragment(new RegistrationFragment3(),null);
                    }
                });



    }
    private void resendOtp(JsonObject object){


        Log.d(TAG, "registerWithMobile: "+object);
        RetrofitInstance.getInstance(getActivity())
                .getRestAdapter().registerWithNumber(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Registration>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Registration registration) {
                        initTimer();
                        Log.d(TAG, "onNext: "+registration.getData().getSno());
                       /* preference.setPref(PrefKeys.SNO,registration.getResults().getSno());
                        Bundle b = new Bundle();
                        b.putString(PrefKeys.MOBILE,etMobile.getText().toString().trim());
                        ((RegistrationActivity)getActivity()).setRegistrationFragment(new RegistrationFragment2(),b);*/
                    }
                });



    }
    private void initTimer() {
        totalTime = timeRequired * 1000;

        Log.d(TAG, "initTimer: totalTime " + totalTime);
        displayTime = timeRequired - 1;
        tStart = System.currentTimeMillis();


        if (timer == null) {
            timer = new CountDownTimer(totalTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {


                    timeConsumed1 = (double) TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                    tvTimer.setText(String.format(Locale.getDefault(), "%d  %s",
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)), "sec"));

                }

                @Override
                public void onFinish() {
                    tEnd = System.currentTimeMillis();
                    timeConsumed = totalTime / 1000;
                    timeConsumed1 = totalTime / 1000.0;
                    //tvTimer.setText(getString(R.string.Finished));
                    tDelta = tEnd - tStart;
                    tv_resend_otp.setEnabled(true);
                    timer=null;

                    /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Time Up!Try Again")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, id) -> {

                                //addAnswerList(dataList.get(selectedQuestionNo));

                                //submitAnswer(getActivity(), answerJson);
                                dialog.dismiss();

                            });
                    AlertDialog alert = builder.create();
                    alert.show();
*/

                }
            }.start();
        } else {
            Log.d(TAG, "initTimer: else....");
        }
    }


    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (timer != null)
            timer.cancel();
    }



}
