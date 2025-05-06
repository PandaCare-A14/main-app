package com.pandacare.mainapp.konsultasi_dokter.model.state;

import java.time.LocalDate;
import java.time.LocalTime;
import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;

public class ApprovedState implements StatusJadwalDokter {

    @Override
    public String getStatusName() {
        return "APPROVED";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void handleRequest(JadwalKonsultasi context, String idPasien, String message) {
        throw new IllegalStateException("Permintaan sudah disetujui.");
    }

    @Override
    public void handleApprove(JadwalKonsultasi context) {
        throw new IllegalStateException("Permintaan sudah disetujui.");
    }

    @Override
    public void handleReject(JadwalKonsultasi context, String reason) {
        throw new IllegalStateException("Permintaan sudah disetujui.");
    }

    @Override
    public void handleChangeSchedule(JadwalKonsultasi context, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, String reason) {
        throw new IllegalStateException("Permintaan sudah disetujui.");
    }
}