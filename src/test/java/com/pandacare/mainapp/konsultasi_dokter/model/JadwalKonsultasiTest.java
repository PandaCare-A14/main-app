package com.pandacare.mainapp.konsultasi_dokter.model;

import com.pandacare.mainapp.konsultasi_dokter.model.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class JadwalKonsultasiTest {
    private JadwalKonsultasi jadwal;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setId("JADWAL001");
        jadwal.setIdDokter("DOC-12345");
        jadwal.setDate(LocalDate.parse("2025-05-06"));
        jadwal.setStartTime(LocalTime.parse("10:00"));
        jadwal.setEndTime(LocalTime.parse("11:00"));
    }

    @Test
    void testDefaultState() {
        assertEquals("AVAILABLE", jadwal.getStatusDokter());
        assertTrue(jadwal.isAvailable());
        assertTrue(jadwal.getCurrentState() instanceof AvailableState);
    }

    @Test
    void testGetterSetter() {
        assertEquals("JADWAL001", jadwal.getId());
        assertEquals("DOC-12345", jadwal.getIdDokter());
        assertNull(jadwal.getIdPasien());
        assertEquals(LocalDate.parse("2025-05-06"), jadwal.getDate());
        assertEquals(LocalTime.parse("10:00"), jadwal.getStartTime());
        assertEquals(LocalTime.parse("11:00"), jadwal.getEndTime());
        assertNull(jadwal.getNote());
        assertNull(jadwal.getMessage());
        assertFalse(jadwal.isChangeSchedule());

        jadwal.setIdPasien("PAT-67890");
        jadwal.setNote("Test note");
        jadwal.setMessage("Test message");
        jadwal.setChangeSchedule(true);
        jadwal.setStatusPacilian("URGENT");

        assertEquals("PAT-67890", jadwal.getIdPasien());
        assertEquals("Test note", jadwal.getNote());
        assertEquals("Test message", jadwal.getMessage());
        assertTrue(jadwal.isChangeSchedule());
        assertEquals("URGENT", jadwal.getStatusPacilian());
    }

    @Test
    void testRequest() {
        assertTrue(jadwal.getCurrentState() instanceof AvailableState);

        jadwal.request("PAT-67890", "Need consultation");

        assertTrue(jadwal.getCurrentState() instanceof RequestedState);
        assertEquals("REQUESTED", jadwal.getStatusDokter());
        assertFalse(jadwal.isAvailable());
        assertEquals("PAT-67890", jadwal.getIdPasien());
        assertEquals("Need consultation", jadwal.getMessage());
    }

    @Test
    void testApproveFromRequested() {
        jadwal.request("PAT-67890", "Need consultation");
        assertTrue(jadwal.getCurrentState() instanceof RequestedState);

        jadwal.approve();

        assertTrue(jadwal.getCurrentState() instanceof ApprovedState);
        assertEquals("APPROVED", jadwal.getStatusDokter());
        assertFalse(jadwal.isAvailable());
    }

    @Test
    void testRejectFromRequested() {
        jadwal.request("PAT-67890", "Need consultation");
        assertTrue(jadwal.getCurrentState() instanceof RequestedState);

        jadwal.reject("Doctor unavailable");

        assertTrue(jadwal.getCurrentState() instanceof RejectedState);
        assertEquals("REJECTED", jadwal.getStatusDokter());
        assertFalse(jadwal.isAvailable());
        assertEquals("Doctor unavailable", jadwal.getNote());
    }

    @Test
    void testChangeScheduleFromRequested() {
        jadwal.request("PAT-67890", "Need consultation");
        assertTrue(jadwal.getCurrentState() instanceof RequestedState);

        LocalDate newDate = LocalDate.parse("2025-05-07");
        LocalTime newStart = LocalTime.parse("14:00");
        LocalTime newEnd = LocalTime.parse("15:00");
        jadwal.changeSchedule(newDate, newStart, newEnd, "Doctor available at new time");

        assertTrue(jadwal.getCurrentState() instanceof ChangeScheduleState);
        assertEquals("CHANGED", jadwal.getStatusDokter());
        assertFalse(jadwal.isAvailable());
        assertEquals(newDate, jadwal.getDate());
        assertEquals(newStart, jadwal.getStartTime());
        assertEquals(newEnd, jadwal.getEndTime());
        assertEquals("Doctor available at new time", jadwal.getNote());
        assertTrue(jadwal.isChangeSchedule());
    }

    @Test
    void testSetState() {
        StatusJadwalDokter approvedState = new ApprovedState();
        jadwal.setState(approvedState);

        assertEquals(approvedState, jadwal.getCurrentState());
        assertEquals("APPROVED", jadwal.getStatusDokter());
        assertFalse(jadwal.isAvailable());
    }

    @Test
    void testInvalidStateTransitions() {
        jadwal.approve();
        assertTrue(jadwal.getCurrentState() instanceof AvailableState);
        assertEquals("AVAILABLE", jadwal.getStatusDokter());
    }

    @Test
    void testCompleteFlow() {
        assertTrue(jadwal.isAvailable());
        jadwal.request("PAT-67890", "Need urgent consultation");
        assertEquals("REQUESTED", jadwal.getStatusDokter());

        LocalDate newDate = LocalDate.parse("2025-05-08");
        LocalTime newStart = LocalTime.parse("09:00");
        LocalTime newEnd = LocalTime.parse("10:00");
        jadwal.changeSchedule(newDate, newStart, newEnd, "Doctor suggests earlier time");
        assertEquals("CHANGED", jadwal.getStatusDokter());
        assertEquals(newDate, jadwal.getDate());

        jadwal.approve();
        assertEquals("APPROVED", jadwal.getStatusDokter());
        assertFalse(jadwal.isAvailable());
    }
}