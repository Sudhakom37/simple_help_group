package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeetingPO {

    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private List<Data> data;
    @Expose
    @SerializedName("success")
    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class Data {
        @Expose
        @SerializedName("groupphoto")
        private String groupphoto;
        @Expose
        @SerializedName("attended_users")
        private int attendedUsers;
        @Expose
        @SerializedName("meeting_date")
        private String meetingDate;
        @Expose
        @SerializedName("meetings_id")
        private int meetingsId;

        public String getGroupphoto() {
            return groupphoto;
        }

        public void setGroupphoto(String groupphoto) {
            this.groupphoto = groupphoto;
        }

        public int getAttendedUsers() {
            return attendedUsers;
        }

        public void setAttendedUsers(int attendedUsers) {
            this.attendedUsers = attendedUsers;
        }

        public String getMeetingDate() {
            return meetingDate;
        }

        public void setMeetingDate(String meetingDate) {
            this.meetingDate = meetingDate;
        }

        public int getMeetingsId() {
            return meetingsId;
        }

        public void setMeetingsId(int meetingsId) {
            this.meetingsId = meetingsId;
        }
    }
}
