package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class CreateManualStrategyTest {
    private CreateJadwalStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new CreateManualStrategy();
    }

    @Test
    void testCreateJadwal() {
        JadwalKonsultasi jadwal = strategy.create("DOK-001", LocalDate.parse("2025-05-06"), LocalTime.parse("08:00"), LocalTime.parse("09:00"));

        assertEquals("DOK-001", jadwal.getIdDokter());
        assertEquals(LocalDate.parse("2025-05-06"), jadwal.getDate());
        assertEquals(LocalTime.parse("08:00"), jadwal.getStartTime());
        assertEquals(LocalTime.parse("09:00"), jadwal.getEndTime());
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
            strategy.create("DOK-001", LocalDate.parse("2025-05-06"), null, LocalTime.parse("09:00"));
        });
    }

    @Test
    void testCreateJadwalIfDayIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.create("DOK-001", null, LocalTime.parse("08:00"), LocalTime.parse("09:00"));
        });
    }

    @Test
    void testCreateJadwalIfEndTimeIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.create("DOK-001", LocalDate.parse("2025-05-06"), LocalTime.parse("08:00"), null);
        });
    }

    @Test
    void testCreateJadwalfIdDokterIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.create(null, LocalDate.parse("2025-05-06"), LocalTime.parse("08:00"), LocalTime.parse("09:00"));
        });
    }
}