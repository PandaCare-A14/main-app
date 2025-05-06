package com.pandacare.mainapp.konsultasi_dokter.model.state;

import java.time.LocalDate;
import java.time.LocalTime;
import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;

public class RejectedState implements StatusJadwalDokter {

    @Override
    public String getStatusName() {
        return "REJECTED";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void handleRequest(JadwalKonsultasi jadwal, String idPasien, String message) {
        throw new IllegalStateException("Permintaan sudah ditolak.");
    }

    @Override
    public void handleApprove(JadwalKonsultasi jadwal) {
        throw new IllegalStateException("Permintaan sudah ditolak.");
    }

    @Override
    public void handleReject(JadwalKonsultasi jadwal, String reason) {
        throw new IllegalStateException("Permintaan sudah ditolak.");
    }

    @Override
    public void handleChangeSchedule(JadwalKonsultasi jadwal, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, String reason) {
        throw new IllegalStateException("Permintaan sudah ditolak.");
    }
}