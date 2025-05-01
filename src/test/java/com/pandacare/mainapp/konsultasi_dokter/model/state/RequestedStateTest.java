package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestedStateTest {
    private JadwalKonsultasi jadwal;
    private RequestedState state;
    private JadwalStateContext context;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setStatusDokter("REQUESTED");
        context = new JadwalStateContext(jadwal);
        state = new RequestedState();
    }

    @Test
    void testHandleApproveShouldSetStatusApproved() {
        state.handleApprove(context);
        assertEquals("APPROVED", jadwal.getStatusDokter());
    }

    @Test
    void testHandleRejectShouldSetStatusRejectedAndReason() {
        state.handleReject(context, "Ada operasi dadakan");

        assertEquals("REJECTED", jadwal.getStatusDokter());
        assertEquals("Ada operasi dadakan", jadwal.getMessage());
    }

    @Test
    void testHandleChangeScheduleShouldSetStatusChangeSchedule() {
        state.handleChangeSchedule(context, "WEDNESDAY", "14:00", "15:00", "Perubahan jadwal");

        assertEquals("CHANGE_SCHEDULE", jadwal.getStatusDokter());
        assertEquals("WEDNESDAY", jadwal.getDay());
        assertEquals("14:00", jadwal.getStartTime());
        assertEquals("15:00", jadwal.getEndTime());
        assertEquals("Perubahan jadwal", jadwal.getMessage());
        assertTrue(jadwal.isChangeSchedule());
    }

    @Test
    void testHandleRequestShouldThrowException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            state.handleRequest(context, "PAT-002", "Mau ganti request");
        });

        assertEquals("Sudah ada permintaan.", ex.getMessage());
    }
}