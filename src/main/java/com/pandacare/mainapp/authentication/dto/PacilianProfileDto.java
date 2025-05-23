package com.pandacare.mainapp.authentication.dto;

import java.util.UUID;

public record PacilianProfileDto(
        UUID id,
        String name,
        String phoneNumber,
        String address,
        String medicalHistory
) {}
