package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SlotValidatorService {

    private final CaregiverScheduleRepository repository;

    public SlotValidatorService(CaregiverScheduleRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<Boolean> isSlotValid(CaregiverSchedule schedule) {
        boolean overlap = repository.existsOverlappingScheduleWithDate(
                schedule.getIdCaregiver(),
                schedule.getDay(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime()
        );
        return CompletableFuture.completedFuture(!overlap);
    }
}