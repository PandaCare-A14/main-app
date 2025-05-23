package com.pandacare.mainapp.reservasi.model.statepacilian;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

public class OnReScheduleState implements ReservasiStatePacilian {

    @Override
    public void edit(ReservasiKonsultasi r, String newDay, String newStartTime, String newEndTime) {
        throw new IllegalStateException("Tidak bisa edit saat ada permintaan perubahan dari dokter.");
    }

    @Override
    public void acceptChange(ReservasiKonsultasi r) {
        r.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
    }

    @Override
    public void rejectChange(ReservasiKonsultasi r) {
        r.setStatusReservasi(StatusReservasiKonsultasi.REJECTED);
    }
}
