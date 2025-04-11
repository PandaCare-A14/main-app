package com.pandacare.mainapp.konsultasi_dokter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JadwalKonsultasi implements JadwalSubject {
    private String id;
    private StatusJadwalDokter statusDokter = StatusJadwalDokter.AVAILABLE;
    private Long idDokter;
    private Long idPasien;
    private String day;
    private String startTime;
    private String endTime;
    private String note;
    private String message;
    private boolean changeSchedule = false;

    private List<JadwalObserver> observers = new ArrayList<>();

    public void addObserver(JadwalObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(JadwalObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (JadwalObserver observer : observers) {
            observer.update(this);
        }
    }

    public void requestByPatient(Long idPasien, String message) {
        if (!statusDokter.isAvailable()) {
            throw new IllegalStateException("Jadwal tidak tersedia untuk permintaan baru");
        }

        this.idPasien = idPasien;
        this.message = message;
        setStatusDokter(StatusJadwalDokter.REQUESTED);
    }

    public void approveConsultation() {
        if (statusDokter != StatusJadwalDokter.REQUESTED) {
            throw new IllegalStateException("Tidak dapat menyetujui, belum ada permintaan konsultasi.");
        }

        setStatusDokter(StatusJadwalDokter.APPROVED);
    }

    public void rejectConsultation(String reason) {
        if (statusDokter != StatusJadwalDokter.REQUESTED) {
            throw new IllegalStateException("Tidak dapat menolak, belum ada permintaan konsultasi.");
        }

        this.message = reason;
        setStatusDokter(StatusJadwalDokter.REJECTED);
    }

    public void proposeScheduleChange(String newDay, String newStartTime, String newEndTime, String reason) {
        if (statusDokter != StatusJadwalDokter.REQUESTED) {
            throw new IllegalStateException("Tidak dapat mengubah jadwal, belum ada permintaan konsultasi.");
        }

        this.day = newDay;
        this.startTime = newStartTime;
        this.endTime = newEndTime;
        this.message = reason;
        this.changeSchedule = true;

        setStatusDokter(StatusJadwalDokter.CHANGE_SCHEDULE);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusJadwalDokter getStatusDokter() {
        return statusDokter;
    }

    public void setStatusDokter(StatusJadwalDokter statusDokter) {
        this.statusDokter = statusDokter;
        notifyObservers(); // Notify pasien ketika dokter melakukan perubahan status konsultasi
    }

    public Long getIdDokter() {
        return idDokter;
    }

    public void setIdDokter(Long idDokter) {
        this.idDokter = idDokter;
    }

    public Long getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(Long idPasien) {
        this.idPasien = idPasien;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isChangeSchedule() {
        return changeSchedule;
    }

    public void setChangeSchedule(boolean changeSchedule) {
        this.changeSchedule = changeSchedule;
    }
}