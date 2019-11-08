package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StudentEnrollmentListModel {

    @SerializedName("enrollmentNo")
    @Expose
    private String enrollmentNo;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("school_id")
    @Expose
    private String schoolId;
    @SerializedName("pstatus")
    @Expose
    private String pstatus;
    @SerializedName("is_enrolled")
    @Expose
    private String is_enrolled;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("camera_config")
    @Expose
    private String cameraconfig;

    public String getCameraconfig() {
        return cameraconfig;
    }

    public void setCameraconfig(String cameraconfig) {
        this.cameraconfig = cameraconfig;
    }

    public String getEnrollmentNo() {
        return enrollmentNo;
    }

    public void setEnrollmentNo(String enrollmentNo) {
        this.enrollmentNo = enrollmentNo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getPstatus() {
        return pstatus;
    }

    public void setPstatus(String pstatus) {
        this.pstatus = pstatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIs_enrolled() {
        return is_enrolled;
    }

    public void setIs_enrolled(String is_enrolled) {
        this.is_enrolled = is_enrolled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
