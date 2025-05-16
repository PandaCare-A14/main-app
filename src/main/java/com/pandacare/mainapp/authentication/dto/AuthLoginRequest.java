package com.pandacare.mainapp.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginRequest {
    private String email;
    private String password;
}