package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public interface CaregiverScheduleService {
    CaregiverSchedule createSchedule(String idCaregiver, LocalDate date, LocalTime startTime, LocalTime endTime);
    List<CaregiverSchedule> createScheduleInterval(String idCaregiver, LocalDate date,
                                                 LocalTime startTime, LocalTime endTime);

    boolean changeSchedule(String idSchedule, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, String message);

    boolean approveSchedule(String idSchedule);

    boolean rejectSchedule(String idSchedule);

    List<CaregiverSchedule> findByIdCaregiver(String idCaregiver);

    List<CaregiverSchedule> findByIdCaregiverAndStatus(String idCaregiver, String status);

    CaregiverSchedule findById(String id);
}