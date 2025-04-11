package com.pandacare.mainapp.jadwal.model;

import lombok.Data;

@Data
public class JadwalKonsultasi {
    private String idDokter;
    private String day;
    private String startTime;
    private String endTime;
    private String statusPacilian;
}
