package com.pandacare.mainapp.reservasi.service.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.stateCaregiver.ApprovedState;
import com.pandacare.mainapp.reservasi.model.stateCaregiver.RejectedState;
import com.pandacare.mainapp.reservasi.model.stateCaregiver.RequestedState;
import com.pandacare.mainapp.reservasi.model.stateCaregiver.RescheduleState;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RequestedStateTest {
    private ScheduleService scheduleService;
    private RequestedState state;
    private ReservasiKonsultasi reservasi;

    @BeforeEach
    void setUp() {
        scheduleService = mock(ScheduleService.class);
        state = new RequestedState(scheduleService);
        reservasi = new ReservasiKonsultasi();
        reservasi.setCurrentState(state);
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
    }

    @Test
    void testGetStatus() {
        assertEquals(StatusReservasiKonsultasi.WAITING, state.getStatus());
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
    void testHandleChangeSchedule() {
        UUID newScheduleId = UUID.randomUUID();

        CaregiverSchedule mockSchedule = new CaregiverSchedule();
        when(scheduleService.getById(newScheduleId)).thenReturn(mockSchedule);

        state.handleChangeSchedule(reservasi, newScheduleId);

        assertEquals(StatusReservasiKonsultasi.ON_RESCHEDULE, reservasi.getStatusReservasi());
        assertInstanceOf(RescheduleState.class, reservasi.getCurrentState());
        assertEquals(mockSchedule, reservasi.getIdSchedule());
    }
}