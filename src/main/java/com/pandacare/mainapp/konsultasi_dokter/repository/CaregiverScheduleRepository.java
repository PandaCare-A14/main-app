package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
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

    public List<CaregiverSchedule> findByIdCaregiverAndDate(String idCaregiver, LocalDate date) {
        return data.values().stream()
                .filter(j -> idCaregiver.equals(j.getIdCaregiver()) && date.equals(j.getDate()))
                .collect(Collectors.toList());
    }

    public List<CaregiverSchedule> findOverlappingSchedule(String idCaregiver, LocalDate date,
                                                           LocalTime startTime, LocalTime endTime) {
        return data.values().stream()
                .filter(j -> idCaregiver.equals(j.getIdCaregiver())
                        && date.equals(j.getDate())
                        && j.getStartTime().isBefore(endTime)
                        && j.getEndTime().isAfter(startTime))
                .collect(Collectors.toList());
    }

    public List<CaregiverSchedule> findByStatus(String statusCaregiver) {
        return data.values().stream()
                .filter(j -> statusCaregiver.equals(j.getStatusCaregiver()))
                .collect(Collectors.toList());
    }
}