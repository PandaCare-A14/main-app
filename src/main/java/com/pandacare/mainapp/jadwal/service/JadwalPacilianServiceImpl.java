package com.pandacare.mainapp.jadwal.service;

import com.pandacare.mainapp.jadwal.enums.StatusJadwalPacilian;
import com.pandacare.mainapp.jadwal.model.JadwalKonsultasi;
import com.pandacare.mainapp.jadwal.repository.JadwalPacilianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JadwalPacilianServiceImpl {

    @Autowired
    private JadwalPacilianRepository repository;

    public JadwalKonsultasi requestJadwal(String idDokter, String day, String startTime, String endTime) {
        if (startTime.compareTo(endTime) >= 0) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        JadwalKonsultasi jadwal = new JadwalKonsultasi();
        jadwal.setIdDokter(idDokter);
        jadwal.setDay(day);
        jadwal.setStartTime(startTime);
        jadwal.setEndTime(endTime);
        jadwal.setStatusPacilian(StatusJadwalPacilian.WAITING);
        return jadwal;
    }

    public JadwalKonsultasi editSchedule(String id, String day, String startTime, String endTime) {
        JadwalKonsultasi jadwal = findById(id);

        if (jadwal == null) {
            throw new IllegalArgumentException("Schedule not found");
        }

        if (jadwal.getStatusPacilian() != StatusJadwalPacilian.WAITING) {
            throw new IllegalStateException("Only schedules with status WAITING can be edited");
        }

        if (startTime.compareTo(endTime) >= 0) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        jadwal.setDay(day);
        jadwal.setStartTime(startTime);
        jadwal.setEndTime(endTime);

        return jadwal;
    }

    public JadwalKonsultasi findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    }
}
