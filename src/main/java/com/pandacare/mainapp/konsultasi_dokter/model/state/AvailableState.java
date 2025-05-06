package com.pandacare.mainapp.konsultasi_dokter.model.state;

import java.time.LocalDate;
import java.time.LocalTime;
import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;

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
    public void handleRequest(JadwalKonsultasi jadwal, String idPasien, String message) {
        if (idPasien == null) {
            throw new IllegalArgumentException("ID pasien tidak boleh null.");
        }

        jadwal.setState(new RequestedState());
        jadwal.setIdPasien(idPasien);
        jadwal.setMessage(message);
    }

    @Override
    public void handleApprove(JadwalKonsultasi ctx) {
        throw new IllegalStateException("Belum ada permintaan.");
    }

    @Override
    public void handleReject(JadwalKonsultasi ctx, String reason) {
        throw new IllegalStateException("Belum ada permintaan.");
    }

    @Override
    public void handleChangeSchedule(JadwalKonsultasi context, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, String reason) {
        throw new IllegalStateException("Belum ada permintaan.");
    }
}