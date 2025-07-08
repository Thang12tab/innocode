package com.example.threadsclone.controller;

import com.example.threadsclone.entity.Post;
import com.example.threadsclone.entity.Profile;
import com.example.threadsclone.repository.ProfileRepository;
import com.example.threadsclone.security.JwtUtil;
import com.example.threadsclone.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final JwtUtil jwtUtil;
    private final ProfileRepository profileRepository;

    public PostController(PostService postService, JwtUtil jwtUtil, ProfileRepository profileRepository) {
        this.postService = postService;
        this.jwtUtil = jwtUtil;
        this.profileRepository = profileRepository;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPost(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "media", required = false) List<MultipartFile> mediaFiles) {
        String username = extractUsernameFromHeader(authHeader);
        Post post = postService.createPost(username, content, mediaFiles);
        Profile profile = profileRepository.findByUser(post.getUser()).orElse(null);
        String avatarUrl = (profile != null && profile.getAvatar() != null && !profile.getAvatar().isEmpty())
                ? profile.getAvatar()
                : "http://localhost:8080/default-avatar.png";
        PostResponse response = PostResponse.fromEntity(post, avatarUrl, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromHeader(authHeader);
        int newLikes = postService.likePost(postId, username);
        return ResponseEntity.ok(newLikes);
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromHeader(authHeader);
        int newLikes = postService.unlikePost(postId, username);
        return ResponseEntity.ok(newLikes);
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getFeed(@RequestHeader("Authorization") String authHeader,
                                     @RequestParam(value = "limit", required = false) Integer limit,
                                     @RequestParam(value = "offset", required = false) Integer offset) {
        String username = extractUsernameFromHeader(authHeader);
        List<Post> posts = postService.getFeed(username);

        var userPostsMap = new java.util.HashMap<String, List<Post>>();
        for (Post post : posts) {
            String uname = post.getUser().getUsername();
            userPostsMap.computeIfAbsent(uname, k -> new java.util.ArrayList<>()).add(post);
        }

        var diversePosts = new java.util.ArrayList<Post>();
        var userOrder = new java.util.ArrayList<>(userPostsMap.keySet());
        java.util.Collections.shuffle(userOrder);
        for (String uname : userOrder) {
            List<Post> userPosts = userPostsMap.get(uname);
            userPosts.sort(java.util.Comparator.comparing(Post::getCreatedAt).reversed()
                    .thenComparing(Post::getLikes, java.util.Comparator.reverseOrder())
                    .thenComparing(Post::getComments, java.util.Comparator.reverseOrder()));
            int take = Math.min(userPosts.size(), 2);
            diversePosts.addAll(userPosts.subList(0, take));
        }

        diversePosts.sort(java.util.Comparator.comparing(Post::getCreatedAt).reversed()
                .thenComparing(Post::getLikes, java.util.Comparator.reverseOrder())
                .thenComparing(Post::getComments, java.util.Comparator.reverseOrder()));

        int from = (offset != null) ? offset : 0;
        int to = (limit != null) ? Math.min(from + limit, diversePosts.size()) : diversePosts.size();
        List<Post> paged = diversePosts.subList(Math.min(from, diversePosts.size()),
                Math.max(Math.min(to, diversePosts.size()), Math.min(from, diversePosts.size())));

        List<PostResponse> response = paged.stream()
                .map(post -> PostResponse.fromEntity(post, profileRepository, username)) // ✅ truyền current user
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private String extractUsernameFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUsername(token);
    }

    // ✅ RESPONSE DTO
    public static class PostResponse {
        private Long id;
        private String username;
        private String avatar;
        private String content;
        private String timeAgo;
        private int likes;
        private int comments;
        private List<String> media;
        private boolean liked; // ✅ mới thêm

        public static PostResponse fromEntity(Post post, ProfileRepository profileRepository, String currentUsername) {
            Profile profile = profileRepository.findByUser(post.getUser()).orElse(null);
            String avatarUrl = (profile != null && profile.getAvatar() != null && !profile.getAvatar().isEmpty())
                    ? profile.getAvatar()
                    : "http://localhost:8080/black-avatar.png";
            if (!avatarUrl.startsWith("http")) {
                avatarUrl = "http://localhost:8080" + avatarUrl;
            }
            PostResponse res = fromEntity(post, avatarUrl, currentUsername);
            return res;
        }

        public static PostResponse fromEntity(Post post, String avatarUrl, String currentUsername) {
            PostResponse res = new PostResponse();
            res.id = post.getId();
            res.username = post.getUser().getUsername();
            res.avatar = avatarUrl;
            res.content = post.getContent();
            res.timeAgo = post.getCreatedAt() != null ? post.getCreatedAt().toString() : "";
            res.likes = post.getLikes();
            res.comments = post.getComments();
            res.liked = post.getLikedUsers().contains(currentUsername); // ✅ kiểm tra user đã tym chưa
            res.media = (post.getMedia() != null) ?
                    post.getMedia().stream()
                            .map(url -> url.startsWith("http") ? url : "http://localhost:8080" + url)
                            .collect(Collectors.toList()) :
                    java.util.Collections.emptyList();
            return res;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getAvatar() { return avatar; }
        public String getContent() { return content; }
        public String getTimeAgo() { return timeAgo; }
        public int getLikes() { return likes; }
        public int getComments() { return comments; }
        public List<String> getMedia() { return media; }
        public boolean isLiked() { return liked; }
    }
}
