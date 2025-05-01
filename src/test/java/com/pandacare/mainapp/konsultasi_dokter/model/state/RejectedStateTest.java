package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RejectedStateTest {
    private JadwalKonsultasi jadwal;
    private RejectedState state;
    private JadwalStateContext context;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setStatusDokter("REJECTED");
        state = new RejectedState();
        context = new JadwalStateContext(jadwal);
    }

    @Test
    void testHandleRequest() {
        assertThrows(IllegalStateException.class, () ->
                state.handleRequest(context, "PAT-009", "Saya banding"));
    }

    @Test
    void testHandleApprove() {
        assertThrows(IllegalStateException.class, () ->
                state.handleApprove(context));
    }

    @Test
    void testHandleReject() {
        assertThrows(IllegalStateException.class, () ->
                state.handleReject(context, "Masih ditolak"));
    }

    @Test
    void testHandleChangeSchedule() {
        assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(context, "Kamis", "13:00", "14:00", "Coba reschedule"));
    }
}