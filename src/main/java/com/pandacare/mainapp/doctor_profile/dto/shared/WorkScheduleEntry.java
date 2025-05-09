package com.pandacare.mainapp.doctor_profile.dto.shared;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class WorkScheduleEntry {
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$",
            message = "Time format must be HH:MM-HH:MM")
    private String timeSlot;
}