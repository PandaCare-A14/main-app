package com.pandacare.mainapp.doctor_profile.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.Map;

@Data
public class CreateDoctorRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?[0-9\\s-]+$", message = "Invalid phone number format")
    private String phoneNumber;

    private String workAddress;

    @NotNull(message = "Work schedule is required")
    private Map<String, String> workSchedule;

    @NotBlank(message = "Speciality is required")
    private String speciality;
}