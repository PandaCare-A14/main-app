package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import java.time.LocalDate;
import java.time.LocalTime;

public interface CreateScheduleStrategy {
    CaregiverSchedule create(String idCaregiver, LocalDate date, LocalTime startTime, LocalTime endTime);
}