package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ChangeScheduleStateTest {
    private JadwalKonsultasi jadwal;
    private ChangeScheduleState state;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setState(new ChangeScheduleState());
        state = new ChangeScheduleState();
    }

    @Test
    void testHandleRequest() {
        assertThrows(IllegalStateException.class, () ->
                state.handleRequest(jadwal, "PAT-003", "Reschedule lagi"));
    }

    @Test
    void testHandleApprove() {
        state.handleApprove(jadwal);

        assertEquals("APPROVED", jadwal.getStatusDokter());
        assertFalse(jadwal.isChangeSchedule());
    }

    @Test
    void testHandleReject() {
        state.handleReject(jadwal, "Pasien tidak setuju");

        assertEquals("REJECTED", jadwal.getStatusDokter());
        assertEquals("Pasien tidak setuju", jadwal.getMessage());
        assertFalse(jadwal.isChangeSchedule());
    }

    @Test
    void testHandleChangeSchedule() {
        assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(jadwal, LocalDate.parse("2025-05-01"), LocalTime.parse("15:00"), LocalTime.parse("16:00"), "Ubah lagi"));
    }
}