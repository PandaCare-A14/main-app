package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaregiverReservationServiceImplTest {
    @Mock
    private ReservasiKonsultasiRepository reservasiRepository;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private CaregiverReservationServiceImpl service;

    private ReservasiKonsultasi reservation;
    private CaregiverSchedule schedule;
    private final String reservationId = "RES12345";
    private UUID caregiverId;

    @BeforeEach
    void setUp() {
        UUID scheduleId = UUID.randomUUID();
        caregiverId = UUID.randomUUID();

        schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setIdCaregiver(caregiverId);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        reservation = new ReservasiKonsultasi();
        reservation.setIdReservasi(reservationId);
        reservation.setIdPacilian("PAT12345");
        reservation.setIdSchedule(schedule);
        reservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
    }

    @Test
    void testGetReservationsForCaregiver() {
        when(reservasiRepository.findByCaregiverId(caregiverId)).thenReturn(List.of(reservation));

        List<ReservasiKonsultasi> result = service.getReservationsForCaregiver(caregiverId);

        assertEquals(1, result.size());
        assertEquals(reservationId, result.getFirst().getIdReservasi());
        verify(reservasiRepository).findByCaregiverId(caregiverId);
    }

    @Test
    void testGetReservationsByCaregiverAndStatus() {
        when(reservasiRepository.findByCaregiverIdAndStatus(caregiverId, StatusReservasiKonsultasi.WAITING))
                .thenReturn(List.of(reservation));

        List<ReservasiKonsultasi> result = service.getReservationsByCaregiverAndStatus(
                caregiverId, StatusReservasiKonsultasi.WAITING);

        assertEquals(1, result.size());
        assertEquals(reservationId, result.getFirst().getIdReservasi());
        assertEquals(StatusReservasiKonsultasi.WAITING, result.getFirst().getStatusReservasi());
        verify(reservasiRepository).findByCaregiverIdAndStatus(caregiverId, StatusReservasiKonsultasi.WAITING);
    }

    @Test
    void testGetReservationsByCaregiverAndDay() {
        when(reservasiRepository.findByCaregiverIdAndDay(caregiverId, DayOfWeek.MONDAY))
                .thenReturn(List.of(reservation));

        List<ReservasiKonsultasi> result = service.getReservationsByCaregiverAndDay(
                caregiverId, DayOfWeek.MONDAY);

        assertEquals(1, result.size());
        assertEquals(reservationId, result.getFirst().getIdReservasi());
        assertEquals(DayOfWeek.MONDAY, result.getFirst().getIdSchedule().getDay());
        verify(reservasiRepository).findByCaregiverIdAndDay(caregiverId, DayOfWeek.MONDAY);
    }

    @Test
    void testGetWaitingReservations() {
        when(reservasiRepository.findByCaregiverIdAndStatus(caregiverId, StatusReservasiKonsultasi.WAITING))
                .thenReturn(List.of(reservation));

        List<ReservasiKonsultasi> result = service.getWaitingReservations(caregiverId);

        assertEquals(1, result.size());
        assertEquals(reservationId, result.getFirst().getIdReservasi());
        verify(reservasiRepository).findByCaregiverIdAndStatus(caregiverId, StatusReservasiKonsultasi.WAITING);
    }

    @Test
    void testApproveReservation() {
        when(reservasiRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        doNothing().when(scheduleService).updateScheduleStatus(schedule, ScheduleStatus.UNAVAILABLE);
        when(reservasiRepository.save(any(ReservasiKonsultasi.class))).thenReturn(reservation);

        ReservasiKonsultasi result = service.approveReservation(reservationId);

        assertEquals(reservationId, result.getIdReservasi());
        assertEquals(StatusReservasiKonsultasi.APPROVED, result.getStatusReservasi());
        verify(reservasiRepository).findById(reservationId);
        verify(scheduleService).updateScheduleStatus(schedule, ScheduleStatus.UNAVAILABLE);
        verify(reservasiRepository).save(reservation);
    }

    @Test
    void testRejectReservation() {
        when(reservasiRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        doNothing().when(scheduleService).updateScheduleStatus(schedule, ScheduleStatus.AVAILABLE);
        when(reservasiRepository.save(any(ReservasiKonsultasi.class))).thenReturn(reservation);

        ReservasiKonsultasi result = service.rejectReservation(reservationId);

        assertEquals(reservationId, result.getIdReservasi());
        assertEquals(StatusReservasiKonsultasi.REJECTED, result.getStatusReservasi());
        verify(reservasiRepository).findById(reservationId);
        verify(scheduleService).updateScheduleStatus(schedule, ScheduleStatus.AVAILABLE);
        verify(reservasiRepository).save(reservation);
    }

    @Test
    void testChangeSchedule() {
        UUID newScheduleId = UUID.randomUUID();
        CaregiverSchedule newSchedule = new CaregiverSchedule();
        newSchedule.setId(newScheduleId);

        when(reservasiRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(scheduleService.getById(newScheduleId)).thenReturn(newSchedule);
        when(reservasiRepository.save(any(ReservasiKonsultasi.class))).thenReturn(reservation);

        ReservasiKonsultasi result = service.changeSchedule(reservationId, newScheduleId);

        assertEquals(reservationId, result.getIdReservasi());
        verify(scheduleService).updateScheduleStatus(schedule, ScheduleStatus.AVAILABLE);
        verify(scheduleService).updateScheduleStatus(newSchedule, ScheduleStatus.UNAVAILABLE);
        verify(reservasiRepository).save(any(ReservasiKonsultasi.class));
        verify(reservasiRepository, never()).saveAndFlush(any());
    }

    @Test
    void testGetReservationOrThrowFound() {
        when(reservasiRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        ReservasiKonsultasi result = service.getReservationOrThrow(reservationId);

        assertEquals(reservationId, result.getIdReservasi());
        verify(reservasiRepository).findById(reservationId);
    }

    @Test
    void testGetReservationOrThrowNotFound() {
        String nonExistentId = "NON_EXISTENT";
        when(reservasiRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> service.getReservationOrThrow(nonExistentId));

        assertTrue(exception.getMessage().contains("not found"));
        verify(reservasiRepository).findById(nonExistentId);
    }

    @Test
    void testApproveReservationInvalidStatus() {
        reservation.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        when(reservasiRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        Exception exception = assertThrows(IllegalStateException.class,
                () -> service.approveReservation(reservationId));

        assertTrue(exception.getMessage().contains("Cannot approve reservation with status"));
        verify(reservasiRepository).findById(reservationId);
        verify(scheduleService, never()).updateScheduleStatus(eq(schedule), any(ScheduleStatus.class));
    }
}