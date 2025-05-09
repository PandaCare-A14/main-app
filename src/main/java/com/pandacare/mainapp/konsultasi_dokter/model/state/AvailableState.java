package com.pandacare.mainapp.konsultasi_dokter.model.state;

import java.time.LocalDate;
import java.time.LocalTime;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

public class AvailableState implements StatusCaregiver {
    @Override
    public String getStatusName() {
        return "AVAILABLE";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void handleRequest(CaregiverSchedule jadwal, String idPacilian, String message) {
        if (idPacilian == null) {
            throw new IllegalArgumentException("Pacilian ID can't be null.");
        }

        jadwal.setState(new RequestedState());
        jadwal.setIdPacilian(idPacilian);
        jadwal.setMessage(message);
    }

    @Override
    public void handleApprove(CaregiverSchedule ctx) {
        throw new IllegalStateException("No request found.");
    }

    @Override
    public void handleReject(CaregiverSchedule ctx, String reason) {
        throw new IllegalStateException("No request found.");
    }

    @Override
    public void handleChangeSchedule(CaregiverSchedule context, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, String reason) {
        throw new IllegalStateException("No request found.");
    }
}