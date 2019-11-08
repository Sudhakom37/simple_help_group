package com.luxand.shg.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupMember implements Parcelable {


    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private List<Data> data;
    @Expose
    @SerializedName("success")
    private boolean success;

    protected GroupMember(Parcel in) {
        message = in.readString();
        success = in.readByte() != 0;
    }

    public static final Creator<GroupMember> CREATOR = new Creator<GroupMember>() {
        @Override
        public GroupMember createFromParcel(Parcel in) {
            return new GroupMember(in);
        }

        @Override
        public GroupMember[] newArray(int size) {
            return new GroupMember[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeByte((byte) (success ? 1 : 0));
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
        @SerializedName("is_present")
        @Expose
        private String isPresent;
        @Expose
        @SerializedName("enrollmentNo")
        private int enrollmentno;

        public String getIsPresent() {
            return isPresent;
        }

        public void setIsPresent(String isPresent) {
            this.isPresent = isPresent;
        }

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
    }
}
