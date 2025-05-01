package com.pandacare.mainapp.jadwalKonsultasi.model;

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
    private String day;
    private String startTime;
    private String endTime;
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