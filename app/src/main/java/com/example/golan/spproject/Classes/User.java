package com.example.golan.spproject.Classes;

/**
 * Created by golan on 24/02/2018.
 */

public class User {
    private String fullName;
    private String email;
    private String user_id;
    private String profile_photo;
    private String cover_photo;
    private String description;


    public User(){

    }

    public User(String fullName, String email, String user_id, String description,String profile_photo, String cover_photo){
        this.fullName = fullName;
        this.email = email;
        this.user_id = user_id;
        this.profile_photo = profile_photo;
        this.cover_photo = cover_photo;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullName(){
        return fullName;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
