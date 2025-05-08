package com.pandacare.mainapp.reservasi.model;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import lombok.Data;

@Data
public class ReservasiKonsultasi {
    private String id;
    private String idDokter;
    private String idPasien;
    private String day;
    private String startTime;
    private String endTime;
    private StatusReservasiKonsultasi statusPacilian;
    private boolean changeSchedule;
    private String newDay;
    private String newStartTime;
    private String newEndTime;
}