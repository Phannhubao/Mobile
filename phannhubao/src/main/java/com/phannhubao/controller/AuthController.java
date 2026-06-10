package com.phannhubao.controller;

import com.phannhubao.dto.request.LoginRequest;
import com.phannhubao.dto.request.RefreshTokenRequest;
import com.phannhubao.dto.request.RegisterRequest;
import com.phannhubao.dto.response.AuthResponse;
import com.phannhubao.security.CustomUserDetails;
import com.phannhubao.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            authService.logout(userDetails.getEmail());
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "User logged out successfully");
        return ResponseEntity.ok(response);
    }
}
