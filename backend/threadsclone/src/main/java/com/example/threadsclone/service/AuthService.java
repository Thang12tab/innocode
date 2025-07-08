package com.example.threadsclone.service;

import com.example.threadsclone.dto.AuthRequest;
import com.example.threadsclone.dto.AuthResponse;
import com.example.threadsclone.entity.User;
import com.example.threadsclone.repository.UserRepository;
import com.example.threadsclone.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse signup(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // ✅ Generate token with username
        String token = jwtUtil.generateToken(user.getUsername(), String.valueOf(user.getId()));

        // ✅ Return token + username
        return new AuthResponse(token, user.getUsername());
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // ✅ Generate token with username
        String token = jwtUtil.generateToken(user.getUsername(), String.valueOf(user.getId()));

        // ✅ Return token + username
        return new AuthResponse(token, user.getUsername());
    }
}
