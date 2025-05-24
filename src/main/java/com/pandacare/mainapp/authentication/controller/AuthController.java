package com.pandacare.mainapp.authentication.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;

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
        String url = rustAuthBaseUrl + "/api/token/obtain";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            return restTemplate.postForEntity(url, entity, Object.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Login failed", "details", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        String url = rustAuthBaseUrl + "/api/register";

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", request.get("email"));
        payload.put("password", request.get("password"));
        payload.put("role", request.get("role"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", response.getBody());
            result.put("http_status", response.getStatusCode().value());

            return ResponseEntity.status(response.getStatusCode()).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed", "details", e.getMessage()));
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String url = rustAuthBaseUrl + "/api/token/refresh";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            return restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token refresh failed", "details", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String url = rustAuthBaseUrl + "/api/token/revoke";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            return restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Logged out locally"));
        }
    }
}