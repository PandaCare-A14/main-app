package com.pandacare.mainapp.reservasi.service;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReservasiKonsultasiServiceTest {

    @Mock
    private ReservasiKonsultasiRepository repository;

    @InjectMocks
    private ReservasiKonsultasiServiceImpl service;

    private ReservasiKonsultasi waitingReservasi;
    private ReservasiKonsultasi approvedReservasi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        waitingReservasi = new ReservasiKonsultasi();
        waitingReservasi.setId("jadwal123");
        waitingReservasi.setIdDokter("dok123");
        waitingReservasi.setDay("Senin");
        waitingReservasi.setStartTime("09:00");
        waitingReservasi.setEndTime("10:00");
        waitingReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        approvedReservasi = new ReservasiKonsultasi();
        approvedReservasi.setId("jadwal124");
        approvedReservasi.setIdDokter("dok123");
        approvedReservasi.setDay("Rabu");
        approvedReservasi.setStartTime("13:00");
        approvedReservasi.setEndTime("14:00");
        approvedReservasi.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);

        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void requestReservasi_shouldReturnWaitingStatus() {
        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> {
            ReservasiKonsultasi savedReservasi = invocation.getArgument(0);
            return savedReservasi;
        });

        ReservasiKonsultasi result = service.requestReservasi("dok123","pac123","Senin", "09:00", "10:00");

        assertNotNull(result);
        assertEquals("dok123", result.getIdDokter());
        assertEquals("Senin", result.getDay());
        assertEquals("09:00", result.getStartTime());
        assertEquals("10:00", result.getEndTime());
        assertEquals(StatusReservasiKonsultasi.WAITING, result.getStatusReservasi());

        verify(repository).save(any(ReservasiKonsultasi.class));
    }

    @Test
    void requestReservasi_shouldThrowException_whenStartTimeAfterEndTime() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.requestReservasi("dok123","pac123","Senin", "14:00", "10:00")
        );
        assertEquals("Start time must be before end time", exception.getMessage());
    }

    @Test
    void editReservasi_shouldUpdateReservasi_whenStatusIsWaiting() {
        when(repository.findById("jadwal123")).thenReturn(Optional.of(waitingReservasi));

        ReservasiKonsultasi updated = service.editReservasi("jadwal123", "Selasa", "10:00", "11:00");

        assertEquals("Selasa", updated.getDay());
        assertEquals("10:00", updated.getStartTime());
        assertEquals("11:00", updated.getEndTime());
        assertEquals(StatusReservasiKonsultasi.WAITING, updated.getStatusReservasi());
    }

    @Test
    void editReservasi_shouldThrowException_whenStatusIsNotWaiting() {
        when(repository.findById("jadwal124")).thenReturn(Optional.of(approvedReservasi));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                service.editReservasi("jadwal124", "Senin", "09:00", "10:00")
        );

        assertEquals("Only schedules with status WAITING can be edited", ex.getMessage());
    }

    @Test
    void acceptChangeReservasi_shouldApplyRequestedChange() {
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setId("jadwal123");
        reservasi.setDay("Selasa");
        reservasi.setStartTime("10:00");
        reservasi.setEndTime("11:00");
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        reservasi.setChangeReservasi(true);
        reservasi.setNewDay("Kamis");
        reservasi.setNewStartTime("15:00");
        reservasi.setNewEndTime("16:00");

        // Setup mock
        when(repository.findById("jadwal123")).thenReturn(Optional.of(reservasi));

        ReservasiKonsultasi result = service.acceptChangeReservasi("jadwal123");

        assertEquals("Kamis", result.getDay());
        assertEquals("15:00", result.getStartTime());
        assertEquals("16:00", result.getEndTime());

        assertNotNull(result);
        assertFalse(result.isChangeReservasi(), "State changeSchedule set to false if accepted");
        assertEquals(StatusReservasiKonsultasi.WAITING, result.getStatusReservasi(), "Status should remain WAITING");
    }

    @Test
    void acceptChangeReservasi_shouldThrowException_whenChangeReservasiIsFalse() {
        ReservasiKonsultasi noChangeRequestReservasi = new ReservasiKonsultasi();
        noChangeRequestReservasi.setId("jadwal125");
        noChangeRequestReservasi.setDay("Rabu");
        noChangeRequestReservasi.setStartTime("13:00");
        noChangeRequestReservasi.setEndTime("14:00");
        noChangeRequestReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        noChangeRequestReservasi.setChangeReservasi(false);

        // Setup mock
        when(repository.findById("jadwal125")).thenReturn(Optional.of(noChangeRequestReservasi));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                service.acceptChangeReservasi("jadwal125")
        );

        assertEquals("No change request exists for this schedule", ex.getMessage());
    }

    @Test
    void rejectChangeReservasi_shouldThrowException_whenChangeReservasiIsFalse() {
        ReservasiKonsultasi noChangeRequestReservasi = new ReservasiKonsultasi();
        noChangeRequestReservasi.setId("jadwal125");
        noChangeRequestReservasi.setDay("Rabu");
        noChangeRequestReservasi.setStartTime("13:00");
        noChangeRequestReservasi.setEndTime("14:00");
        noChangeRequestReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        noChangeRequestReservasi.setChangeReservasi(false);

        // Setup mock
        when(repository.findById("jadwal125")).thenReturn(Optional.of(noChangeRequestReservasi));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                service.rejectChangeReservasi("jadwal125")
        );

        assertEquals("No change request exists for this schedule", ex.getMessage());
    }

    @Test
    void rejectChangeReservasi_shouldDeleteReservasi() {
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setId("jadwal127");
        reservasi.setDay("Selasa");
        reservasi.setStartTime("10:00");
        reservasi.setEndTime("11:00");
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        reservasi.setChangeReservasi(true);

        // Setup mock
        when(repository.findById("jadwal127")).thenReturn(Optional.of(reservasi));
        doNothing().when(repository).deleteById("jadwal127");

        service.rejectChangeReservasi("jadwal127");

        // Verify the deleteById method was called
        verify(repository).deleteById("jadwal127");
    }

    @Test
    void findAllByPasien_shouldReturnAllReservasiForGivenUser() {
        List<ReservasiKonsultasi> reservasiList = List.of(waitingReservasi, approvedReservasi);

        when(repository.findAllByIdPasien("pac123")).thenReturn(reservasiList);

        List<ReservasiKonsultasi> result = service.findAllByPasien("pac123");

        assertEquals(2, result.size());
        assertEquals("dok123", result.get(0).getIdDokter());
        verify(repository).findAllByIdPasien("pac123");
    }

    @Test
    void editReservasi_shouldThrowException_ifReservasiNotFound() {
        when(repository.findById("unknown")).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.editReservasi("unknown", "Senin", "09:00", "10:00")
        );

        assertEquals("Schedule not found", ex.getMessage());
    }

    @Test
    void acceptChangeReservasi_shouldThrowException_ifReservasiNotFound() {
        when(repository.findById("not_found")).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.acceptChangeReservasi("not_found")
        );

        assertEquals("Schedule not found", ex.getMessage());
    }

    @Test
    void rejectChangeReservasi_shouldThrowException_ifReservasiNotFound() {
        when(repository.findById("not_exist")).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.rejectChangeReservasi("not_exist")
        );

        assertEquals("Schedule not found", ex.getMessage());
    }
}