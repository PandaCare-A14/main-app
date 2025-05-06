package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public interface JadwalDokterService {
    JadwalKonsultasi createJadwal(String idDokter, LocalDate date, LocalTime startTime, LocalTime endTime);

    boolean changeJadwal(String idJadwal, String newDay, String newStartTime, String newEndTime, String message);

    boolean approveJadwal(String idJadwal);

    boolean rejectJadwal(String idJadwal);

    List<JadwalKonsultasi> findByIdDokter(String idDokter);

    List<JadwalKonsultasi> findByIdDokterAndStatus(String idDokter, String status);

    List<JadwalKonsultasi> findByIdPasien(String idPasien);

    JadwalKonsultasi findById(String id);
}