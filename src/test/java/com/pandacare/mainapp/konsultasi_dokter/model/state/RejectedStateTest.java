package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RejectedStateTest {
    private CaregiverSchedule jadwal;
    private RejectedState state;

    @BeforeEach
    void setUp() {
        jadwal = new CaregiverSchedule();
        jadwal.setState(new RejectedState());
        state = new RejectedState();
    }

    @Test
    void testHandleRequest() {
        assertThrows(IllegalStateException.class, () ->
                state.handleRequest(jadwal, "PAT-009", "Saya banding"));
    }

    @Test
    void testHandleApprove() {
        assertThrows(IllegalStateException.class, () ->
                state.handleApprove(jadwal));
    }

    @Test
    void testHandleReject() {
        assertThrows(IllegalStateException.class, () ->
                state.handleReject(jadwal, "Masih ditolak"));
    }

    @Test
    void testHandleChangeSchedule() {
        assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(jadwal, LocalDate.parse("2025-05-01"), LocalTime.parse("13:00"), LocalTime.parse("14:00"), "Coba reschedule"));
    }
}