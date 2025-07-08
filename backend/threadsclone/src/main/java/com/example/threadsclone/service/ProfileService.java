package com.example.threadsclone.service;

import com.example.threadsclone.entity.Profile;
import com.example.threadsclone.entity.User;
import com.example.threadsclone.repository.ProfileRepository;
import com.example.threadsclone.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final String uploadDir = "uploads/avatars";

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public Profile updateProfile(String username, MultipartFile avatar, String fullname, String dob, String bio, String hobbies, String nickname) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        String avatarPath = null;
        if (avatar != null && !avatar.isEmpty()) {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = username + "_" + System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            avatar.transferTo(filePath);
            avatarPath = "/" + uploadDir + "/" + fileName;
        }
        LocalDate dateOfBirth = LocalDate.parse(dob);
        Profile profile = profileRepository.findByUser(user).orElse(null);
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
        }
        if (avatarPath != null) profile.setAvatar(avatarPath);
        profile.setFullname(fullname);
        profile.setDob(dateOfBirth);
        profile.setBio(bio);
        profile.setHobbies(hobbies);
        profile.setNickname(nickname);
        return profileRepository.save(profile);
    }

    public Profile getProfileByUsername(String username) {
        Profile profile = profileRepository.findByUser_Username(username).orElseThrow(() -> new RuntimeException("Profile not found"));
        // Đã loại bỏ các trường liên quan đến follow/followers/following/posts
        return profile;
    }
}
