package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.model.state.AvailableState;

import java.time.LocalTime;
import java.time.LocalDate;

public class CreateManualStrategy implements CreateJadwalStrategy {

    @Override
    public JadwalKonsultasi create(String idDokter, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (idDokter == null || idDokter.isBlank()
                || date == null
                || startTime == null
                || endTime == null) {
            throw new IllegalArgumentException("Field tidak boleh null atau kosong.");
        }

        JadwalKonsultasi jadwal = new JadwalKonsultasi();
        jadwal.setIdDokter(idDokter);
        jadwal.setDate(date);
        jadwal.setStartTime(startTime);
        jadwal.setEndTime(endTime);
        jadwal.setState(new AvailableState());
        jadwal.setChangeSchedule(false);

        return jadwal;
    }
}