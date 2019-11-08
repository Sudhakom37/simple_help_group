package com.luxand.shg.views.fragments.registration;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.luxand.shg.R;
import com.luxand.shg.views.activities.RegistrationActivity;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.Registration;


import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class RegistrationFragment1 extends Fragment {

    private static final String TAG = "RegistrationFragment1";
    EditText etMobile;
    Button btGenerateOtp;
    MySharedPreference preference;
   public RegistrationFragment1(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_registration1,container,false);

       etMobile = view.findViewById(R.id.user_mobile);
       btGenerateOtp = view.findViewById(R.id.bt_generate_otp);
       preference = new MySharedPreference(getActivity());
        //((MainActivity)getActivity()).tv_title.setText("Registration");
       btGenerateOtp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               JsonObject object = new JsonObject();
               object.addProperty("mobile",etMobile.getText().toString().trim());// Change this to get from runtime
               object.addProperty("mac_address",RetrofitInstance.getMacAddress());
               registerWithMobile(object);
              // verifyOtp(etMobile.getText().toString().trim());
           }
       });
        return view;

    }

    private void verifyOtp(String mobile){

      Bundle b = new Bundle();
      b.putString(PrefKeys.MOBILE,mobile);
       if(mobile!= null && getActivity()!=null){
           ((RegistrationActivity)getActivity()).setRegistrationFragment(new RegistrationFragment2(),b);
       }
    }


    private void registerWithMobile(JsonObject object){


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
                            Log.d(TAG, "onNext: "+registration.getData().getSno());
                            preference.setPref(PrefKeys.SNO,registration.getData().getSno());
                            preference.setPref(PrefKeys.ROLE_ID,registration.getData().getRole_id());

                            Bundle b = new Bundle();
                            b.putString(PrefKeys.MOBILE,etMobile.getText().toString().trim());
                            b.putString(PrefKeys.OTP,registration.getData().getOtp());
                            b.putString(PrefKeys.SNO,registration.getData().getSno());
                            ((RegistrationActivity)getActivity()).setRegistrationFragment(new RegistrationFragment2(),b);
                        }
                    });



    }


}
