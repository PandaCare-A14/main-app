package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;

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
    public void handleRequest(JadwalStateContext context, String idPasien, String message) {
        throw new IllegalStateException("Permintaan sudah disetujui.");
    }

    @Override
    public void handleApprove(JadwalStateContext context) {
        throw new IllegalStateException("Permintaan sudah disetujui.");
    }

    @Override
    public void handleReject(JadwalStateContext context, String reason) {
        throw new IllegalStateException("Permintaan sudah disetujui.");
    }

    @Override
    public void handleChangeSchedule(JadwalStateContext context, String newDay, String newStartTime, String newEndTime, String reason) {
        throw new IllegalStateException("Permintaan sudah disetujui.");
    }
}