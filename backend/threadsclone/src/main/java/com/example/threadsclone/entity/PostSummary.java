package com.example.threadsclone.entity;

public class PostSummary {
    private String image;
    private String content;
    // Có thể bổ sung các trường khác nếu cần

    public PostSummary() {}
    public PostSummary(String image, String content) {
        this.image = image;
        this.content = content;
    }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
