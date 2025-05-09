package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateIntervalStrategyTest {
    private CreateIntervalStrategy intervalStrategy;
    private String idDokter;
    private LocalDate date;
    private static final int DURATION_MINUTES = 30;

    @BeforeEach
    void setUp() {
        intervalStrategy = new CreateIntervalStrategy();
        idDokter = "doctor-123";
        date = LocalDate.now().plusDays(1);
    }

    @Test
    void testCreateSingleJadwal() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(12, 0);

        JadwalKonsultasi jadwal = intervalStrategy.create(idDokter, date, startTime, endTime);

        assertNotNull(jadwal);
        assertEquals(idDokter, jadwal.getIdDokter());
        assertEquals(date, jadwal.getDate());
        assertEquals(startTime, jadwal.getStartTime());
        assertEquals(startTime.plusMinutes(DURATION_MINUTES), jadwal.getEndTime());
        assertEquals("AVAILABLE", jadwal.getStatusDokter());
    }

    @Test
    void testCreateMultipleJadwalExactSlots() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        List<JadwalKonsultasi> jadwalList = intervalStrategy.createMultipleSlots(
                idDokter, date, startTime, endTime);

        assertEquals(4, jadwalList.size());

        assertEquals(LocalTime.of(9, 0), jadwalList.get(0).getStartTime());
        assertEquals(LocalTime.of(9, 30), jadwalList.get(0).getEndTime());

        assertEquals(LocalTime.of(9, 30), jadwalList.get(1).getStartTime());
        assertEquals(LocalTime.of(10, 0), jadwalList.get(1).getEndTime());

        assertEquals(LocalTime.of(10, 0), jadwalList.get(2).getStartTime());
        assertEquals(LocalTime.of(10, 30), jadwalList.get(2).getEndTime());

        assertEquals(LocalTime.of(10, 30), jadwalList.get(3).getStartTime());
        assertEquals(LocalTime.of(11, 0), jadwalList.get(3).getEndTime());

        jadwalList.forEach(jadwal -> {
            assertEquals("AVAILABLE", jadwal.getStatusDokter());
            assertEquals(idDokter, jadwal.getIdDokter());
            assertEquals(date, jadwal.getDate());
            assertNotNull(jadwal.getId());
        });
    }

    @Test
    void testCreateMultipleJadwalNonExactSlots() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 30);

        List<JadwalKonsultasi> jadwalList = intervalStrategy.createMultipleSlots(
                idDokter, date, startTime, endTime);

        assertEquals(3, jadwalList.size());

        assertEquals(LocalTime.of(10, 0), jadwalList.get(2).getStartTime());
        assertEquals(LocalTime.of(10, 30), jadwalList.get(2).getEndTime());
    }

    @Test
    void testInvalidTimeRangeThrowsException() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 25);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> intervalStrategy.createMultipleSlots(idDokter, date, startTime, endTime));

        assertTrue(exception.getMessage().contains("Rentang waktu harus habis dibagi"));
    }

    @Test
    void testStartTimeEqualsEndTimeThrowsException() {
        LocalTime time = LocalTime.of(9, 0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> intervalStrategy.createMultipleSlots(idDokter, date, time, time));

        assertTrue(exception.getMessage().contains("Waktu mulai tidak boleh sama dengan waktu selesai"));
    }

    @Test
    void testStartTimeAfterEndTimeThrowsException() {
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> intervalStrategy.createMultipleSlots(idDokter, date, startTime, endTime));

        assertTrue(exception.getMessage().contains("Waktu mulai tidak boleh setelah waktu selesai"));
    }
}