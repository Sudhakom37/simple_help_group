package com.luxand.shg.views.fragments.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.luxand.shg.R;
import com.luxand.shg.views.activities.MainActivity;

import java.util.Objects;


public class RoleSelectionFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_role_selection,container,false);

        Button btGo = view.findViewById(R.id.bt_role_go);
        Spinner spinner = view.findViewById(R.id.sp_role);
        ((MainActivity)getActivity()).tv_title.setText("Registration");
        String[] roles = new String[]{"District Manager","Mandal Manager","Group Leader","Group Member"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),android.R.layout.simple_spinner_item,roles);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        btGo.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });
        return view;
    }
}
