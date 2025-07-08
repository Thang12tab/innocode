package com.example.threadsclone.entity;

public class UserSummary {
    private String username;
    private String avatar;

    public UserSummary() {}
    public UserSummary(String username, String avatar) {
        this.username = username;
        this.avatar = avatar;
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
