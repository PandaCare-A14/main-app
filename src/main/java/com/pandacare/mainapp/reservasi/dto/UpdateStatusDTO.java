package com.pandacare.mainapp.reservasi.dto;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusDTO {
    @NotNull
    private StatusReservasiKonsultasi status;
    private UUID newScheduleId;
}