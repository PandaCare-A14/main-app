package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class ChangeScheduleStateTest {
    private CaregiverSchedule schedule;
    private ChangeScheduleState state;

    @BeforeEach
    void setUp() {
        schedule = new CaregiverSchedule();
        state = new ChangeScheduleState();
        schedule.setChangeSchedule(true);
    }

    @Test
    void testGetStatusName() {
        assertEquals("CHANGE_SCHEDULE", state.getStatusName());
    }

    @Test
    void testIsAvailable() {
        assertFalse(state.isAvailable());
    }

    @Test
    void testHandleRequest_ThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleRequest(schedule, "PAT-123", null));

        assertEquals("Request on changing process.", exception.getMessage());
    }

    @Test
    void testHandleApprove() {
        state.handleApprove(schedule);

        assertEquals("APPROVED", schedule.getStatusCaregiver());
        assertFalse(schedule.isChangeSchedule());
        assertTrue(schedule.getCurrentState() instanceof ApprovedState);
    }

    @Test
    void testHandleReject() {
        String rejectionReason = "Pasien tidak setuju dengan perubahan jadwal";
        state.handleReject(schedule, rejectionReason);

        assertEquals("REJECTED", schedule.getStatusCaregiver());
        assertEquals(rejectionReason, schedule.getMessage());
        assertFalse(schedule.isChangeSchedule());
        assertTrue(schedule.getCurrentState() instanceof RejectedState);
    }

    @Test
    void testHandleChangeScheduleThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(schedule, DayOfWeek.FRIDAY,
                        LocalTime.of(14, 0), LocalTime.of(15, 0),
                        "Coba ubah lagi"));

        assertEquals("Request on changing process.", exception.getMessage());
    }
}