package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateManualStrategyTest {
    private CreateJadwalStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new CreateManualStrategy();
    }

    @Test
    void testCreateJadwal() {
        JadwalKonsultasi jadwal = strategy.create("DOK-001", "Senin", "08:00", "09:00");

        assertEquals("DOK-001", jadwal.getIdDokter());
        assertEquals("Senin", jadwal.getDay());
        assertEquals("08:00", jadwal.getStartTime());
        assertEquals("09:00", jadwal.getEndTime());
        assertEquals("AVAILABLE", jadwal.getStatusDokter());

        assertNull(jadwal.getNote());
        assertNull(jadwal.getIdPasien());
        assertNull(jadwal.getMessage());
        assertNull(jadwal.getStatusPacilian());
        assertFalse(jadwal.isChangeSchedule());
    }

    @Test
    void testCreateJadwalIfStartTimeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.create("DOK-001", "Senin", null, "09:00");
        });
    }

    @Test
    void testCreateJadwalIfDayIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.create("DOK-001", null, "08:00", "09:00");
        });
    }

    @Test
    void testCreateJadwalIfEndTimeIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.create("DOK-001", "Senin", "08:00", "");
        });
    }

    @Test
    void testCreateJadwalfIdDokterIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.create(null, "Senin", "08:00", "09:00");
        });
    }
}