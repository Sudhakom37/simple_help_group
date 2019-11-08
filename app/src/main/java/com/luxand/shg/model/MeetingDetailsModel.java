package com.luxand.shg.model;


import android.provider.MediaStore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MeetingDetailsModel {


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
        @SerializedName("village_name")
        private String villageName;
        @Expose
        @SerializedName("meeting_date")
        private String meetingDate;

        @Expose
        @SerializedName("images")
        private ArrayList<String> images;
        @Expose
        @SerializedName("audio")
        private List<Audio> audio;
        @Expose
        @SerializedName("video")
        private List<Video> video;
        @Expose
        @SerializedName("users")
        private List<Users> users;


        public String getVillageName() {
            return villageName;
        }

        public void setVillageName(String villageName) {
            this.villageName = villageName;
        }

        public String getMeetingDate() {
            return meetingDate;
        }

        public void setMeetingDate(String meetingDate) {
            this.meetingDate = meetingDate;
        }

        public ArrayList<String> getImages() {
            return images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
        }

        public List<Audio> getAudio() {
            return audio;
        }

        public void setAudio(List<Audio> audio) {
            this.audio = audio;
        }

        public List<Video> getVideo() {
            return video;
        }

        public void setVideo(List<Video> video) {
            this.video = video;
        }

        public List<Users> getUsers() {
            return users;
        }

        public void setUsers(List<Users> users) {
            this.users = users;
        }
    }

    public static class Audio {
        @Expose
        @SerializedName("size")
        private String size;
        @Expose
        @SerializedName("time")
        private String time;
        @Expose
        @SerializedName("file")
        private String file;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }
    public static class Video {
        @Expose
        @SerializedName("size")
        private String size;
        @Expose
        @SerializedName("time")
        private String time;
        @Expose
        @SerializedName("file")
        private String file;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }

    public static class Users {
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("uuid")
        private int uuid;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("is_present")
        private int isPresent;
        public int getIsPresent() {
            return isPresent;
        }

        public void setIsPresent(int isPresent) {
            this.isPresent = isPresent;
        }


        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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
}
