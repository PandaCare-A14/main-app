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

    @Setter
    @ManyToOne
    @JoinColumn(name = "caregiver_id")
    private com.pandacare.mainapp.authentication.model.Caregiver idCaregiver;

    @Enumerated(EnumType.STRING)
    private DayOfWeek day;
    @Column
    private LocalDate date;
    @Column
    private LocalTime startTime;
    @Column
    private LocalTime endTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ScheduleStatus status;

    public CaregiverSchedule() {
        this.id = java.util.UUID.randomUUID();
        this.status = ScheduleStatus.AVAILABLE;
    }

    // Convenience methods for ID-based access
    public UUID getIdCaregiver() {
        return idCaregiver != null ? idCaregiver.getId() : null;
    }

    public void setIdCaregiver(UUID caregiverId) {
        // This method is kept for backward compatibility but should be avoided
        // The proper way is to set the caregiver entity directly
        if (caregiverId != null) {
            com.pandacare.mainapp.authentication.model.Caregiver caregiver = new com.pandacare.mainapp.authentication.model.Caregiver();
            caregiver.setId(caregiverId);
            this.idCaregiver = caregiver;
        } else {
            this.idCaregiver = null;
        }
    }

}