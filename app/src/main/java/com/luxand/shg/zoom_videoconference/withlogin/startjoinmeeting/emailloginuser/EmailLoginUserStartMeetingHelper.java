package com.luxand.shg.zoom_videoconference.withlogin.startjoinmeeting.emailloginuser;

import android.content.Context;

import com.luxand.shg.zoom_videoconference.withlogin.inmeetingfunction.zoommeetingui.ZoomMeetingUISettingHelper;

import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParams4NormalUser;
import us.zoom.sdk.ZoomSDK;

public class EmailLoginUserStartMeetingHelper {
    private final static String TAG = "EmailLoginUserStart";

    private static EmailLoginUserStartMeetingHelper mEmailLoginUserStartMeetingHelper;

    private ZoomSDK mZoomSDK;

    private EmailLoginUserStartMeetingHelper() {
        mZoomSDK = ZoomSDK.getInstance();
    }

    public synchronized static EmailLoginUserStartMeetingHelper getInstance() {
        mEmailLoginUserStartMeetingHelper = new EmailLoginUserStartMeetingHelper();
        return mEmailLoginUserStartMeetingHelper;
    }

    public int startMeetingWithNumber(Context context, String meetingNo) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if(meetingService == null) {
            return ret;
        }

        StartMeetingOptions opts = ZoomMeetingUISettingHelper.getStartMeetingOptions();



        StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
        params.meetingNo = meetingNo;
        return meetingService.startMeetingWithParams(context, params, opts);
    }

    public int startMeetingWithVanityId(Context context, String vanityId) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if(meetingService == null) {
            return ret;
        }

        StartMeetingOptions opts = ZoomMeetingUISettingHelper.getStartMeetingOptions();

        StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
        params.vanityID = vanityId;
        return meetingService.startMeetingWithParams(context, params, opts);
    }

    public int startInstanceMeeting(Context context) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if(meetingService == null) {
            return ret;
        }

        InstantMeetingOptions opts = ZoomMeetingUISettingHelper.getInstantMeetingOptions();

        return meetingService.startInstantMeeting(context, opts);
    }
}
