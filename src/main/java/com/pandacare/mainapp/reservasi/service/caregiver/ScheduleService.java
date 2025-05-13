package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;

public interface ScheduleService {
    CaregiverSchedule getById(String id);
    boolean isScheduleAvailable(String scheduleId);
    void updateScheduleStatus(CaregiverSchedule schedule, ScheduleStatus status);
    void updateScheduleStatus(String scheduleId, ScheduleStatus status);
}