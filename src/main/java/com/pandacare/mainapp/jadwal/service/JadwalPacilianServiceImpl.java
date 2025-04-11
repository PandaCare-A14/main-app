package com.pandacare.mainapp.jadwal.service;

import com.pandacare.mainapp.jadwal.model.JadwalKonsultasi;
import org.springframework.stereotype.Service;

@Service
public class JadwalPacilianServiceImpl {
    public JadwalKonsultasi requestJadwal(String idDokter, String day, String startTime, String endTime) {
        if (startTime.compareTo(endTime) >= 0) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        JadwalKonsultasi jadwal = new JadwalKonsultasi();
        jadwal.setIdDokter(idDokter);
        jadwal.setDay(day);
        jadwal.setStartTime(startTime);
        jadwal.setEndTime(endTime);
        jadwal.setStatusPacilian("WAITING");
        return jadwal;
    }
}
