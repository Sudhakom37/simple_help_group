package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuccessModel {
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("otp")
    @Expose
    private String otp;
    @SerializedName("fr_key")
    @Expose
    private String fr_key;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("role_id")
    @Expose
    private String role_id;
    @SerializedName("sno")
    @Expose
    private String sno;
    @SerializedName("is_allow")
    @Expose
    private String is_allow;
    @SerializedName("delay_time")
    @Expose
    private String delay_time;
    @SerializedName("map_link")
    @Expose
    private String map_link;



    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getFr_key() {
        return fr_key;
    }

    public void setFr_key(String fr_key) {
        this.fr_key = fr_key;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getRole_id() {
        return role_id;
    }
    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getIs_allow() {
        return is_allow;
    }

    public String getDelay_time() {
        return delay_time;
    }

    public void setDelay_time(String delay_time) {
        this.delay_time = delay_time;
    }
    public void setIs_allow(String is_allow) {
        this.is_allow = is_allow;
    }


    @Override
    public String toString() {
        return "SuccessModel{" +
                "msg='" + msg + '\'' +
                ", otp='" + otp + '\'' +
                ", fr_key='" + fr_key + '\'' +
                ", status='" + status + '\'' +
                ", role_id='" + role_id + '\'' +
                ", sno='" + sno + '\'' +
                ", is_allow='" + is_allow + '\'' +
                ", delay_time='" + delay_time + '\'' +
                '}';
    }
}
