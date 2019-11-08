package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class VillageOfDashBoard {


    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private Data data;
    @Expose
    @SerializedName("success")
    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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
        @SerializedName("group_users")
        private List<GroupUsers> groupUsers;
        @Expose
        @SerializedName("group_meetings")
        private List<GroupMeetings> groupMeetings;

        public List<GroupUsers> getGroupUsers() {
            return groupUsers;
        }

        public void setGroupUsers(List<GroupUsers> groupUsers) {
            this.groupUsers = groupUsers;
        }

        public List<GroupMeetings> getGroupMeetings() {
            return groupMeetings;
        }

        public void setGroupMeetings(List<GroupMeetings> groupMeetings) {
            this.groupMeetings = groupMeetings;
        }
    }

    public static class GroupUsers {
        @Expose
        @SerializedName("total")
        private int total;
        @Expose
        @SerializedName("group_name")
        private String groupName;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }

    public static class GroupMeetings {
        @Expose
        @SerializedName("count")
        private int count;
        @Expose
        @SerializedName("group_name")
        private String groupName;
        @SerializedName("group_id")
        private String group_id;

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }
}
