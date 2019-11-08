package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class Users {
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("amount")
        private String  amount;
        @Expose
        @SerializedName("mobileNo")
        private String mobileno;
        @Expose
        @SerializedName("is_enrolled")
        private String isEnrolled;
        @Expose
        @SerializedName("uuid")
        private int uuid;
        @Expose
        @SerializedName("name")
        private String name;

        @Expose
        @SerializedName("mobile")
        private String mobile;


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getMobileno() {
            return mobileno;
        }

        public void setMobileno(String mobileno) {
            this.mobileno = mobileno;
        }

        public String getIsEnrolled() {
            return isEnrolled;
        }

        public void setIsEnrolled(String isEnrolled) {
            this.isEnrolled = isEnrolled;
        }

        public int getUuid() {
            return uuid;
        }

        public void setUuid(int uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }