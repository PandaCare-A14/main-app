package com.pandacare.mainapp.reservasi.model.state;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;

public interface ReservasiState {
    StatusReservasiKonsultasi getStatus();
    void handleApprove(ReservasiKonsultasi reservasi);
    void handleReject(ReservasiKonsultasi reservasi);
    void handleChangeSchedule(ReservasiKonsultasi reservasi, String newScheduleId);
}