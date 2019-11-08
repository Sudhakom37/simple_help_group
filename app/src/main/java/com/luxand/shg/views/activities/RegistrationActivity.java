package com.luxand.shg.views.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.luxand.shg.R;
import com.luxand.shg.views.fragments.registration.RegistrationFragment1;


public class RegistrationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setRegistrationFragment(new RegistrationFragment1(),null);
    }

    public void setRegistrationFragment(Fragment fragment,Bundle bundle){
        fragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container_registration,fragment).addToBackStack("fragment").commit();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}
