package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Results {
    @Expose
    @SerializedName("date")
    private String date;
    @Expose
    @SerializedName("repayment")
    private String repayment;
    @Expose
    @SerializedName("distribution")
    private String distribution;
    @Expose
    @SerializedName("amount")
    private String amount;
    @Expose
    @SerializedName("loan_id")
    private String loanId;
    @Expose
    @SerializedName("group_loan_id")
    private int groupLoanId;

    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("enrollmentNo")
    private int enrollmentno;
    @Expose
    @SerializedName("user_id")
    private int userId;
    @Expose
    @SerializedName("paid")
    private String paid;
    @Expose
    @SerializedName("loan_user_id")
    private int loanUserId;
    @Expose
    @SerializedName("loan_id")
    private String loan_id;
    @Expose
    @SerializedName("group_loan_id")
    private int group_loan_id;



    public String getRepayment() {
        return repayment;
    }

    public void setRepayment(String repayment) {
        this.repayment = repayment;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public int getGroupLoanId() {
        return groupLoanId;
    }

    public void setGroupLoanId(int groupLoanId) {
        this.groupLoanId = groupLoanId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public int getEnrollmentno() {
        return enrollmentno;
    }

    public void setEnrollmentno(int enrollmentno) {
        this.enrollmentno = enrollmentno;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public int getLoanUserId() {
        return loanUserId;
    }

    public void setLoanUserId(int loanUserId) {
        this.loanUserId = loanUserId;
    }

    public String getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(String loan_id) {
        this.loan_id = loan_id;
    }

    public int getGroup_loan_id() {
        return group_loan_id;
    }

    public void setGroup_loan_id(int group_loan_id) {
        this.group_loan_id = group_loan_id;
    }
}