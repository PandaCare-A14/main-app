package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final CaregiverScheduleRepository repository;

    public ScheduleServiceImpl(CaregiverScheduleRepository repository) {
        this.repository = repository;
    }

    @Override
    public CaregiverSchedule getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + id));
    }

    @Override
    public boolean isScheduleAvailable(UUID scheduleId) {
        CaregiverSchedule schedule = getById(scheduleId);
        return schedule.getStatus() == ScheduleStatus.AVAILABLE;
    }

    @Override
    @Transactional
    public void updateScheduleStatus(CaregiverSchedule schedule, ScheduleStatus status) {
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule cannot be null");
        }

        schedule.setStatus(status);
        repository.save(schedule);
    }

    @Override
    @Transactional
    public void updateScheduleStatus(UUID scheduleId, ScheduleStatus status) {
        CaregiverSchedule schedule = getById(scheduleId);
        updateScheduleStatus(schedule, status);
    }
}