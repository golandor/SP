package com.example.golan.spproject.Classes;

/**
 * Created by golan on 03/03/2018.
 */

public class UserSettings {
    private User user;
    private UserAccountSettings settings;

    public UserSettings(User user, UserAccountSettings settings) {
        this.user = user;
        this.settings = settings;
    }

    public UserSettings() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSettings getSettings() {
        return settings;
    }

    public void setSettings(UserAccountSettings settings) {
        this.settings = settings;
    }
}
