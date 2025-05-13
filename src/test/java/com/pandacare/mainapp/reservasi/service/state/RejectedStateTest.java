package com.pandacare.mainapp.reservasi.service.state;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.state.RejectedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RejectedStateTest {
    private RejectedState state;
    private ReservasiKonsultasi reservasi;

    @BeforeEach
    void setUp() {
        state = new RejectedState();
        reservasi = new ReservasiKonsultasi();
        reservasi.setCurrentState(state);
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.REJECTED);
    }

    @Test
    void testGetStatus() {
        assertEquals(StatusReservasiKonsultasi.REJECTED, state.getStatus());
    }

    @Test
    void testHandleApproveThrowsException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                state.handleApprove(reservasi));
        assertEquals("This reservation has already been rejected.", ex.getMessage());
    }

    @Test
    void testHandleRejectThrowsException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                state.handleReject(reservasi));
        assertEquals("This reservation has already been rejected.", ex.getMessage());
    }

    @Test
    void testHandleChangeScheduleThrowsException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(reservasi, "new-sched-id"));
        assertEquals("This reservation has already been rejected.", ex.getMessage());
    }
}