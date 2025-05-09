package com.pandacare.mainapp.doctor_profile.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class DoctorResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String workAddress;
    private Map<String, String> workSchedule;
    private String speciality;
    private double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}