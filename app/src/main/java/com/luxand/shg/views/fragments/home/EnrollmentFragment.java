package com.luxand.shg.views.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luxand.shg.R;
import com.luxand.shg.views.activities.MainActivity;


public class EnrollmentFragment extends Fragment {


    EditText etName,etAge,etMobile;
    Spinner spGender,spVillage;

    Button btCancel,btSubmit;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrollment, container, false);
        initViews(view);
        ((MainActivity)getActivity()).tv_title.setText("Enrollment");
        return view;
    }

    private void initViews(@NonNull View view){
        etName = view.findViewById(R.id.et_member_name);
        etAge = view.findViewById(R.id.et_member_age);
        etMobile = view.findViewById(R.id.et_member_mobile);
        spGender = view.findViewById(R.id.sp_gender);
        spVillage = view.findViewById(R.id.et_village);
        btCancel = view.findViewById(R.id.bt_cancel_enrollment);
        btSubmit = view.findViewById(R.id.bt_submit_enrollment);


    }
}
