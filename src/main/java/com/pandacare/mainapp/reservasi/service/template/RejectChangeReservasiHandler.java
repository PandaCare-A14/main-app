package com.pandacare.mainapp.reservasi.service.template;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;

import java.util.UUID;

public class RejectChangeReservasiHandler extends ReservasiKonsultasiTemplate {

    private final String id;
    private final ReservasiKonsultasiRepository repository;
    private ReservasiKonsultasi reservasi;
    private final ScheduleService scheduleService;

    public RejectChangeReservasiHandler(String id, ReservasiKonsultasiRepository repository, ScheduleService scheduleService) {
        this.id = id;
        this.repository = repository;
        this.scheduleService = scheduleService;
    }

    @Override
    protected void validate() {
        reservasi = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (reservasi.getStatusReservasi() != StatusReservasiKonsultasi.ON_RESCHEDULE) {
            throw new IllegalStateException("No change request exists for this schedule");
        }
    }

    @Override
    protected ReservasiKonsultasi prepare() {
        return reservasi;
    }

    @Override
    protected ReservasiKonsultasi save(ReservasiKonsultasi reservasi) {
        UUID scheduleId = reservasi.getIdSchedule().getId();

        scheduleService.updateScheduleStatus(scheduleId, ScheduleStatus.AVAILABLE);
        repository.deleteById(reservasi.getId());
        return null;
    }
}
