package com.pandacare.mainapp.konsultasi_dokter.model;

public interface JadwalSubject {
    void addObserver(JadwalObserver observer);

    void removeObserver(JadwalObserver observer);

    void notifyObservers();
}