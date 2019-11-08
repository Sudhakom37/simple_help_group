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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.adapters.AdapterSavings;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.Results;
import com.luxand.shg.model.UserSavings;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.SavingsViewModel;
import com.luxand.shg.views.activities.MainActivity;


import java.util.List;
import java.util.Objects;

public class SavingsFragment extends Fragment {
    private static final String TAG = "SavingsFragment";

    private RecyclerView rvHome;
    MySharedPreference preference ;
    List<UserSavings.Results> results ;
    TextView tv_total_savings_value;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings, container, false);
        rvHome = view.findViewById(R.id.rv_savings_list);
        preference = new MySharedPreference(getActivity());
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
        tv_total_savings_value = view.findViewById(R.id.tv_total_savings_value);
        SavingsViewModel viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SavingsViewModel.class);
        viewModel.getSavingsData(preference.getPref(PrefKeys.GROUP_ID));
        ((MainActivity)getActivity()).tv_title.setText("Savings");
        getSavingsList(viewModel);

        rvHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rvHome.addItemDecoration(new GridDividerDecoration(getActivity()));


        rvHome.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvHome, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(PrefKeys.USER_ID,String.valueOf(results.get(position).getUser_id()));
                bundle.putString(PrefKeys.MEMBER_NAME,String.valueOf(results.get(position).getName()));
                bundle.putInt("Start",1);
                ((MainActivity)getActivity()).setHomeFragment(new SavingsDetailsFragment(),bundle);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }


    private void getSavingsList(SavingsViewModel viewModel){
        ProgressBarDialog.showLoadingDialog(getActivity());
        viewModel.getData().observe(Objects.requireNonNull(getActivity()), homeModel -> {

            if(homeModel!=null && homeModel.getData() != null) {
                results = homeModel.getData().getResults();
                tv_total_savings_value.setText(String.valueOf(homeModel.getData().getTotal()));
                AdapterSavings adapterHome = new AdapterSavings(getContext(), homeModel.getData().getResults(),1);
                rvHome.setAdapter(adapterHome);
                adapterHome.notifyDataSetChanged();
                ProgressBarDialog.cancelLoading();
            }
        });

    }

}
