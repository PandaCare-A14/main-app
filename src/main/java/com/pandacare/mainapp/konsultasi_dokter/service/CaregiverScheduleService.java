package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;

import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public interface CaregiverScheduleService {
    CaregiverSchedule createSchedule(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime);
    List<CaregiverSchedule> createMultipleSchedules(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime);
    List<CaregiverSchedule> createRepeatedSchedules(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime, int weeks);
    List<CaregiverSchedule> createRepeatedMultipleSchedules(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime, int weeks);
    List<CaregiverSchedule> getSchedulesByCaregiver(UUID idCaregiver);
    List<CaregiverSchedule> getSchedulesByCaregiverAndDay(UUID idCaregiver, DayOfWeek day);
    List<CaregiverSchedule> getSchedulesByCaregiverAndStatus(UUID idCaregiver, ScheduleStatus status);
    CaregiverSchedule getSchedulesByCaregiverAndIdSchedule(UUID idCaregiver, UUID idSchedule);
    List<CaregiverSchedule> getSchedulesByCaregiverStatusAndDay(UUID idCaregiver, ScheduleStatus status, DayOfWeek day);
    CaregiverSchedule deleteSchedule(UUID idSchedule);
}