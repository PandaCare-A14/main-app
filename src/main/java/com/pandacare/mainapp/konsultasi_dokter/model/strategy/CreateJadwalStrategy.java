package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import java.time.LocalDate;
import java.time.LocalTime;

public interface CreateJadwalStrategy {
    JadwalKonsultasi create(String idDokter, LocalDate date, LocalTime startTime, LocalTime endTime);
}