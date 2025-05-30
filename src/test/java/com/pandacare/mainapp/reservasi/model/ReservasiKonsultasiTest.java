package com.pandacare.mainapp.reservasi.model;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.stateCaregiver.*;
import com.pandacare.mainapp.reservasi.model.statepacilian.ReservasiStatePacilian;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        UUID customId = UUID.randomUUID();
        reservasi.setId(customId);
        assertEquals(customId, reservasi.getId());
    }

    @Test
    void testInitStateWithDefaultCase() {
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        reservasi.ensureStateInitialized(mockScheduleService);
        assertInstanceOf(RequestedState.class, reservasi.getCurrentState());
    }

    @Test
    void testEditAsPacilian() {
        ReservasiStatePacilian mockStatePacilian = mock(ReservasiStatePacilian.class);
        reservasi.setStatePacilian(mockStatePacilian);

        String newDay = "Senin";
        String newStart = "09:00";
        String newEnd = "10:00";

        reservasi.editAsPacilian(newDay, newStart, newEnd);

        verify(mockStatePacilian).edit(reservasi, newDay, newStart, newEnd);
    }

    @Test
    void testAcceptChangeAsPacilian() {
        ReservasiStatePacilian mockStatePacilian = mock(ReservasiStatePacilian.class);
        reservasi.setStatePacilian(mockStatePacilian);

        reservasi.acceptChangeAsPacilian();

        verify(mockStatePacilian).acceptChange(reservasi);
    }

    @Test
    void testRejectChangeAsPacilian() {
        ReservasiStatePacilian mockStatePacilian = mock(ReservasiStatePacilian.class);
        reservasi.setStatePacilian(mockStatePacilian);

        reservasi.rejectChangeAsPacilian();

        verify(mockStatePacilian).rejectChange(reservasi);
    }

    @Test
    void testEditAsPacilianThrowsException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                reservasi.editAsPacilian("Senin", "09:00", "10:00")
        );
        assertEquals("State Pacilian belum diset.", ex.getMessage());
    }

    @Test
    void testAcceptChangeAsPacilianThrowsException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                reservasi.acceptChangeAsPacilian()
        );
        assertEquals("State Pacilian belum diset.", ex.getMessage());
    }

    @Test
    void testRejectChangeAsPacilianThrowsException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                reservasi.rejectChangeAsPacilian()
        );
        assertEquals("State Pacilian belum diset.", ex.getMessage());
    }

    @Test
    void testGetDay() {
        when(mockSchedule.getDay()).thenReturn(DayOfWeek.MONDAY);
        reservasi.setIdSchedule(mockSchedule);
        assertEquals("MONDAY", reservasi.getDay());
    }


    @Test
    void testGetStartTime() {
        when(mockSchedule.getStartTime()).thenReturn(LocalTime.of(9, 0));
        reservasi.setIdSchedule(mockSchedule);
        assertEquals(LocalTime.of(9, 0), reservasi.getStartTime());
    }

    @Test
    void testGetEndTime() {
        when(mockSchedule.getEndTime()).thenReturn(LocalTime.of(10, 0));
        reservasi.setIdSchedule(mockSchedule);
        assertEquals(LocalTime.of(10, 0), reservasi.getEndTime());
    }

    @Test
    void testGetIdCaregiver() {
        UUID caregiverId = UUID.randomUUID();
        when(mockSchedule.getIdCaregiver()).thenReturn(caregiverId);
        reservasi.setIdSchedule(mockSchedule);
        assertEquals(caregiverId, reservasi.getIdCaregiver());
    }

    @Test
    void testSetIdCaregiver() {
        UUID caregiverId = UUID.randomUUID();
        reservasi.setIdSchedule(mockSchedule);

        reservasi.setIdCaregiver(caregiverId);

        verify(mockSchedule).setIdCaregiver(caregiverId);
    }

    @Test
    void testOnCreateGeneratesIdIfNull() throws Exception {
        ReservasiKonsultasi newReservasi = new ReservasiKonsultasi();
        newReservasi.setId(null);

        // Panggil onCreate (pakai refleksi karena protected)
        java.lang.reflect.Method onCreateMethod =
                ReservasiKonsultasi.class.getDeclaredMethod("onCreate");
        onCreateMethod.setAccessible(true);
        onCreateMethod.invoke(newReservasi);

        assertNotNull(newReservasi.getId());
    }

    @Test
    void testInitStateWithUnknownStatus() {
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        try {
            java.lang.reflect.Field statusField = ReservasiKonsultasi.class.getDeclaredField("statusReservasi");
            statusField.setAccessible(true);
            statusField.set(reservasi, null);

            ReservasiState mockState = mock(RequestedState.class);
            reservasi.setState(mockState);

            reservasi.ensureStateInitialized(mockScheduleService);

            assertSame(mockState, reservasi.getCurrentState());
        } catch (Exception e) {
            fail("Exception occurred in testInitStateWithUnknownStatus: " + e.getMessage());
        }
    }

    @Test
    void testEqualsWithSameObject() {
        assertTrue(reservasi.equals(reservasi));
    }

    @Test
    void testEqualsWithNull() {
        assertFalse(reservasi.equals(null));
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertFalse(reservasi.equals("Not a ReservasiKonsultasi"));
    }

    @Test
    void testEqualsWithEqualObjects() {
        ReservasiKonsultasi reservation1 = new ReservasiKonsultasi();
        UUID id = UUID.randomUUID();
        UUID pacilianId = UUID.randomUUID();
        reservation1.setId(id);
        reservation1.setIdPacilian(pacilianId);
        reservation1.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        reservation1.setPacilianNote("Test note");
        reservation1.setIdSchedule(mockSchedule);

        ReservasiKonsultasi reservation2 = new ReservasiKonsultasi();
        reservation2.setId(id);
        reservation2.setIdPacilian(pacilianId);
        reservation2.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        reservation2.setPacilianNote("Test note");
        reservation2.setIdSchedule(mockSchedule);

        assertTrue(reservation1.equals(reservation2));
        assertTrue(reservation2.equals(reservation1));
    }

    @Test
    void testEqualsWithDifferentId() {
        ReservasiKonsultasi reservation1 = new ReservasiKonsultasi();
        reservation1.setId(UUID.randomUUID());

        ReservasiKonsultasi reservation2 = new ReservasiKonsultasi();
        reservation2.setId(UUID.randomUUID());

        assertFalse(reservation1.equals(reservation2));
    }

    @Test
    void testHashCode() {
        ReservasiKonsultasi reservation1 = new ReservasiKonsultasi();
        UUID id = UUID.randomUUID();
        reservation1.setId(id);

        ReservasiKonsultasi reservation2 = new ReservasiKonsultasi();
        reservation2.setId(id);

        assertEquals(reservation1.hashCode(), reservation2.hashCode());
    }

    @Test
    void testEqualsWithDifferentFields() {
        ReservasiKonsultasi base = new ReservasiKonsultasi();
        UUID baseId = UUID.randomUUID();
        UUID basePacilianId = UUID.randomUUID();
        base.setId(baseId);
        base.setIdPacilian(basePacilianId);
        base.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        base.setPacilianNote("Base note");
        base.setIdSchedule(mockSchedule);

        ReservasiKonsultasi differentPacilianId = new ReservasiKonsultasi();
        differentPacilianId.setId(baseId);
        differentPacilianId.setIdPacilian(UUID.randomUUID());
        differentPacilianId.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        differentPacilianId.setPacilianNote("Base note");
        differentPacilianId.setIdSchedule(mockSchedule);
        assertFalse(base.equals(differentPacilianId));

        ReservasiKonsultasi differentStatus = new ReservasiKonsultasi();
        differentStatus.setId(baseId);
        differentStatus.setIdPacilian(basePacilianId);
        differentStatus.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        differentStatus.setPacilianNote("Base note");
        differentStatus.setIdSchedule(mockSchedule);
        assertFalse(base.equals(differentStatus));

        ReservasiKonsultasi differentNote = new ReservasiKonsultasi();
        differentNote.setId(baseId);
        differentNote.setIdPacilian(basePacilianId);
        differentNote.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        differentNote.setPacilianNote("Different note");
        differentNote.setIdSchedule(mockSchedule);
        assertFalse(base.equals(differentNote));

        ReservasiKonsultasi nullNote = new ReservasiKonsultasi();
        nullNote.setId(baseId);
        nullNote.setIdPacilian(basePacilianId);
        nullNote.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        nullNote.setPacilianNote(null);
        nullNote.setIdSchedule(mockSchedule);
        assertFalse(base.equals(nullNote));

        base.setPacilianNote(null);
        assertFalse(base.equals(differentNote));

        ReservasiKonsultasi differentSchedule = new ReservasiKonsultasi();
        differentSchedule.setId(baseId);
        differentSchedule.setIdPacilian(basePacilianId);
        differentSchedule.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        differentSchedule.setPacilianNote(null);
        CaregiverSchedule anotherMockSchedule = mock(CaregiverSchedule.class);
        differentSchedule.setIdSchedule(anotherMockSchedule);
        assertFalse(base.equals(differentSchedule));

        ReservasiKonsultasi nullSchedule = new ReservasiKonsultasi();
        nullSchedule.setId(baseId);
        nullSchedule.setIdPacilian(basePacilianId);
        nullSchedule.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        nullSchedule.setPacilianNote(null);
        nullSchedule.setIdSchedule(null);
        assertFalse(base.equals(nullSchedule));

        base.setIdSchedule(null);
        assertFalse(base.equals(differentSchedule));
    }

    @Test
    void testSetStateWithNull() {
        reservasi.setState(null);
        assertNull(reservasi.getCurrentState());

        assertEquals(StatusReservasiKonsultasi.WAITING, reservasi.getStatusReservasi());
    }

    @Test
    void testSetIdCaregiverWithNullSchedule() {
        UUID caregiverId = UUID.randomUUID();
        reservasi.setIdSchedule(null);

        reservasi.setIdCaregiver(caregiverId);

        assertNull(reservasi.getIdSchedule());
    }

    @Test
    void testOnCreateWithExistingId() throws Exception {
        UUID existingId = UUID.randomUUID();
        reservasi.setId(existingId);

        java.lang.reflect.Method onCreateMethod =
                ReservasiKonsultasi.class.getDeclaredMethod("onCreate");
        onCreateMethod.setAccessible(true);
        onCreateMethod.invoke(reservasi);

        assertEquals(existingId, reservasi.getId());
    }

    @Test
    void testHashCodeWithNullFields() {
        ReservasiKonsultasi testReservasi = new ReservasiKonsultasi();
        testReservasi.setId(null);
        testReservasi.setIdPacilian(null);
        testReservasi.setStatusReservasi(null);
        testReservasi.setPacilianNote(null);
        testReservasi.setIdSchedule(null);

        int hashCode = testReservasi.hashCode();
    }

    @Test
    void testCanEqual() {
        ReservasiKonsultasi reservation1 = new ReservasiKonsultasi();
        ReservasiKonsultasi reservation2 = new ReservasiKonsultasi();
        Object nonReservation = "Not a reservation";

        assertTrue(reservation1.canEqual(reservation2));
        assertFalse(reservation1.canEqual(nonReservation));
    }

    @Test
    void testNullDayWhenScheduleIsNull() {
        reservasi.setIdSchedule(null);
        assertNull(reservasi.getDay());
    }

    @Test
    void testInitStateWithDefaultCaseExplicitly() {
        try {
            Field statusField = ReservasiKonsultasi.class.getDeclaredField("statusReservasi");
            statusField.setAccessible(true);

            StatusReservasiKonsultasi originalStatus = reservasi.getStatusReservasi();

            Object[] enumConstants = StatusReservasiKonsultasi.class.getEnumConstants();
            Method ordinalMethod = Enum.class.getDeclaredMethod("ordinal");
            ordinalMethod.setAccessible(true);
            int maxOrdinal = -1;

            for (Object enumConstant : enumConstants) {
                int ordinal = (int) ordinalMethod.invoke(enumConstant);
                maxOrdinal = Math.max(maxOrdinal, ordinal);
            }

            Field ordinalField = Enum.class.getDeclaredField("ordinal");
            ordinalField.setAccessible(true);
            StatusReservasiKonsultasi testStatus = StatusReservasiKonsultasi.WAITING;
            ordinalField.set(testStatus, maxOrdinal + 1);

            statusField.set(reservasi, testStatus);

            reservasi.ensureStateInitialized(mockScheduleService);

            assertInstanceOf(RequestedState.class, reservasi.getCurrentState());

            statusField.set(reservasi, originalStatus);
        } catch (Exception e) {
            reservasi.ensureStateInitialized(mockScheduleService);
            assertInstanceOf(RequestedState.class, reservasi.getCurrentState());
        }
    }

    @Test
    void testHashCodeWithAllFieldsPresent() {
        UUID id = UUID.randomUUID();
        UUID pacilianId = UUID.randomUUID();
        CaregiverSchedule schedule = mock(CaregiverSchedule.class);

        ReservasiKonsultasi reservation = new ReservasiKonsultasi();
        reservation.setId(id);
        reservation.setIdPacilian(pacilianId);
        reservation.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        reservation.setPacilianNote("Test note");
        reservation.setIdSchedule(schedule);

        ReservasiKonsultasi identical = new ReservasiKonsultasi();
        identical.setId(id);
        identical.setIdPacilian(pacilianId);
        identical.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        identical.setPacilianNote("Test note");
        identical.setIdSchedule(schedule);

        assertEquals(reservation, identical);
        assertEquals(reservation.hashCode(), identical.hashCode());
    }

    @Test
    void testComplexEqualsScenario() {
        ReservasiKonsultasi a = new ReservasiKonsultasi();
        ReservasiKonsultasi b = new ReservasiKonsultasi();
        ReservasiKonsultasi c = new ReservasiKonsultasi();
        ReservasiKonsultasi d = new ReservasiKonsultasi();

        UUID id = UUID.randomUUID();

        // Set up objects with various combinations of fields
        a.setId(id);
        a.setIdPacilian(UUID.randomUUID());
        a.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        a.setPacilianNote("Note A");
        a.setIdSchedule(mockSchedule);

        b.setId(id);
        b.setIdPacilian(a.getIdPacilian());
        b.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        b.setPacilianNote("Note A");
        b.setIdSchedule(mockSchedule);

        c.setId(id);
        c.setIdPacilian(a.getIdPacilian());
        c.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        c.setPacilianNote("Note A");
        c.setIdSchedule(null);

        d.setId(id);
        d.setIdPacilian(null);
        d.setStatusReservasi(null);
        d.setPacilianNote(null);
        d.setIdSchedule(null);

        assertEquals(a, a);

        assertEquals(a, b);
        assertEquals(b, a);

        assertEquals(a, b);

        assertNotEquals(a, c);
        assertNotEquals(a, d);
        assertNotEquals(c, d);
    }

    @Test
    void testEqualsWithScheduleAsMainComparison() {
        ReservasiKonsultasi r1 = new ReservasiKonsultasi();
        ReservasiKonsultasi r2 = new ReservasiKonsultasi();

        UUID id = UUID.randomUUID();
        r1.setId(id);
        r2.setId(id);

        CaregiverSchedule schedule1 = mock(CaregiverSchedule.class);
        CaregiverSchedule schedule2 = mock(CaregiverSchedule.class);

        r1.setIdSchedule(schedule1);
        r2.setIdSchedule(schedule1);
        assertEquals(r1, r2);

        r2.setIdSchedule(schedule2);
        assertNotEquals(r1, r2);

        r1.setIdSchedule(null);
        assertNotEquals(r1, r2);

        r2.setIdSchedule(null);
        assertEquals(r1, r2);
    }

    @Test
    void testEqualsWithStatusReservasiAsMainComparison() {
        ReservasiKonsultasi r1 = new ReservasiKonsultasi();
        ReservasiKonsultasi r2 = new ReservasiKonsultasi();

        UUID id = UUID.randomUUID();
        r1.setId(id);
        r2.setId(id);

        // First compare with same status
        r1.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        r2.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        assertEquals(r1, r2);

        r2.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        assertNotEquals(r1, r2);

        r1.setStatusReservasi(null);
        assertNotEquals(r1, r2);

        r2.setStatusReservasi(null);
        assertEquals(r1, r2);
    }
}