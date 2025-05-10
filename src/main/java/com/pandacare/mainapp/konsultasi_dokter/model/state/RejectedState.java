package com.pandacare.mainapp.konsultasi_dokter.model.state;

import java.time.DayOfWeek;
import java.time.LocalTime;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

public class RejectedState implements StatusCaregiver {
    @Override
    public String getStatusName() {
        return "REJECTED";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void handleRequest(CaregiverSchedule jadwal, String idPasien, String message) {
        throw new IllegalStateException("Request has been rejected.");
    }

    @Override
    public void handleApprove(CaregiverSchedule jadwal) {
        throw new IllegalStateException("Request has been rejected.");
    }

    @Override
    public void handleReject(CaregiverSchedule jadwal, String reason) {
        throw new IllegalStateException("Request has been rejected.");
    }

    @Override
    public void handleChangeSchedule(CaregiverSchedule jadwal, DayOfWeek newDay, LocalTime newStartTime, LocalTime newEndTime, String reason) {
        throw new IllegalStateException("Request has been rejected.");
    }
}