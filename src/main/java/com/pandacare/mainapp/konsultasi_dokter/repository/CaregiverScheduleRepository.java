package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Repository
public class CaregiverScheduleRepository {
    private final Map<String, CaregiverSchedule> data = new HashMap<>();

    public CaregiverSchedule save(CaregiverSchedule schedule) {
        if (schedule.getId() == null) {
            schedule.setId(UUID.randomUUID().toString());
        }
        data.put(schedule.getId(), schedule);
        return schedule;
    }

    public CaregiverSchedule findById(String id) {
        return data.get(id);
    }

    public List<CaregiverSchedule> findByIdCaregiver(String idCaregiver) {
        return data.values().stream()
                .filter(j -> idCaregiver.equals(j.getIdCaregiver()))
                .collect(Collectors.toList());
    }

    public List<CaregiverSchedule> findByIdCaregiverAndDay(String idCaregiver, DayOfWeek day) {
        return data.values().stream()
                .filter(schedule -> idCaregiver.equals(schedule.getIdCaregiver()) && day.equals(schedule.getDay()))
                .collect(Collectors.toList());
    }

    public List<CaregiverSchedule> findOverlappingSchedule(String idCaregiver, DayOfWeek day,
                                                           LocalTime startTime, LocalTime endTime) {
        return data.values().stream()
                .filter(schedule -> idCaregiver.equals(schedule.getIdCaregiver())
                        && day.equals(schedule.getDay())
                        && schedule.getStartTime().isBefore(endTime)
                        && schedule.getEndTime().isAfter(startTime))
                .collect(Collectors.toList());
    }

    public List<CaregiverSchedule> findByIdCaregiverAndStatus(String idCaregiver, String statusCaregiver) {
        return data.values().stream()
                .filter(schedule -> idCaregiver.equals(schedule.getIdCaregiver())
                        && statusCaregiver.equals(schedule.getStatusCaregiver()))
                .collect(Collectors.toList());
    }
}