package com.pandacare.mainapp.reservasi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.statepacilian.ReservasiStatePacilian;
import jakarta.persistence.*;
import com.pandacare.mainapp.reservasi.model.stateCaregiver.*;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
import jakarta.annotation.PostConstruct;

import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;


@Entity
@Table(name = "reservasi_konsultasi")
@Data
public class ReservasiKonsultasi {
    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private StatusReservasiKonsultasi statusReservasi;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_schedule")
    private CaregiverSchedule idSchedule;

    @Column
    private UUID idPacilian;

    @Column
    private String pacilianNote;

    @Transient
    @JsonIgnore
    private ReservasiState currentState;

    @Transient
    @JsonIgnore
    @Lazy
    private ScheduleService scheduleService;

    @Transient
    @JsonIgnore
    private ReservasiStatePacilian statePacilian;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public ReservasiKonsultasi() {
        this.id = UUID.randomUUID();
        this.statusReservasi = StatusReservasiKonsultasi.WAITING;
    }

    @PostConstruct
    public void initializeState() {
        loadState();
    }

    public void approve() {
        if (currentState != null) {
            currentState.handleApprove(this);
        } else {
            setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        }
    }

    public void reject() {
        if (currentState != null) {
            currentState.handleReject(this);
        } else {
            setStatusReservasi(StatusReservasiKonsultasi.REJECTED);
        }
    }

    public void handleChangeSchedule(UUID newScheduleId) {
        if (currentState != null) {
            currentState.handleChangeSchedule(this, newScheduleId);
        } else {
            setStatusReservasi(StatusReservasiKonsultasi.ON_RESCHEDULE);
        }
    }

    public void setState(ReservasiState state) {
        this.currentState = state;
        if (state != null)
            this.statusReservasi = state.getStatus();
    }

    public void ensureStateInitialized(ScheduleService externalScheduleService) {
        if (this.scheduleService == null) {
            this.scheduleService = externalScheduleService;
        }

        if (currentState == null) {
            initState();
        }
    }

    @PostLoad
    private void loadState() {
        if (scheduleService != null) {
            initState();
        }
    }

    private void initState() {
        switch(statusReservasi) {
            case WAITING:
                this.currentState = new RequestedState(scheduleService);
                break;
            case APPROVED:
                this.currentState = new ApprovedState();
                break;
            case ON_RESCHEDULE:
                this.currentState = new RescheduleState();
                break;
            case REJECTED:
                this.currentState = new RejectedState();
                break;
            default:
                this.currentState = new RequestedState(scheduleService);
        }
    }

    // State methods for pacilian
    public void setStatePacilian(ReservasiStatePacilian status) {
        this.statePacilian = status;
    }

    public void editAsPacilian(String newDay, String newStartTime, String newEndTime) {
        if (statePacilian == null) {
            throw new IllegalStateException("State Pacilian belum diset.");
        }
        statePacilian.edit(this, newDay, newStartTime, newEndTime);
    }

    public void acceptChangeAsPacilian() {
        if (statePacilian == null) {
            throw new IllegalStateException("State Pacilian belum diset.");
        }
        statePacilian.acceptChange(this);
    }

    public void rejectChangeAsPacilian() {
        if (statePacilian == null) {
            throw new IllegalStateException("State Pacilian belum diset.");
        }
        statePacilian.rejectChange(this);
    }

    // Access methods for schedule properties
    public String getDay() {
        return idSchedule != null ? idSchedule.getDay().toString() : null;
    }

    public LocalTime getStartTime() {
        return idSchedule != null ? idSchedule.getStartTime() : null;
    }

    public LocalTime getEndTime() {
        return idSchedule != null ? idSchedule.getEndTime() : null;
    }

    public UUID getIdCaregiver() {
        return idSchedule != null ? idSchedule.getIdCaregiver() : null;
    }

    public UUID getIdPasien() {
        return idPacilian;
    }

    public void setIdCaregiver(UUID validDoctorId) {
        if (idSchedule != null) {
            idSchedule.setIdCaregiver(validDoctorId);
        }
    }

    public void setIdPasien(UUID validPatientId) {
        this.idPacilian = validPatientId;
    }
}