package com.luxand.shg.views.fragments.samuha_sakhi;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.adapters.DivisionMeetingAdapter;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.GroupsModel;
import com.luxand.shg.model.ListData;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.views.activities.division_offcer.DivisionOfficerMainActivity;
import com.luxand.shg.views.activities.division_offcer.DivisionRootMapActivity;
import com.luxand.shg.views.activities.division_offcer.MeetingIdentificationActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DivisionMeetingListFragment extends Fragment {
    private static final String TAG = "DivisionMeetingListFragment";
    ImageView iv_create_group_meeting;
    MySharedPreference preference;
    DivisionMeetingAdapter adapterMeeting;
    RecyclerView rvMeeting;
    List<ListData> dataList;
    Dialog dialog_offline;
    ImageView ivMenuOpen,ivMenuClose;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_list, container, false);
        rvMeeting = view.findViewById(R.id.rv_meeting_list);
        iv_create_group_meeting = view.findViewById(R.id.iv_create_group_meeting);

        preference = new MySharedPreference(getActivity());
        ((DivisionOfficerMainActivity)getActivity()).tvTitle.setText("Meeting");
        ((DivisionOfficerMainActivity)getActivity()).ivMenuOpen.setVisibility(View.GONE);
        ((DivisionOfficerMainActivity)getActivity()).ivMenuBack.setVisibility(View.VISIBLE);



        getMeetingList();
        rvMeeting.setLayoutManager(new LinearLayoutManager(getActivity()));

        iv_create_group_meeting.setOnClickListener(view1 -> {
            openDialog();

        });

        rvMeeting.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvMeeting, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                preference.setPref(PrefKeys.MEETING_ID,String.valueOf(dataList.get(position).getMeetingsId()));
                if(dataList.get(position).getStatus().equalsIgnoreCase("Started")){
                    Intent intent = new Intent(getActivity(), DivisionStartMeetingMapActivity.class);
                    intent.putExtra("Action","Start Meeting");
                    startActivity(intent);
                }else if(dataList.get(position).getStatus().equalsIgnoreCase("InProgress")){

                    Intent intent = new Intent(getActivity(), MeetingIdentificationActivity.class);
                    intent.putExtra("Action","End Meeting");
                    startActivity(intent);

                }else{
                    Intent intent = new Intent(getActivity(), DivisionRootMapActivity.class);
                    startActivity(intent);
                }
                //((DivisionOfficerMainActivity)getActivity()).setHomeFragment(new MeetingDetailsFragment(),null);
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
                .getDivisionGroupMeetings(preference.getPref(PrefKeys.GROUP_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<GroupsModel>() {
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
                    public void onNext(GroupsModel meeting) {
                        ProgressBarDialog.cancelLoading();
                        dataList = meeting.getData();
                        adapterMeeting = new DivisionMeetingAdapter(getActivity(),meeting.getData());
                        rvMeeting.setAdapter(adapterMeeting);
                        adapterMeeting.notifyDataSetChanged();

                    }
                });

    }

    void openDialog(){
        dialog_offline = new Dialog(getActivity());
        dialog_offline.setCancelable(false);
        Window window = dialog_offline.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog_offline.setContentView(R.layout.layout_confirm_meeting_dialog);
        Button btn_ok =  dialog_offline.findViewById(R.id.btn_ok);
        Button btn_cancel =  dialog_offline.findViewById(R.id.btn_cancel);
        TextView tvDate = dialog_offline.findViewById(R.id.tv_date);
        tvDate.setText(getDate());
        btn_ok.setOnClickListener(v -> {


            dialog_offline.dismiss();
            Intent intent = new Intent(getActivity(), MeetingIdentificationActivity.class);
            intent.putExtra("Action","Start Meeting");
            startActivity(intent);

        });

        btn_cancel.setOnClickListener(v -> dialog_offline.dismiss());
        dialog_offline.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationOpen;
        dialog_offline.show();
    }

    private String getDate(){

        SimpleDateFormat df = new SimpleDateFormat("dd'th' MMM yyyy", Locale.ENGLISH);
        return df.format(new Date(System.currentTimeMillis()));
    }
}
