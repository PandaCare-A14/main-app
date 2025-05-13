package com.pandacare.mainapp.konsultasi_dokter.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "caregiver_schedules")
@Setter
@Getter
public class CaregiverSchedule {
    @Id
    private String id;
    @Column(name = "caregiver_id")
    private String idCaregiver;
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;
    @Column
    private LocalDate date;
    @Column
    private LocalTime startTime;
    @Column
    private LocalTime endTime;
    @Enumerated(EnumType.STRING)
    @Column
    private ScheduleStatus status;

    public CaregiverSchedule() {
        this.id = java.util.UUID.randomUUID().toString();
        this.status = ScheduleStatus.AVAILABLE;
    }
}