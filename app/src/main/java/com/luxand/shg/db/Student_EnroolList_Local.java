package com.luxand.shg.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = MyDatabase.class)
public class Student_EnroolList_Local extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    String group_id;
    @Column
    String data;
    @Column
    String name;
    @Column
    String gender;
    @Column
    String image;
    @Column
    String isEnrolled;
    @Column
    String isEnrolledLocal;
    @Column
    String pstatus;
    @Column
    String schoolId;
    @Column
    String uuid;
    @Column
    String sectiondatfile;
    @Column
    String studentdatfile;
    @Column
    String camera_config;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsEnrolled() {
        return isEnrolled;
    }

    public void setIsEnrolled(String isEnrolled) {
        this.isEnrolled = isEnrolled;
    }

    public String getPstatus() {
        return pstatus;
    }

    public void setPstatus(String pstatus) {
        this.pstatus = pstatus;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGroupId() {
        return group_id;
    }

    public void setGroupId(String group_id) {
        this.group_id = group_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCamera_config() {
        return camera_config;
    }

    public void setCamera_config(String camera_config) {
        this.camera_config = camera_config;
    }

    public String getSectiondatfile() {
        return sectiondatfile;
    }

    public void setSectiondatfile(String sectiondatfile) {
        this.sectiondatfile = sectiondatfile;
    }

    public String getStudentDatfile() {
        return studentdatfile;
    }

    public void setStudentdatfile(String studentdatfile) {
        this.studentdatfile = studentdatfile;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsEnrolledLocal() {
        return isEnrolledLocal;
    }

    public void setIsEnrolledLocal(String isEnrolledLocal) {
        this.isEnrolledLocal = isEnrolledLocal;
    }

}
