package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;

public interface CreateScheduleStrategy {
    CaregiverSchedule create(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime);
}