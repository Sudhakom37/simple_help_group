package com.luxand.shg.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.List;

public class AddLoan {


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
        @SerializedName("is_enrolled")
        private String isEnrolled;
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("camera_config")
        private String cameraConfig;
        @Expose
        @SerializedName("status")
        private String status;
        @Expose
        @SerializedName("uuid")
        private int uuid;
        @Expose
        @SerializedName("group_id")
        private int groupId;
        @Expose
        @SerializedName("gender")
        private String gender;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("enrollmentNo")
        private int enrollmentno;

        public String getIsEnrolled() {
            return isEnrolled;
        }

        public void setIsEnrolled(String isEnrolled) {
            this.isEnrolled = isEnrolled;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getCameraConfig() {
            return cameraConfig;
        }

        public void setCameraConfig(String cameraConfig) {
            this.cameraConfig = cameraConfig;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getUuid() {
            return uuid;
        }

        public void setUuid(int uuid) {
            this.uuid = uuid;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getEnrollmentno() {
            return enrollmentno;
        }

        public void setEnrollmentno(int enrollmentno) {
            this.enrollmentno = enrollmentno;
        }

        @NonNull
        @Override
        public String toString() {
            return name ;
        }
    }
}
