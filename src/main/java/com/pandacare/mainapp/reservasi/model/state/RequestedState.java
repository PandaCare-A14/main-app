package com.pandacare.mainapp.reservasi.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;

public class RequestedState implements ReservasiState {
    private final ScheduleService scheduleService;

    public RequestedState(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public StatusReservasiKonsultasi getStatus() {
        return StatusReservasiKonsultasi.WAITING;
    }

    @Override
    public void handleApprove(ReservasiKonsultasi reservasi) {
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        reservasi.setState(new ApprovedState());
    }

    @Override
    public void handleReject(ReservasiKonsultasi reservasi) {
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.REJECTED);
        reservasi.setState(new RejectedState());
    }

    @Override
    public void handleChangeSchedule(ReservasiKonsultasi reservasi, String newScheduleId) {
        CaregiverSchedule newSchedule = scheduleService.getById(newScheduleId);
        reservasi.setIdSchedule(newSchedule);
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.ON_RESCHEDULE);
        reservasi.setState(new RescheduleState());
    }
}