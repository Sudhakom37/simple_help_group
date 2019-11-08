package com.luxand.shg.views.fragments.vo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.luxand.shg.R;
import com.luxand.shg.model.FeedbackModelVO;
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.views.activities.village_officer.VillageOfficerMainActivity;
import com.luxand.shg.adapters.AdapterFeedback;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.Feedback;
import com.luxand.shg.util.ProgressBarDialog;

import java.util.List;
import java.util.Objects;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class FeedBackListFragmentPo extends Fragment {

    private static final String TAG = "FeedBackListFragmentPo";
    ImageView iv_add_feedback;
    MySharedPreference preference;
    RecyclerView rvFeedback;
    AdapterFeedback adapterFeedback;
    private List<FeedbackModelVO.Data> dataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_list, container, false);
        rvFeedback = view.findViewById(R.id.rv_feedback_list1);
        iv_add_feedback = view.findViewById(R.id.iv_add_feedback);


        preference = new MySharedPreference(getActivity());
        rvFeedback.setLayoutManager(new LinearLayoutManager(getActivity()));
        getMeetingList();
        Log.d(TAG, "onCreateView: "+preference.getPref(PrefKeys.MEETING_ID));
        if(preference.getPref(PrefKeys.ROLE_ID).equalsIgnoreCase("3")){
            ((VillageOfficerMainActivity) Objects.requireNonNull(getActivity())).tv_title.setText("FeedBack");
        }else{
            ((MainActivity) Objects.requireNonNull(getActivity())).tv_title.setText("FeedBack");
        }
        //iv_add_feedback.setOnClickListener(view1 -> ((MainActivity) Objects.requireNonNull(getActivity())).setHomeFragment(new AddFeedbackActivity(),null));
        rvFeedback.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvFeedback, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }

    private void getMeetingList(){

        ProgressBarDialog.showLoadingDialog(getActivity());
        RetrofitInstance.getInstance(getActivity())
                .getRestAdapter()
                .getFeedbackListVO(preference.getPref(PrefKeys.MEETING_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<FeedbackModelVO>() {
                    @Override
                    public void onCompleted() {
                        ProgressBarDialog.cancelLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.e(TAG, "onError: "+e.getMessage() );
                    }

                    @Override
                    public void onNext(FeedbackModelVO meeting) {
                        ProgressBarDialog.cancelLoading();
                        dataList = meeting.getData();
                        adapterFeedback = new AdapterFeedback(getActivity(),dataList);
                        rvFeedback.setAdapter(adapterFeedback);

                    }
                });

    }

}
