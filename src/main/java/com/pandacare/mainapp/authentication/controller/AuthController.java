package com.pandacare.mainapp.authentication.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RestTemplate restTemplate;
    @Value("${rust.auth.base-url}")
    private String rustAuthBaseUrl;

    public AuthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String url = rustAuthBaseUrl + "/token/obtain";
        return restTemplate.postForEntity(url, request, Object.class);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        String url = rustAuthBaseUrl + "/register";
        return restTemplate.postForEntity(url, request, Object.class);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String url = rustAuthBaseUrl + "/token/refresh";
        return restTemplate.postForEntity(url, request, Object.class);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String url = rustAuthBaseUrl + "/token/revoke";
        return restTemplate.postForEntity(url, request, Object.class);
    }
}