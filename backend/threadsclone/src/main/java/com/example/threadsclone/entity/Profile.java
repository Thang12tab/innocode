package com.example.threadsclone.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "avatar_url")
    private String avatar;
    private String fullname;
    private LocalDate dob;
    private String bio;
    private String hobbies;
    private String nickname;

    // Các trường mở rộng cho profile trả về frontend
    private String email;
    private String username;
    // Đã loại bỏ toàn bộ các trường và getter/setter liên quan đến follow/followers/following/posts
    @Transient
    private int postsCount;

    public Profile() {}
    public Profile(User user, String avatar, String fullname, LocalDate dob, String bio, String hobbies, String nickname) {
        this.user = user;
        this.avatar = avatar;
        this.fullname = fullname;
        this.dob = dob;
        this.bio = bio;
        this.hobbies = hobbies;
        this.nickname = nickname;
    }
    // Getters & setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    @com.fasterxml.jackson.annotation.JsonProperty("avatar")
    public String getAvatar() {
        if (avatar == null || avatar.isEmpty()) return null;
        if (avatar.startsWith("http")) return avatar;
        // Trả về URL đầy đủ cho frontend
        return "http://localhost:8080" + avatar;
    }
    public String getFullname() { return fullname; }
    public LocalDate getDob() { return dob; }
    public String getBio() { return bio; }
    public String getHobbies() { return hobbies; }
    public String getNickname() { return nickname; }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    // Xoá getter/setter followers/following dạng int để tránh trùng tên
    public int getPostsCount() {
        return postsCount;
    }
    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public void setBio(String bio) { this.bio = bio; }
    public void setHobbies(String hobbies) { this.hobbies = hobbies; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
