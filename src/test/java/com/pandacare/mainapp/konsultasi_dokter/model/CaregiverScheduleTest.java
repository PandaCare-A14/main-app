package com.pandacare.mainapp.konsultasi_dokter.model;

import com.pandacare.mainapp.konsultasi_dokter.model.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class CaregiverScheduleTest {
    private CaregiverSchedule schedule;

    @BeforeEach
    void setUp() {
        schedule = new CaregiverSchedule();
        schedule.setId("SCHED001");
        schedule.setIdCaregiver("DOC-12345");
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.parse("10:00"));
        schedule.setEndTime(LocalTime.parse("11:00"));
    }

    @Test
    void testDefaultState() {
        assertEquals("AVAILABLE", schedule.getStatusCaregiver());
        assertTrue(schedule.isAvailable());
        assertInstanceOf(AvailableState.class, schedule.getCurrentState());
    }

    @Test
    void testGetterSetter() {
        assertEquals("SCHED001", schedule.getId());
        assertEquals("DOC-12345", schedule.getIdCaregiver());
        assertNull(schedule.getIdPacilian());
        assertEquals(DayOfWeek.MONDAY, schedule.getDay());
        assertEquals(LocalTime.parse("10:00"), schedule.getStartTime());
        assertEquals(LocalTime.parse("11:00"), schedule.getEndTime());
        assertNull(schedule.getNote());
        assertNull(schedule.getMessage());
        assertFalse(schedule.isChangeSchedule());

        schedule.setIdPacilian("PAT67890");
        schedule.setNote("Test note pasien");
        schedule.setMessage("Test message dokter");
        schedule.setChangeSchedule(true);
        schedule.setStatusPacilian("WAITING");

        assertEquals("PAT67890", schedule.getIdPacilian());
        assertEquals("Test note pasien", schedule.getNote());
        assertEquals("Test message dokter", schedule.getMessage());
        assertTrue(schedule.isChangeSchedule());
        assertEquals("WAITING", schedule.getStatusPacilian());
    }

    @Test
    void testRequest() {
        assertInstanceOf(AvailableState.class, schedule.getCurrentState());
        schedule.request("PAT67890", "Ada benjolan di telinga");
        assertInstanceOf(RequestedState.class, schedule.getCurrentState());
        assertEquals("REQUESTED", schedule.getStatusCaregiver());
        assertFalse(schedule.isAvailable());
        assertEquals("PAT67890", schedule.getIdPacilian());
        assertEquals("Ada benjolan di telinga", schedule.getMessage());
    }

    @Test
    void testApproveFromRequested() {
        schedule.request("PAT-67890", "Ada benjolan di telinga");
        assertInstanceOf(RequestedState.class, schedule.getCurrentState());
        schedule.approve();
        assertInstanceOf(ApprovedState.class, schedule.getCurrentState());
        assertEquals("APPROVED", schedule.getStatusCaregiver());
        assertFalse(schedule.isAvailable());
    }

    @Test
    void testRejectFromRequested() {
        schedule.request("PAT-67890", "Ada benjolan di telinga");
        assertInstanceOf(RequestedState.class, schedule.getCurrentState());
        schedule.reject("schedule nabrak");
        assertInstanceOf(RejectedState.class, schedule.getCurrentState());
        assertEquals("REJECTED", schedule.getStatusCaregiver());
        assertFalse(schedule.isAvailable());
        assertEquals("schedule nabrak", schedule.getMessage());
    }

    @Test
    void testChangeScheduleFromRequested() {
        schedule.request("PAT-67890", "");
        assertInstanceOf(RequestedState.class, schedule.getCurrentState());

        DayOfWeek newDay = DayOfWeek.WEDNESDAY;
        LocalTime newStart = LocalTime.parse("14:00");
        LocalTime newEnd = LocalTime.parse("15:00");
        schedule.changeSchedule(newDay, newStart, newEnd, "Ada urusan mendadak, mohon ganti schedule");

        assertInstanceOf(ChangeScheduleState.class, schedule.getCurrentState());
        assertEquals("CHANGE_SCHEDULE", schedule.getStatusCaregiver());
        assertFalse(schedule.isAvailable());
        assertEquals(newDay, schedule.getDay());
        assertEquals(newStart, schedule.getStartTime());
        assertEquals(newEnd, schedule.getEndTime());
        assertEquals("Ada urusan mendadak, mohon ganti schedule", schedule.getMessage());
        assertTrue(schedule.isChangeSchedule());
    }

    @Test
    void testSetState() {
        StatusCaregiver approvedState = new ApprovedState();
        schedule.setState(approvedState);

        assertEquals(approvedState, schedule.getCurrentState());
        assertEquals("APPROVED", schedule.getStatusCaregiver());
        assertFalse(schedule.isAvailable());
    }

    @Test
    void testInvalidStateTransitions() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> schedule.approve()
        );

        assertEquals("No request found.", exception.getMessage());

        assertInstanceOf(AvailableState.class, schedule.getCurrentState());
        assertEquals("AVAILABLE", schedule.getStatusCaregiver());
    }

    @Test
    void testCompleteFlow() {
        assertTrue(schedule.isAvailable());
        schedule.request("PAT-67890", "");
        assertEquals("REQUESTED", schedule.getStatusCaregiver());

        DayOfWeek newDay = DayOfWeek.THURSDAY;
        LocalTime newStart = LocalTime.parse("09:00");
        LocalTime newEnd = LocalTime.parse("10:00");
        schedule.changeSchedule(newDay, newStart, newEnd, "Mohon dimajukan waktunya, ada tindakan darurat");
        assertEquals("CHANGE_SCHEDULE", schedule.getStatusCaregiver());
        assertEquals(newDay, schedule.getDay());

        schedule.approve();
        assertEquals("APPROVED", schedule.getStatusCaregiver());
        assertFalse(schedule.isAvailable());
    }
}