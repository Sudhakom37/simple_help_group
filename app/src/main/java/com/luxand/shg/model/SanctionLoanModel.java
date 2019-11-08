package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SanctionLoanModel {


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
        @SerializedName("total")
        private String total;
        @Expose
        @SerializedName("result")
        private List<Result> result;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public List<Result> getResult() {
            return result;
        }

        public void setResult(List<Result> result) {
            this.result = result;
        }
    }

    public static class Result {
        @Expose
        @SerializedName("loan_amount")
        private String loanAmount;
        @Expose
        @SerializedName("created_date")
        private String createdDate;
        @Expose
        @SerializedName("loan_id")
        private String loanId;
        @Expose
        @SerializedName("group_name")
        private String groupName;
        @Expose
        @SerializedName("group_id")
        private int groupId;
        @Expose
        @SerializedName("group_loan_id")
        private int groupLoanId;

        public String getLoanAmount() {
            return loanAmount;
        }

        public void setLoanAmount(String loanAmount) {
            this.loanAmount = loanAmount;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getLoanId() {
            return loanId;
        }

        public void setLoanId(String loanId) {
            this.loanId = loanId;
        }

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

        public int getGroupLoanId() {
            return groupLoanId;
        }

        public void setGroupLoanId(int groupLoanId) {
            this.groupLoanId = groupLoanId;
        }
    }
}
