package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RejectedStateTest {
    private CaregiverSchedule schedule;
    private RejectedState state;

    @BeforeEach
    void setUp() {
        schedule = new CaregiverSchedule();
        state = new RejectedState();
        schedule.setState(state);
    }

    @Test
    void testGetStatusName() {
        assertEquals("REJECTED", state.getStatusName());
    }

    @Test
    void testIsAvailable() {
        assertFalse(state.isAvailable());
    }

    @Test
    void testHandleRequest() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleRequest(schedule, "PAT-009", "Saya banding"));

        assertEquals("Request has been rejected.", exception.getMessage());
        assertInstanceOf(RejectedState.class, schedule.getCurrentState());
    }

    @Test
    void testHandleApprove() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleApprove(schedule));

        assertEquals("Request has been rejected.", exception.getMessage());
        assertInstanceOf(RejectedState.class, schedule.getCurrentState());
    }

    @Test
    void testHandleReject() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleReject(schedule, "Masih ditolak"));

        assertEquals("Request has been rejected.", exception.getMessage());
        assertInstanceOf(RejectedState.class, schedule.getCurrentState());
    }

    @Test
    void testHandleChangeSchedule() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(schedule, DayOfWeek.WEDNESDAY,
                        LocalTime.of(13, 0), LocalTime.of(14, 0),
                        "Coba reschedule"));

        assertEquals("Request has been rejected.", exception.getMessage());
        assertInstanceOf(RejectedState.class, schedule.getCurrentState());
    }
}