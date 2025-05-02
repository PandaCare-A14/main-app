package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;

public class CreateManualStrategy implements CreateJadwalStrategy {

    @Override
    public JadwalKonsultasi create(String idDokter, String day, String startTime, String endTime) {
        if (idDokter == null || idDokter.isBlank()
                || day == null || day.isBlank()
                || startTime == null || startTime.isBlank()
                || endTime == null || endTime.isBlank()) {
            throw new IllegalArgumentException("Field tidak boleh null atau kosong.");
        }

        JadwalKonsultasi jadwal = new JadwalKonsultasi();
        jadwal.setIdDokter(idDokter);
        jadwal.setDay(day);
        jadwal.setStartTime(startTime);
        jadwal.setEndTime(endTime);
        jadwal.setStatusDokter("AVAILABLE");
        jadwal.setChangeSchedule(false);

        return jadwal;
    }
}