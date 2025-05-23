package com.pandacare.mainapp.reservasi.model.state;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

import java.util.UUID;

public class ApprovedState implements ReservasiState {
    @Override
    public StatusReservasiKonsultasi getStatus() {
        return StatusReservasiKonsultasi.APPROVED;
    }

    @Override
    public void handleApprove(ReservasiKonsultasi reservasi) {
        throw new IllegalStateException("This reservation has already been approved.");
    }

    @Override
    public void handleReject(ReservasiKonsultasi reservasi) {
        throw new IllegalStateException("This reservation has already been approved.");
    }

    @Override
    public void handleChangeSchedule(ReservasiKonsultasi reservasi, UUID newScheduleId) {
        throw new IllegalStateException("This reservation has already been approved.");
    }
}