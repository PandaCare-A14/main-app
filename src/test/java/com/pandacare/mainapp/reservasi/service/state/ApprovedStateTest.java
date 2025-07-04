package com.pandacare.mainapp.reservasi.service.state;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.stateCaregiver.ApprovedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ApprovedStateTest {
    private ApprovedState state;
    private ReservasiKonsultasi reservasi;

    @BeforeEach
    void setUp() {
        state = new ApprovedState();
        reservasi = new ReservasiKonsultasi();
        reservasi.setCurrentState(state);
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
    }

    @Test
    void testGetStatus() {
        assertEquals(StatusReservasiKonsultasi.APPROVED, state.getStatus());
    }

    @Test
    void testHandleApproveThrowsException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                state.handleApprove(reservasi));
        assertEquals("This reservation has already been approved.", ex.getMessage());
    }

    @Test
    void testHandleRejectThrowsException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                state.handleReject(reservasi));
        assertEquals("This reservation has already been approved.", ex.getMessage());
    }

    @Test
    void testHandleChangeScheduleThrowsException() {
        UUID id = UUID.randomUUID();
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(reservasi, id));
        assertEquals("This reservation has already been approved.", ex.getMessage());
    }
}