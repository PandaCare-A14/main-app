package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.model.strategy.CreateScheduleStrategy;
import com.pandacare.mainapp.konsultasi_dokter.model.strategy.CreateManualStrategy;
import com.pandacare.mainapp.konsultasi_dokter.model.strategy.CreateIntervalStrategy;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CaregiverScheduleServiceImpl implements CaregiverScheduleService {
    private final CaregiverScheduleRepository repository;

    public CaregiverScheduleServiceImpl(CaregiverScheduleRepository repository) {
        this.repository = repository;
    }

    @Override
    public CaregiverSchedule createSchedule(String idCaregiver, LocalDate date, LocalTime startTime, LocalTime endTime) {
        validateScheduleTime(startTime, endTime);
        validateNoOverlap(idCaregiver, date, startTime, endTime);

        CreateScheduleStrategy strategy = new CreateManualStrategy();
        CaregiverSchedule schedule = strategy.create(idCaregiver, date, startTime, endTime);
        schedule.setId(UUID.randomUUID().toString());

        return repository.save(schedule);
    }

    @Override
    public List<CaregiverSchedule> createScheduleInterval(String idCaregiver, LocalDate date,
                                                        LocalTime startTime, LocalTime endTime) {
        validateScheduleTime(startTime, endTime);
        validateNoOverlap(idCaregiver, date, startTime, endTime);

        CreateIntervalStrategy strategy = new CreateIntervalStrategy();
        List<CaregiverSchedule> scheduleList = strategy.createMultipleSlots(
                idCaregiver, date, startTime, endTime);

        return scheduleList.stream()
                .map(repository::save)
                .toList();
    }

    private void validateScheduleTime(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time can't be set after end time.");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time can't be set after start time.");
        }
    }

    private void validateNoOverlap(String idCaregiver, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<CaregiverSchedule> overlappingSchedule = repository.findOverlappingSchedule(idCaregiver, date, startTime, endTime);

        if (!overlappingSchedule.isEmpty()) {
            throw new IllegalArgumentException("A schedule at the same time already exists.");
        }
    }

    @Override
    public boolean changeSchedule(String idSchedule, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, String message) {
        validateScheduleTime(newStartTime, newEndTime);

        CaregiverSchedule schedule = repository.findById(idSchedule);
        if (schedule == null) return false;

        schedule.changeSchedule(newDate, newStartTime, newEndTime, message);

        repository.save(schedule);
        return true;
    }

    @Override
    public boolean approveSchedule(String idSchedule) {
        CaregiverSchedule schedule = repository.findById(idSchedule);
        if (schedule == null) return false;
        schedule.approve();
        repository.save(schedule);
        return true;
    }

    @Override
    public boolean rejectSchedule(String idSchedule) {
        CaregiverSchedule schedule = repository.findById(idSchedule);
        if (schedule == null) return false;
        schedule.reject("Jadwal tidak sesuai");
        repository.save(schedule);
        return true;
    }

    @Override
    public List<CaregiverSchedule> findByIdCaregiver(String idCaregiver) {
        return repository.findByIdCaregiver(idCaregiver);
    }

    @Override
    public List<CaregiverSchedule> findByIdCaregiverAndStatus(String idCaregiver, String status) {
        return repository.findByIdCaregiver(idCaregiver).stream()
                .filter(j -> status.equals(j.getStatusCaregiver()))
                .collect(Collectors.toList());
    }

    @Override
    public CaregiverSchedule findById(String id) {
        return repository.findById(id);
    }
}