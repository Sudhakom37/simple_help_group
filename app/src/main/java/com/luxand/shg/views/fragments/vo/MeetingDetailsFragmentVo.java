package com.luxand.shg.views.fragments.vo;

import android.content.Context;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.CustomJzvd.MyJzvdStd;
import com.luxand.shg.R;
import com.luxand.shg.views.activities.village_officer.VillageOfficerMainActivity;
import com.luxand.shg.adapters.AdapterDashBoardImages;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.MeetingDetailsModel;
import com.luxand.shg.util.CircleTransform;
import com.luxand.shg.util.ProgressBarDialog;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import cn.jzvd.Jzvd;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MeetingDetailsFragmentVo extends Fragment implements MediaPlayer.OnPreparedListener {

    private static final String TAG = "MeetingDetailFragmentV";
    private TextView tvMeetingDate, tvVillageName,tvAudioDate,tvAudioDuration,tvVideoDate,tvVideoDuration;
    private RecyclerView rvMeetingPhotos,rvMeetingUserList;
    private ImageView ivPlayAudio,ivPauseAudio;
    private MySharedPreference preference;

    private MyJzvdStd myJzvdStd;
    private MediaPlayer mediaPlayer;
    private boolean isPrepared;
    private String audioUrl = ""; // your URL here

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_details_vo, container, false);
        initViews(view);
        preference = new MySharedPreference(getActivity());
        getMeetingDetails(preference.getPref(PrefKeys.GROUP_ID),preference.getPref(PrefKeys.MEETING_ID));
        ((VillageOfficerMainActivity)getActivity()).tv_title.setText("Meeting");
        rvMeetingPhotos.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        rvMeetingUserList.setLayoutManager(new GridLayoutManager(getActivity(),4));

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            Log.d(TAG, "onCompletion: mediaPlayer"+mediaPlayer.getDuration());
            ivPauseAudio.setVisibility(View.GONE);
            ivPlayAudio.setVisibility(View.VISIBLE);
        });

        mediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
            Log.e(TAG, "onError: mediaPlayer"+mediaPlayer.getDuration());
            //mediaPlayer.release();

            ivPauseAudio.setVisibility(View.GONE);
            ivPlayAudio.setVisibility(View.VISIBLE);

            return false;
        });
        ivPlayAudio.setOnClickListener(view13 -> {

           ivPlayAudio.setVisibility(View.GONE);
           ivPauseAudio.setVisibility(View.VISIBLE);
            if(mediaPlayer != null && isPrepared) {
                mediaPlayer.start();
            }
            //isAudioPlaying = true;
        });
        ivPauseAudio.setOnClickListener(view12 -> {
            ivPauseAudio.setVisibility(View.GONE);
            ivPlayAudio.setVisibility(View.VISIBLE);
            if(mediaPlayer != null){
                mediaPlayer.stop();
                //mediaPlayer.release();
            }
        });
        mediaPlayer.setOnPreparedListener(this);
        myJzvdStd.setOnClickListener(view1 -> myJzvdStd.startVideo());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getMeetingDetails(preference.getPref(PrefKeys.GROUP_ID),preference.getPref(PrefKeys.MEETING_ID));
        //mediaPlayer.setOnPreparedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
        if(mediaPlayer != null){
            //mediaPlayer.pause();
            //mediaPlayer.release();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mediaPlayer != null){
            mediaPlayer.release();
            //mediaPlayer = null;
        }
    }

    private void initViews(View view){
        tvMeetingDate = view.findViewById(R.id.tv_meeting_date);
        tvVillageName = view.findViewById(R.id.tv_village_name);
        tvAudioDate= view.findViewById(R.id.tv_audio_date);
        tvAudioDuration= view.findViewById(R.id.tv_audio_duration);
        tvVideoDate= view.findViewById(R.id.tv_video_date);
        tvVideoDuration= view.findViewById(R.id.tv_video_duration);

        ivPlayAudio = view.findViewById(R.id.iv_play_audio);
        ivPauseAudio = view.findViewById(R.id.iv_pause_audio);
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

                        if(meetingDetailsModel != null){
                            rvMeetingUserList.setAdapter(new AdapterMeetingMember(meetingDetailsModel.getData().getUsers()));
                            rvMeetingPhotos.setAdapter(new AdapterDashBoardImages(getActivity(),meetingDetailsModel.getData().getImages()));
                            tvVillageName.setText(meetingDetailsModel.getData().getVillageName());

                            tvMeetingDate.setText(meetingDetailsModel.getData().getMeetingDate());
                            tvAudioDate.setText(meetingDetailsModel.getData().getAudio().get(0).getTime());
                            tvAudioDuration.setText(meetingDetailsModel.getData().getAudio().get(0).getSize());
                            Log.d(TAG, "onNext: video  "+meetingDetailsModel.getData().getVideo());
                            if(meetingDetailsModel.getData().getVideo()!=null){
                                tvVideoDate.setText(meetingDetailsModel.getData().getVideo().get(0).getTime());
                                tvVideoDuration.setText(meetingDetailsModel.getData().getVideo().get(0).getSize());

                                myJzvdStd.setUp(meetingDetailsModel.getData().getVideo().get(0).getFile(),"");
                            }

                            audioUrl = meetingDetailsModel.getData().getAudio().get(0).getFile();
                            Log.d(TAG, "onNext: meetingDetailsModel"+meetingDetailsModel.getData().getAudio().get(0).getFile());
                            try {
                                mediaPlayer.setDataSource(audioUrl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)

                        }
                        ProgressBarDialog.cancelLoading();
                        //mut_data.postValue(homeModel);
                    }
                });


    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        isPrepared = true;
    }


    class AdapterMeetingMember extends RecyclerView.Adapter<AdapterMeetingMember.ViewHolderMeeting> {


        private List<MeetingDetailsModel.Users> data;

        AdapterMeetingMember( List<MeetingDetailsModel.Users> data) {
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
                        .placeholder(R.drawable.ic_user)
                        .rotate(270f)
                        .into(holder.iv_member_image);
                if(data.get(position).getIsPresent()==1){
                    holder.ivCheckSelect.setVisibility(View.GONE);
                    holder.iv_check.setVisibility(View.VISIBLE);
                }else{
                    holder.iv_check.setVisibility(View.GONE);
                    holder.ivCheckSelect.setVisibility(View.VISIBLE);
                }
            }



        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        class ViewHolderMeeting extends RecyclerView.ViewHolder {

            TextView tv_member_name;
            ImageView iv_member_image,iv_check,ivCheckSelect;
            ViewHolderMeeting(@NonNull View itemView) {
                super(itemView);

                tv_member_name = itemView.findViewById(R.id.tv_member_name);
                iv_member_image = itemView.findViewById(R.id.iv_member_image);
                iv_check = itemView.findViewById(R.id.iv_check);
                ivCheckSelect = itemView.findViewById(R.id.iv_check_select);
            }
        }
    }



}
