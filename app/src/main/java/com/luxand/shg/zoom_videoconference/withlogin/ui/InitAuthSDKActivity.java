package com.luxand.shg.zoom_videoconference.withlogin.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.luxand.shg.R;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.zoom_videoconference.withlogin.initsdk.InitAuthSDKCallback;
import com.luxand.shg.zoom_videoconference.withlogin.initsdk.InitAuthSDKHelper;
import com.luxand.shg.zoom_videoconference.withlogin.startjoinmeeting.UserLoginCallback;


import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;

public class InitAuthSDKActivity extends Activity implements InitAuthSDKCallback, UserLoginCallback.ZoomDemoAuthenticationListener, OnClickListener {

	private final static String TAG = "Loading Please Wait";
	
	private Button mBtnEmailLogin;
	private Button mBtnSSOLogin;
	private Button mBtnWithoutLogin;
	private View mProgressPanel;
	private ZoomSDK mZoomSDK;
	ImageView back;
	MySharedPreference preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mZoomSDK = ZoomSDK.getInstance();
		if(mZoomSDK.isLoggedIn()) {
			finish();
            showEmailLoginUserStartJoinActivity();
			return;
		}
		
		setContentView(R.layout.init_auth_sdk);

		preferences =new MySharedPreference(InitAuthSDKActivity.this);
		back =findViewById(R.id.back);
		mBtnEmailLogin = (Button)findViewById(R.id.btnEmailLogin);
		mBtnEmailLogin.setOnClickListener(this);

		mBtnSSOLogin = (Button)findViewById(R.id.btnSSOLogin);
		mBtnSSOLogin.setOnClickListener(this);

		mBtnWithoutLogin = (Button)findViewById(R.id.btnWithoutLogin);
		mBtnWithoutLogin.setOnClickListener(this);
		mProgressPanel = (View)findViewById(R.id.progressPanel);

		mProgressPanel.setVisibility(View.GONE);
		
		if(savedInstanceState == null) {
			InitAuthSDKHelper.getInstance().initSDK(this, this);
		}

		if(mZoomSDK.isInitialized()) {
			if (preferences.getPref(PrefKeys.USER_TYPE).equalsIgnoreCase("admin")||preferences.getPref(PrefKeys.USER_TYPE).equalsIgnoreCase("districtadmin")) {
				mBtnEmailLogin.setVisibility(View.VISIBLE);
				mBtnWithoutLogin.setVisibility(View.VISIBLE);
			}else{
				finish();
				Intent intent = new Intent(this, LoginUserStartJoinMeetingActivity.class);
				startActivity(intent);

			}

		} else {
			mBtnEmailLogin.setVisibility(View.GONE);
			//mBtnSSOLogin.setVisibility(View.GONE);
			mBtnWithoutLogin.setVisibility(View.GONE);
		}

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				finish();
			}
		});
	}
	
	@Override
	public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
		Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);
		
		if(errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
			//Toast.makeText(this, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG).show();
		} else {
			//Toast.makeText(this, "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();
			if(mZoomSDK.tryAutoLoginZoom() == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
                UserLoginCallback.getInstance().addListener(this);

				if (!preferences.getPref(PrefKeys.USER_TYPE).equalsIgnoreCase("admin")||!preferences.getPref(PrefKeys.USER_TYPE).equalsIgnoreCase("districtadmin")) {
					finish();
					Intent intent = new Intent(this, LoginUserStartJoinMeetingActivity.class);
					startActivity(intent);
				}else{
					showProgressPanel(true);
				}
			} else {

				if (!preferences.getPref(PrefKeys.USER_TYPE).equalsIgnoreCase("admin")||!preferences.getPref(PrefKeys.USER_TYPE).equalsIgnoreCase("districtadmin")) {
					finish();
					Intent intent = new Intent(this, LoginUserStartJoinMeetingActivity.class);
					startActivity(intent);
				}else{
					showProgressPanel(false);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnEmailLogin) {
			showEmailLoginActivity();
		} else if(v.getId() == R.id.btnSSOLogin) {
			showSSOLoginActivity();
		} else if(v.getId() == R.id.btnWithoutLogin) {
			showAPIUserActivity();
		}
	}

	@Override
	public void onZoomSDKLoginResult(long result) {
		if((int)result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            showEmailLoginUserStartJoinActivity();
			//finish();
		} else {
			showProgressPanel(false);
		}
	}

	@Override
	public void onZoomSDKLogoutResult(long result) {

	}

	@Override
	public void onZoomIdentityExpired() {
        if(mZoomSDK.isLoggedIn()) {
            mZoomSDK.logoutZoom();
        }
	}

	private void showProgressPanel(boolean show) {
		if(show) {
			mBtnEmailLogin.setVisibility(View.GONE);
			//mBtnSSOLogin.setVisibility(View.GONE);
			mBtnWithoutLogin.setVisibility(View.GONE);
			mProgressPanel.setVisibility(View.VISIBLE);
		} else {
			mBtnWithoutLogin.setVisibility(View.VISIBLE);
			mBtnEmailLogin.setVisibility(View.VISIBLE);
			//mBtnSSOLogin.setVisibility(View.VISIBLE);
			mProgressPanel.setVisibility(View.GONE);
		}
    }

	private void showEmailLoginActivity() {
		Intent intent = new Intent(this, EmailUserLoginActivity.class);
		startActivity(intent);
	}

	private void showSSOLoginActivity() {
		Intent intent = new Intent(this, SSOUserLoginActivity.class);
		startActivity(intent);
	}

	private void showAPIUserActivity() {
		Intent intent = new Intent(this, LoginUserStartJoinMeetingActivity.class);
		startActivity(intent);
	}

    private void showEmailLoginUserStartJoinActivity() {
        Intent intent = new Intent(this, LoginUserStartJoinMeetingActivity.class);
        startActivity(intent);
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        UserLoginCallback.getInstance().removeListener(this);
	}
}