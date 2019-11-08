package com.luxand.shg.views.fragments.home;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luxand.shg.R;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.views.activities.MainActivity;

import java.io.IOException;


public class PlayAudioFragment extends Fragment implements MediaPlayer.OnPreparedListener{
    private static final String TAG = "PlayAudioFragment";

    String audioUrl;
    private ImageView ivPlayAudio,ivPauseAudio;
    private MySharedPreference preference;

    private MediaPlayer mediaPlayer;
    private boolean isPrepared;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioUrl = getArguments().getString(PrefKeys.AUDIO_URL);
        //videoName = getArguments().getString(PrefKeys.NAME);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_audios,container,false);
        ivPlayAudio = view.findViewById(R.id.iv_play_audio);
        ivPauseAudio = view.findViewById(R.id.iv_pause_audio);
        ((MainActivity)getActivity()).tv_title.setText("Audio");
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mediaPlayer.setDataSource(audioUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mediaPlayer != null){
            mediaPlayer.release();
            //mediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        isPrepared = true;
    }
}
