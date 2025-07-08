package com.example.threadsclone.controller;

import com.example.threadsclone.dto.AuthRequest;
import com.example.threadsclone.dto.AuthResponse;
import com.example.threadsclone.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5500") // ✅ Đảm bảo CORS cho frontend
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody AuthRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.ok(response); // ✅ Trả ResponseEntity
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response); // ✅ Trả ResponseEntity
    }
}
