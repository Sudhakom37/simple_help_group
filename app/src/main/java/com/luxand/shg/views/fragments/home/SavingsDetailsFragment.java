package com.luxand.shg.views.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.adapters.AdapterSavings;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.SavingDetailsViewModel;
import com.luxand.shg.views.activities.MainActivity;


import java.util.Objects;

public class SavingsDetailsFragment extends Fragment {

    private static final String TAG = "SavingsDetailsFragment";

    private RecyclerView rv_member_savings_list;
    TextView tv_total_savings_value,tv_member_name,tv_loan_name;
    MySharedPreference preference;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings_details, container, false);
        rv_member_savings_list = view.findViewById(R.id.rv_member_savings_list);
        tv_total_savings_value = view.findViewById(R.id.tv_total_savings_value);
        tv_member_name  = view.findViewById(R.id.tv_member_name);
        tv_loan_name= view.findViewById(R.id.tv_loan_name);
        preference = new MySharedPreference(getActivity());
        String user_id = getArguments().getString(PrefKeys.USER_ID);
        SavingDetailsViewModel viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SavingDetailsViewModel.class);
        tv_member_name.setText(getArguments().getString(PrefKeys.MEMBER_NAME));

        Log.d(TAG, "onCreateView: user_id "+user_id);
        int from = getArguments().getInt("Start");
        if(from==1){
            //tv_loan_name.setText("Savings Amount :");
            ((MainActivity)getActivity()).tv_title.setText("Savings");
            viewModel.getSavingsDetails(preference.getPref(PrefKeys.GROUP_ID),user_id);
        }else{
            //tv_loan_name.setText("Loan Amount :");
            ((MainActivity)getActivity()).tv_title.setText("Loans");
            viewModel.getGroupUserLoan(user_id);
        }


        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
        getHomeData(viewModel);

        rv_member_savings_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rvHome.addItemDecoration(new GridDividerDecoration(getActivity()));


        rv_member_savings_list.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_member_savings_list, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }


    private void getHomeData(SavingDetailsViewModel viewModel){
        ProgressBarDialog.showLoadingDialog(getActivity());
        viewModel.getData().observe(Objects.requireNonNull(getActivity()), homeModel -> {

            if(homeModel!=null && homeModel.getData() != null) {
                Log.d(TAG, "onChanged: "+homeModel.getData());
                tv_total_savings_value.setText(String.valueOf(homeModel.getData().getTotal()));
                AdapterSavings adapterHome = new AdapterSavings(getActivity(), homeModel.getData().getResults(),2);
                rv_member_savings_list.setAdapter(adapterHome);

                adapterHome.notifyDataSetChanged();
                ProgressBarDialog.cancelLoading();
            }
        });

    }
}
