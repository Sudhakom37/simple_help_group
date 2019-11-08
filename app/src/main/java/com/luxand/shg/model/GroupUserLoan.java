package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupUserLoan {


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
        @SerializedName("results")
        private List<Results> results;
        @Expose
        @SerializedName("loan_amount")
        private String loanAmount;
        @Expose
        @SerializedName("loan_id")
        private String loanId;
        @Expose
        @SerializedName("group_loan_id")
        private int group_loan_id;

        public List<Results> getResults() {
            return results;
        }

        public void setResults(List<Results> results) {
            this.results = results;
        }

        public int getGroup_loan_id() {
            return group_loan_id;
        }

        public void setGroup_loan_id(int group_loan_id) {
            this.group_loan_id = group_loan_id;
        }

        public String getLoanAmount() {
            return loanAmount;
        }

        public void setLoanAmount(String loanAmount) {
            this.loanAmount = loanAmount;
        }

        public String getLoanId() {
            return loanId;
        }

        public void setLoanId(String loanId) {
            this.loanId = loanId;
        }
    }

    public static class Results {

        @Expose
        @SerializedName("amount")
        private String amount;
        @Expose
        @SerializedName("used_amount")
        private String used_amount;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("loan_user_id")
        private int loanUserId;

        public String getUsed_amount() {
            return used_amount;
        }

        public void setUsed_amount(String used_amount) {
            this.used_amount = used_amount;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getLoanUserId() {
            return loanUserId;
        }

        public void setLoanUserId(int loanUserId) {
            this.loanUserId = loanUserId;
        }
    }
}
