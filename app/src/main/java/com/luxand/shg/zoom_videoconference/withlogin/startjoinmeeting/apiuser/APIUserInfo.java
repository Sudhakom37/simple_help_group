package com.luxand.shg.zoom_videoconference.withlogin.startjoinmeeting.apiuser;

public class APIUserInfo {
    public String userId;
    public String userZoomToken;
    public String userZoomAccessToken;

    public APIUserInfo(String userId, String userZoomToken, String userZoomAccessToken) {
        this.userId = userId;
        this.userZoomToken = userZoomToken;
        this.userZoomAccessToken = userZoomAccessToken;
    }
}
