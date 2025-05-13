package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;

import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalTime;

public interface CaregiverScheduleService {
    CaregiverSchedule createSchedule(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime);
    List<CaregiverSchedule> createMultipleSchedules(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime);
    List<CaregiverSchedule> createRepeatedSchedules(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime, int weeks);
    List<CaregiverSchedule> createRepeatedMultipleSchedules(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime, int weeks);
    List<CaregiverSchedule> getSchedulesByCaregiver(String idCaregiver);
    List<CaregiverSchedule> getSchedulesByCaregiverAndDay(String idCaregiver, DayOfWeek day);
    List<CaregiverSchedule> getSchedulesByCaregiverAndStatus(String idCaregiver, ScheduleStatus status);
    CaregiverSchedule getSchedulesByCaregiverAndIdSchedule(String idCaregiver, String idSchedule);
    CaregiverSchedule deleteSchedule(String idSchedule);
}