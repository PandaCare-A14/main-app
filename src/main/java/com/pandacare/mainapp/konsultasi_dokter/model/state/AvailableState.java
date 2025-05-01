package com.pandacare.mainapp.konsultasi_dokter.model.state;

public class AvailableState implements StatusJadwalDokter {
    @Override
    public String getStatusName() {
        return "AVAILABLE";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void handleRequest(JadwalStateContext ctx, String idPasien, String message) {
        if (idPasien == null) {
            throw new IllegalArgumentException("ID pasien tidak boleh null.");
        }

        ctx.setStatus("REQUESTED");
        ctx.setPasien(idPasien);
        ctx.setMessage(message);
    }

    @Override
    public void handleApprove(JadwalStateContext ctx) {
        throw new IllegalStateException("Belum ada permintaan.");
    }

    @Override
    public void handleReject(JadwalStateContext ctx, String reason) {
        throw new IllegalStateException("Belum ada permintaan.");
    }

    @Override
    public void handleChangeSchedule(JadwalStateContext ctx, String day, String startTime, String endTime,
            String reason) {
        throw new IllegalStateException("Belum ada permintaan.");
    }
}