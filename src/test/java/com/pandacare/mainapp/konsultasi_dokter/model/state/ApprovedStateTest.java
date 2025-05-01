package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApprovedStateTest {
    private JadwalKonsultasi jadwal;
    private ApprovedState state;
    private JadwalStateContext context;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setStatusDokter("APPROVED");
        state = new ApprovedState();
        context = new JadwalStateContext(jadwal);
    }

    @Test
    void testHandleRequest() {
        assertThrows(IllegalStateException.class, () ->
                state.handleRequest(context, "PAT-002", "Saya mau konsultasi ulang"));
    }

    @Test
    void testHandleApprove() {
        assertThrows(IllegalStateException.class, () ->
                state.handleApprove(context));
    }

    @Test
    void testHandleReject() {
        assertThrows(IllegalStateException.class, () ->
                state.handleReject(context, "Alasan X"));
    }

    @Test
    void testHandleChangeSchedule() {
        assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(context, "Selasa", "10:00", "11:00", "Reschedule"));
    }
}