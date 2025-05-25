package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.service.CaregiverScheduleFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final CaregiverScheduleFacade caregiverFacade;

    public ScheduleServiceImpl(CaregiverScheduleFacade caregiverFacade) {
        this.caregiverFacade = caregiverFacade;
    }

    @Override
    public CaregiverSchedule getById(UUID id) {
        return caregiverFacade.findById(id);
    }

    @Override
    public boolean isScheduleAvailable(UUID scheduleId) {
        return caregiverFacade.isAvailable(scheduleId);
    }

    @Override
    @Transactional
    public void updateScheduleStatus(CaregiverSchedule schedule, ScheduleStatus status) {
        caregiverFacade.updateStatus(schedule.getId(), status);
    }

    @Override
    @Transactional
    public void updateScheduleStatus(UUID scheduleId, ScheduleStatus status) {
        caregiverFacade.updateStatus(scheduleId, status);
    }
}