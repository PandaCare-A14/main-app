package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;

public interface JadwalDokterService {
    JadwalKonsultasi createJadwal(String idDokter, String day, String startTime, String endTime);
    boolean changeJadwal(String idJadwal, String newDay, String newStartTime, String newEndTime, String message);
    boolean approveJadwal(String idJadwal);
    boolean rejectJadwal(String idJadwal);
}