package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class ChangeScheduleState implements StatusCaregiver {

    @Override
    public String getStatusName() {
        return "CHANGE_SCHEDULE";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void handleRequest(CaregiverSchedule context, String idPasien, String message) {
        throw new IllegalStateException("Request on changing process.");
    }

    @Override
    public void handleApprove(CaregiverSchedule context) {
        context.setState(new ApprovedState());
        context.setChangeSchedule(false);
    }

    @Override
    public void handleReject(CaregiverSchedule jadwal, String reason) {
        jadwal.setState(new RejectedState());
        jadwal.setMessage(reason);
        jadwal.setChangeSchedule(false);
    }

    @Override
    public void handleChangeSchedule(CaregiverSchedule context, DayOfWeek newDay, LocalTime newStartTime, LocalTime newEndTime, String reason) {
        throw new IllegalStateException("Request on changing process.");
    }
}