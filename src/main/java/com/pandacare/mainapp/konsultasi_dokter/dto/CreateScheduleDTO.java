package com.pandacare.mainapp.konsultasi_dokter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateScheduleDTO {
    @NotNull
    private String day;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
    private Integer weeks;
}
