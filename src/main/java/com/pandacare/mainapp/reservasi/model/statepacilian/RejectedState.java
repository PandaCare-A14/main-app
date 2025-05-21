package com.pandacare.mainapp.reservasi.model.statepacilian;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

public class RejectedState implements ReservasiStatePacilian {

    @Override
    public void edit(ReservasiKonsultasi r, String newDay, String newStartTime, String newEndTime) {
        throw new IllegalStateException("Reservasi sudah ditolak.");
    }

    @Override
    public void acceptChange(ReservasiKonsultasi r) {
        throw new IllegalStateException("Reservasi sudah ditolak.");
    }

    @Override
    public void rejectChange(ReservasiKonsultasi r) {
        throw new IllegalStateException("Reservasi sudah ditolak.");
    }
}
