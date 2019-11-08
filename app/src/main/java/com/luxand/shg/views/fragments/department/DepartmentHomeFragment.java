package com.luxand.shg.views.fragments.department;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.adapters.AdapterDepartmentDashboard;
import com.luxand.shg.adapters.AdapterHome;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.DepartmentHomeViewModel;
import com.luxand.shg.viewmodel.HomeViewModel;
import com.luxand.shg.views.activities.BaseActivity;
import com.luxand.shg.views.activities.ForgotPinActivity;
import com.luxand.shg.views.activities.GroupMemberEnrollmentList_Activity;
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.views.activities.state_officer.DepartmentActiveGroupsActivity;
import com.luxand.shg.views.activities.state_officer.DepartmentDefaultersActivity;
import com.luxand.shg.views.activities.state_officer.DepartmentFeedbackActivity;
import com.luxand.shg.views.activities.state_officer.DepartmentInActiveGroupsActivity;
import com.luxand.shg.views.activities.state_officer.DepartmentLoanActivity;
import com.luxand.shg.views.activities.state_officer.DepartmentSavingsActivity;
import com.luxand.shg.views.activities.state_officer.DepartmentsMeetingsActivity;
import com.luxand.shg.views.activities.state_officer.StateOfficerMainActivity;
import com.luxand.shg.views.fragments.home.DashBoardFragment;
import com.luxand.shg.views.fragments.home.GroupLoansFragment;
import com.luxand.shg.views.fragments.home.SavingsFragment;
import com.luxand.shg.views.fragments.home.TipsFragment;
import com.luxand.shg.views.fragments.home.TrainingVideosFragment;
import com.luxand.shg.views.fragments.meeting.MeetingListFragment;
import com.luxand.shg.views.fragments.vo.FeedBackListFragmentPo;

import java.util.Objects;

public class DepartmentHomeFragment extends Fragment {

    private static final String TAG = "DepartmentHomeFragment";
    private RecyclerView rvHome;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvHome = view.findViewById(R.id.rv_home);
        DepartmentHomeViewModel viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DepartmentHomeViewModel.class);
        viewModel.getHomeData("4");
        ((StateOfficerMainActivity)getActivity()).tv_title.setText(R.string.home);
        ((StateOfficerMainActivity)getActivity()).iv_menu_back.setVisibility(View.GONE);
        ((StateOfficerMainActivity)getActivity()).iv_menu_open.setVisibility(View.VISIBLE);
        getHomeData(viewModel);

        rvHome.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        //rvHome.addItemDecoration(new GridDividerDecoration(getActivity()));


        rvHome.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvHome, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    case 0:
                        ((BaseActivity) Objects.requireNonNull(getActivity())).startActivityBase(getActivity(), DepartmentSavingsActivity.class);

                        break;
                    case 1:
                        ((BaseActivity) Objects.requireNonNull(getActivity())).startActivityBase(getActivity(), DepartmentLoanActivity.class);

                        break;
                    case 2:
                        ((BaseActivity) Objects.requireNonNull(getActivity())).startActivityBase(getActivity(), DepartmentsMeetingsActivity.class);

                        //((MainActivity) getActivity()).setHomeFragment(new EnrollmentListFragment(), null);
                        break;
                    case 3:
                        ((BaseActivity) Objects.requireNonNull(getActivity())).startActivityBase(getActivity(), DepartmentActiveGroupsActivity.class);
                        /*if (getActivity() != null)
                            ((StateOfficerMainActivity) getActivity()).setHomeFragment(new DepartmentActiveGroupsFragment(), null);
                       */ break;
                    case 4:
                        ((BaseActivity) Objects.requireNonNull(getActivity())).startActivityBase(getActivity(), DepartmentInActiveGroupsActivity.class);

                        break;
                    case 5:
                        ((BaseActivity) Objects.requireNonNull(getActivity())).startActivityBase(getActivity(), DepartmentDefaultersActivity.class);

                        break;
                    case 6:
                        ((BaseActivity) Objects.requireNonNull(getActivity())).startActivityBase(getActivity(), DepartmentFeedbackActivity.class);
                        break;
                    default:
                        Toast.makeText(getActivity(), "Nothing to show!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }


    private void getHomeData(DepartmentHomeViewModel viewModel){
        ProgressBarDialog.showLoadingDialog(getActivity());
        viewModel.getData().observe(Objects.requireNonNull(getActivity()), homeModel -> {

            if(homeModel!=null && homeModel.getData() != null) {
                Log.d(TAG, "onChanged: "+homeModel.getData());
                AdapterDepartmentDashboard adapterHome = new AdapterDepartmentDashboard(getActivity(), homeModel.getData());
                rvHome.setAdapter(adapterHome);
                adapterHome.notifyDataSetChanged();
                ProgressBarDialog.cancelLoading();
            }
        });

    }
}
