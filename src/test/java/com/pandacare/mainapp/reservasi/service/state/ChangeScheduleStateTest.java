package com.pandacare.mainapp.reservasi.service.state;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.stateCaregiver.ApprovedState;
import com.pandacare.mainapp.reservasi.model.stateCaregiver.RejectedState;
import com.pandacare.mainapp.reservasi.model.stateCaregiver.RescheduleState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChangeScheduleStateTest {
    private ReservasiKonsultasi reservasi;
    private RescheduleState state;

    @BeforeEach
    void setUp() {
        reservasi = new ReservasiKonsultasi();
        state = new RescheduleState();
    }

    @Test
    void testGetStatus() {
        assertEquals(StatusReservasiKonsultasi.ON_RESCHEDULE, state.getStatus());
    }

    @Test
    void testHandleApprove() {
        state.handleApprove(reservasi);

        assertEquals(StatusReservasiKonsultasi.APPROVED, reservasi.getStatusReservasi());
        assertInstanceOf(ApprovedState.class, reservasi.getCurrentState());
    }

    @Test
    void testHandleReject() {
        state.handleReject(reservasi);

        assertEquals(StatusReservasiKonsultasi.REJECTED, reservasi.getStatusReservasi());
        assertInstanceOf(RejectedState.class, reservasi.getCurrentState());
    }

    @Test
    void testHandleChangeScheduleThrowsException() {
        UUID id = UUID.randomUUID();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(reservasi, id));

        assertEquals("Operation not allowed. This reservation is on reschedule.", exception.getMessage());
    }
}