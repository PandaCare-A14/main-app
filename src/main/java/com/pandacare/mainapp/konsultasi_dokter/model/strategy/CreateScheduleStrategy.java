package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CreateScheduleStrategy {
    CaregiverSchedule create(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime);
    CaregiverSchedule createWithDate(String idCaregiver, DayOfWeek day, LocalDate date, LocalTime startTime, LocalTime endTime);
    List<CaregiverSchedule> createRepeated(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime, int recurrenceCount);
}