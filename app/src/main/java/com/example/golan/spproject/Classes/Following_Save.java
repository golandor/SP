package com.example.golan.spproject.Classes;

/**
 * Created by golan on 17/03/2018.
 */

public class Following_Save {
    public String  user_id,profile_image,full_name, email;

    public Following_Save() {
    }

    public Following_Save(String user_id) {
        this.user_id = user_id;
    }

    public Following_Save(String user_id, String profile_image, String full_name, String email) {
        this.user_id = user_id;
        this.profile_image = profile_image;
        this.full_name = full_name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
}
