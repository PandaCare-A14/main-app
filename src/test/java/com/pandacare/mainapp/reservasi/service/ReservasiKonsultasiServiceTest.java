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

    private ReservasiKonsultasi waitingJadwal;
    private ReservasiKonsultasi approvedJadwal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        waitingJadwal = new ReservasiKonsultasi();
        waitingJadwal.setId("jadwal123");
        waitingJadwal.setIdDokter("dok123");
        waitingJadwal.setDay("Senin");
        waitingJadwal.setStartTime("09:00");
        waitingJadwal.setEndTime("10:00");
        waitingJadwal.setStatusPacilian(StatusReservasiKonsultasi.WAITING);

        approvedJadwal = new ReservasiKonsultasi();
        approvedJadwal.setId("jadwal124");
        approvedJadwal.setIdDokter("dok123");
        approvedJadwal.setDay("Rabu");
        approvedJadwal.setStartTime("13:00");
        approvedJadwal.setEndTime("14:00");
        approvedJadwal.setStatusPacilian(StatusReservasiKonsultasi.APPROVED);

        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void requestJadwal_shouldReturnWaitingStatus() {
        when(repository.save(any(ReservasiKonsultasi.class))).thenAnswer(invocation -> {
            ReservasiKonsultasi savedJadwal = invocation.getArgument(0);
            return savedJadwal;
        });

        ReservasiKonsultasi result = service.requestJadwal("dok123","pac123","Senin", "09:00", "10:00");

        assertNotNull(result);
        assertEquals("dok123", result.getIdDokter());
        assertEquals("Senin", result.getDay());
        assertEquals("09:00", result.getStartTime());
        assertEquals("10:00", result.getEndTime());
        assertEquals(StatusReservasiKonsultasi.WAITING, result.getStatusPacilian());

        verify(repository).save(any(ReservasiKonsultasi.class));
    }

    @Test
    void requestJadwal_shouldThrowException_whenStartTimeAfterEndTime() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.requestJadwal("dok123","pac123","Senin", "14:00", "10:00")
        );
        assertEquals("Start time must be before end time", exception.getMessage());
    }

    @Test
    void editSchedule_shouldUpdateSchedule_whenStatusIsWaiting() {
        // Setup mock to return waitingJadwal when findById is called
        when(repository.findById("jadwal123")).thenReturn(Optional.of(waitingJadwal));

        ReservasiKonsultasi updated = service.editSchedule("jadwal123", "Selasa", "10:00", "11:00");

        assertEquals("Selasa", updated.getDay());
        assertEquals("10:00", updated.getStartTime());
        assertEquals("11:00", updated.getEndTime());
        assertEquals(StatusReservasiKonsultasi.WAITING, updated.getStatusPacilian());
    }

    @Test
    void editSchedule_shouldThrowException_whenStatusIsNotWaiting() {
        // Setup mock to return approvedJadwal when findById is called
        when(repository.findById("jadwal124")).thenReturn(Optional.of(approvedJadwal));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                service.editSchedule("jadwal124", "Senin", "09:00", "10:00")
        );

        assertEquals("Only schedules with status WAITING can be edited", ex.getMessage());
    }

    @Test
    void acceptChangeSchedule_shouldApplyRequestedChange() {
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
    void acceptChangeSchedule_shouldThrowException_whenChangeScheduleIsFalse() {
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
    void rejectChangeSchedule_shouldDeleteSchedule() {
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