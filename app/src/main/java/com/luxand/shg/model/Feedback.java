package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Feedback {

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
        @SerializedName("attachment")
        private String attachment;
        @Expose
        @SerializedName("feedback_type")
        private String feedbackType;
        @Expose
        @SerializedName("meetings_feedback_id")
        private int meetingsFeedbackId;

        public String getAttachment() {
            return attachment;
        }

        public void setAttachment(String attachment) {
            this.attachment = attachment;
        }

        public String getFeedbackType() {
            return feedbackType;
        }

        public void setFeedbackType(String feedbackType) {
            this.feedbackType = feedbackType;
        }

        public int getMeetingsFeedbackId() {
            return meetingsFeedbackId;
        }

        public void setMeetingsFeedbackId(int meetingsFeedbackId) {
            this.meetingsFeedbackId = meetingsFeedbackId;
        }
    }
}
