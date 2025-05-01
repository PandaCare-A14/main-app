package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;

public class RequestedState implements StatusJadwalDokter {

    @Override
    public String getStatusName() {
        return "REQUESTED";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void handleRequest(JadwalStateContext context, String idPasien, String message) {
        throw new IllegalStateException("Sudah ada permintaan.");
    }

    @Override
    public void handleApprove(JadwalStateContext context) {
        JadwalKonsultasi jadwal = context.getJadwal();
        jadwal.setStatusDokter("APPROVED");
    }

    @Override
    public void handleReject(JadwalStateContext context, String reason) {
        JadwalKonsultasi jadwal = context.getJadwal();
        jadwal.setStatusDokter("REJECTED");
        jadwal.setMessage(reason);
    }

    @Override
    public void handleChangeSchedule(JadwalStateContext context, String newDay, String newStartTime, String newEndTime, String reason) {
        JadwalKonsultasi jadwal = context.getJadwal();
        jadwal.setDay(newDay);
        jadwal.setStartTime(newStartTime);
        jadwal.setEndTime(newEndTime);
        jadwal.setMessage(reason);
        jadwal.setStatusDokter("CHANGE_SCHEDULE");
        jadwal.setChangeSchedule(true);
    }
}