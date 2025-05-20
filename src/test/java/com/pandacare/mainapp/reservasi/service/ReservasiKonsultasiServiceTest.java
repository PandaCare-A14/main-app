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

        // Create a caregiver schedule
        scheduleId = UUID.randomUUID();
        schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        // Set up test data
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
        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scheduleService.getById(any(UUID.class))).thenReturn(schedule);
        when(scheduleService.isScheduleAvailable(any(UUID.class))).thenReturn(true); // Add this line

        ReservasiKonsultasi result = service.requestReservasi(scheduleId, "pac123");

        assertNotNull(result);
        assertEquals("pac123", result.getIdPacilian());
        assertEquals(scheduleId, result.getIdSchedule().getId());
        assertEquals(StatusReservasiKonsultasi.WAITING, result.getStatusReservasi());

        verify(repository).save(any(ReservasiKonsultasi.class));
    }

    @Test
    void editReservasi_shouldUpdateReservasi_whenStatusIsWaiting() {
        when(repository.findById(reservationId)).thenReturn(Optional.of(waitingReservasi));

        String newDay = "TUESDAY";
        String newStartTime = "10:00";
        String newEndTime = "11:00";

        ReservasiKonsultasi updated = service.editReservasi(reservationId, newDay, newStartTime, newEndTime);

        assertEquals(StatusReservasiKonsultasi.WAITING, updated.getStatusReservasi());
        verify(repository).save(any(ReservasiKonsultasi.class));
    }

    @Test
    void editReservasi_shouldThrowException_whenStatusIsNotWaiting() {
        when(repository.findById("jadwal124")).thenReturn(Optional.of(approvedReservasi));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                service.editReservasi("jadwal124", "MONDAY", "09:00", "10:00")
        );

        assertEquals("Only schedules with status WAITING can be edited", ex.getMessage());
    }

    @Test
    void findAllByPasien_shouldReturnAllReservasiForGivenUser() {
        List<ReservasiKonsultasi> reservasiList = List.of(waitingReservasi, approvedReservasi);

        when(repository.findAllByIdPasien("pac123")).thenReturn(reservasiList);

        List<ReservasiKonsultasi> result = service.findAllByPasien("pac123");

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
    void acceptChangeReservasi_shouldApplyRequestedChanges() {
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setId(reservationId);
        reservasi.setIdPacilian("pac123");
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.ON_RESCHEDULE);
        reservasi.setChangeReservasi(true);
        reservasi.setIdSchedule(schedule); // Set the schedule object to prevent NPE

        when(repository.findById(reservationId)).thenReturn(Optional.of(reservasi));
        // For void methods, use doNothing() instead of when/thenReturn
        doNothing().when(scheduleService).updateScheduleStatus(any(UUID.class), any());

        ReservasiKonsultasi result = service.acceptChangeReservasi(reservationId);

        verify(repository).findById(reservationId);
        verify(repository).deleteById(reservationId);
        verify(scheduleService).updateScheduleStatus(schedule.getId(), ScheduleStatus.UNAVAILABLE);
    }

    @Test
    void rejectChangeReservasi_shouldClearChangeRequest() {
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setId(reservationId);
        reservasi.setIdPacilian("pac123");
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.ON_RESCHEDULE);
        reservasi.setChangeReservasi(true);
        reservasi.setIdSchedule(schedule);

        when(repository.findById(reservationId)).thenReturn(Optional.of(reservasi));

        service.rejectChangeReservasi(reservationId);

        verify(repository).findById(reservationId);
        verify(repository).deleteById(reservationId);
    }

    @Test
    void deleteById_shouldCallRepositoryDelete() {
        service.deleteById(reservationId);
        verify(repository).deleteById(reservationId);
    }
}