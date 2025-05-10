package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ApprovedStateTest {
    private CaregiverSchedule schedule;
    private ApprovedState state;

    @BeforeEach
    void setUp() {
        schedule = new CaregiverSchedule();
        state = new ApprovedState();
        schedule.setState(state);
    }

    @Test
    void testGetStatusName() {
        assertEquals("APPROVED", state.getStatusName());
    }

    @Test
    void testIsAvailable() {
        assertFalse(state.isAvailable());
    }

    @Test
    void testHandleRequestThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleRequest(schedule, "PAT-789", "Permintaan baru"));

        assertEquals("Request has been approved.", exception.getMessage());
    }

    @Test
    void testHandleApprove_ThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleApprove(schedule));

        assertEquals("Request has been approved.", exception.getMessage());
    }

    @Test
    void testHandleRejectThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleReject(schedule, "Ada operasi mendadak, saya masih belum tahu kapan kosong"));

        assertEquals("Request has been approved.", exception.getMessage());
    }

    @Test
    void testHandleChangeScheduleThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(schedule, DayOfWeek.THURSDAY,
                        LocalTime.of(14, 0), LocalTime.of(15, 0),
                        "Mohon diganti"));

        assertEquals("Request has been approved.", exception.getMessage());
    }

    @Test
    void testNoAllowedStateTransitions() {
        try {
            state.handleRequest(schedule, "PAT-789", "Permintaan baru");
        } catch (IllegalStateException e) {
        }

        try {
            state.handleApprove(schedule);
        } catch (IllegalStateException e) {
        }

        try {
            state.handleReject(schedule, "Alasan penolakan");
        } catch (IllegalStateException e) {
        }

        try {
            state.handleChangeSchedule(schedule, DayOfWeek.THURSDAY,
                    LocalTime.of(14, 0), LocalTime.of(15, 0),
                    "Permintaan perubahan jadwal");
        } catch (IllegalStateException e) {
        }

        assertInstanceOf(ApprovedState.class, schedule.getCurrentState());
        assertEquals("APPROVED", schedule.getStatusCaregiver());
    }
}