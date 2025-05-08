package com.pandacare.mainapp.jadwal.service.template;

import com.pandacare.mainapp.jadwal.enums.StatusJadwalPacilian;
import com.pandacare.mainapp.jadwal.model.ReservasiKonsultasi;
import com.pandacare.mainapp.jadwal.repository.JadwalPacilianRepository;

public class EditScheduleHandler extends ReservasiKonsultasiTemplate {

    private final String id, newDay, newStartTime, newEndTime;
    private final JadwalPacilianRepository repository;
    private ReservasiKonsultasi jadwal;

    public EditScheduleHandler(String id, String newDay, String newStartTime, String newEndTime, JadwalPacilianRepository repository) {
        this.id = id;
        this.newDay = newDay;
        this.newStartTime = newStartTime;
        this.newEndTime = newEndTime;
        this.repository = repository;
    }

    @Override
    protected void validate() {
        jadwal = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (jadwal.getStatusPacilian() != StatusJadwalPacilian.WAITING) {
            throw new IllegalStateException("Only schedules with status WAITING can be edited");
        }

        if (newStartTime.compareTo(newEndTime) >= 0) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    @Override
    protected ReservasiKonsultasi prepare() {
        jadwal.setDay(newDay);
        jadwal.setStartTime(newStartTime);
        jadwal.setEndTime(newEndTime);
        return jadwal;
    }

    @Override
    protected ReservasiKonsultasi save(ReservasiKonsultasi jadwal) {
        return repository.save(jadwal);
    }
}

