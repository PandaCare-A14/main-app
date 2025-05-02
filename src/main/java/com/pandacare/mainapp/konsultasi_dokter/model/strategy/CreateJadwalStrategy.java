package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;

public interface CreateJadwalStrategy {
    JadwalKonsultasi create(String idDokter, String day, String startTime, String endTime);
}