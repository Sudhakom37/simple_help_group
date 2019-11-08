package com.luxand.shg.views.fragments.meeting;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

import com.bumptech.glide.Glide;
import com.luxand.shg.CustomJzvd.MyJzvdStd;
import com.luxand.shg.R;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.views.activities.BaseActivity;
import com.luxand.shg.views.activities.ImagePreviewActivity;
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.adapters.AdapterDashBoardImages;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.MeetingDetailsModel;
import com.luxand.shg.util.CircleTransform;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.MeetingDetailsViewModel;
import com.luxand.shg.views.activities.MeetingPhotosActivity;
import com.luxand.shg.views.activities.division_offcer.DivisionOfficerMainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MeetingDetailsFragment extends Fragment {

    private static final String TAG = "MeetingDetailsFragment";
    TextView tvMeetingDate,tvUserCount;
    RecyclerView rvMeetingPhotos,rvMeetingUserList;
    ImageView iv_group_image,ivPlayAudio,ivPauseAudio,iv_meting_camera;
    MySharedPreference preference;

    MyJzvdStd myJzvdStd;
    MediaPlayer mediaPlayer;
    boolean isPrepared;
    String audioUrl = ""; // your URL here
    MeetingDetailsViewModel viewModel;
    ArrayList<String> images;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_details, container, false);
        initViews(view);
        preference = new MySharedPreference(getActivity());
        viewModel = ViewModelProviders.of(getActivity()).get(MeetingDetailsViewModel.class);
        getMeetingDetails(preference.getPref(PrefKeys.GROUP_ID),preference.getPref(PrefKeys.MEETING_ID));
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
       // ((MainActivity)getActivity()).tv_title.setText("Meeting");
        rvMeetingPhotos.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        rvMeetingUserList.setLayoutManager(new GridLayoutManager(getActivity(),4));
        iv_meting_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MeetingPhotosActivity.class);
                getActivity().startActivity(intent);
            }
        });
        rvMeetingPhotos.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvMeetingPhotos, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
                intent.putStringArrayListExtra(PrefKeys.IMAGE_PATHS,images);
                intent.putExtra("position",1);
                getActivity().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getMeetingDetails(preference.getPref(PrefKeys.GROUP_ID),preference.getPref(PrefKeys.MEETING_ID));
        //mediaPlayer.setOnPreparedListener(this);
    }


    private void initViews(View view){
        tvMeetingDate = view.findViewById(R.id.tv_meeting_date);
        tvUserCount = view.findViewById(R.id.tv_user_count);
        iv_group_image = view.findViewById(R.id.iv_group_image);
        ivPlayAudio = view.findViewById(R.id.iv_play_audio);
        ivPauseAudio = view.findViewById(R.id.iv_pause_audio);
        iv_meting_camera = view.findViewById(R.id.iv_meting_camera);
        myJzvdStd = view.findViewById(R.id.jz_video);
        rvMeetingPhotos = view.findViewById(R.id.rv_meeting_photos);
        rvMeetingUserList = view.findViewById(R.id.rv_meeting_user_list);


    }
    private void  getMeetingDetails(String group_id, String meeting_id){

        Log.d(TAG, "login: object group_id "+group_id+" meeting id "+meeting_id);
        ProgressBarDialog.showLoadingDialog(getActivity());
        RetrofitInstance.getInstance(getActivity()).getRestAdapter()
                .getMeetingDetails(group_id,meeting_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MeetingDetailsModel>() {
                    @Override
                    public void onCompleted() {
                        ProgressBarDialog.cancelLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        ProgressBarDialog.cancelLoading();
                        //progress_Dialog(false);
                    }

                    @Override
                    public void onNext(MeetingDetailsModel meetingDetailsModel) {
                        //if(meetingDetailsModel != null)Log.d(TAG, "onChanged: getAudio"+meetingDetailsModel.getData().getAudio());
                        if(meetingDetailsModel != null){
                            images = meetingDetailsModel.getData().getImages();
                            rvMeetingUserList.setAdapter(new AdapterMeetingMember(meetingDetailsModel.getData().getUsers()));
                            rvMeetingPhotos.setAdapter(new AdapterDashBoardImages(getActivity(),meetingDetailsModel.getData().getImages()));
                            tvUserCount.setText(String.valueOf(meetingDetailsModel.getData().getUsers().size()));

                            tvMeetingDate.setText(meetingDetailsModel.getData().getMeetingDate());
                            /*Glide.with(getActivity())
                                    .load(meetingDetailsModel.getData().getImages().get(0))
                                    .into(iv_group_image);*/


                        }
                        ProgressBarDialog.cancelLoading();
                        //mut_data.postValue(homeModel);
                    }
                });


    }

    class AdapterMeetingMember extends RecyclerView.Adapter<AdapterMeetingMember.ViewHolderMeeting> {


        private List<MeetingDetailsModel.Users> data;

        public AdapterMeetingMember( List<MeetingDetailsModel.Users> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public AdapterMeetingMember.ViewHolderMeeting onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.adapter_enrollment,parent,false);
            return new AdapterMeetingMember.ViewHolderMeeting(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterMeetingMember.ViewHolderMeeting holder, int position) {
            holder.tv_member_name.setText(data.get(position).getName());
            if(data.get(position).getImage()!=null && !data.get(position).getImage().equalsIgnoreCase("")){
                Picasso.with(getActivity()).load(data.get(position).getImage())
                        .transform(new CircleTransform())
                        .rotate(270f)
                        .placeholder(R.drawable.ic_user)
                        .into(holder.iv_member_image);
            }


            if(data.get(position).getIsPresent()==1){
                holder.ivCheckSelect.setVisibility(View.GONE);
                holder.iv_check.setVisibility(View.VISIBLE);
            }else{
                holder.iv_check.setVisibility(View.GONE);
                holder.ivCheckSelect.setVisibility(View.VISIBLE);
            }



        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        class ViewHolderMeeting extends RecyclerView.ViewHolder {

            TextView tv_member_name;
            ImageView iv_member_image,iv_check,ivCheckSelect;
            public ViewHolderMeeting(@NonNull View itemView) {
                super(itemView);

                tv_member_name = itemView.findViewById(R.id.tv_member_name);
                iv_member_image = itemView.findViewById(R.id.iv_member_image);
                iv_check = itemView.findViewById(R.id.iv_check);
                ivCheckSelect = itemView.findViewById(R.id.iv_check_select);
            }
        }
    }



}
