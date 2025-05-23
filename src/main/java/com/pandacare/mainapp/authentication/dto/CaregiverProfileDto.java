package com.pandacare.mainapp.authentication.dto;

import java.util.UUID;

public record CaregiverProfileDto(
        UUID id,
        String name,
        String phoneNumber,
        String workAddress,
        String speciality
) {}