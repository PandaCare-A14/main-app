package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import java.time.LocalDate;
import java.time.LocalTime;

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
    public void handleRequest(JadwalKonsultasi context, String idPasien, String message) {
        throw new IllegalStateException("Sudah ada permintaan.");
    }

    @Override
    public void handleApprove(JadwalKonsultasi jadwal) {
        jadwal.setState(new ApprovedState());
    }

    @Override
    public void handleReject(JadwalKonsultasi jadwal, String reason) {
        jadwal.setState(new RejectedState());
        jadwal.setMessage(reason);
    }

    @Override
    public void handleChangeSchedule(JadwalKonsultasi jadwal, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, String reason) {
        jadwal.setDate(newDate);
        jadwal.setStartTime(newStartTime);
        jadwal.setEndTime(newEndTime);
        jadwal.setMessage(reason);
        jadwal.setState(new ChangeScheduleState());
        jadwal.setChangeSchedule(true);
    }
}