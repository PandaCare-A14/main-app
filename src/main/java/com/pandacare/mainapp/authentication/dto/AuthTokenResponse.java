package com.pandacare.mainapp.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenResponse {
    private String access;
    private String refresh;
}