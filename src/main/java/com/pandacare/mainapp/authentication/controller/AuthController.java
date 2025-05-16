package com.pandacare.mainapp.authentication.controller;

import com.pandacare.mainapp.authentication.dto.AuthLoginRequest;
import com.pandacare.mainapp.authentication.dto.AuthTokenResponse;
import com.pandacare.mainapp.authentication.dto.AuthUserRegistrationRequest;
import com.pandacare.mainapp.authentication.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthUserRegistrationRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AuthTokenResponse> refreshToken(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.refreshToken(request.get("refresh_token")));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.logout(request.get("refresh_token")));
    }
}