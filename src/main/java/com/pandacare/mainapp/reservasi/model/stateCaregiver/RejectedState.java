package com.pandacare.mainapp.reservasi.model.stateCaregiver;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

import java.util.UUID;

public class RejectedState implements ReservasiState {
    @Override
    public StatusReservasiKonsultasi getStatus() {
        return StatusReservasiKonsultasi.REJECTED;
    }

    @Override
    public void handleApprove(ReservasiKonsultasi reservasi) {
        throw new IllegalStateException("This reservation has already been rejected.");
    }

    @Override
    public void handleReject(ReservasiKonsultasi reservasi) {
        throw new IllegalStateException("This reservation has already been rejected.");
    }

    @Override
    public void handleChangeSchedule(ReservasiKonsultasi reservasi, UUID newScheduleId) {
        throw new IllegalStateException("This reservation has already been rejected.");
    }
}
