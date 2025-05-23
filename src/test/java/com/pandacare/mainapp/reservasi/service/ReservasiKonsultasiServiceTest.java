package com.pandacare.mainapp.reservasi.service;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReservasiKonsultasiServiceTest {

    @Mock
    private ReservasiKonsultasiRepository repository;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ReservasiKonsultasiServiceImpl service;

    private ReservasiKonsultasi waitingReservasi;
    private ReservasiKonsultasi approvedReservasi;
    private CaregiverSchedule schedule;
    private UUID scheduleId;
    private String reservationId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        scheduleId = UUID.randomUUID();
        schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        reservationId = "jadwal123";

        waitingReservasi = new ReservasiKonsultasi();
        waitingReservasi.setId(reservationId);
        waitingReservasi.setIdPacilian("pac123");
        waitingReservasi.setIdSchedule(schedule);
        waitingReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        approvedReservasi = new ReservasiKonsultasi();
        approvedReservasi.setId("jadwal124");
        approvedReservasi.setIdPacilian("pac123");
        approvedReservasi.setIdSchedule(schedule);
        approvedReservasi.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);

        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scheduleService.getById(any(UUID.class))).thenReturn(schedule);
    }

    @Test
    void requestReservasi_shouldReturnWaitingStatus() {
        UUID scheduleId = UUID.randomUUID();
        UUID caregiverId = UUID.randomUUID();

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setIdCaregiver(caregiverId);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(10, 0));
        schedule.setEndTime(LocalTime.of(11, 0));

        when(scheduleService.getById(scheduleId)).thenReturn(schedule);
        when(scheduleService.isScheduleAvailable(scheduleId)).thenReturn(true);

        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scheduleService.getById(any(UUID.class))).thenReturn(schedule);
        when(scheduleService.isScheduleAvailable(any(UUID.class))).thenReturn(true);

        ReservasiKonsultasi result = service.requestReservasi(scheduleId, "pac123");

        assertNotNull(result);
        assertEquals("pac123", result.getIdPacilian());
        assertEquals(scheduleId, result.getIdSchedule().getId());
        assertEquals(StatusReservasiKonsultasi.WAITING, result.getStatusReservasi());

        verify(repository).save(any(ReservasiKonsultasi.class));
    }

    @Test
    void editReservasi_shouldUpdateReservasi_whenStatusIsWaiting() {
        UUID newScheduleId = UUID.randomUUID();
        CaregiverSchedule newSchedule = new CaregiverSchedule();
        newSchedule.setId(newScheduleId);
        newSchedule.setDay(DayOfWeek.TUESDAY);
        newSchedule.setStartTime(LocalTime.of(10, 0));
        newSchedule.setEndTime(LocalTime.of(11, 0));
        newSchedule.setStatus(ScheduleStatus.AVAILABLE);

        when(repository.findById(reservationId)).thenReturn(Optional.of(waitingReservasi));
        when(scheduleService.getById(newScheduleId)).thenReturn(newSchedule);
        when(scheduleService.isScheduleAvailable(newScheduleId)).thenReturn(true);

        ReservasiKonsultasi updated = service.editReservasi(reservationId, newScheduleId);

        assertEquals(StatusReservasiKonsultasi.WAITING, updated.getStatusReservasi());
        assertEquals("TUESDAY", updated.getDay());
        assertEquals(LocalTime.of(10, 0), updated.getStartTime());
        assertEquals(LocalTime.of(11, 0), updated.getEndTime());
        assertEquals(newSchedule, updated.getIdSchedule());

        verify(repository).save(any(ReservasiKonsultasi.class));
    }

    @Test
    void editReservasi_shouldThrowException_whenScheduleNotAvailable() {
        UUID newScheduleId = UUID.randomUUID();
        when(repository.findById(reservationId)).thenReturn(Optional.of(waitingReservasi));
        when(scheduleService.getById(newScheduleId)).thenReturn(new CaregiverSchedule());
        when(scheduleService.isScheduleAvailable(newScheduleId)).thenReturn(false);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.editReservasi(reservationId, newScheduleId)
        );

        assertEquals("Jadwal baru tidak tersedia", ex.getMessage());
    }

    @Test
    void editReservasi_shouldThrowException_whenReservasiNotFound() {
        UUID newScheduleId = UUID.randomUUID();
        when(repository.findById("nonexistent")).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.editReservasi("nonexistent", newScheduleId)
        );

        assertEquals("Reservasi tidak ditemukan", ex.getMessage());
    }

    @Test
    void editReservasi_shouldThrowException_whenStatusIsNotWaiting() {
        when(repository.findById("jadwal124")).thenReturn(Optional.of(approvedReservasi));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                service.editReservasi("jadwal124", UUID.randomUUID())
        );

        assertEquals("Tidak bisa mengedit reservasi yang sudah disetujui.", ex.getMessage());
    }

    @Test
    void acceptChangeReservasi_shouldApplyRequestedChanges() {
        CaregiverSchedule currentSchedule = schedule;

        // Create proposed schedule (Monday 10-11)
        UUID proposedScheduleId = UUID.randomUUID();
        CaregiverSchedule proposedSchedule = new CaregiverSchedule();
        proposedSchedule.setId(proposedScheduleId);
        proposedSchedule.setDay(DayOfWeek.MONDAY);
        proposedSchedule.setStartTime(LocalTime.of(10, 0));
        proposedSchedule.setEndTime(LocalTime.of(11, 0));
        proposedSchedule.setStatus(ScheduleStatus.AVAILABLE);

        // Create reservation with ON_RESCHEDULE status
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setId(reservationId);
        reservasi.setIdPacilian("pac123");
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.ON_RESCHEDULE);
        reservasi.setIdSchedule(currentSchedule);
        reservasi.setProposedSchedule(proposedSchedule); // Set the proposed schedule

        // Mock repository behavior
        when(repository.findById(reservationId)).thenReturn(Optional.of(reservasi));
        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the service method
        ReservasiKonsultasi result = service.acceptChangeReservasi(reservationId);

        // Verify the result
        assertEquals(StatusReservasiKonsultasi.WAITING, result.getStatusReservasi());
        assertEquals(proposedSchedule, result.getIdSchedule()); // Schedule should be updated to proposed schedule
        assertNull(result.getProposedSchedule()); // Proposed schedule should be cleared after accepting

        verify(repository).findById(reservationId);
        verify(repository).save(any(ReservasiKonsultasi.class));
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    void rejectChangeReservasi_shouldSetStatusToRejected() {
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setId(reservationId);
        reservasi.setIdPacilian("pac123");
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.ON_RESCHEDULE);
        reservasi.setIdSchedule(schedule);

        when(repository.findById(reservationId)).thenReturn(Optional.of(reservasi));
        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.rejectChangeReservasi(reservationId);

        assertEquals(StatusReservasiKonsultasi.REJECTED, reservasi.getStatusReservasi());

        verify(repository).findById(reservationId);
        verify(repository).save(reservasi);
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    void findAllByPasien_shouldReturnAllReservasiForGivenUser() {
        List<ReservasiKonsultasi> reservasiList = List.of(waitingReservasi, approvedReservasi);

        when(repository.findAllByIdPasien("pac123")).thenReturn(reservasiList);

        CompletableFuture<List<ReservasiKonsultasi>> futureResult = service.findAllByPasien("pac123");

        // Wait for and get the result
        List<ReservasiKonsultasi> result;
        try {
            result = futureResult.get();
        } catch (InterruptedException | ExecutionException e) {
            fail("Exception while getting future result: " + e.getMessage());
            return;
        }

        assertEquals(2, result.size());
        assertEquals("pac123", result.get(0).getIdPacilian());
        verify(repository).findAllByIdPasien("pac123");
    }

    @Test
    void findById_shouldReturnReservation() {
        when(repository.findById(reservationId)).thenReturn(Optional.of(waitingReservasi));

        ReservasiKonsultasi result = service.findById(reservationId);

        assertNotNull(result);
        assertEquals(reservationId, result.getId());
        verify(repository).findById(reservationId);
    }

    @Test
    void findById_shouldThrowWhenNotExists() {
        String nonExistingId = "nonexistent";
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.findById(nonExistingId));

        assertEquals("Schedule not found", exception.getMessage());
        verify(repository).findById(nonExistingId);
    }

    @Test
    void deleteById_shouldCallRepositoryDelete() {
        service.deleteById(reservationId);
        verify(repository).deleteById(reservationId);
    }
}