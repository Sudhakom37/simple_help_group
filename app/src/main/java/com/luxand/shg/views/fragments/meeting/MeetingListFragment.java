package com.luxand.shg.views.fragments.meeting;

import android.content.Intent;
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
import com.luxand.shg.model.ListData;
import com.luxand.shg.views.activities.CreateMeetingActivity;
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.adapters.AdapterMeeting;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.Meeting;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.views.activities.division_offcer.DivisionOfficerMainActivity;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MeetingListFragment extends Fragment{

    private static final String TAG = "MeetingListFragment";
        ImageView iv_create_group_meeting;
        MySharedPreference preference;
        AdapterMeeting adapterMeeting;
        RecyclerView rvMeeting;
        List<ListData> dataList;
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_meeting_list, container, false);
            rvMeeting = view.findViewById(R.id.rv_meeting_list);
            iv_create_group_meeting = view.findViewById(R.id.iv_create_group_meeting);
            preference = new MySharedPreference(getActivity());
            ((MainActivity)getActivity()).tv_title.setText("Meeting");
            ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
            ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
            getMeetingList();
            rvMeeting.setLayoutManager(new LinearLayoutManager(getActivity()));

            iv_create_group_meeting.setOnClickListener(view1 -> {
                Intent intent = new Intent(getActivity(), CreateMeetingActivity.class);
                startActivity(intent);
            });

            rvMeeting.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvMeeting, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    preference.setPref(PrefKeys.MEETING_ID,String.valueOf(dataList.get(position).getMeetingsId()));
                    ((MainActivity)getActivity()).setHomeFragment(new MeetingDetailsFragment(),null);
                    //startActivity(new Intent(getActivity(),MeetingPhotosActivity.class));
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
                    .getMeetingList(preference.getPref(PrefKeys.GROUP_ID))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new rx.Observer<Meeting>() {
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
                        public void onNext(Meeting meeting) {
                            ProgressBarDialog.cancelLoading();
                            dataList = meeting.getData();
                            adapterMeeting = new AdapterMeeting(getActivity(),meeting.getData(),3);
                            rvMeeting.setAdapter(adapterMeeting);
                            adapterMeeting.notifyDataSetChanged();

                        }
                    });

        }


}
