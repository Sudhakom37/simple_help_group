package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {


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
        @SerializedName("mac_no")
        private String macNo;
        @Expose
        @SerializedName("password_update_date")
        private String passwordUpdateDate;
        @Expose
        @SerializedName("user_id")
        private int userId;
        @Expose
        @SerializedName("group_name")
        private String groupName;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("group_id")
        private int groupId;
        @Expose
        @SerializedName("district_id")
        private int districtId;
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("stream_id")
        private String streamId;
        @Expose
        @SerializedName("role_id")
        private int roleId;
        @Expose
        @SerializedName("status")
        private String status;
        @Expose
        @SerializedName("userName")
        private String username;
        @Expose
        @SerializedName("verify_mode")
        private String verify_mode;
        @Expose
        @SerializedName("sno")
        private int sno;

        public String getVerify_mode() {
            return verify_mode;
        }

        public void setVerify_mode(String verify_mode) {
            this.verify_mode = verify_mode;
        }

        public String getMacNo() {
            return macNo;
        }

        public void setMacNo(String macNo) {
            this.macNo = macNo;
        }

        public String getPasswordUpdateDate() {
            return passwordUpdateDate;
        }

        public void setPasswordUpdateDate(String passwordUpdateDate) {
            this.passwordUpdateDate = passwordUpdateDate;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public int getDistrictId() {
            return districtId;
        }

        public void setDistrictId(int districtId) {
            this.districtId = districtId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getStreamId() {
            return streamId;
        }

        public void setStreamId(String streamId) {
            this.streamId = streamId;
        }

        public int getRoleId() {
            return roleId;
        }

        public void setRoleId(int roleId) {
            this.roleId = roleId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getSno() {
            return sno;
        }

        public void setSno(int sno) {
            this.sno = sno;
        }
    }
}
