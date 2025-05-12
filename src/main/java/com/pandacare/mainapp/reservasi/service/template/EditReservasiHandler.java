package com.pandacare.mainapp.reservasi.service.template;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;

public class EditReservasiHandler extends ReservasiKonsultasiTemplate {

    private final String id, newDay, newStartTime, newEndTime;
    private final ReservasiKonsultasiRepository repository;
    private ReservasiKonsultasi reservasi;

    public EditReservasiHandler(String id, String newDay, String newStartTime, String newEndTime, ReservasiKonsultasiRepository repository) {
        this.id = id;
        this.newDay = newDay;
        this.newStartTime = newStartTime;
        this.newEndTime = newEndTime;
        this.repository = repository;
    }

    @Override
    protected void validate() {
        reservasi = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (reservasi.getStatusReservasi() != StatusReservasiKonsultasi.WAITING) {
            throw new IllegalStateException("Only schedules with status WAITING can be edited");
        }

        if (newStartTime.compareTo(newEndTime) >= 0) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    @Override
    protected ReservasiKonsultasi prepare() {
        reservasi.setDay(newDay);
        reservasi.setStartTime(newStartTime);
        reservasi.setEndTime(newEndTime);
        return reservasi;
    }

    @Override
    protected ReservasiKonsultasi save(ReservasiKonsultasi reservasi) {
        return repository.save(reservasi);
    }
}

