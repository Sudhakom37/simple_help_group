package com.luxand.shg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DashBoard {


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
        @SerializedName("repayment")
        private String repayment;
        @Expose
        @SerializedName("currentloans")
        private String currentloans;
        @Expose
        @SerializedName("total_savings")
        private String totalSavings;
        @Expose
        @SerializedName("users")
        private List<Users> users;
        @Expose
        @SerializedName("users_count")
        private int usersCount;
        @Expose
        @SerializedName("meeting_date")
        private String meetingDate;
        @Expose
        @SerializedName("weeks")
        private Weeks weeks;
        @Expose
        @SerializedName("weeks_covered")
        private int weeksCovered;
        @Expose
        @SerializedName("meeting_id")
        private int meeting_id;
        @Expose
        @SerializedName("meeting_photos")
        private ArrayList<String> meetingPhotos;

        public int getMeeting_id() {
            return meeting_id;
        }

        public void setMeeting_id(int meeting_id) {
            this.meeting_id = meeting_id;
        }

        public String getRepayment() {
            return repayment;
        }

        public void setRepayment(String repayment) {
            this.repayment = repayment;
        }

        public String getCurrentloans() {
            return currentloans;
        }

        public void setCurrentloans(String currentloans) {
            this.currentloans = currentloans;
        }

        public String getTotalSavings() {
            return totalSavings;
        }

        public void setTotalSavings(String totalSavings) {
            this.totalSavings = totalSavings;
        }

        public List<Users> getUsers() {
            return users;
        }

        public void setUsers(List<Users> users) {
            this.users = users;
        }

        public int getUsersCount() {
            return usersCount;
        }

        public void setUsersCount(int usersCount) {
            this.usersCount = usersCount;
        }

        public String getMeetingDate() {
            return meetingDate;
        }

        public void setMeetingDate(String meetingDate) {
            this.meetingDate = meetingDate;
        }

        public Weeks getWeeks() {
            return weeks;
        }

        public void setWeeks(Weeks weeks) {
            this.weeks = weeks;
        }

        public int getWeeksCovered() {
            return weeksCovered;
        }

        public void setWeeksCovered(int weeksCovered) {
            this.weeksCovered = weeksCovered;
        }

        public ArrayList<String> getMeetingPhotos() {
            return meetingPhotos;
        }

        public void setMeetingPhotos(ArrayList<String> meetingPhotos) {
            this.meetingPhotos = meetingPhotos;
        }
    }

    /*public static class Users {
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("amount")
        private int amount;
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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
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
    }*/

    public static class Weeks {
        @Expose
        @SerializedName("week5")
        private int week5;
        @Expose
        @SerializedName("week4")
        private int week4;
        @Expose
        @SerializedName("week3")
        private int week3;
        @Expose
        @SerializedName("week2")
        private int week2;
        @Expose
        @SerializedName("week1")
        private int week1;

        public int getWeek5() {
            return week5;
        }

        public void setWeek5(int week5) {
            this.week5 = week5;
        }

        public int getWeek4() {
            return week4;
        }

        public void setWeek4(int week4) {
            this.week4 = week4;
        }

        public int getWeek3() {
            return week3;
        }

        public void setWeek3(int week3) {
            this.week3 = week3;
        }

        public int getWeek2() {
            return week2;
        }

        public void setWeek2(int week2) {
            this.week2 = week2;
        }

        public int getWeek1() {
            return week1;
        }

        public void setWeek1(int week1) {
            this.week1 = week1;
        }
    }
}
