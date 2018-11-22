package com.example.golan.spproject.Classes;

/**
 * Created by golan on 03/03/2018.
 */

public class UserAccountSettings {
    private String user_id;
    private int follwoing;
    private int followers;
    private String profile_photo;
    private String cover_photo;


    public UserAccountSettings() {
    }

    public UserAccountSettings(String user_id, int follwoing, int followers, String profile_photo, String cover_photo) {
        this.user_id = user_id;
        this.follwoing = follwoing;
        this.followers = followers;
        this.profile_photo = profile_photo;
        this.cover_photo = cover_photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getFollowing() {
        return follwoing;
    }

    public void setFollowing(int following) {
        this.follwoing = follwoing;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getCover_photo() {
        return cover_photo;
    }

    public void setCover_photo(String cover_photo) {
        this.cover_photo = cover_photo;
    }
}
