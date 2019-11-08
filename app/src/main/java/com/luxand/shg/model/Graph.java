package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Graph {

    @SerializedName("question_family")
    @Expose
    private String questionFamily;
    @SerializedName("count")
    @Expose
    private String count;
    @SerializedName("per")
    @Expose
    private String per;

    public String getQuestionFamily() {
        return questionFamily;
    }

    public void setQuestionFamily(String questionFamily) {
        this.questionFamily = questionFamily;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPer() {
        return per;
    }

    public void setPer(String per) {
        this.per = per;
    }

}
