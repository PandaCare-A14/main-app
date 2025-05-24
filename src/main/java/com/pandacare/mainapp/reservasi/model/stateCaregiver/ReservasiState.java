package com.pandacare.mainapp.reservasi.model.stateCaregiver;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;

import java.util.UUID;

public interface ReservasiState {
    StatusReservasiKonsultasi getStatus();
    void handleApprove(ReservasiKonsultasi reservasi);
    void handleReject(ReservasiKonsultasi reservasi);
    void handleChangeSchedule(ReservasiKonsultasi reservasi, UUID newScheduleId);
}