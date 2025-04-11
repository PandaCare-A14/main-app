package com.pandacare.mainapp.konsultasi_dokter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PatientObserverTest {

    private PatientObserver patientObserver;
    private JadwalKonsultasi jadwal;

    @BeforeEach
    void setUp() {
        patientObserver = new PatientObserver(2L);

        jadwal = new JadwalKonsultasi();
        jadwal.setId("JADWAL001");
        jadwal.setIdDokter(1L);
        jadwal.setDay("Senin");
        jadwal.setStartTime("10:00");
        jadwal.setEndTime("11:00");
        jadwal.setIdPasien(2L);
    }

    @Test
    void testUpdateStatus() {
        jadwal.setStatusDokter(StatusJadwalDokter.APPROVED);
        patientObserver.update(jadwal);

        jadwal.setStatusDokter(StatusJadwalDokter.REJECTED);
        patientObserver.update(jadwal);

        jadwal.setStatusDokter(StatusJadwalDokter.CHANGE_SCHEDULE);
        patientObserver.update(jadwal);

        jadwal.setStatusDokter(StatusJadwalDokter.AVAILABLE);
        patientObserver.update(jadwal);
    }
}