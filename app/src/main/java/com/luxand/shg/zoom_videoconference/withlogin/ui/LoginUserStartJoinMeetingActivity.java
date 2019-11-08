package com.luxand.shg.zoom_videoconference.withlogin.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.ConferenceListModel;
import com.luxand.shg.zoom_videoconference.withlogin.initsdk.AuthConstants;
import com.luxand.shg.zoom_videoconference.withlogin.inmeetingfunction.customizedmeetingui.MyMeetingActivity;
import com.luxand.shg.zoom_videoconference.withlogin.inmeetingfunction.customizedmeetingui.view.MeetingWindowHelper;
import com.luxand.shg.zoom_videoconference.withlogin.otherfeatures.scheduleforloginuser.ScheduleMeetingExampleActivity;
import com.luxand.shg.zoom_videoconference.withlogin.startjoinmeeting.emailloginuser.EmailLoginUserStartMeetingHelper;
import com.luxand.shg.zoom_videoconference.withlogin.startjoinmeeting.joinmeetingonly.JoinMeetingHelper;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rx.android.schedulers.AndroidSchedulers;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;


public class LoginUserStartJoinMeetingActivity extends Activity implements AuthConstants, MeetingServiceListener, ZoomSDKAuthenticationListener {

    private final static String TAG = "ZoomSDKExample";

    private EditText mEdtMeetingNo;
    private EditText mEdtMeetingPassword;
    private EditText mEdtVanityId;
    private Button mBtnStartInstantMeeting;
    private Button mBtnPreMeeting;
    private Button mBtnLoginOut;
    private Button mBtnSettings;
    private Button mReturnMeeting;
    private final static int STYPE = MeetingService.USER_TYPE_API_USER;
    private final static String DISPLAY_NAME = "ZoomUS SDK";

    private boolean mbPendingStartMeeting = false;
    private boolean isResumed = false;
    MySharedPreference preferences;
    RecyclerView recyclerview;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_user_start_join);

        back =findViewById(R.id.back);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(LoginUserStartJoinMeetingActivity.this));
        mEdtMeetingNo = (EditText) findViewById(R.id.edtMeetingNo);
        mEdtVanityId = (EditText) findViewById(R.id.edtVanityUrl);
        mEdtMeetingPassword = (EditText) findViewById(R.id.edtMeetingPassword);
        mBtnStartInstantMeeting = (Button) findViewById(R.id.btnLoginUserStartInstant);
        mBtnPreMeeting = (Button) findViewById(R.id.btnPreMeeting);
        mBtnLoginOut = (Button) findViewById(R.id.btnLogout);
        mBtnSettings = findViewById(R.id.btn_settings);
        mReturnMeeting = findViewById(R.id.btn_return);
        preferences = new MySharedPreference(LoginUserStartJoinMeetingActivity.this);

        registerListener();


        if (preferences.getPref(PrefKeys.USER_TYPE).equalsIgnoreCase("admin")||preferences.getPref(PrefKeys.USER_TYPE).equalsIgnoreCase("districtadmin")) {
            mBtnPreMeeting.setVisibility(View.VISIBLE);
        }else{
            mBtnPreMeeting.setVisibility(View.GONE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void registerListener() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        zoomSDK.addAuthenticationListener(this);
        MeetingService meetingService = zoomSDK.getMeetingService();
        if (meetingService != null) {
            meetingService.addListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        refreshUI();
        //getZoomMeetings();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    protected void onDestroy() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        zoomSDK.removeAuthenticationListener(this);//unregister ZoomSDKAuthenticationListener
        if (zoomSDK.isInitialized()) {
            MeetingService meetingService = zoomSDK.getMeetingService();
            meetingService.removeListener(this);//unregister meetingServiceListener
        }

        super.onDestroy();
    }

    public void onClickBtnJoinMeeting(View view) {
        String meetingNo = mEdtMeetingNo.getText().toString().trim();
        String meetingPassword = mEdtMeetingPassword.getText().toString().trim();

        String vanityId = mEdtVanityId.getText().toString().trim();

        if (meetingNo.length() == 0 && vanityId.length() == 0) {
            Toast.makeText(this, "You need to enter a meeting number/ vanity id which you want to join.", Toast.LENGTH_LONG).show();
            return;
        }

        if (meetingNo.length() != 0 && vanityId.length() != 0) {
            Toast.makeText(this, "Both meeting number and vanity id have value,  just set one of them", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        MeetingService meetingService = zoomSDK.getMeetingService();


        int ret = -1;
        if (vanityId.length() != 0) {
            ret = JoinMeetingHelper.getInstance().joinMeetingWithVanityId(this, vanityId, meetingPassword, preferences.getPref(PrefKeys.NAME));
        } else {
            ret = JoinMeetingHelper.getInstance().joinMeetingWithNumber(this, meetingNo, meetingPassword, preferences.getPref(PrefKeys.NAME));
        }

        Log.i(TAG, "onClickBtnJoinMeeting, ret=" + ret);
    }

    public void onClickBtnStartMeeting(View view) {
        String meetingNo = mEdtMeetingNo.getText().toString().trim();
        String vanityId = mEdtVanityId.getText().toString().trim();

        if (meetingNo.length() == 0 && vanityId.length() == 0) {
            Toast.makeText(this, "You need to enter a meeting number/ vanity  which you want to join.", Toast.LENGTH_LONG).show();
            return;
        }

        if (meetingNo.length() != 0 && vanityId.length() != 0) {
            Toast.makeText(this, "Both meeting number and vanity  have value,  just set one of them", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        final MeetingService meetingService = zoomSDK.getMeetingService();

        if (meetingService.getMeetingStatus() != MeetingStatus.MEETING_STATUS_IDLE) {
            long lMeetingNo = 0;
            try {
                lMeetingNo = Long.parseLong(meetingNo);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid meeting number: " + meetingNo, Toast.LENGTH_LONG).show();
                return;
            }

            if (meetingService.getCurrentRtcMeetingNumber() == lMeetingNo) {
                meetingService.returnToMeeting(this);
                return;
            }

            new AlertDialog.Builder(this)
                    .setMessage("Do you want to leave current meeting and start another?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mbPendingStartMeeting = true;
                            meetingService.leaveCurrentMeeting(false);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }

        int ret = -1;
        if (vanityId.length() != 0) {
            ret = EmailLoginUserStartMeetingHelper.getInstance().startMeetingWithVanityId(this, vanityId);
        } else {
            ret = EmailLoginUserStartMeetingHelper.getInstance().startMeetingWithNumber(this, meetingNo);
        }
        Log.i(TAG, "onClickBtnStartMeeting, ret=" + ret);
    }

    public void onClickBtnLoginUserStartInstant(View view) {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        int ret = EmailLoginUserStartMeetingHelper.getInstance().startInstanceMeeting(this);

        Log.i(TAG, "onClickBtnLoginUserStartInstant, ret=" + ret);
    }

    public void onClickReturnMeeting(View view) {
        MeetingWindowHelper.getInstance().hiddenMeetingWindow(true);
        Intent intent = new Intent(this, MyMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void onClickBtnPreMeeting(View view) {
        Intent intent = new Intent(this, ScheduleMeetingExampleActivity.class);
        startActivity(intent);
    }

    public void onClickBtnLogout(View view) {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (!zoomSDK.logoutZoom()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickSettings(View view) {
        Intent intent = new Intent(this, MeetingSettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode,
                                       int internalErrorCode) {
        Log.i(TAG, "onMeetingStatusChanged, meetingStatus=" + meetingStatus + ", errorCode=" + errorCode
                + ", internalErrorCode=" + internalErrorCode);

        if (meetingStatus == MeetingStatus.MEETING_STATUS_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
            Toast.makeText(this, "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
        }

        if (mbPendingStartMeeting && meetingStatus == MeetingStatus.MEETING_STATUS_IDLE) {
            mbPendingStartMeeting = false;
            onClickBtnStartMeeting(null);
        }
        if (meetingStatus == MeetingStatus.MEETING_STATUS_CONNECTING) {
            showMeetingUi();
        }
        refreshUI();
    }

    private void refreshUI() {
        MeetingStatus meetingStatus = ZoomSDK.getInstance().getMeetingService().getMeetingStatus();
        if (meetingStatus == MeetingStatus.MEETING_STATUS_CONNECTING || meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING
                || meetingStatus == MeetingStatus.MEETING_STATUS_RECONNECTING) {
            mBtnSettings.setVisibility(View.GONE);
            mReturnMeeting.setVisibility(View.VISIBLE);
        } else {
            mBtnSettings.setVisibility(View.VISIBLE);
            mReturnMeeting.setVisibility(View.GONE);
        }
        if (ZoomSDK.getInstance().getMeetingSettingsHelper().isCustomizedMeetingUIEnabled()) {
            if (meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING && isResumed) {
                MeetingWindowHelper.getInstance().showMeetingWindow(this);
            } else {
                MeetingWindowHelper.getInstance().hiddenMeetingWindow(true);
            }
        }
    }

    private void showMeetingUi() {
        if (ZoomSDK.getInstance().getMeetingSettingsHelper().isCustomizedMeetingUIEnabled()) {
            Intent intent = new Intent(this, MyMeetingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            this.startActivity(intent);
        }
    }

	/*@Override
	public void onBackPressed() {
		if (ZoomSDK.getInstance().isLoggedIn()) {
			moveTaskToBack(true);
		} else {
			super.onBackPressed();
		}
	}*/

    @Override
    public void onZoomSDKLoginResult(long l) {

    }

    @Override
    public void onZoomSDKLogoutResult(long result) {
        if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
            showLoginView();
            finish();
        } else {
            Toast.makeText(this, "Logout failed result code = " + result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onZoomIdentityExpired() {
        ZoomSDK.getInstance().logoutZoom();
    }

    private void showLoginView() {
        Intent intent = new Intent(this, EmailUserLoginActivity.class);
        startActivity(intent);
    }

    public class ConferenceList_Adapter extends RecyclerView.Adapter<ConferenceList_Adapter.MyViewHolder> {
        List<ConferenceListModel> names;
        Context mContext;

        public ConferenceList_Adapter(Context mContext, List<ConferenceListModel> names) {
            this.mContext = mContext;
            this.names = names;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public ConferenceList_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_conference, viewGroup, false);

            return new ConferenceList_Adapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ConferenceList_Adapter.MyViewHolder myViewHolder, int i) {

            final int pos = i;
            myViewHolder.title.setText(names.get(pos).getTitle());
            myViewHolder.date.setText(names.get(pos).getTime());
            myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (preferences.getPref(PrefKeys.USER_TYPE).equalsIgnoreCase("admin")||preferences.getPref(PrefKeys.USER_TYPE).equalsIgnoreCase("districtadmin")) {
                        EmailLoginUserStartMeetingHelper.getInstance().startMeetingWithNumber(LoginUserStartJoinMeetingActivity.this, names.get(pos).getZoom_id());
                    }else{
                        JoinMeetingHelper.getInstance().joinMeetingWithNumber(LoginUserStartJoinMeetingActivity.this, names.get(pos).getZoom_id(), "", preferences.getPref(PrefKeys.NAME));
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return this.names == null ? 0 : this.names.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView title, date;
            CardView cardView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                date = itemView.findViewById(R.id.date);
                cardView = itemView.findViewById(R.id.cardView);

            }
        }

    }

    /*public void getZoomMeetings() {

        Helper.getInstance(LoginUserStartJoinMeetingActivity.this).RestApi()
                .getZoomMeetings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(__ -> ProgressBar.showLoadingDialog(LoginUserStartJoinMeetingActivity.this))
                .doOnTerminate(() -> ProgressBar.cancelLoading())
                .subscribe(new Observer<List<ConferenceListModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ConferenceListModel> data) {
                        if (data.size() > 0) {
                            recyclerview.setAdapter(new ConferenceList_Adapter(LoginUserStartJoinMeetingActivity.this, data));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBar.cancelLoading();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }*/
}
