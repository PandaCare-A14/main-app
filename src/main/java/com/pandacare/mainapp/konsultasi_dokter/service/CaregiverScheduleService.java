package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalTime;

public interface CaregiverScheduleService {
    CaregiverSchedule createSchedule(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime);
    List<CaregiverSchedule> createScheduleInterval(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime);
    boolean changeSchedule(String idSchedule, DayOfWeek newDay, LocalTime newStartTime, LocalTime newEndTime, String message);
    boolean approveSchedule(String idSchedule);
    boolean rejectSchedule(String idSchedule);
    List<CaregiverSchedule> findByIdCaregiver(String idCaregiver);
    List<CaregiverSchedule> findByIdCaregiverAndDay(String idCaregiver, DayOfWeek day);
    List<CaregiverSchedule> findOverlappingSchedule(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime);
    List<CaregiverSchedule> findByIdCaregiverAndStatus(String idCaregiver, String status);
    CaregiverSchedule findById(String id);
}