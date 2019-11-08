package com.luxand.shg.views.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.Users;
import com.luxand.shg.views.activities.ImagePreviewActivity;
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.adapters.AdapterDashBoardFeedBack;
import com.luxand.shg.adapters.AdapterDashBoardImages;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.DashBoard;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.views.fragments.meeting.MeetingDetailsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DashBoardFragment extends Fragment {

    private static final String TAG = "DashBoardFragment";
    private RecyclerView rvDashBoardImages,rvDashBoardFeedBack;
    private TextView tvMeetingsConducted, tvMeetingDate, tvAttendedCount,tvTotalSavings,tvCurrentLoan,tvRepayments;
    LinearLayout ll_last_meeting,ll_total_savings,ll_total_loans;
    CardView card_last_meeting;
    private ImageView ivFirstWeek,ivSecondWeek,ivThirdWeek,ivFourthWeek,ivFifthWeek;
    MySharedPreference preference;
    List<Users> users;
    ArrayList<String> images;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initViews(view);
        ((MainActivity) Objects.requireNonNull(getActivity())).tv_title.setText(R.string.dashboard);
        preference = new MySharedPreference(getActivity());
        rvDashBoardImages.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        rvDashBoardFeedBack.setLayoutManager(new LinearLayoutManager(getActivity()));
        getDashboardDetails(preference.getPref(PrefKeys.GROUP_ID));
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
        rvDashBoardFeedBack.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvDashBoardFeedBack, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getActivity(), users.get(position).getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        rvDashBoardImages.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvDashBoardFeedBack, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent examIntent=new Intent(getActivity(), ImagePreviewActivity.class);
                if (rvDashBoardImages != null) {
                    Log.d(TAG, "onClick: event.getEventImages().toArray()");
                    examIntent.putExtra("position",position);
                    examIntent.putStringArrayListExtra(PrefKeys.IMAGE_PATHS, images);
                }else{
                    Log.d(TAG, "onClick: else ");
                }
                startActivity(examIntent);
                //((BaseActivity)getActivity()).startActivityBase(getActivity(), ImagePreviewActivity.class);
                //Toast.makeText(getActivity(), users.get(position).getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        card_last_meeting.setOnClickListener(view1 -> ((MainActivity)getActivity()).setHomeFragment(new MeetingDetailsFragment(),null));
        ll_total_savings.setOnClickListener(view1 -> ((MainActivity)getActivity()).setHomeFragment(new SavingsFragment(),null));
        ll_total_loans.setOnClickListener(view1 -> ((MainActivity)getActivity()).setHomeFragment(new GroupLoansFragment(),null));

        return view;
    }

    private void initViews(View view) {
        rvDashBoardImages = view.findViewById(R.id.rv_dashboard_images);
        tvMeetingsConducted = view.findViewById(R.id.tv_meetings_conducted);
        tvMeetingDate = view.findViewById(R.id.tv_last_meeting_date);
        tvAttendedCount = view.findViewById(R.id.tv_village_name);
        tvTotalSavings = view.findViewById(R.id.tv_total_savings_value);
        tvCurrentLoan = view.findViewById(R.id.tv_current_loan_value);
        tvRepayments = view.findViewById(R.id.tv_repayments_value);
        rvDashBoardFeedBack = view.findViewById(R.id.rv_group_members_list);
        ivFirstWeek = view.findViewById(R.id.iv_first_week);
        ivSecondWeek = view.findViewById(R.id.iv_second_week);
        ivThirdWeek = view.findViewById(R.id.iv_third_week);
        ivFourthWeek = view.findViewById(R.id.iv_fourth_week);
        ivFifthWeek = view.findViewById(R.id.iv_fifth_week);

        ll_last_meeting = view.findViewById(R.id.ll_last_meeting);
        ll_total_savings = view.findViewById(R.id.ll_total_savings);
        ll_total_loans= view.findViewById(R.id.ll_total_loans);
        card_last_meeting = view.findViewById(R.id.card_last_meeting);

    }
    private void  getDashboardDetails(String group_id){

        Log.d(TAG, "login: object group_id "+group_id);
        ProgressBarDialog.showLoadingDialog(getActivity());
        RetrofitInstance.getInstance(getActivity()).getRestAdapter()
                .getDashBoard(group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DashBoard>() {
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
                    public void onNext(DashBoard dashBoard) {
                        if(dashBoard != null)Log.d(TAG, "onChanged: getMeetingDate"+dashBoard.getData().getMeetingDate());
                        if(dashBoard != null){
                            users = dashBoard.getData().getUsers();
                            images = dashBoard.getData().getMeetingPhotos();
                            DashBoard.Weeks weeks =dashBoard.getData().getWeeks();
                            if(weeks.getWeek1()==1){
                                ivFirstWeek.setVisibility(View.VISIBLE);
                            }else{
                                ivFirstWeek.setVisibility(View.INVISIBLE);
                            }
                            if(weeks.getWeek2()==1){
                                ivSecondWeek.setVisibility(View.VISIBLE);
                            }else{
                                ivSecondWeek.setVisibility(View.INVISIBLE);
                            }
                            if(weeks.getWeek3()==1){
                                ivThirdWeek.setVisibility(View.VISIBLE);
                            }else{
                                ivThirdWeek.setVisibility(View.INVISIBLE);
                            }
                            if(weeks.getWeek4()==1){
                                ivFourthWeek.setVisibility(View.VISIBLE);
                            }else{
                                ivFourthWeek.setVisibility(View.INVISIBLE);
                            }
                            if(weeks.getWeek5()==1){
                                ivFifthWeek.setVisibility(View.VISIBLE);
                            }else{
                                ivFifthWeek.setVisibility(View.INVISIBLE);
                            }
                            tvAttendedCount.setText(String.valueOf(dashBoard.getData().getUsersCount()));
                            tvTotalSavings.setText(String.valueOf(dashBoard.getData().getTotalSavings()));
                            tvCurrentLoan.setText(String.valueOf(dashBoard.getData().getCurrentloans()));
                            tvRepayments.setText(String.valueOf(dashBoard.getData().getRepayment()));
                            preference.setPref(PrefKeys.MEETING_ID,String.valueOf(dashBoard.getData().getMeeting_id()));
                            rvDashBoardFeedBack.setAdapter(new AdapterDashBoardFeedBack(getActivity(),users));
                            rvDashBoardImages.setAdapter(new AdapterDashBoardImages(getActivity(),images));
                            tvMeetingsConducted.setText(String.valueOf(dashBoard.getData().getWeeksCovered()));
                            tvMeetingDate.setText(dashBoard.getData().getMeetingDate());

                            ProgressBarDialog.cancelLoading();
                        }else{
                            ProgressBarDialog.cancelLoading();
                        }

                    }
                });


    }



}
