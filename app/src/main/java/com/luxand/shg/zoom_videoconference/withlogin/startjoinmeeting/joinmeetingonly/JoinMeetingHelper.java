package com.luxand.shg.zoom_videoconference.withlogin.startjoinmeeting.joinmeetingonly;

import android.content.Context;

import com.luxand.shg.zoom_videoconference.withlogin.inmeetingfunction.zoommeetingui.ZoomMeetingUISettingHelper;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.ZoomSDK;

public class JoinMeetingHelper {
    private final static String TAG = "JoinMeetingHelper";

    private static JoinMeetingHelper mJoinMeetingHelper;

    private ZoomSDK mZoomSDK;

    private final static String DISPLAY_NAME = "ZoomUS SDK";

    private JoinMeetingHelper() {
        mZoomSDK = ZoomSDK.getInstance();
    }

    public synchronized static JoinMeetingHelper getInstance() {
        mJoinMeetingHelper = new JoinMeetingHelper();
        return mJoinMeetingHelper;
    }

    public int joinMeetingWithNumber(Context context, String meetingNo, String meetingPassword, String displayname) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if(meetingService == null) {
            return ret;
        }

        JoinMeetingOptions opts = ZoomMeetingUISettingHelper.getJoinMeetingOptions();

        JoinMeetingParams params = new JoinMeetingParams();

        params.displayName = displayname;
        params.meetingNo = meetingNo;
        params.password = meetingPassword;
        return meetingService.joinMeetingWithParams(context, params,opts);
    }

    public int joinMeetingWithVanityId(Context context, String vanityId, String meetingPassword, String displayname) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if(meetingService == null) {
            return ret;
        }

        JoinMeetingOptions opts =ZoomMeetingUISettingHelper.getJoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = displayname;
        params.vanityID = vanityId;
        params.password = meetingPassword;
        return meetingService.joinMeetingWithParams(context, params,opts);
    }
}
