package com.pandacare.mainapp.authentication.service;

import com.pandacare.mainapp.authentication.dto.AuthLoginRequest;
import com.pandacare.mainapp.authentication.dto.AuthTokenResponse;
import com.pandacare.mainapp.authentication.dto.AuthUserRegistrationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AuthServiceClient authServiceClient;

    public AuthService(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    public AuthTokenResponse login(AuthLoginRequest request) {
        return authServiceClient.login(request);
    }

    @Transactional
    public String register(AuthUserRegistrationRequest request) {
        return authServiceClient.register(request);
    }

    public AuthTokenResponse refreshToken(String refreshToken) {
        return authServiceClient.refreshToken(refreshToken);
    }

    public String logout(String refreshToken) {
        return authServiceClient.revokeToken(refreshToken);
    }
}