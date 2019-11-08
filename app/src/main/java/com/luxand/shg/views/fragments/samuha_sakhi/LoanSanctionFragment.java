package com.luxand.shg.views.fragments.samuha_sakhi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.luxand.shg.R;
import com.luxand.shg.adapters.AdapterGroupLoans;
import com.luxand.shg.adapters.AdapterSanctionLoan;
import com.luxand.shg.adapters.AdapterSavings;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.Results;
import com.luxand.shg.model.SanctionLoanModel;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.SavingsViewModel;
import com.luxand.shg.viewmodel.samooha_sakhi.LoanSanctionViewModel;
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.views.activities.division_offcer.DivisionOfficerMainActivity;
import com.luxand.shg.views.fragments.home.SavingsDetailsFragment;

import java.util.List;
import java.util.Objects;

public class LoanSanctionFragment extends Fragment {
    private RecyclerView rvLoanSanction;
    TextView tv_total_sanction_value;
    ImageView iv_add_loan;
    MySharedPreference preference ;
    List<SanctionLoanModel.Result> results ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loan_sanction,container,false);
        rvLoanSanction = view.findViewById(R.id.rv_group_loan_sanction_list);
        preference = new MySharedPreference(getActivity());
        tv_total_sanction_value = view.findViewById(R.id.tv_total_sanctions_value);
        iv_add_loan = view.findViewById(R.id.iv_add_loan);
        ((DivisionOfficerMainActivity)getActivity()).ivMenuOpen.setVisibility(View.GONE);
        ((DivisionOfficerMainActivity)getActivity()).ivMenuBack.setVisibility(View.VISIBLE);
        ((DivisionOfficerMainActivity)getActivity()).tvTitle.setText("Loans");
        LoanSanctionViewModel viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(LoanSanctionViewModel.class);
        viewModel.getSavingsData(preference.getPref(PrefKeys.USER_ID));
        //((MainActivity)getActivity()).tv_title.setText("Savings");
        getSavingsList(viewModel);

        rvLoanSanction.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rvHome.addItemDecoration(new GridDividerDecoration(getActivity()));

        iv_add_loan.setOnClickListener(view1 -> ((DivisionOfficerMainActivity)getActivity()).setHomeFragment(new AddLoanFragment(),null));
        rvLoanSanction.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvLoanSanction, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
               /* Bundle bundle = new Bundle();
                bundle.putString(PrefKeys.USER_ID,String.valueOf(results.get(position).getUserId()));
                bundle.putString(PrefKeys.MEMBER_NAME,String.valueOf(results.get(position).getName()));
                bundle.putInt("Start",1);
                ((MainActivity)getActivity()).setHomeFragment(new SavingsDetailsFragment(),bundle);*/
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



        return view;
    }
    private void getSavingsList(LoanSanctionViewModel viewModel){
        ProgressBarDialog.showLoadingDialog(getActivity());
        viewModel.getData().observe(Objects.requireNonNull(getActivity()), homeModel -> {

            if(homeModel!=null && homeModel.getData() != null) {
                results = homeModel.getData().getResult();
                tv_total_sanction_value.setText(String.valueOf(homeModel.getData().getTotal()));
                AdapterSanctionLoan adapterHome = new AdapterSanctionLoan(getContext(), homeModel.getData().getResult(),1);
                rvLoanSanction.setAdapter(adapterHome);
                adapterHome.notifyDataSetChanged();
                ProgressBarDialog.cancelLoading();
            }
        });

    }

}
