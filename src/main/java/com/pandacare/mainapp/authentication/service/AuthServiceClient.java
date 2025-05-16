package com.pandacare.mainapp.authentication.service;

import com.pandacare.mainapp.authentication.dto.AuthLoginRequest;
import com.pandacare.mainapp.authentication.dto.AuthTokenResponse;
import com.pandacare.mainapp.authentication.dto.AuthUserRegistrationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceClient {
    private final RestTemplate restTemplate;
    private final String authBaseUrl;

    public AuthServiceClient(RestTemplate restTemplate,
                             @Value("${pandacare.auth.baseUrl:https://pandacare-auth.up.railway.app/api}") String authBaseUrl) {
        this.restTemplate = restTemplate;
        this.authBaseUrl = authBaseUrl;
    }

    public AuthTokenResponse login(AuthLoginRequest loginRequest) {
        String url = authBaseUrl + "/token/obtain";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthLoginRequest> request = new HttpEntity<>(loginRequest, headers);

        return restTemplate.postForObject(url, request, AuthTokenResponse.class);
    }

    public String register(AuthUserRegistrationRequest registrationRequest) {
        String url = authBaseUrl + "/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthUserRegistrationRequest> request = new HttpEntity<>(registrationRequest, headers);

        return restTemplate.postForObject(url, request, String.class);
    }

    public AuthTokenResponse refreshToken(String refreshToken) {
        String url = authBaseUrl + "/token/refresh";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url, request, AuthTokenResponse.class);
    }

    public String revokeToken(String token) {
        String url = authBaseUrl + "/token/revoke";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("refresh_token", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url, request, String.class); // Ubah Map.class ke String.class
    }
}