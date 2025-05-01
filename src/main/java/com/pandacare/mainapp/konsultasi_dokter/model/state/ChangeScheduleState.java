package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;

public class ChangeScheduleState implements StatusJadwalDokter {

    @Override
    public String getStatusName() {
        return "CHANGE_SCHEDULE";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void handleRequest(JadwalStateContext context, String idPasien, String message) {
        throw new IllegalStateException("Permintaan sedang dalam proses perubahan jadwal.");
    }

    @Override
    public void handleApprove(JadwalStateContext context) {
        JadwalKonsultasi jadwal = context.getJadwal();
        jadwal.setStatusDokter("APPROVED");
        jadwal.setChangeSchedule(false);
    }

    @Override
    public void handleReject(JadwalStateContext context, String reason) {
        JadwalKonsultasi jadwal = context.getJadwal();
        jadwal.setStatusDokter("REJECTED");
        jadwal.setMessage(reason);
        jadwal.setChangeSchedule(false);
    }

    @Override
    public void handleChangeSchedule(JadwalStateContext context, String newDay, String newStartTime, String newEndTime, String reason) {
        throw new IllegalStateException("Permintaan sedang dalam proses perubahan jadwal.");
    }
}