package com.pandacare.mainapp.reservasi.service;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
        waitingReservasi.setStatusPacilian(StatusReservasiKonsultasi.WAITING);

        approvedReservasi = new ReservasiKonsultasi();
        approvedReservasi.setId("jadwal124");
        approvedReservasi.setIdDokter("dok123");
        approvedReservasi.setDay("Rabu");
        approvedReservasi.setStartTime("13:00");
        approvedReservasi.setEndTime("14:00");
        approvedReservasi.setStatusPacilian(StatusReservasiKonsultasi.APPROVED);

        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void requestReservasi_shouldReturnWaitingStatus() {
        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> {
            ReservasiKonsultasi savedJadwal = invocation.getArgument(0);
            return savedJadwal;
        });

        ReservasiKonsultasi result = service.requestReservasi("dok123","pac123","Senin", "09:00", "10:00");

        assertNotNull(result);
        assertEquals("dok123", result.getIdDokter());
        assertEquals("Senin", result.getDay());
        assertEquals("09:00", result.getStartTime());
        assertEquals("10:00", result.getEndTime());
        assertEquals(StatusReservasiKonsultasi.WAITING, result.getStatusPacilian());

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
        // Setup mock to return waitingJadwal when findById is called
        when(repository.findById("jadwal123")).thenReturn(Optional.of(waitingReservasi));

        ReservasiKonsultasi updated = service.editReservasi("jadwal123", "Selasa", "10:00", "11:00");

        assertEquals("Selasa", updated.getDay());
        assertEquals("10:00", updated.getStartTime());
        assertEquals("11:00", updated.getEndTime());
        assertEquals(StatusReservasiKonsultasi.WAITING, updated.getStatusPacilian());
    }

    @Test
    void editReservasi_shouldThrowException_whenStatusIsNotWaiting() {
        // Setup mock to return approvedJadwal when findById is called
        when(repository.findById("jadwal124")).thenReturn(Optional.of(approvedReservasi));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                service.editReservasi("jadwal124", "Senin", "09:00", "10:00")
        );

        assertEquals("Only schedules with status WAITING can be edited", ex.getMessage());
    }

    @Test
    void acceptChangeReservasi_shouldApplyRequestedChange() {
        ReservasiKonsultasi jadwal = new ReservasiKonsultasi();
        jadwal.setId("jadwal123");
        jadwal.setDay("Selasa");
        jadwal.setStartTime("10:00");
        jadwal.setEndTime("11:00");
        jadwal.setStatusPacilian(StatusReservasiKonsultasi.WAITING);
        jadwal.setChangeSchedule(true);
        jadwal.setNewDay("Kamis");
        jadwal.setNewStartTime("15:00");
        jadwal.setNewEndTime("16:00");

        // Setup mock
        when(repository.findById("jadwal123")).thenReturn(Optional.of(jadwal));

        ReservasiKonsultasi result = service.acceptChangeSchedule("jadwal123");

        assertEquals("Kamis", result.getDay());
        assertEquals("15:00", result.getStartTime());
        assertEquals("16:00", result.getEndTime());

        assertNotNull(result);
        assertFalse(result.isChangeSchedule(), "State changeSchedule set to false if accepted");
        assertEquals(StatusReservasiKonsultasi.WAITING, result.getStatusPacilian(), "Status should remain WAITING");
    }

    @Test
    void acceptChangeReservasi_shouldThrowException_whenChangeReservasiIsFalse() {
        ReservasiKonsultasi noChangeRequestJadwal = new ReservasiKonsultasi();
        noChangeRequestJadwal.setId("jadwal125");
        noChangeRequestJadwal.setDay("Rabu");
        noChangeRequestJadwal.setStartTime("13:00");
        noChangeRequestJadwal.setEndTime("14:00");
        noChangeRequestJadwal.setStatusPacilian(StatusReservasiKonsultasi.WAITING);
        noChangeRequestJadwal.setChangeSchedule(false);

        // Setup mock
        when(repository.findById("jadwal125")).thenReturn(Optional.of(noChangeRequestJadwal));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                service.acceptChangeSchedule("jadwal125")
        );

        assertEquals("No change request exists for this schedule", ex.getMessage());
    }

    @Test
    void rejectChangeReservasi_shouldDeleteReservasi() {
        ReservasiKonsultasi jadwal = new ReservasiKonsultasi();
        jadwal.setId("jadwal127");
        jadwal.setDay("Selasa");
        jadwal.setStartTime("10:00");
        jadwal.setEndTime("11:00");
        jadwal.setStatusPacilian(StatusReservasiKonsultasi.WAITING);
        jadwal.setChangeSchedule(true);

        // Setup mock
        when(repository.findById("jadwal127")).thenReturn(Optional.of(jadwal));
        doNothing().when(repository).deleteById("jadwal127");

        service.rejectChangeSchedule("jadwal127");

        // Verify the deleteById method was called
        verify(repository).deleteById("jadwal127");
    }
}