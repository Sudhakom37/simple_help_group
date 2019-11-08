package com.luxand.shg.views.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.adapters.AdapterGroupLoans;
import com.luxand.shg.adapters.AdapterUserLoans;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.GroupLoan;
import com.luxand.shg.model.GroupUserLoan;
import com.luxand.shg.model.Results;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.GroupLoansViewModel;
import com.luxand.shg.viewmodel.GroupLonDetailsViewModel;
import com.luxand.shg.viewmodel.HomeViewModel;
import com.luxand.shg.views.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupLoanDetailsFragment extends Fragment {

    private static final String TAG = "GroupLoanDetails";
    private RecyclerView rv_member_savings_list;
    TextView tv_loan_id,tv_amount_value,tv_distribution;
    ImageView ivAddLoan;
    List<GroupUserLoan.Results> results;
    int group_loan;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_loans_detals, container, false);
        rv_member_savings_list = view.findViewById(R.id.rv_member_savings_list);
        tv_loan_id = view.findViewById(R.id.tv_loan_id);
        ivAddLoan = view.findViewById(R.id.iv_add_loan);
        tv_distribution = view.findViewById(R.id.tv_distribution);
        tv_amount_value = view.findViewById(R.id.tv_amount_value);
        String loan_id = getArguments().getString(PrefKeys.LOAN_ID);
        String group_loan_id = getArguments().getString(PrefKeys.GROUP_LOAN_ID);
        String type = getArguments().getString(PrefKeys.USER_TYPE);;
        //tv_member_name.setText(getArguments().getString(PrefKeys.MEMBER_NAME)+", Savings Amount :");
        Log.d(TAG, "onCreateView: type "+type);
        tv_loan_id.setText("Loan ID : "+loan_id);
        tv_distribution.setText(type);
        GroupLonDetailsViewModel viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(GroupLonDetailsViewModel.class);
        viewModel.getGroupLoans(group_loan_id,type);
        ((MainActivity)getActivity()).tv_title.setText("Loans");
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);


        getHomeData(viewModel);

        ivAddLoan.setOnClickListener(view1 ->{
            Bundle bundle = new Bundle();
            bundle.putInt(PrefKeys.GROUP_LOAN_ID,group_loan);
            if(type != null && type.equalsIgnoreCase(GroupLoansFragment.DISTRIBUTION)){
                ((MainActivity)getActivity()).setHomeFragment(new AddLoanAmountFragment(),bundle);
            }else if(type != null && type.equalsIgnoreCase(GroupLoansFragment.REPAYMENT)){
                ((MainActivity)getActivity()).setHomeFragment(new AddRepaymentAmountFragment(),bundle);
            }

        });

        rv_member_savings_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rvHome.addItemDecoration(new GridDividerDecoration(getActivity()));


        rv_member_savings_list.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_member_savings_list, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(PrefKeys.USER_ID,String.valueOf(results.get(position).getLoanUserId()));
                bundle.putString(PrefKeys.MEMBER_NAME,String.valueOf(results.get(position).getName()));
                bundle.putInt("Start",2);
                ((MainActivity)getActivity()).setHomeFragment(new SavingsDetailsFragment(),bundle);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }


    private void getHomeData(GroupLonDetailsViewModel viewModel){
        ProgressBarDialog.showLoadingDialog(getActivity());
        viewModel.getData().observe(Objects.requireNonNull(getActivity()), homeModel -> {

            if(homeModel!=null && homeModel.getData() != null) {
                Log.d(TAG, "onChanged: "+homeModel.getData());
                //tv_amount_value.setText(String.valueOf(homeModel.getData().getLoanAmount()));
                results = homeModel.getData().getResults();
                group_loan= homeModel.getData().getGroup_loan_id();
                Log.d(TAG, "getHomeData: "+group_loan);
                tv_loan_id.setText("Loan ID : "+homeModel.getData().getLoanId());
                tv_amount_value.setText("Rs."+homeModel.getData().getLoanAmount());
                AdapterUserLoans adapterHome = new AdapterUserLoans(getActivity(), homeModel.getData().getResults());
                rv_member_savings_list.setAdapter(adapterHome);
                adapterHome.notifyDataSetChanged();
                ProgressBarDialog.cancelLoading();
            }else{
                ProgressBarDialog.cancelLoading();
            }
        });

    }


}
