package com.pandacare.mainapp.reservasi.model.statepacilian;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import java.time.LocalTime;

public class WaitingState implements ReservasiStatePacilian {

    @Override
    public void edit(ReservasiKonsultasi r, String newDay, String newStartTime, String newEndTime) {
        r.setDay(newDay);
        r.setStartTime(LocalTime.parse(newStartTime));
        r.setEndTime(LocalTime.parse(newEndTime));
    }

    @Override
    public void acceptChange(ReservasiKonsultasi r) {
        throw new IllegalStateException("Tidak ada perubahan jadwal yang bisa diterima.");
    }

    @Override
    public void rejectChange(ReservasiKonsultasi r) {
        throw new IllegalStateException("Tidak ada perubahan jadwal yang bisa ditolak.");
    }
}
