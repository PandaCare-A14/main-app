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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private com.pandacare.mainapp.authentication.model.Caregiver idCaregiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "day")  // Tambahkan column name mapping
    private DayOfWeek day;

    @Column(name = "date")  // Tambahkan column name mapping
    private LocalDate date;

    @Column(name = "start_time")  // Penting! Map ke snake_case
    private LocalTime startTime;

    @Column(name = "end_time")    // Penting! Map ke snake_case
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "schedule_status")  // Ini yang paling penting!
    private ScheduleStatus status;

    public CaregiverSchedule() {
        this.id = java.util.UUID.randomUUID();
        this.status = ScheduleStatus.AVAILABLE;
    }

    public UUID getIdCaregiver() {
        return idCaregiver != null ? idCaregiver.getId() : null;
    }

    public void setIdCaregiver(UUID caregiverId) {
        if (caregiverId != null) {
            com.pandacare.mainapp.authentication.model.Caregiver caregiver =
                    new com.pandacare.mainapp.authentication.model.Caregiver();
            caregiver.setId(caregiverId);
            this.idCaregiver = caregiver;
        } else {
            this.idCaregiver = null;
        }
    }
}