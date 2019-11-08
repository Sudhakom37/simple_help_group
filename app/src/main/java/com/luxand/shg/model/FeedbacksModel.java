package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedbacksModel {

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
        @SerializedName("videos")
        private List<Videos> videos;
        @Expose
        @SerializedName("audios")
        private List<Audios> audios;

        public List<Videos> getVideos() {
            return videos;
        }

        public void setVideos(List<Videos> videos) {
            this.videos = videos;
        }

        public List<Audios> getAudios() {
            return audios;
        }

        public void setAudios(List<Audios> audios) {
            this.audios = audios;
        }
    }

    public static class Videos {
        @Expose
        @SerializedName("created_date")
        private String createdDate;
        @Expose
        @SerializedName("duration")
        private String duration;
        @Expose
        @SerializedName("file_path")
        private String filePath;

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

    public static class Audios {
        @Expose
        @SerializedName("created_date")
        private String createdDate;
        @Expose
        @SerializedName("duration")
        private String duration;
        @Expose
        @SerializedName("file_path")
        private String filePath;

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
