package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import java.time.LocalDate;
import java.time.LocalTime;

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
    public void handleRequest(JadwalKonsultasi context, String idPasien, String message) {
        throw new IllegalStateException("Permintaan sedang dalam proses perubahan jadwal.");
    }

    @Override
    public void handleApprove(JadwalKonsultasi context) {
        context.setState(new ApprovedState());
        context.setChangeSchedule(false);
    }

    @Override
    public void handleReject(JadwalKonsultasi jadwal, String reason) {
        jadwal.setState(new RejectedState());
        jadwal.setMessage(reason);
        jadwal.setChangeSchedule(false);
    }

    @Override
    public void handleChangeSchedule(JadwalKonsultasi context, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, String reason) {
        throw new IllegalStateException("Permintaan sedang dalam proses perubahan jadwal.");
    }
}