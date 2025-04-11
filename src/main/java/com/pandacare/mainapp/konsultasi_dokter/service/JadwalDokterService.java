package com.pandacare.mainapp.konsultasi_dokter.service;

public interface JadwalDokterService {
    void createJadwal(Long idDokter, String day, String startTime, String endTime);

    boolean approveJadwal(String idJadwal);

    boolean rejectJadwal(String idJadwal);

    boolean changeJadwal(String idJadwal, String day, String startTime, String endTime);
}