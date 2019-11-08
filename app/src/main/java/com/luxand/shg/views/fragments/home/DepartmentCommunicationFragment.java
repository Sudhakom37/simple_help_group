package com.luxand.shg.views.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.FeedbacksModel;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.DepartmentCommunicationViewModel;
import com.luxand.shg.views.activities.MainActivity;

import java.util.List;
import java.util.Objects;

public class DepartmentCommunicationFragment extends Fragment {

    private static final String TAG = "DepartmentCommunication";
    private RecyclerView rvFbAudio,rvFbVideo;
    DepartmentCommunicationViewModel viewModel;
    private List<FeedbacksModel.Audios> audiosList;
    private List<FeedbacksModel.Videos> videosList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_department_comminication, container, false);

        rvFbAudio = view.findViewById(R.id.rv_fb_audio);
        rvFbVideo = view.findViewById(R.id.rv_fb_video);
        MySharedPreference preference = new MySharedPreference(Objects.requireNonNull(getActivity()));
        ((MainActivity)getActivity()).tv_title.setText(R.string.feedback);
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
        rvFbAudio.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFbVideo.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewModel = ViewModelProviders.of(getActivity()).get(DepartmentCommunicationViewModel.class);
        viewModel.getDepartmentCommunicationData(preference.getPref(PrefKeys.GROUP_ID));
        getDepartmentCommunicationData(viewModel);

        rvFbVideo.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvFbVideo, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle= new Bundle();
                bundle.putString(PrefKeys.VIDEO_URL,videosList.get(position).getFilePath());
                bundle.putString(PrefKeys.NAME,videosList.get(position).getCreatedDate());
                ((MainActivity) Objects.requireNonNull(getActivity())).setHomeFragment(new PlayTrainingVideosFragment(),bundle);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        rvFbAudio.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvFbVideo, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle= new Bundle();
                bundle.putString(PrefKeys.AUDIO_URL,audiosList.get(position).getFilePath());
                //bundle.putString(PrefKeys.NAME,audiosList.get(position).getCreatedDate());
                ((MainActivity)getActivity()).setHomeFragment(new PlayAudioFragment(),bundle);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return view;
    }


    private void getDepartmentCommunicationData(DepartmentCommunicationViewModel viewModel){
        ProgressBarDialog.showLoadingDialog(getActivity());
        viewModel.getData().observe(Objects.requireNonNull(getActivity()), tipsModel -> {

            if(tipsModel!=null && tipsModel.getData() != null) {
                Log.d(TAG, "onChanged: "+tipsModel.getData());
                audiosList = tipsModel.getData().getAudios();
                videosList = tipsModel.getData().getVideos();

                rvFbAudio.setAdapter(new AudioAdapter());
                rvFbVideo.setAdapter(new VideoAdapter());
                ProgressBarDialog.cancelLoading();
            }
        });

    }

    class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

        @NonNull
        @Override
        public AudioAdapter.AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View view = inflater.inflate(R.layout.adapter_feedback_header, parent, false);
            return new AudioViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AudioAdapter.AudioViewHolder holder, int position) {
            holder.tv_feedback_date.setText(audiosList.get(position).getCreatedDate());
            holder.tv_feedback_duration.setText(audiosList.get(position).getDuration());
        }

        @Override
        public int getItemCount() {
            return audiosList.size();
        }

        class AudioViewHolder extends RecyclerView.ViewHolder {
            TextView tv_feedback_date, tv_feedback_duration;
            AudioViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_feedback_date = itemView.findViewById(R.id.tv_feedback_date);
                tv_feedback_duration = itemView.findViewById(R.id.tv_feedback_duration);
            }
        }
    }
    class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

        @NonNull
        @Override
        public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View view = inflater.inflate(R.layout.adapter_feedback_footer, parent, false);
            return new VideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VideoAdapter.VideoViewHolder holder, int position) {

            holder.tv_feedback_date_time.setText(videosList.get(position).getCreatedDate());
            holder.tv_feedback_video_size.setText(videosList.get(position).getDuration());
        }

        @Override
        public int getItemCount() {
            return videosList.size();
        }
        class VideoViewHolder extends RecyclerView.ViewHolder {
            TextView tv_feedback_date_time,tv_feedback_video_size;
            VideoViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_feedback_date_time = itemView.findViewById(R.id.tv_feedback_date_time);
                tv_feedback_video_size = itemView.findViewById(R.id.tv_feedback_video_size);
            }
        }
    }


}
