package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RequestedStateTest {
    private JadwalKonsultasi jadwal;
    private RequestedState state;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setState(new RequestedState());
        state = new RequestedState();
    }

    @Test
    void testHandleApproveShouldSetStatusApproved() {
        state.handleApprove(jadwal);
        assertEquals("APPROVED", jadwal.getStatusDokter());
    }

    @Test
    void testHandleRejectShouldSetStatusRejectedAndReason() {
        state.handleReject(jadwal, "Ada operasi dadakan");

        assertEquals("REJECTED", jadwal.getStatusDokter());
        assertEquals("Ada operasi dadakan", jadwal.getMessage());
    }

    @Test
    void testHandleChangeScheduleShouldSetStatusChangeSchedule() {
        state.handleChangeSchedule(jadwal,
                LocalDate.parse("2025-04-29"),
                LocalTime.parse("14:00"),
                LocalTime.parse("15:00"),
                "Perubahan jadwal"
        );

        assertEquals("CHANGE_SCHEDULE", jadwal.getStatusDokter());
        assertEquals(LocalDate.parse("2025-04-29"), jadwal.getDate());
        assertEquals(LocalTime.parse("14:00"), jadwal.getStartTime());
        assertEquals(LocalTime.parse("15:00"), jadwal.getEndTime());
        assertEquals("Perubahan jadwal", jadwal.getMessage());
        assertTrue(jadwal.isChangeSchedule());
    }

    @Test
    void testHandleRequestShouldThrowException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            state.handleRequest(jadwal, "PAT-002", "Mau ganti");
        });

        assertEquals("Sudah ada permintaan.", ex.getMessage());
    }
}