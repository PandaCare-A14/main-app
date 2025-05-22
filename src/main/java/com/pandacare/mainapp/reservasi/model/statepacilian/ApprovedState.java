package com.pandacare.mainapp.reservasi.model.statepacilian;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

public class ApprovedState implements ReservasiStatePacilian {

    @Override
    public void edit(ReservasiKonsultasi r, String newDay, String newStartTime, String newEndTime) {
        throw new IllegalStateException("Tidak bisa mengedit reservasi yang sudah disetujui.");
    }

    @Override
    public void acceptChange(ReservasiKonsultasi r) {
        throw new IllegalStateException("Reservasi sudah disetujui.");
    }

    @Override
    public void rejectChange(ReservasiKonsultasi r) {
        throw new IllegalStateException("Tidak bisa menolak jadwal yang sudah disetujui.");
    }
}
