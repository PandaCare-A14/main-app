package com.pandacare.mainapp.konsultasi_dokter.model;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JadwalKonsultasiTest {
    private JadwalKonsultasi jadwal;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setId("JADWAL001");
        jadwal.setIdDokter("DOC-12345");
        jadwal.setIdPasien("PAT-12345");
        jadwal.setDay("Monday");
        jadwal.setStartTime("10:00");
        jadwal.setEndTime("11:00");
        jadwal.setNote(null);
        jadwal.setMessage(null);
        jadwal.setChangeSchedule(true);
    }

    @Test
    void testDefaultStatusDokter() {
        JadwalKonsultasi baru = new JadwalKonsultasi();
        assertEquals("AVAILABLE", baru.getStatusDokter());
    }

    @Test
    void testGetterSetter() {
        assertEquals("JADWAL001", jadwal.getId());
        assertEquals("DOC-12345", jadwal.getIdDokter());
        assertEquals("PAT-12345", jadwal.getIdPasien());
        assertEquals("Monday", jadwal.getDay());
        assertEquals("10:00", jadwal.getStartTime());
        assertEquals("11:00", jadwal.getEndTime());
        assertNull(jadwal.getNote());
        assertNull(jadwal.getMessage());
        assertTrue(jadwal.isChangeSchedule());
    }


    @Test
    void testIsAvailableTrue() {
        jadwal.setStatusDokter("AVAILABLE");
        assertTrue(jadwal.isAvailable());
    }

    @Test
    void testIsAvailableFalse() {
        jadwal.setStatusDokter("REQUESTED");
        assertFalse(jadwal.isAvailable());
    }
}