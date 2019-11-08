package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BarChartModel {

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
        @SerializedName("total")
        private int total;@Expose
        @SerializedName("collected")
        private int collected;
        @Expose
        @SerializedName("disbursed")
        private int disbursed;
        @Expose
        @SerializedName("label")
        private String label;

        public int getCollected() {
            return collected;
        }

        public void setCollected(int collected) {
            this.collected = collected;
        }

        public int getDisbursed() {
            return disbursed;
        }

        public void setDisbursed(int disbursed) {
            this.disbursed = disbursed;
        }

        public List<Results> getResults() {
            return results;
        }

        public void setResults(List<Results> results) {
            this.results = results;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public static class Results {
        @Expose
        @SerializedName("count")
        private int count;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("id")
        private int id;
        @Expose
        @SerializedName("collected")
        private int collectedAmount;
        @Expose
        @SerializedName("disbursed")
        private int disbursedAmount;
        @Expose
        @SerializedName("audiocount")
        private int audioCount;
        @Expose
        @SerializedName("videocount")
        private int videoCount;

        public int getAudioCount() {
            return audioCount;
        }

        public void setAudioCount(int audioCount) {
            this.audioCount = audioCount;
        }

        public int getVideoCount() {
            return videoCount;
        }

        public void setVideoCount(int videoCount) {
            this.videoCount = videoCount;
        }


        public int getCollectedAmount() {
            return collectedAmount;
        }

        public void setCollectedAmount(int collectedAmount) {
            this.collectedAmount = collectedAmount;
        }

        public int getDisbursedAmount() {
            return disbursedAmount;
        }

        public void setDisbursedAmount(int disbursedAmount) {
            this.disbursedAmount = disbursedAmount;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
