package com.example.threadsclone.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "friendships")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private User friend;

    public Friendship() {}
    public Friendship(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public User getFriend() { return friend; }
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setFriend(User friend) { this.friend = friend; }
}
