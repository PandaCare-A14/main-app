package com.pandacare.mainapp.konsultasi_dokter.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusJadwalDokterTest {

    @Test
    void testEnumValues() {
        assertEquals(5, StatusJadwalDokter.values().length);
        assertEquals("AVAILABLE", StatusJadwalDokter.AVAILABLE.name());
        assertEquals("REQUESTED", StatusJadwalDokter.REQUESTED.name());
        assertEquals("APPROVED", StatusJadwalDokter.APPROVED.name());
        assertEquals("REJECTED", StatusJadwalDokter.REJECTED.name());
        assertEquals("CHANGE_SCHEDULE", StatusJadwalDokter.CHANGE_SCHEDULE.name());
    }

    @Test
    void testAvailabilityMethods() {
        assertTrue(StatusJadwalDokter.AVAILABLE.isAvailable());

        assertFalse(StatusJadwalDokter.REQUESTED.isAvailable());
        assertFalse(StatusJadwalDokter.APPROVED.isAvailable());
        assertFalse(StatusJadwalDokter.CHANGE_SCHEDULE.isAvailable());
        assertFalse(StatusJadwalDokter.REJECTED.isAvailable());
    }
}