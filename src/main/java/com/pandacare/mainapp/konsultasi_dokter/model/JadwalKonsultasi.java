package com.pandacare.mainapp.konsultasi_dokter.model;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import com.pandacare.mainapp.konsultasi_dokter.model.state.StatusJadwalDokter;
import com.pandacare.mainapp.konsultasi_dokter.model.state.AvailableState;

@Setter
@Getter
public class JadwalKonsultasi {
    private String id;
    private String statusPacilian;
    private String idDokter;
    private String idPasien;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;
    private String message;
    private boolean changeSchedule = false;
    private StatusJadwalDokter currentState;

    public JadwalKonsultasi() {
        this.currentState = new AvailableState();
    }

    public void request(String idPasien, String message) {
        currentState.handleRequest(this, idPasien, message);
    }

    public void approve() {
        currentState.handleApprove(this);
    }

    public void reject(String reason) {
        currentState.handleReject(this, reason);
    }

    public void changeSchedule(LocalDate day, LocalTime start, LocalTime end, String reason) {
        currentState.handleChangeSchedule(this, day, start, end, reason);
    }

    public void setState(StatusJadwalDokter state) {
        this.currentState = state;
    }

    public String getStatusDokter() {
        return currentState.getStatusName();
    }

    public boolean isAvailable() {
        return currentState.isAvailable();
    }
}