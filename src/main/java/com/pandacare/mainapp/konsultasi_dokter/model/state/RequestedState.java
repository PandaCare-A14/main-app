package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class RequestedState implements StatusCaregiver {
    @Override
    public String getStatusName() {
        return "REQUESTED";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void handleRequest(CaregiverSchedule context, String idPasien, String message) {
        throw new IllegalStateException("Schedule is being requested.");
    }

    @Override
    public void handleApprove(CaregiverSchedule jadwal) {
        jadwal.setState(new ApprovedState());
    }

    @Override
    public void handleReject(CaregiverSchedule jadwal, String reason) {
        jadwal.setState(new RejectedState());
        jadwal.setMessage(reason);
    }

    @Override
    public void handleChangeSchedule(CaregiverSchedule jadwal, DayOfWeek newDay, LocalTime newStartTime, LocalTime newEndTime, String reason) {
        jadwal.setDay(newDay);
        jadwal.setStartTime(newStartTime);
        jadwal.setEndTime(newEndTime);
        jadwal.setMessage(reason);
        jadwal.setState(new ChangeScheduleState());
        jadwal.setChangeSchedule(true);
    }
}