package com.pandacare.mainapp.reservasi.model.stateCaregiver;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

import java.util.UUID;

public class RescheduleState implements ReservasiState {
    @Override
    public StatusReservasiKonsultasi getStatus() {
        return StatusReservasiKonsultasi.ON_RESCHEDULE;
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
    public void handleChangeSchedule(ReservasiKonsultasi reservasi, UUID newScheduleId) {
        throw new IllegalStateException("Operation not allowed. This reservation is on reschedule.");
    }
}