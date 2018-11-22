package com.example.golan.spproject.Classes;

/**
 * Created by golan on 10/03/2018.
 */

public class Posts {
    public String  user_id, time,profile_image,post_image,full_name,description,date;

public Posts(){

}

    public Posts(String user_id, String time, String profile_image, String post_image, String full_name, String description, String date) {
        this.user_id = user_id;
        this.time = time;
        this.profile_image = profile_image;
        this.post_image = post_image;
        this.full_name = full_name;
        this.description = description;
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
