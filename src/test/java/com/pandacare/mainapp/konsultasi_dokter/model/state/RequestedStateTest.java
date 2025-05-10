package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class RequestedStateTest {
    private CaregiverSchedule schedule;
    private RequestedState state;
    private final DayOfWeek INITIAL_DAY = DayOfWeek.MONDAY;
    private final LocalTime INITIAL_START_TIME = LocalTime.of(9, 0);
    private final LocalTime INITIAL_END_TIME = LocalTime.of(10, 0);

    @BeforeEach
    void setUp() {
        schedule = new CaregiverSchedule();
        schedule.setDay(INITIAL_DAY);
        schedule.setStartTime(INITIAL_START_TIME);
        schedule.setEndTime(INITIAL_END_TIME);
        state = new RequestedState();
        schedule.setState(state);
    }

    @Test
    void testGetStatusName() {
        assertEquals("REQUESTED", state.getStatusName());
    }

    @Test
    void testIsAvailable() {
        assertFalse(state.isAvailable());
    }

    @Test
    void testHandleRequest_ThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleRequest(schedule, "PAT-456", "Request ulang"));

        assertEquals("Schedule is being requested.", exception.getMessage());
    }

    @Test
    void testHandleApprove_TransitionsToApprovedState() {
        state.handleApprove(schedule);

        assertEquals("APPROVED", schedule.getStatusCaregiver());
        assertTrue(schedule.getCurrentState() instanceof ApprovedState);
    }

    @Test
    void testHandleReject() {
        String rejectionReason = "Ada operasi besar harus ditangani";
        state.handleReject(schedule, rejectionReason);

        assertEquals("REJECTED", schedule.getStatusCaregiver());
        assertEquals(rejectionReason, schedule.getMessage());
        assertTrue(schedule.getCurrentState() instanceof RejectedState);
    }

    @Test
    void testHandleChangeSchedule() {
        DayOfWeek newDay = DayOfWeek.WEDNESDAY;
        LocalTime newStartTime = LocalTime.of(14, 0);
        LocalTime newEndTime = LocalTime.of(15, 0);
        String changeReason = "Ada urgensi mendadak";

        state.handleChangeSchedule(schedule, newDay, newStartTime, newEndTime, changeReason);

        assertEquals("CHANGE_SCHEDULE", schedule.getStatusCaregiver());
        assertEquals(newDay, schedule.getDay());
        assertEquals(newStartTime, schedule.getStartTime());
        assertEquals(newEndTime, schedule.getEndTime());
        assertEquals(changeReason, schedule.getMessage());
        assertTrue(schedule.isChangeSchedule());
        assertTrue(schedule.getCurrentState() instanceof ChangeScheduleState);
    }
}