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
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
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
    private UUID reservationId;
    private UUID pacilianId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        scheduleId = UUID.randomUUID();
        pacilianId = UUID.randomUUID();
        reservationId = UUID.randomUUID();

        schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        waitingReservasi = new ReservasiKonsultasi();
        waitingReservasi.setId(reservationId);
        waitingReservasi.setIdPacilian(pacilianId);
        waitingReservasi.setIdSchedule(schedule);
        waitingReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        approvedReservasi = new ReservasiKonsultasi();
        approvedReservasi.setId(UUID.randomUUID());
        approvedReservasi.setIdPacilian(pacilianId);
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

        ReservasiKonsultasi result = service.requestReservasi(scheduleId, pacilianId);

        assertNotNull(result);
        assertEquals(pacilianId, result.getIdPacilian());
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
        when(repository.findById(null)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.editReservasi(UUID.randomUUID(), newScheduleId)
        );

        assertEquals("Reservasi tidak ditemukan", ex.getMessage());
    }

    @Test
    void editReservasi_shouldThrowException_whenStatusIsNotWaiting() {
        when(repository.findById(approvedReservasi.getId())).thenReturn(Optional.of(approvedReservasi));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                service.editReservasi(approvedReservasi.getId(), UUID.randomUUID())
        );

        assertEquals("Tidak bisa mengedit reservasi yang sudah disetujui.", ex.getMessage());
    }

    @Test
    void rejectChangeReservasi_shouldSetStatusToRejected() {
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setId(reservationId);
        reservasi.setIdPacilian(pacilianId);
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.ON_RESCHEDULE);
        reservasi.setIdSchedule(schedule);

        when(repository.findById(reservationId)).thenReturn(Optional.of(reservasi));
        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.rejectChangeReservasi(reservationId);

        assertEquals(StatusReservasiKonsultasi.REJECTED, reservasi.getStatusReservasi());

        verify(repository).findById(reservationId);
        verify(repository).save(reservasi);
        verify(repository, never()).deleteById(UUID.randomUUID());
    }

    @Test
    void findAllByPasien_shouldReturnAllReservasiForGivenUser() {
        List<ReservasiKonsultasi> reservasiList = List.of(waitingReservasi, approvedReservasi);

        when(repository.findAllByIdPasien(pacilianId)).thenReturn(reservasiList);

        List<ReservasiKonsultasi> result = service.findAllByPasien(pacilianId);

        assertEquals(2, result.size());
        assertEquals(pacilianId, result.get(0).getIdPacilian());
        verify(repository).findAllByIdPasien(pacilianId);
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
        UUID nonExistingId = UUID.randomUUID();
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