package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.repository.JadwalKonsultasiRepository;
import com.pandacare.mainapp.konsultasi_dokter.model.state.AvailableState;
import com.pandacare.mainapp.konsultasi_dokter.model.state.RequestedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JadwalDokterServiceImplTest {
    @Mock private JadwalKonsultasiRepository repository;

    private JadwalDokterService service;

    private final String idDokter = "DOC-12345";
    private final String idPasien = "PAT-67890";
    private final String jadwalId = "JADWAL-001";
    private final LocalDate date = LocalDate.parse("2025-05-06");
    private final LocalTime start = LocalTime.parse("10:00");
    private final LocalTime end = LocalTime.parse("11:00");

    private JadwalKonsultasi jadwal;

    @BeforeEach
    void setUp() {
        service = new JadwalDokterServiceImpl(repository);
        jadwal = new JadwalKonsultasi();
        jadwal.setId(jadwalId);
        jadwal.setIdDokter(idDokter);
        jadwal.setDate(date);
        jadwal.setStartTime(start);
        jadwal.setEndTime(end);
    }

    @Test
    void createJadwalSuccess() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JadwalKonsultasi result = service.createJadwal(idDokter, date, start, end);

        assertNotNull(result);
        assertEquals(idDokter, result.getIdDokter());
        assertEquals(date, result.getDate());
        assertEquals(start, result.getStartTime());
        assertEquals(end, result.getEndTime());
        assertEquals("AVAILABLE", result.getStatusDokter());

        verify(repository).save(result);
    }

    @Test
    void changeJadwalSuccess() {
        jadwal.setState(new RequestedState());
        LocalDate newDate = LocalDate.parse("2025-05-10");
        LocalTime newStart = LocalTime.parse("14:00");
        LocalTime newEnd = LocalTime.parse("15:00");
        String message = "Schedule changed";

        when(repository.findById(jadwalId)).thenReturn(jadwal);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        boolean result = service.changeJadwal(jadwalId, newDate, newStart, newEnd, message);

        assertTrue(result);
        assertEquals(newDate, jadwal.getDate());
        assertEquals(newStart, jadwal.getStartTime());
        assertEquals(newEnd, jadwal.getEndTime());
        assertEquals(message, jadwal.getMessage());
        assertTrue(jadwal.isChangeSchedule());
    }

    @Test
    void changeJadwalNotFound() {
        LocalDate date = LocalDate.parse("2025-05-10");
        LocalTime startTime = LocalTime.parse("14:00");
        LocalTime endTime = LocalTime.parse("15:00");

        when(repository.findById(jadwalId)).thenReturn(null);
        assertFalse(service.changeJadwal(jadwalId, date, startTime, endTime, ""));
        verify(repository, never()).save(any());
    }

    @Test
    void approveJadwalSuccess() {
        when(repository.findById(jadwalId)).thenReturn(jadwal);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        jadwal.setState(new RequestedState());
        assertTrue(service.approveJadwal(jadwalId));
    }

    @Test
    void approveJadwalNotFound() {
        when(repository.findById(jadwalId)).thenReturn(null);
        assertFalse(service.approveJadwal(jadwalId));
    }

    @Test
    void rejectJadwalSuccess() {
        jadwal.setState(new RequestedState());
        when(repository.findById(jadwalId)).thenReturn(jadwal);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        jadwal.setState(new RequestedState());
        assertTrue(service.rejectJadwal(jadwalId));
        assertEquals("Jadwal tidak sesuai", jadwal.getMessage());
    }

    @Test
    void rejectJadwalNotFound() {
        when(repository.findById(jadwalId)).thenReturn(null);
        assertFalse(service.rejectJadwal(jadwalId));
    }

    @Test
    void findByIdDokterReturnsList() {
        when(repository.findByIdDokter(idDokter)).thenReturn(Arrays.asList(jadwal, jadwal));
        List<JadwalKonsultasi> result = service.findByIdDokter(idDokter);
        assertEquals(2, result.size());
    }

    @Test
    void findByIdDokterEmpty() {
        when(repository.findByIdDokter(idDokter)).thenReturn(Collections.emptyList());
        assertTrue(service.findByIdDokter(idDokter).isEmpty());
    }

    @Test
    void findByIdDokterAndStatusMatch() {
        jadwal.setState(new AvailableState());
        JadwalKonsultasi other = new JadwalKonsultasi();
        other.setIdDokter(idDokter);
        other.setState(new RequestedState());

        when(repository.findByIdDokter(idDokter)).thenReturn(Arrays.asList(jadwal, other));

        List<JadwalKonsultasi> result = service.findByIdDokterAndStatus(idDokter, "AVAILABLE");
        assertEquals(1, result.size());
        assertEquals("AVAILABLE", result.getFirst().getStatusDokter());
    }

    @Test
    void findByIdDokterAndStatusNoMatch() {
        jadwal.setState(new AvailableState());
        when(repository.findByIdDokter(idDokter)).thenReturn(Collections.singletonList(jadwal));
        assertTrue(service.findByIdDokterAndStatus(idDokter, "REJECTED").isEmpty());
    }

    @Test
    void findByIdFound() {
        when(repository.findById(jadwalId)).thenReturn(jadwal);
        assertEquals(jadwal, service.findById(jadwalId));
    }

    @Test
    void findByIdNotFound() {
        when(repository.findById(jadwalId)).thenReturn(null);
        assertNull(service.findById(jadwalId));
    }

    @Test
    void createJadwalIntervalSuccess() {
        LocalDate testDate = LocalDate.parse("2025-05-06");
        LocalTime testStart = LocalTime.parse("10:00");
        LocalTime testEnd = LocalTime.parse("12:00");
        int testDuration = 30;

        when(repository.save(any(JadwalKonsultasi.class)))
                .thenAnswer(invocation -> {
                    JadwalKonsultasi jadwal = invocation.getArgument(0);
                    if (jadwal.getId() == null) {
                        jadwal.setId(UUID.randomUUID().toString());
                    }
                    return jadwal;
                });

        List<JadwalKonsultasi> result = service.createJadwalInterval(idDokter, testDate, testStart, testEnd);

        assertEquals(4, result.size());

        assertEquals(idDokter, result.getFirst().getIdDokter());
        assertEquals(testDate, result.getFirst().getDate());
        assertEquals(testStart, result.getFirst().getStartTime());
        assertEquals(testStart.plusMinutes(testDuration), result.get(0).getEndTime());
        assertEquals("AVAILABLE", result.get(0).getStatusDokter());

        assertEquals(idDokter, result.get(3).getIdDokter());
        assertEquals(testDate, result.get(3).getDate());
        assertEquals(testStart.plusMinutes(testDuration * 3), result.get(3).getStartTime());
        assertEquals(testEnd, result.get(3).getEndTime());
        assertEquals("AVAILABLE", result.get(3).getStatusDokter());

        verify(repository, times(4)).save(any(JadwalKonsultasi.class));
    }


    @Test
    void createJadwalIntervalWithInvalidTime() {
        LocalDate testDate = LocalDate.parse("2025-05-06");
        LocalTime testStart = LocalTime.parse("12:00");
        LocalTime testEnd = LocalTime.parse("10:00");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createJadwalInterval(idDokter, testDate, testStart, testEnd);
        });

        assertTrue(exception.getMessage().contains("Waktu mulai tidak boleh lebih setelah waktu selesai") ||
                exception.getMessage().contains("Waktu selesai tidak boleh lebih awal dari waktu mulai"));

        verify(repository, never()).save(any(JadwalKonsultasi.class));
    }

    @Test
    void createJadwalIntervalWithOverlap() {
        LocalDate testDate = LocalDate.parse("2025-05-06");
        LocalTime testStart = LocalTime.parse("10:00");
        LocalTime testEnd = LocalTime.parse("12:00");

        JadwalKonsultasi existingJadwal = new JadwalKonsultasi();
        existingJadwal.setId("existing-jadwal");
        existingJadwal.setIdDokter(idDokter);
        existingJadwal.setDate(testDate);
        existingJadwal.setStartTime(LocalTime.parse("11:00"));
        existingJadwal.setEndTime(LocalTime.parse("11:30"));

        when(repository.findOverlappingSchedule(eq(idDokter), eq(testDate), eq(testStart), eq(testEnd)))
                .thenReturn(Collections.singletonList(existingJadwal));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createJadwalInterval(idDokter, testDate, testStart, testEnd);
        });

        // Sesuaikan dengan pesan error yang ada di JadwalDokterServiceImpl
        assertTrue(exception.getMessage().contains("Jadwal sudah ada pada waktu yang sama"));

        verify(repository, never()).save(any(JadwalKonsultasi.class));
    }
}