package com.luxand.shg.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ListData {
    @Expose
    @SerializedName("group_name")
    private String groupName;
    @Expose
    @SerializedName("group_id")
    private int groupId;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Expose
    @SerializedName("users")
    private int users;
    @Expose
    @SerializedName("meeting_date")
    private String meetingDate;
    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("meetings_id")
    private int meetingsId;
    @Expose
    @SerializedName("sno")
    private int sno;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
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

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    @NonNull
    @Override
    public String toString() {
        return  groupName;
    }
}
