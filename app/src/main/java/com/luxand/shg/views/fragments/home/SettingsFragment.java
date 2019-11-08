package com.luxand.shg.views.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luxand.shg.R;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.views.activities.division_offcer.DivisionOfficerMainActivity;

public class SettingsFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        RadioButton rbFrontCamera = view.findViewById(R.id.rb_front_camera);
        RadioButton rbBackCamera = view.findViewById(R.id.rb_back_camera);
        MySharedPreference preference = new MySharedPreference(getActivity());
        if(preference.getPref(PrefKeys.ROLE_ID).equalsIgnoreCase("6")){
            ((DivisionOfficerMainActivity)getActivity()).tvTitle.setText("Settings");
            ((DivisionOfficerMainActivity)getActivity()).ivMenuOpen.setVisibility(View.GONE);
            ((DivisionOfficerMainActivity)getActivity()).ivMenuBack.setVisibility(View.VISIBLE);
        }else{
            ((MainActivity)getActivity()).tv_title.setText("Settings");
            ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
            ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
        }



        if(preference.getPref(PrefKeys.CAM_CONFIG).equalsIgnoreCase("front")){
            //rbBackCamera.setSelected(false);
            rbFrontCamera.setChecked(true);
        }else{
            //rbFrontCamera.setSelected(false);
            rbBackCamera.setChecked(true);
        }
        rbBackCamera.setOnClickListener(view1 -> preference.setPref(PrefKeys.CAM_CONFIG,"back"));
        rbFrontCamera.setOnClickListener(view12 -> preference.setPref(PrefKeys.CAM_CONFIG,"front"));

        return view;
    }
}
