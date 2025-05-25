package com.pandacare.mainapp.konsultasi_dokter.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "caregiver_schedules")
public class CaregiverSchedule {
    @Id
    private UUID id;
    @Column(name = "caregiver_id")
    private UUID idCaregiver;
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
        this.id = java.util.UUID.randomUUID();
        this.status = ScheduleStatus.AVAILABLE;
    }
}