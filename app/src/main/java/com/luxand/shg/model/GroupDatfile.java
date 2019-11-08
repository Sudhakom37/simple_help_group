package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupDatfile {


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
        @SerializedName("sync_status")
        private String syncStatus;
        @Expose
        @SerializedName("is_download_dat")
        private int isDownloadDat;
        @Expose
        @SerializedName("dat_url")
        private List<DatUrl> datUrl;

        public String getSyncStatus() {
            return syncStatus;
        }

        public void setSyncStatus(String syncStatus) {
            this.syncStatus = syncStatus;
        }

        public int getIsDownloadDat() {
            return isDownloadDat;
        }

        public void setIsDownloadDat(int isDownloadDat) {
            this.isDownloadDat = isDownloadDat;
        }

        public List<DatUrl> getDatUrl() {
            return datUrl;
        }

        public void setDatUrl(List<DatUrl> datUrl) {
            this.datUrl = datUrl;
        }
    }

    public static class DatUrl {
        @Expose
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
