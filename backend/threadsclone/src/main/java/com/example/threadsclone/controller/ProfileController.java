package com.example.threadsclone.controller;

import com.example.threadsclone.entity.Profile;
import com.example.threadsclone.service.ProfileService;
import com.example.threadsclone.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final JwtUtil jwtUtil;

    public ProfileController(ProfileService profileService, JwtUtil jwtUtil) {
        this.profileService = profileService;
        this.jwtUtil = jwtUtil;
    }

    // ✅ API RESTful - lấy profile của chính mình từ JWT
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromHeader(authHeader);
        Profile profile = profileService.getProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    // ✅ Cập nhật profile (bao gồm avatar)
    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("avatar") MultipartFile avatar,
            @RequestParam("fullname") String fullname,
            @RequestParam("dob") String dob,
            @RequestParam("bio") String bio,
            @RequestParam(value = "hobbies", required = false) String hobbies,
            @RequestParam(value = "nickname", required = false) String nickname
    ) throws Exception {
        String username = extractUsernameFromHeader(authHeader);
        Profile profile = profileService.updateProfile(username, avatar, fullname, dob, bio, hobbies, nickname);
        return ResponseEntity.ok(profile);
    }

    // ⚠️ Tùy chọn - giữ lại nếu cần xem profile của người khác
    @GetMapping("/{username}")
    public ResponseEntity<?> getProfileByUsername(@PathVariable String username) {
        Profile profile = profileService.getProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    // ✅ Hàm phụ - Trích xuất username từ token
    private String extractUsernameFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUsername(token);
    }
}
