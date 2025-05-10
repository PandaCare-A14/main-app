package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AvailableStateTest {
    private CaregiverSchedule schedule;
    private AvailableState state;

    @BeforeEach
    void setUp() {
        schedule = new CaregiverSchedule();
        state = new AvailableState();
        schedule.setState(state);
    }

    @Test
    void testGetStatusName() {
        assertEquals("AVAILABLE", state.getStatusName());
    }

    @Test
    void testIsAvailable() {
        assertTrue(state.isAvailable());
    }

    @Test
    void testHandleRequestSuccess() {
        String idPacilian = "PAT-123";
        String message = "Konsultasi tentang penyakit jantung";

        state.handleRequest(schedule, idPacilian, message);

        assertInstanceOf(RequestedState.class, schedule.getCurrentState());
        assertEquals("REQUESTED", schedule.getStatusCaregiver());
        assertEquals(idPacilian, schedule.getIdPacilian());
        assertEquals(message, schedule.getMessage());
        assertFalse(schedule.isAvailable());
    }

    @Test
    void testHandleRequestWithNullId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                state.handleRequest(schedule, null, "Konsultasi"));

        assertEquals("Pacilian ID can't be null.", exception.getMessage());

        assertInstanceOf(AvailableState.class, schedule.getCurrentState());
        assertEquals("AVAILABLE", schedule.getStatusCaregiver());
        assertTrue(schedule.isAvailable());
    }

    @Test
    void testHandleRequestWithNullMessage() {
        String idPacilian = "PAT-123";

        state.handleRequest(schedule, idPacilian, null);

        assertInstanceOf(RequestedState.class, schedule.getCurrentState());
        assertEquals("REQUESTED", schedule.getStatusCaregiver());
        assertEquals(idPacilian, schedule.getIdPacilian());
        assertNull(schedule.getMessage());
    }

    @Test
    void testHandleApproveUnsuccessful() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleApprove(schedule));

        assertEquals("No request found.", exception.getMessage());

        assertInstanceOf(AvailableState.class, schedule.getCurrentState());
        assertEquals("AVAILABLE", schedule.getStatusCaregiver());
    }

    @Test
    void testHandleRejectUnsuccessful() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleReject(schedule, "Alasan penolakan"));

        assertEquals("No request found.", exception.getMessage());

        assertInstanceOf(AvailableState.class, schedule.getCurrentState());
        assertEquals("AVAILABLE", schedule.getStatusCaregiver());
    }

    @Test
    void testHandleChangeScheduleUnsuccessful() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(schedule, DayOfWeek.FRIDAY,
                        LocalTime.of(14, 0), LocalTime.of(15, 0),
                        "Alasan perubahan"));

        assertEquals("No request found.", exception.getMessage());

        assertInstanceOf(AvailableState.class, schedule.getCurrentState());
        assertEquals("AVAILABLE", schedule.getStatusCaregiver());
    }
}