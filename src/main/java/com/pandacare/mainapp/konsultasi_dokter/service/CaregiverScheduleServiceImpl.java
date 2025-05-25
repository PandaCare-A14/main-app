package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.model.strategy.CreateIntervalStrategy;
import com.pandacare.mainapp.konsultasi_dokter.model.strategy.CreateManualStrategy;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CaregiverScheduleServiceImpl implements CaregiverScheduleService {
    private final CaregiverScheduleRepository repository;
    private final CreateManualStrategy manualStrategy;
    private final CreateIntervalStrategy intervalStrategy;
    private final SlotValidatorService slotValidator;

    @Autowired
    public CaregiverScheduleServiceImpl(CaregiverScheduleRepository repository, SlotValidatorService slotValidator) {
        this.repository = repository;
        this.slotValidator = slotValidator;
        this.manualStrategy = new CreateManualStrategy();
        this.intervalStrategy = new CreateIntervalStrategy();
    }

    @Override
    @Transactional
    public CaregiverSchedule createSchedule(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        if (repository.existsOverlappingSchedule(idCaregiver, day, startTime, endTime)) {
            throw new RuntimeException("Schedule already exists.");
        }
        CaregiverSchedule schedule = manualStrategy.create(idCaregiver, day, startTime, endTime);
        return repository.save(schedule);
    }

    @Override
    @Transactional
    public List<CaregiverSchedule> createMultipleSchedules(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        List<CaregiverSchedule> allSlots = intervalStrategy.createMultipleSlots(idCaregiver, day, startTime, endTime);
        List<CaregiverSchedule> availableSlots = filterAvailableSlots(allSlots);

        if (availableSlots.isEmpty()) {
            throw new RuntimeException("All requested slots overlap with existing schedules.");
        }

        return repository.saveAll(availableSlots);
    }

    @Override
    @Transactional
    public List<CaregiverSchedule> createRepeatedSchedules(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime, int weeks) {
        List<CaregiverSchedule> allSchedules = manualStrategy.createRepeated(idCaregiver, day, startTime, endTime, weeks);
        List<CaregiverSchedule> availableSchedules = filterAvailableSlotsWithDate(allSchedules);

        if (availableSchedules.isEmpty()) {
            throw new RuntimeException("All requested schedules overlap with existing schedules.");
        }

        return repository.saveAll(availableSchedules);
    }

    @Override
    @Transactional
    public List<CaregiverSchedule> createRepeatedMultipleSchedules(UUID idCaregiver, DayOfWeek day,
                                                                   LocalTime startTime, LocalTime endTime, int weeks) {
        List<CaregiverSchedule> allSlots = intervalStrategy.createRepeated(idCaregiver, day, startTime, endTime, weeks);

        List<CaregiverSchedule> availableSlots = filterAvailableSlotsAsync(allSlots);

        if (availableSlots.isEmpty()) {
            throw new RuntimeException("All requested slots overlap with existing schedules.");
        }

        return repository.saveAll(availableSlots);
    }

    @Override
    public List<CaregiverSchedule> getSchedulesByCaregiver(UUID idCaregiver) {
        return repository.findByIdCaregiver(idCaregiver);
    }

    @Override
    public List<CaregiverSchedule> getSchedulesByCaregiverAndDay(UUID idCaregiver, DayOfWeek day) {
        return repository.findByIdCaregiverAndDay(idCaregiver, day);
    }

    @Override
    public List<CaregiverSchedule> getSchedulesByCaregiverAndStatus(UUID idCaregiver, ScheduleStatus status) {
        return repository.findByIdCaregiverAndStatus(idCaregiver, status);
    }

    @Override
    public CaregiverSchedule getSchedulesByCaregiverAndIdSchedule(UUID idCaregiver, UUID idSchedule) {
        return repository.findByIdCaregiverAndIdSchedule(idCaregiver, idSchedule)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + idSchedule + " and caregiver: " + idCaregiver));
    }

    @Override
    @Transactional
    public CaregiverSchedule deleteSchedule(UUID idSchedule) {
        CaregiverSchedule schedule = repository.findById(idSchedule)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + idSchedule));

        if (schedule.getStatus() == ScheduleStatus.UNAVAILABLE) {
            throw new IllegalStateException("Cannot deactivate schedule that is UNAVAILABLE.");
        }

        schedule.setStatus(ScheduleStatus.INACTIVE);
        return repository.save(schedule);
    }

    private List<CaregiverSchedule> filterAvailableSlots(List<CaregiverSchedule> slots) {
        return slots.stream()
                .filter(slot -> !repository.existsOverlappingSchedule(
                        slot.getIdCaregiver(), slot.getDay(), slot.getStartTime(), slot.getEndTime()))
                .collect(Collectors.toList());
    }

    private List<CaregiverSchedule> filterAvailableSlotsWithDate(List<CaregiverSchedule> slots) {
        return slots.stream()
                .filter(slot -> !repository.existsOverlappingScheduleWithDate(
                        slot.getIdCaregiver(), slot.getDay(), slot.getDate(),
                        slot.getStartTime(), slot.getEndTime()))
                .collect(Collectors.toList());
    }

    private List<CaregiverSchedule> filterAvailableSlotsAsync(List<CaregiverSchedule> slots) {
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (CaregiverSchedule slot : slots) {
            futures.add(slotValidator.isSlotValid(slot));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<CaregiverSchedule> validSlots = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            if (futures.get(i).join()) {
                validSlots.add(slots.get(i));
            }
        }

        return validSlots;
    }
}