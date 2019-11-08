package com.luxand.shg.helper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class Twin {


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
        @SerializedName("matching_user")
        private List<Twin_uuid.Data> matchingUser;
        @Expose
        @SerializedName("first_name")
        private String firstName;
        @Expose
        @SerializedName("uuid")
        private String uuid;

        public List<Twin_uuid.Data> getMatchingUser() {
            return matchingUser;
        }

        public void setMatchingUser(List<Twin_uuid.Data> matchingUser) {
            this.matchingUser = matchingUser;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }

}
