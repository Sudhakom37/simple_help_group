package com.luxand.shg.views.fragments.home;

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
import com.luxand.shg.views.activities.ForgotPinActivity;
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.views.activities.GroupMemberEnrollmentList_Activity;
import com.luxand.shg.adapters.AdapterHome;

import com.luxand.shg.views.fragments.meeting.MeetingListFragment;
import com.luxand.shg.views.fragments.vo.FeedBackListFragmentPo;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.HomeViewModel;

import java.util.Objects;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView rvHome;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvHome = view.findViewById(R.id.rv_home);
        HomeViewModel viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(HomeViewModel.class);
        viewModel.getHomeData("4");
        ((MainActivity)getActivity()).tv_title.setText(R.string.home);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.VISIBLE);
        getHomeData(viewModel);

        rvHome.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        //rvHome.addItemDecoration(new GridDividerDecoration(getActivity()));


        rvHome.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvHome, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    case 0:
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).setHomeFragment(new DashBoardFragment(), null);
                        break;
                    case 1:
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).setHomeFragment(new MeetingListFragment(), null);


                        break;
                    case 2:
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).setHomeFragment(new SavingsFragment(), null);

                        //((MainActivity) getActivity()).setHomeFragment(new EnrollmentListFragment(), null);
                        break;
                    case 3:
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).setHomeFragment(new GroupLoansFragment(), null);
                        break;
                    case 4:
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).setHomeFragment(new TipsFragment(), null);
                        break;
                    case 5:
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).setHomeFragment(new TrainingVideosFragment(), null);
                        break;case 6:
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).setHomeFragment(new DepartmentCommunicationFragment(), null);
                        break;
                    case 7:
                        startActivity(new Intent(getActivity(), GroupMemberEnrollmentList_Activity.class));break;
                        case 8:
                            startActivity(new Intent(getActivity(), ForgotPinActivity.class));
                        break;
                        case 9:
                            getActivity().finish();
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


    private void getHomeData(HomeViewModel viewModel){
        ProgressBarDialog.showLoadingDialog(getActivity());
        viewModel.getData().observe(Objects.requireNonNull(getActivity()), homeModel -> {

            if(homeModel!=null && homeModel.getData() != null) {
                Log.d(TAG, "onChanged: "+homeModel.getData());
                AdapterHome adapterHome = new AdapterHome(getActivity(), homeModel.getData());
                rvHome.setAdapter(adapterHome);
                adapterHome.notifyDataSetChanged();
                ProgressBarDialog.cancelLoading();
            }
        });

    }


}
