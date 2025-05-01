package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.model.state.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class JadwalKonsultasiStateHandler {

    private final Map<String, Supplier<StatusJadwalDokter>> stateMap = Map.of(
            "AVAILABLE", AvailableState::new,
            "REQUESTED", RequestedState::new,
            "APPROVED", ApprovedState::new,
            "REJECTED", RejectedState::new,
            "CHANGE_SCHEDULE", ChangeScheduleState::new);


    private StatusJadwalDokter resolveState(String status) {
        return Optional.ofNullable(stateMap.get(status))
                .map(Supplier::get)
                .orElseThrow(() -> new IllegalStateException("Unknown status: " + status));
    }

    public void request(JadwalKonsultasi jadwal, String idPasien, String message) {
        StatusJadwalDokter state = resolveState(jadwal.getStatusDokter());
        state.handleRequest(new JadwalStateContext(jadwal), idPasien, message);
    }

    public void approve(JadwalKonsultasi jadwal) {
        StatusJadwalDokter state = resolveState(jadwal.getStatusDokter());
        state.handleApprove(new JadwalStateContext(jadwal));
    }

    public void reject(JadwalKonsultasi jadwal, String reason) {
        StatusJadwalDokter state = resolveState(jadwal.getStatusDokter());
        state.handleReject(new JadwalStateContext(jadwal), reason);
    }

    public void changeSchedule(JadwalKonsultasi jadwal, String day, String start, String end, String reason) {
        StatusJadwalDokter state = resolveState(jadwal.getStatusDokter());
        state.handleChangeSchedule(new JadwalStateContext(jadwal), day, start, end, reason);
    }
}