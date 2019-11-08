package com.luxand.shg.views.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.gson.JsonObject;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.FeedBackResult;
import com.luxand.shg.util.ProgressBarDialog;


import java.io.File;

import cafe.adriel.androidaudiorecorder.AudioRecorderActivity;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.luxand.shg.BuildConfig.APPLICATION_ID;


public class AddFeedbackActivity extends BaseActivity {

    private static final String TAG = "AddFeedbackActivity";

    private static final String EXTRA_FILE_PATH = "filePath";
    private static final String EXTRA_COLOR = "color";
    private static final String EXTRA_SOURCE = "source";
    private static final String EXTRA_CHANNEL = "channel";
    private static final String EXTRA_SAMPLE_RATE = "sampleRate";
    private static final String EXTRA_AUTO_START = "autoStart";
    private static final String EXTRA_KEEP_DISPLAY_ON = "keepDisplayOn";

    static final int REQUEST_VIDEO_CAPTURE = 1;
    MySharedPreference preference;
    String path;
    File file;
    public AddFeedbackActivity() {
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.fragment_add_feedback);
        preference = new MySharedPreference(this);
        //((MainActivity)getActivity()).tv_title.setText("Add FeedBack");
        findViewById(R.id.iv_menu_open).setVisibility(View.GONE);
        findViewById(R.id.iv_menu_back).setVisibility(View.VISIBLE);
       TextView view = findViewById(R.id.tv_title);
       view.setText("Meeting");
        findViewById(R.id.iv_feedback_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecorder();

            }
        });
        findViewById(R.id.iv_feedback_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dispatchTakeVideoIntent();

            }
        });
        findViewById(R.id.iv_menu_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFeedback();

            }
        });
        findViewById(R.id.submit_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFeedback();
            }
        });

    }

    private void checkFeedback(){

        createFeedbackToServer();

    }
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();

    }
    public Uri setImageUri() {
        File file = null;
        File testExistDir = new File(Environment.getExternalStorageDirectory() + "/SHG/Feedback");
        if (testExistDir.exists()) {
            testExistDir.delete();

            File makedir = new File(Environment.getExternalStorageDirectory() + "/SHG/Feedback");
            makedir.mkdir();
            file = new File(Environment.getExternalStorageDirectory() + "/SHG/Feedback","feedback_video"+".mp4");

        } else {

            File makedir = new File(Environment.getExternalStorageDirectory() + "/SHG/Feedback");
            makedir.mkdir();
            file = new File(Environment.getExternalStorageDirectory() + "/SHG/Feedback", "feedback_video" + ".mp4");

        }
        Uri imgUri = null;
        try {
            if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
                imgUri = FileProvider.getUriForFile(this, APPLICATION_ID + ".provider", file);
            } else {
                imgUri = Uri.fromFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (file != null) {
            path = file.getAbsolutePath();
        }
        return imgUri;
    }
    private void openRecorder(){
        String filePath = Environment.getExternalStorageDirectory() + "/recorded_feedback_audio.mp3";
        int color =getResources().getColor(R.color.colorPrimaryDark);
        int requestCode = 0;
        Intent intent = new Intent(AddFeedbackActivity.this, AudioRecorderActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        intent.putExtra(EXTRA_COLOR, color);
        intent.putExtra(EXTRA_SOURCE, AudioSource.MIC);
        intent.putExtra(EXTRA_CHANNEL, AudioChannel.STEREO);
        intent.putExtra(EXTRA_SAMPLE_RATE, AudioSampleRate.HZ_48000);
        intent.putExtra(EXTRA_AUTO_START, true);
        intent.putExtra(EXTRA_KEEP_DISPLAY_ON, true);
        startActivityForResult(intent, requestCode);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                file = new File(Environment.getExternalStorageDirectory() + "/recorded_feedback_audio.mp3");
                Toast.makeText(this, "Audio Recorded!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: "+file.getAbsolutePath());
                // Great! User has recorded and saved the audio file
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
            }
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            //Uri videoUri = data.getResults();
            Toast.makeText(this, "Video Recorded!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onActivityResult: "+path);
            //videoView.setVideoURI(videoUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createFeedbackToServer(){

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        JsonObject final_object = new JsonObject();
        final_object.addProperty("group_id", preference.getPref(PrefKeys.GROUP_ID));
        final_object.addProperty("meetings_id", preference.getPref(PrefKeys.MEETING_ID));

        //File file = new File();
        if(file!= null){
            builder.addFormDataPart("audio", file.getName(),
                    RequestBody.create(okhttp3.MediaType.parse("file/*"), file));
        }
        if(path!=null && !path.equalsIgnoreCase("")){
            File file1 = new File(path);
            builder.addFormDataPart("video", file1.getName(),
                    RequestBody.create(okhttp3.MediaType.parse("file/*"), file1));
        }

                    /*if(offline_list_students.get(i).getStudentDatfile()!=null&&offline_list_students.get(i).getStudentDatfile().length()!=0){
                    File dat_file = new File(offline_list_students.get(i).getStudentDatfile());
                    if(dat_file.exists()){
                        builder.addFormDataPart("dat_files[]", dat_file.getName(),
                                RequestBody.create(okhttp3.MediaType.parse("file/*"), dat_file));
                    }}*/



        //final_object.add("data", array);
        builder.addFormDataPart("content", final_object.toString());

        MultipartBody requestBody = builder.build();
        sendFeedbackToServer(requestBody,this);

    }


    private void sendFeedbackToServer(MultipartBody multipartBody, Context context){

        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .saveMeetingsFeedback(multipartBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FeedBackResult>() {
                    @Override
                    public void onCompleted() {
                        ProgressBarDialog.cancelLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.e(TAG, "onError: sendFeedbackToServer"+e.getMessage() );
                    }

                    @Override
                    public void onNext(FeedBackResult meeting) {

                        Log.d(TAG, "onNext: sendFeedbackToServer"+meeting.getMessage());
                        ProgressBarDialog.cancelLoading();
                        //Toast.makeText(context, "Feedback Submitted Successfully!", Toast.LENGTH_SHORT).show();
                        startActivityBase(AddFeedbackActivity.this, MainActivity.class);
                        finish();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        checkFeedback();
    }
}
