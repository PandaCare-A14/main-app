package com.pandacare.mainapp.reservasi.model;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.state.*;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ReservasiKonsultasiTest {
    private ReservasiKonsultasi reservasi;

    @Mock
    private ScheduleService mockScheduleService;

    @Mock
    private CaregiverSchedule mockSchedule;

    @BeforeEach
    void setUp() {
        reservasi = new ReservasiKonsultasi();
        reservasi.setIdPacilian(UUID.randomUUID());
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
        assertNotNull(reservasi.getId());
    }

    @Test
    void testInitializeState() {
        reservasi.setScheduleService(mockScheduleService);

        reservasi.initializeState();

        assertNotNull(reservasi.getCurrentState());
        assertInstanceOf(RequestedState.class, reservasi.getCurrentState());
    }

    @Test
    void testLoadStateWithScheduleService() {
        reservasi.setScheduleService(mockScheduleService);

        try {
            java.lang.reflect.Method loadStateMethod =
                    ReservasiKonsultasi.class.getDeclaredMethod("loadState");
            loadStateMethod.setAccessible(true);
            loadStateMethod.invoke(reservasi);

            assertNotNull(reservasi.getCurrentState());
            assertInstanceOf(RequestedState.class, reservasi.getCurrentState());
        } catch (Exception e) {
            fail("Exception occurred while testing loadState: " + e.getMessage());
        }
    }

    @Test
    void testLoadStateWithoutScheduleService() {
        reservasi.setScheduleService(null);

        try {
            java.lang.reflect.Method loadStateMethod =
                    ReservasiKonsultasi.class.getDeclaredMethod("loadState");
            loadStateMethod.setAccessible(true);
            loadStateMethod.invoke(reservasi);

            assertNull(reservasi.getCurrentState());
        } catch (Exception e) {
            fail("Exception occurred while testing loadState: " + e.getMessage());
        }
    }

    @Test
    void testEnsureStateInitializedWithExistingState() {
        ReservasiState mockState = mock(ReservasiState.class);
        reservasi.setState(mockState);
        reservasi.ensureStateInitialized(mockScheduleService);
        assertSame(mockState, reservasi.getCurrentState());
    }

    @Test
    void testEnsureStateInitializedWithExistingScheduleService() {
        ScheduleService existingService = mock(ScheduleService.class);
        reservasi.setScheduleService(existingService);
        reservasi.ensureStateInitialized(mockScheduleService);
        assertSame(existingService, reservasi.getScheduleService());
    }

    @Test
    void testIdScheduleAssociation() {
        reservasi.setIdSchedule(mockSchedule);
        assertSame(mockSchedule, reservasi.getIdSchedule());
    }

    @Test
    void testPacilianNote() {
        String testNote = "Test patient note";
        reservasi.setPacilianNote(testNote);
        assertEquals(testNote, reservasi.getPacilianNote());
    }

    @Test
    void testDefaultStatus() {
        ReservasiKonsultasi newReservasi = new ReservasiKonsultasi();
        assertEquals(StatusReservasiKonsultasi.WAITING, newReservasi.getStatusReservasi());
    }

    @Test
    void testSetIdReservasi() {
        UUID id = UUID.randomUUID();
        reservasi.setId(id);
        assertEquals(id, reservasi.getId());
    }

    @Test
    void testInitStateWithDefaultCase() {
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        reservasi.ensureStateInitialized(mockScheduleService);
        assertInstanceOf(RequestedState.class, reservasi.getCurrentState());
    }
}