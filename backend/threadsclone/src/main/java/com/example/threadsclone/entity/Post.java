package com.example.threadsclone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;
    private LocalDateTime createdAt;
    private int likes;
    private int comments;

    // Danh sách media đính kèm bài viết (ảnh/video)
    @ElementCollection
    @CollectionTable(name = "post_media", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "media_url")
    private List<String> media = new ArrayList<>();

    // Danh sách người đã like bài viết (username)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "username")
    private List<String> likedUsers = new ArrayList<>();

    // Constructors
    public Post() {}

    public Post(User user, String content) {
        this.user = user;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.likes = 0;
        this.comments = 0;
    }

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getLikes() { return likes; }
    public int getComments() { return comments; }
    public List<String> getMedia() { return media; }
    public List<String> getLikedUsers() { return likedUsers; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setComments(int comments) { this.comments = comments; }
    public void setMedia(List<String> media) { this.media = media; }
    public void setLikedUsers(List<String> likedUsers) { this.likedUsers = likedUsers; }

    // ✅ Logic hỗ trợ like và unlike (có thể dùng trong service)
    public boolean like(String username) {
        if (!likedUsers.contains(username)) {
            likedUsers.add(username);
            likes++;
            return true;
        }
        return false;
    }

    public boolean unlike(String username) {
        if (likedUsers.contains(username)) {
            likedUsers.remove(username);
            likes = Math.max(0, likes - 1);
            return true;
        }
        return false;
    }
}
