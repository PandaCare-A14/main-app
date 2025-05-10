package com.pandacare.mainapp.konsultasi_dokter.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import com.pandacare.mainapp.konsultasi_dokter.model.state.StatusCaregiver;
import com.pandacare.mainapp.konsultasi_dokter.model.state.AvailableState;

@Setter
@Getter
public class CaregiverSchedule {
    private String id;
    private String statusPacilian;
    private String idCaregiver;
    private String idPacilian;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;
    private String message;
    private boolean changeSchedule = false;
    private StatusCaregiver currentState;

    public CaregiverSchedule() {
        this.id = UUID.randomUUID().toString();
        this.currentState = new AvailableState();
    }

    public void request(String idPacilian, String message) {
        currentState.handleRequest(this, idPacilian, message);
    }

    public void approve() {
        currentState.handleApprove(this);
    }

    public void reject(String reason) {
        currentState.handleReject(this, reason);
    }

    public void changeSchedule(DayOfWeek day, LocalTime start, LocalTime end, String reason) {
        currentState.handleChangeSchedule(this, day, start, end, reason);
    }

    public void setState(StatusCaregiver state) {
        this.currentState = state;
    }

    @JsonIgnore
    public String getStatusCaregiver() {
        return currentState.getStatusName();
    }

    @JsonIgnore
    public boolean isAvailable() {
        return currentState.isAvailable();
    }
}