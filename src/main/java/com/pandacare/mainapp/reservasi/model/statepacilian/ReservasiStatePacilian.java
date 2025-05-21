package com.pandacare.mainapp.reservasi.model.statepacilian;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

public interface ReservasiStatePacilian {
    void edit(ReservasiKonsultasi reservasi, String newDay, String newStartTime, String newEndTime);
    void acceptChange(ReservasiKonsultasi reservasi);
    void rejectChange(ReservasiKonsultasi reservasi);
}
