package com.pandacare.mainapp.reservasi.service.template;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;

import java.util.UUID;

import java.util.UUID;

public class RequestReservasiHandler extends ReservasiKonsultasiTemplate {
    private final String idPacilian;
    private UUID idSchedule;
    private final ReservasiKonsultasiRepository repository;
    private final ScheduleService scheduleService;

    public RequestReservasiHandler(
            UUID idSchedule,
            String idPacilian,
            ReservasiKonsultasiRepository repository,
            ScheduleService scheduleService) {
        this.idSchedule = idSchedule;
        this.idPacilian = idPacilian;
        this.repository = repository;
        this.scheduleService = scheduleService;
    }

    @Override
    protected void validate() {
        if (idSchedule == null || idPacilian == null) {
            throw new IllegalArgumentException("Schedule ID and Patient ID are required");
        }

        if (!scheduleService.isScheduleAvailable(idSchedule)) {
            throw new IllegalArgumentException("Selected schedule is not available");
        }
    }

    @Override
    protected ReservasiKonsultasi prepare() {
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        // Get the schedule data
        CaregiverSchedule schedule = scheduleService.getById(idSchedule);

        reservasi.setId(UUID.randomUUID().toString());
        reservasi.setIdPacilian(idPacilian);
        reservasi.setIdSchedule(scheduleService.getById(idSchedule));
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        // Get day and time from the schedule if available
        if (schedule != null) {
            reservasi.setDay(schedule.getDay().toString());
            reservasi.setStartTime(schedule.getStartTime());
            reservasi.setEndTime(schedule.getEndTime());
            // If the CaregiverSchedule has access to caregiver ID
            if (schedule.getIdCaregiver() != null) {
                reservasi.setIdDokter(schedule.getIdCaregiver().toString());
            }
        }

        return reservasi;
    }

    @Override
    protected ReservasiKonsultasi save(ReservasiKonsultasi reservasi) {
        scheduleService.updateScheduleStatus(idSchedule, ScheduleStatus.UNAVAILABLE);
        return repository.save(reservasi);
    }
}
