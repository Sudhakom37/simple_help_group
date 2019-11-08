package com.luxand.shg.views.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.adapters.AdapterDashBoardFeedBack;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.Users;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.DefaultersViewModel;
import com.luxand.shg.views.activities.MainActivity;

import java.util.List;
import java.util.Objects;

public class DefaultersFragment extends Fragment {
    private static final String TAG = "DefaultersFragment";
    private RecyclerView rv_tips;
    MySharedPreference preference;
    List<Users> dataList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_videos, container, false);
        rv_tips = view.findViewById(R.id.rv_tips);
        preference = new MySharedPreference(getActivity());
        DefaultersViewModel viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DefaultersViewModel.class);
        viewModel.getDefaultersList(preference.getPref(PrefKeys.GROUP_ID));
        ((MainActivity)getActivity()).tv_title.setText("Training Videos");
        getHomeData(viewModel);

        rv_tips.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rvHome.addItemDecoration(new GridDividerDecoration(getActivity()));


        rv_tips.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_tips, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                /*Bundle bundle= new Bundle();
                bundle.putString("tip_description",dataList.get(position).g());
                ((MainActivity)getActivity()).setHomeFragment(new TipsDetailsFragment(),bundle);*/
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }


    private void getHomeData(DefaultersViewModel viewModel){
        ProgressBarDialog.showLoadingDialog(getActivity());
        viewModel.getData().observe(Objects.requireNonNull(getActivity()), tipsModel -> {

            if(tipsModel!=null && tipsModel.getData() != null) {
                Log.d(TAG, "onChanged: "+tipsModel.getData());
                dataList = tipsModel.getData();
                AdapterDashBoardFeedBack adapterHome = new AdapterDashBoardFeedBack(getActivity(), tipsModel.getData());
                rv_tips.setAdapter(adapterHome);
                adapterHome.notifyDataSetChanged();
                ProgressBarDialog.cancelLoading();
            }
        });

    }
}
