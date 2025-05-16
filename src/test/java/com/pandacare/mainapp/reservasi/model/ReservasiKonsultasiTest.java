package com.pandacare.mainapp.reservasi.model;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.state.*;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReservasiKonsultasiTest {
    private ReservasiKonsultasi reservasi;

    @Mock
    private ScheduleService mockScheduleService;

    @BeforeEach
    void setUp() {
        reservasi = new ReservasiKonsultasi();
        reservasi.setIdPacilian("PATIENT1");
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
    }

    @Test
    void testInitStateForWaiting() {
        reservasi.ensureStateInitialized(mockScheduleService);
        assertInstanceOf(RequestedState.class, reservasi.getCurrentState());
    }

    @Test
    void testInitStateForApproved() {
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        reservasi.ensureStateInitialized(mockScheduleService);
        assertInstanceOf(ApprovedState.class, reservasi.getCurrentState());
    }

    @Test
    void testInitStateForRejected() {
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.REJECTED);
        reservasi.ensureStateInitialized(mockScheduleService);
        assertInstanceOf(RejectedState.class, reservasi.getCurrentState());
    }

    @Test
    void testInitStateForReschedule() {
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.ON_RESCHEDULE);
        reservasi.ensureStateInitialized(mockScheduleService);
        assertInstanceOf(RescheduleState.class, reservasi.getCurrentState());
    }

    @Test
    void testApproveWithState() {
        reservasi.ensureStateInitialized(mockScheduleService);
        reservasi.approve();
        assertEquals(StatusReservasiKonsultasi.APPROVED, reservasi.getStatusReservasi());
        assertInstanceOf(ApprovedState.class, reservasi.getCurrentState());
    }

    @Test
    void testApproveWithoutState() {
        reservasi.approve();
        assertEquals(StatusReservasiKonsultasi.APPROVED, reservasi.getStatusReservasi());
    }

    @Test
    void testRejectWithState() {
        reservasi.ensureStateInitialized(mockScheduleService);
        reservasi.reject();
        assertEquals(StatusReservasiKonsultasi.REJECTED, reservasi.getStatusReservasi());
        assertInstanceOf(RejectedState.class, reservasi.getCurrentState());
    }

    @Test
    void testRejectWithoutState() {
        reservasi.reject();
        assertEquals(StatusReservasiKonsultasi.REJECTED, reservasi.getStatusReservasi());
    }

    @Test
    void testHandleChangeScheduleWithState() {
        UUID newScheduleId = UUID.randomUUID();
        reservasi.ensureStateInitialized(mockScheduleService);
        reservasi.handleChangeSchedule(newScheduleId);
        assertEquals(StatusReservasiKonsultasi.ON_RESCHEDULE, reservasi.getStatusReservasi());
        assertInstanceOf(RescheduleState.class, reservasi.getCurrentState());
    }

    @Test
    void testHandleChangeScheduleWithoutState() {
        UUID newScheduleId = UUID.randomUUID();
        reservasi.handleChangeSchedule(newScheduleId);
        assertEquals(StatusReservasiKonsultasi.ON_RESCHEDULE, reservasi.getStatusReservasi());
    }

    @Test
    void testSetState() {
        ReservasiState newState = new ApprovedState();
        reservasi.setState(newState);
        assertSame(newState, reservasi.getCurrentState());
        assertEquals(StatusReservasiKonsultasi.APPROVED, reservasi.getStatusReservasi());
    }

    @Test
    void testConstructorGeneratesId() {
        assertNotNull(reservasi.getIdReservasi());
        assertFalse(reservasi.getIdReservasi().isEmpty());
    }
}