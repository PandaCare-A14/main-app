package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ApprovedStateTest {
    private JadwalKonsultasi jadwal;
    private ApprovedState state;
    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setState(new ApprovedState());
        state = new ApprovedState();
    }

    @Test
    void testHandleRequest() {
        assertThrows(IllegalStateException.class, () ->
                state.handleRequest(jadwal, "PAT-002", "Saya mau konsultasi ulang"));
    }

    @Test
    void testHandleApprove() {
        assertThrows(IllegalStateException.class, () ->
                state.handleApprove(jadwal));
    }

    @Test
    void testHandleReject() {
        assertThrows(IllegalStateException.class, () ->
                state.handleReject(jadwal, "Alasan X"));
    }

    @Test
    void testHandleChangeSchedule() {
        assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(jadwal, LocalDate.parse("2025-05-06"), LocalTime.parse("10:00"), LocalTime.parse("11:00"), "Reschedule"));
    }
}