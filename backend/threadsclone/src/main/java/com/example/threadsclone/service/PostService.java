package com.example.threadsclone.service;

import com.example.threadsclone.entity.Post;
import com.example.threadsclone.entity.User;
import com.example.threadsclone.repository.PostRepository;
import com.example.threadsclone.repository.UserRepository;
import com.example.threadsclone.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    // private final FriendshipRepository friendshipRepository;

    @Value("${media.upload.dir:uploads/media}")
    private String mediaUploadDir;

    public PostService(PostRepository postRepository, UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        // this.friendshipRepository = friendshipRepository;
    }

    // ✅ Like bài viết
    public int likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (post.like(username)) {
            postRepository.save(post);
        }
        return post.getLikes();
    }

    // ✅ Unlike bài viết
    public int unlikePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (post.unlike(username)) {
            postRepository.save(post);
            return post.getLikes();
        } else {
            throw new RuntimeException("User has not liked this post");
        }
    }

    // ✅ Tạo bài post mới
    public Post createPost(String username, String content, List<MultipartFile> mediaFiles) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post(user, content);

        List<String> mediaUrls = new ArrayList<>();
        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            try {
                Path uploadPath = Paths.get(mediaUploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (MultipartFile file : mediaFiles) {
                    if (file.isEmpty()) continue;

                    String originalFilename = StringUtils.cleanPath(
                            Optional.ofNullable(file.getOriginalFilename()).orElse("file")
                    );

                    String ext = "";
                    int dotIdx = originalFilename.lastIndexOf('.');
                    if (dotIdx > 0) ext = originalFilename.substring(dotIdx);

                    String filename = System.currentTimeMillis() + "-" + UUID.randomUUID() + ext;
                    Path filePath = uploadPath.resolve(filename);
                    Files.copy(file.getInputStream(), filePath);

                    mediaUrls.add("/uploads/media/" + filename);
                }

            } catch (Exception e) {
                throw new RuntimeException("Failed to save media files", e);
            }
        }

        post.setMedia(mediaUrls);
        return postRepository.save(post);
    }

    // ✅ Lấy toàn bộ bài viết (có thể điều chỉnh để lọc theo bạn bè sau)
    public List<Post> getFeed(String username) {
        return postRepository.findAll();
    }
}
