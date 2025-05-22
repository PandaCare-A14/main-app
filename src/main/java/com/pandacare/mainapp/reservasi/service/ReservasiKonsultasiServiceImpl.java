package com.pandacare.mainapp.reservasi.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.statepacilian.StateFactory;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
//import com.pandacare.mainapp.reservasi.service.template.AcceptChangeReservasiHandler;
//import com.pandacare.mainapp.reservasi.service.template.EditReservasiHandler;
//import com.pandacare.mainapp.reservasi.service.template.RejectChangeReservasiHandler;
//import com.pandacare.mainapp.reservasi.service.template.RequestReservasiHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReservasiKonsultasiServiceImpl {
    @Autowired
    private ReservasiKonsultasiRepository repository;

    @Autowired
    private ScheduleService scheduleService;

    public ReservasiKonsultasi requestReservasi(UUID idSchedule, String idPacilian) {
        // Validasi apakah jadwal tersedia
        CaregiverSchedule schedule = scheduleService.getById(idSchedule);

        if (!scheduleService.isScheduleAvailable(idSchedule)) {
            throw new IllegalArgumentException("Selected schedule is not available");
        }

        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setIdDokter(schedule.getIdCaregiver().toString());
        reservasi.setIdPacilian(idPacilian);
        reservasi.setIdSchedule(schedule);
        reservasi.setDay(schedule.getDay().toString());
        reservasi.setStartTime(schedule.getStartTime());
        reservasi.setEndTime(schedule.getEndTime());
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        return repository.save(reservasi);
    }

    public ReservasiKonsultasi editReservasi(String id, UUID newScheduleId) {
        ReservasiKonsultasi reservasi = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservasi tidak ditemukan"));

        if (reservasi.getStatusReservasi() != StatusReservasiKonsultasi.WAITING) {
            throw new IllegalStateException("Tidak bisa mengedit reservasi yang sudah disetujui.");
        }

        CaregiverSchedule newSchedule = scheduleService.getById(newScheduleId);

        if (!scheduleService.isScheduleAvailable(newScheduleId)) {
            throw new IllegalArgumentException("Jadwal baru tidak tersedia");
        }

        reservasi.setStatePacilian(StateFactory.from(reservasi.getStatusReservasi()));

        // Extract and pass the day, startTime, and endTime as strings to editAsPacilian
        reservasi.editAsPacilian(
                newSchedule.getDay().toString(),
                newSchedule.getStartTime().toString(),
                newSchedule.getEndTime().toString()
        );

        // Update data dari schedule
        reservasi.setIdSchedule(newSchedule);
        reservasi.setDay(newSchedule.getDay().toString());
        reservasi.setStartTime(newSchedule.getStartTime());
        reservasi.setEndTime(newSchedule.getEndTime());

        return repository.save(reservasi);
    }

    public ReservasiKonsultasi acceptChangeReservasi(String id) {
        ReservasiKonsultasi reservasi = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservasi tidak ditemukan"));

        reservasi.setStatePacilian(StateFactory.from(reservasi.getStatusReservasi()));
        reservasi.acceptChangeAsPacilian();

        return repository.save(reservasi);
    }

    public void rejectChangeReservasi(String id) {
        ReservasiKonsultasi reservasi = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservasi tidak ditemukan"));

        reservasi.setStatePacilian(StateFactory.from(reservasi.getStatusReservasi()));
        reservasi.rejectChangeAsPacilian();

        repository.save(reservasi);
    }

    public ReservasiKonsultasi findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public List<ReservasiKonsultasi> findAllByPasien(String idPasien) {
        return repository.findAllByIdPasien(idPasien);
    }
}