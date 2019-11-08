package com.luxand.shg.views.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luxand.shg.CustomJzvd.MyJzvdStd;
import com.luxand.shg.R;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.views.activities.MainActivity;

import cn.jzvd.Jzvd;

public class PlayTrainingVideosFragment extends Fragment {

    String videoUrl,videoName;
    private MyJzvdStd myJzvdStd;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoUrl = getArguments().getString(PrefKeys.VIDEO_URL);
        videoName = getArguments().getString(PrefKeys.NAME);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_videos,container,false);
        myJzvdStd = view.findViewById(R.id.jz_video);
        ((MainActivity)getActivity()).tv_title.setText("Video");
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
        myJzvdStd.setUp(videoUrl,videoName);
        myJzvdStd.setOnClickListener(view1 -> myJzvdStd.startVideo());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        myJzvdStd.startVideo();
    }

    @Override
    public void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();

    }
}
