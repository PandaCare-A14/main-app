package com.pandacare.mainapp.konsultasi_dokter.model;

import com.pandacare.mainapp.konsultasi_dokter.model.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class JadwalKonsultasiTest {
    private CaregiverSchedule jadwal;

    @BeforeEach
    void setUp() {
        jadwal = new CaregiverSchedule();
        jadwal.setId("JADWAL001");
        jadwal.setIdCaregiver("DOC-12345");
        jadwal.setDate(LocalDate.parse("2025-05-06"));
        jadwal.setStartTime(LocalTime.parse("10:00"));
        jadwal.setEndTime(LocalTime.parse("11:00"));
    }

    @Test
    void testDefaultState() {
        assertEquals("AVAILABLE", jadwal.getStatusCaregiver());
        assertTrue(jadwal.isAvailable());
        assertInstanceOf(AvailableState.class, jadwal.getCurrentState());
    }

    @Test
    void testGetterSetter() {
        assertEquals("JADWAL001", jadwal.getId());
        assertEquals("DOC-12345", jadwal.getIdCaregiver());
        assertNull(jadwal.getIdPacilian());
        assertEquals(LocalDate.parse("2025-05-06"), jadwal.getDate());
        assertEquals(LocalTime.parse("10:00"), jadwal.getStartTime());
        assertEquals(LocalTime.parse("11:00"), jadwal.getEndTime());
        assertNull(jadwal.getNote());
        assertNull(jadwal.getMessage());
        assertFalse(jadwal.isChangeSchedule());

        jadwal.setIdPacilian("PAT-67890");
        jadwal.setNote("Test note pasien");
        jadwal.setMessage("Test message dokter");
        jadwal.setChangeSchedule(true);
        jadwal.setStatusPacilian("URGENT");

        assertEquals("PAT-67890", jadwal.getIdPacilian());
        assertEquals("Test note pasien", jadwal.getNote());
        assertEquals("Test message dokter", jadwal.getMessage());
        assertTrue(jadwal.isChangeSchedule());
        assertEquals("URGENT", jadwal.getStatusPacilian());
    }

    @Test
    void testRequest() {
        assertInstanceOf(AvailableState.class, jadwal.getCurrentState());

        jadwal.request("PAT-67890", "Ada benjolan di telinga");

        assertInstanceOf(RequestedState.class, jadwal.getCurrentState());
        assertEquals("REQUESTED", jadwal.getStatusCaregiver());
        assertFalse(jadwal.isAvailable());
        assertEquals("PAT-67890", jadwal.getIdPacilian());
        assertEquals("Ada benjolan di telinga", jadwal.getMessage());
    }

    @Test
    void testApproveFromRequested() {
        jadwal.request("PAT-67890", "Ada benjolan di telinga");
        assertInstanceOf(RequestedState.class, jadwal.getCurrentState());

        jadwal.approve();

        assertInstanceOf(ApprovedState.class, jadwal.getCurrentState());
        assertEquals("APPROVED", jadwal.getStatusCaregiver());
        assertFalse(jadwal.isAvailable());
    }

    @Test
    void testRejectFromRequested() {
        jadwal.request("PAT-67890", "Ada benjolan di telinga");
        assertInstanceOf(RequestedState.class, jadwal.getCurrentState());

        jadwal.reject("Jadwal nabrak");

        assertInstanceOf(RejectedState.class, jadwal.getCurrentState());
        assertEquals("REJECTED", jadwal.getStatusCaregiver());
        assertFalse(jadwal.isAvailable());
        assertEquals("Jadwal nabrak", jadwal.getMessage());
    }

    @Test
    void testChangeScheduleFromRequested() {
        jadwal.request("PAT-67890", "");
        assertInstanceOf(RequestedState.class, jadwal.getCurrentState());

        LocalDate newDate = LocalDate.parse("2025-05-07");
        LocalTime newStart = LocalTime.parse("14:00");
        LocalTime newEnd = LocalTime.parse("15:00");
        jadwal.changeSchedule(newDate, newStart, newEnd, "Ada urusan mendadak, mohon ganti jadwal");

        assertInstanceOf(ChangeScheduleState.class, jadwal.getCurrentState());
        assertEquals("CHANGE_SCHEDULE", jadwal.getStatusCaregiver());
        assertFalse(jadwal.isAvailable());
        assertEquals(newDate, jadwal.getDate());
        assertEquals(newStart, jadwal.getStartTime());
        assertEquals(newEnd, jadwal.getEndTime());
        assertEquals("Ada urusan mendadak, mohon ganti jadwal", jadwal.getMessage());
        assertTrue(jadwal.isChangeSchedule());
    }

    @Test
    void testSetState() {
        StatusCaregiver approvedState = new ApprovedState();
        jadwal.setState(approvedState);

        assertEquals(approvedState, jadwal.getCurrentState());
        assertEquals("APPROVED", jadwal.getStatusCaregiver());
        assertFalse(jadwal.isAvailable());
    }

    @Test
    void testInvalidStateTransitions() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> jadwal.approve()
        );

        assertEquals("No request found.", exception.getMessage());

        assertInstanceOf(AvailableState.class, jadwal.getCurrentState());
        assertEquals("AVAILABLE", jadwal.getStatusCaregiver());
    }

    @Test
    void testCompleteFlow() {
        assertTrue(jadwal.isAvailable());
        jadwal.request("PAT-67890", "");
        assertEquals("REQUESTED", jadwal.getStatusCaregiver());

        LocalDate newDate = LocalDate.parse("2025-05-08");
        LocalTime newStart = LocalTime.parse("09:00");
        LocalTime newEnd = LocalTime.parse("10:00");
        jadwal.changeSchedule(newDate, newStart, newEnd, "Mohon dimajukan waktunya, ada tindakan darurat");
        assertEquals("CHANGE_SCHEDULE", jadwal.getStatusCaregiver());
        assertEquals(newDate, jadwal.getDate());

        jadwal.approve();
        assertEquals("APPROVED", jadwal.getStatusCaregiver());
        assertFalse(jadwal.isAvailable());
    }
}