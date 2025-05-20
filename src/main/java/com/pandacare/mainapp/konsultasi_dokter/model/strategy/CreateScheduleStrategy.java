package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface CreateScheduleStrategy {
    CaregiverSchedule create(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime);
    CaregiverSchedule createWithDate(UUID idCaregiver, DayOfWeek day, LocalDate date, LocalTime startTime, LocalTime endTime);
    List<CaregiverSchedule> createRepeated(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime, int recurrenceCount);
}