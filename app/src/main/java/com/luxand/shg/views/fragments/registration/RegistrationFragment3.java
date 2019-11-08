package com.luxand.shg.views.fragments.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luxand.shg.R;
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.views.activities.RegistrationActivity;


public class RegistrationFragment3 extends Fragment {

    private EditText etGroupName,etUserName,etAge;
    private Spinner spGender,spState,spDistrict,spVillage;
    private Button btGo;
    private String[] gender = new String[]{"Male","Fe Male","Other"};
    private String[] state = new String[]{"District Manager","Mandal Manager","Group Leader","Group Member"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registration3,container,false);
        initViews(view);
        ((MainActivity)getActivity()).tv_title.setText("Registration");
        setUpSpinner(spGender,gender);
        setUpSpinner(spState,state);
        setUpSpinner(spDistrict,state);
        setUpSpinner(spVillage,state);



        btGo.setOnClickListener(view1 -> {
            if(getActivity() != null)
            ((RegistrationActivity)getActivity()).setRegistrationFragment(new RoleSelectionFragment(),null);
        });
        return view;
    }

    private void initViews(View v){

        etGroupName = v.findViewById(R.id.et_group_name);
        etUserName = v.findViewById(R.id.et_user_name);
        etAge = v.findViewById(R.id.et_age);
        spGender = v.findViewById(R.id.sp_gender);
        spState = v.findViewById(R.id.sp_state);
        spDistrict = v.findViewById(R.id.sp_district);
        spVillage = v.findViewById(R.id.et_village);
        btGo = v.findViewById(R.id.bt_go);


    }

    private void setUpSpinner(Spinner spinner,String[] roles){

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,roles);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }
}
