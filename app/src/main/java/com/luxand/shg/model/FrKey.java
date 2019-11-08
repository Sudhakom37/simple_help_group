package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FrKey {


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
        @SerializedName("delay_time")
        private String delay_time;
        @Expose
        @SerializedName("role_id")
        private String role_id;
        @Expose
        @SerializedName("status")
        private String status;
        @Expose
        @SerializedName("fr_key")
        private String fr_key;
        @Expose
        @SerializedName("is_download_dat")
        private String is_download_dat;

        public String getIs_download_dat() {
            return is_download_dat;
        }

        public void setIs_download_dat(String is_download_dat) {
            this.is_download_dat = is_download_dat;
        }

        public String getDelay_time() {
            return delay_time;
        }

        public void setDelay_time(String delay_time) {
            this.delay_time = delay_time;
        }

        public String getRole_id() {
            return role_id;
        }

        public void setRole_id(String role_id) {
            this.role_id = role_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFr_key() {
            return fr_key;
        }

        public void setFr_key(String fr_key) {
            this.fr_key = fr_key;
        }
    }
}
