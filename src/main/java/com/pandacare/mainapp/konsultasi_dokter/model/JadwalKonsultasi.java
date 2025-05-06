package com.pandacare.mainapp.konsultasi_dokter.model;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JadwalKonsultasi {
    private String id;
    private String statusDokter;
    private String statusPacilian;
    private String idDokter;
    private String idPasien;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;
    private String message;
    private boolean changeSchedule = false;

    public JadwalKonsultasi() {
        this.statusDokter = "AVAILABLE";
    }

    public boolean isAvailable() {
        return "AVAILABLE".equals(statusDokter);
    }
}