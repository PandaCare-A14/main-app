package com.pandacare.mainapp.reservasi.service;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.statepacilian.StateFactory;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ReservasiKonsultasiServiceImpl {
    @Autowired
    private ReservasiKonsultasiRepository repository;

    @Autowired
    private ScheduleService scheduleService;

    public ReservasiKonsultasi requestReservasi(UUID idSchedule, UUID idPacilian) {
        // Validasi apakah jadwal tersedia
        CaregiverSchedule schedule = scheduleService.getById(idSchedule);

        if (!scheduleService.isScheduleAvailable(idSchedule)) {
            throw new IllegalArgumentException("Selected schedule is not available");
        }

        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setIdPacilian(idPacilian);
        reservasi.setIdSchedule(schedule);
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        scheduleService.updateScheduleStatus(schedule, ScheduleStatus.UNAVAILABLE);

        // Update schedule status to UNAVAILABLE
        scheduleService.updateScheduleStatus(schedule, ScheduleStatus.UNAVAILABLE);

        return repository.save(reservasi);
    }

    public ReservasiKonsultasi editReservasi(UUID id, UUID newScheduleId) {
        ReservasiKonsultasi reservasi = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservasi tidak ditemukan"));

        if (reservasi.getStatusReservasi() != StatusReservasiKonsultasi.WAITING) {
            throw new IllegalStateException("Tidak bisa mengedit reservasi yang sudah disetujui.");
        }

        // Get the old schedule reference before updating
        CaregiverSchedule oldSchedule = reservasi.getIdSchedule();
        CaregiverSchedule newSchedule = scheduleService.getById(newScheduleId);

        if (!scheduleService.isScheduleAvailable(newScheduleId)) {
            throw new IllegalArgumentException("Jadwal baru tidak tersedia");
        }

        reservasi.setStatePacilian(StateFactory.from(reservasi.getStatusReservasi()));

        String newDay = newSchedule.getDay().toString();
        String newStartTime = newSchedule.getStartTime().toString();
        String newEndTime = newSchedule.getEndTime().toString();

        reservasi.editAsPacilian(newDay, newStartTime, newEndTime);
        reservasi.setIdSchedule(newSchedule);

        // Free up the old schedule
        scheduleService.updateScheduleStatus(oldSchedule, ScheduleStatus.AVAILABLE);

        // Mark the new schedule as unavailable
        scheduleService.updateScheduleStatus(newSchedule, ScheduleStatus.UNAVAILABLE);

        return repository.save(reservasi);
    }

    public ReservasiKonsultasi acceptChangeReservasi(UUID id) {
        ReservasiKonsultasi reservasi = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservasi tidak ditemukan"));

        reservasi.setStatePacilian(StateFactory.from(reservasi.getStatusReservasi()));
        reservasi.acceptChangeAsPacilian();

        return repository.save(reservasi);
    }

    public void rejectChangeReservasi(UUID id) {
        ReservasiKonsultasi reservasi = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservasi tidak ditemukan"));

        reservasi.setStatePacilian(StateFactory.from(reservasi.getStatusReservasi()));
        reservasi.rejectChangeAsPacilian();

        repository.save(reservasi);
    }

    public ReservasiKonsultasi findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Async
    public CompletableFuture<List<ReservasiKonsultasi>> findAllByPacilian(UUID idPacilian) {
        List<ReservasiKonsultasi> list = repository.findAllByIdPacilian(idPacilian);
        return CompletableFuture.completedFuture(list);
    }
}