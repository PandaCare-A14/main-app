package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangeScheduleStateTest {
    private JadwalKonsultasi jadwal;
    private ChangeScheduleState state;
    private JadwalStateContext context;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setStatusDokter("CHANGE_SCHEDULE");
        state = new ChangeScheduleState();
        context = new JadwalStateContext(jadwal);
    }

    @Test
    void testHandleRequest() {
        assertThrows(IllegalStateException.class, () ->
                state.handleRequest(context, "PAT-003", "Reschedule lagi"));
    }

    @Test
    void testHandleApprove() {
        state.handleApprove(context);

        assertEquals("APPROVED", jadwal.getStatusDokter());
        assertFalse(jadwal.isChangeSchedule());
    }

    @Test
    void testHandleReject() {
        state.handleReject(context, "Pasien tidak setuju");

        assertEquals("REJECTED", jadwal.getStatusDokter());
        assertEquals("Pasien tidak setuju", jadwal.getMessage());
        assertFalse(jadwal.isChangeSchedule());
    }

    @Test
    void testHandleChangeSchedule() {
        assertThrows(IllegalStateException.class, () ->
                state.handleChangeSchedule(context, "Jumat", "15:00", "16:00", "Ubah lagi"));
    }
}