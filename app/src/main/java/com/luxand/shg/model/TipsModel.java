package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TipsModel {

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
        @SerializedName("details")
        private String details;
        @Expose
        @SerializedName("tip_name")
        private String tipName;
        @Expose
        @SerializedName("business_expansion_id")
        private int businessExpansionId;

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getTipName() {
            return tipName;
        }

        public void setTipName(String tipName) {
            this.tipName = tipName;
        }

        public int getBusinessExpansionId() {
            return businessExpansionId;
        }

        public void setBusinessExpansionId(int businessExpansionId) {
            this.businessExpansionId = businessExpansionId;
        }
    }
}
