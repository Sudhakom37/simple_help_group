package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Caste {

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
        @SerializedName("cast_name")
        private String castName;
        @Expose
        @SerializedName("caste_id")
        private int casteId;

        public String getCastName() {
            return castName;
        }

        public void setCastName(String castName) {
            this.castName = castName;
        }

        public int getCasteId() {
            return casteId;
        }

        public void setCasteId(int casteId) {
            this.casteId = casteId;
        }
    }
}
