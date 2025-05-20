package com.pandacare.mainapp.reservasi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import jakarta.persistence.*;
import com.pandacare.mainapp.reservasi.model.state.*;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
import jakarta.annotation.PostConstruct;

import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;


@Entity
@Table(name = "reservasi_konsultasi")
@Data
public class ReservasiKonsultasi {
    @Id
    @Column(name = "id")
    private String id;
    private String idDokter;
    private String idPasien;

    @Column(name = "appointment_day")
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private StatusReservasiKonsultasi statusReservasi;

    private boolean changeReservasi;

    @Column(name = "new_appointment_day")
    private String newDay;
    private LocalTime newStartTime;
    private LocalTime newEndTime;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_schedule")
    private CaregiverSchedule idSchedule;

    @Column
    private String idPacilian;

    @Column
    private String pacilianNote;
    @Transient
    @JsonIgnore
    private ReservasiState currentState;
    @Transient
    @JsonIgnore
    @Lazy
    private ScheduleService scheduleService;

    public ReservasiKonsultasi() {
        this.id = UUID.randomUUID().toString();
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
}