package com.pandacare.mainapp.reservasi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.state.*;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.UUID;

@Entity
@Table(name = "reservasi_konsultasi")
@Data
public class ReservasiKonsultasi {
    @Id
    private String idReservasi;
    @OneToOne
    @JoinColumn(name = "id_schedule")
    private CaregiverSchedule idSchedule;
    @Column(name = "pacilian_id", nullable = false)
    private String idPacilian;
    @Enumerated(EnumType.STRING)
    @Column(name = "status_reservasi", nullable = false)
    private StatusReservasiKonsultasi statusReservasi;
    @Column
    private String pacilianNote;
    @Transient
    @JsonIgnore
    private ReservasiState currentState;
    @Transient
    @JsonIgnore
    @Autowired
    @Lazy
    private ScheduleService scheduleService;

    public ReservasiKonsultasi() {
        this.idReservasi = UUID.randomUUID().toString();
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

    public void handleChangeSchedule(String newScheduleId) {
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