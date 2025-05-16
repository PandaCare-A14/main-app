package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;

import java.util.UUID;

public interface ScheduleService {
    CaregiverSchedule getById(UUID id);
    boolean isScheduleAvailable(UUID scheduleId);
    void updateScheduleStatus(CaregiverSchedule schedule, ScheduleStatus status);
    void updateScheduleStatus(UUID scheduleId, ScheduleStatus status);
}