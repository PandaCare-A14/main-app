package com.pandacare.mainapp.konsultasi_dokter.model.state;

import java.time.LocalDate;
import java.time.LocalTime;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

public class ApprovedState implements StatusCaregiver {

    @Override
    public String getStatusName() {
        return "APPROVED";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void handleRequest(CaregiverSchedule context, String idPasien, String message) {
        throw new IllegalStateException("Request has been approved.");
    }

    @Override
    public void handleApprove(CaregiverSchedule context) {
        throw new IllegalStateException("Request has been approved.");
    }

    @Override
    public void handleReject(CaregiverSchedule context, String reason) {
        throw new IllegalStateException("Request has been approved.");
    }

    @Override
    public void handleChangeSchedule(CaregiverSchedule context, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, String reason) {
        throw new IllegalStateException("Request has been approved.");
    }
}