package com.pandacare.mainapp.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserRegistrationRequest {
    private String email;
    private String password;
    private String name;
    private BigDecimal nik;
    @JsonProperty("phone_number")
    private BigDecimal phoneNumber;
}