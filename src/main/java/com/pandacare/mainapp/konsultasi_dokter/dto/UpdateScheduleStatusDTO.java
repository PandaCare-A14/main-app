package com.pandacare.mainapp.konsultasi_dokter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateScheduleStatusDTO {
    @NotNull
    private String statusCaregiver;
    private String day;
    private String startTime;
    private String endTime;
    private String message;
}
