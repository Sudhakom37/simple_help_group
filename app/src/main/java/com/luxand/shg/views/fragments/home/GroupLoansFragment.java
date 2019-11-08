package com.luxand.shg.views.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.adapters.AdapterGroupLoans;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.GroupLoan;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.GroupLoansViewModel;
import com.luxand.shg.views.activities.MainActivity;


import java.util.List;
import java.util.Objects;

public class GroupLoansFragment extends Fragment {

    private static final String TAG = "GroupLoansFragment";
    private RecyclerView rv_member_savings_list;
    public static final String DISTRIBUTION= "Distribution";
    public static final String REPAYMENT= "Repayment";
    MySharedPreference preference ;
    List<GroupLoan.Results> data;
    AdapterGroupLoans adapterHome;
    Button btDistribution,btRepayment;
    TextView tv_distribution;
    String type = DISTRIBUTION;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_loans, container, false);
        rv_member_savings_list = view.findViewById(R.id.rv_member_savings_list);
        btDistribution = view.findViewById(R.id.bt_distribution);
        btRepayment = view.findViewById(R.id.bt_repayment);
        tv_distribution = view.findViewById(R.id.tv_distribution);
        preference = new MySharedPreference(getActivity());
        GroupLoansViewModel viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(GroupLoansViewModel.class);
        viewModel.getGroupLoans(preference.getPref(PrefKeys.GROUP_ID));
        ((MainActivity)getActivity()).tv_title.setText("Loans");
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);

        getHomeData(viewModel);

        rv_member_savings_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rvHome.addItemDecoration(new GridDividerDecoration(getActivity()));

        btDistribution.setOnClickListener(view1 -> {
            btDistribution.setBackground(getActivity().getDrawable(R.drawable.group_loan_distribution_bg));
            btDistribution.setTextColor(getActivity().getColor(R.color.white));
            btRepayment.setBackground(getActivity().getDrawable(R.drawable.views_background));
            btRepayment.setTextColor(getActivity().getColor(R.color.black));
            tv_distribution.setText(DISTRIBUTION);
            type = DISTRIBUTION;
            adapterHome.setState(0);
        });
        btRepayment.setOnClickListener(view12 -> {
            btRepayment.setBackground(getActivity().getDrawable(R.drawable.group_loan_distribution_bg));
            btRepayment.setTextColor(getActivity().getColor(R.color.white));
            btDistribution.setBackground(getActivity().getDrawable(R.drawable.views_background));
            btDistribution.setTextColor(getActivity().getColor(R.color.black));
            tv_distribution.setText(REPAYMENT);
            type = REPAYMENT;
            adapterHome.setState(1);

        });

        rv_member_savings_list.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_member_savings_list, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(PrefKeys.GROUP_LOAN_ID,String.valueOf(data.get(position).getGroupLoanId()));
                //bundle.putString(PrefKeys.LOAN_ID,String.valueOf(data.get(position).getLoan_id()));

                bundle.putString(PrefKeys.USER_TYPE,type);
                bundle.putInt("Start",1);
                ((MainActivity)getActivity()).setHomeFragment(new GroupLoanDetailsFragment(),bundle);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }


    private void getHomeData(GroupLoansViewModel viewModel){
        ProgressBarDialog.showLoadingDialog(getActivity());
        viewModel.getData().observe(Objects.requireNonNull(getActivity()), homeModel -> {

            if(homeModel!=null && homeModel.getData() != null) {

                data = homeModel.getData().getResults();
                adapterHome = new AdapterGroupLoans(getActivity(), data,1);
                rv_member_savings_list.setAdapter(adapterHome);
                adapterHome.notifyDataSetChanged();
                ProgressBarDialog.cancelLoading();
            }
        });

    }
}
