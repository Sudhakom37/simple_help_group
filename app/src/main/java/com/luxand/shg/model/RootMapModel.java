package com.luxand.shg.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RootMapModel {


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
        @SerializedName("msg")
        private String msg;
        @Expose
        @SerializedName("end_address")
        private String endAddress;
        @Expose
        @SerializedName("start_address")
        private String startAddress;
        @Expose
        @SerializedName("distance")
        private String distance;
        @Expose
        @SerializedName("map_link")
        private String mapLink;
        @Expose
        @SerializedName("status")
        private String status;
        @Expose
        @SerializedName("time_taken")
        private String time_taken;

        public String getTime_taken() {
            return time_taken;
        }

        public void setTime_taken(String time_taken) {
            this.time_taken = time_taken;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getEndAddress() {
            return endAddress;
        }

        public void setEndAddress(String endAddress) {
            this.endAddress = endAddress;
        }

        public String getStartAddress() {
            return startAddress;
        }

        public void setStartAddress(String startAddress) {
            this.startAddress = startAddress;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getMapLink() {
            return mapLink;
        }

        public void setMapLink(String mapLink) {
            this.mapLink = mapLink;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
