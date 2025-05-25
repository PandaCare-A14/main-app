package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CaregiverScheduleFacade {
    private final CaregiverScheduleRepository repository;

    public CaregiverScheduleFacade(CaregiverScheduleRepository repository) {
        this.repository = repository;
    }

    public CaregiverSchedule findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + id));
    }

    public void updateStatus(UUID id, ScheduleStatus status) {
        CaregiverSchedule schedule = findById(id);
        schedule.setStatus(status);
        repository.save(schedule);
    }

    public boolean isAvailable(UUID id) {
        CaregiverSchedule schedule = findById(id);
        return schedule.getStatus() == ScheduleStatus.AVAILABLE;
    }
}