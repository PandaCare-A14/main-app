package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvailableStateTest {
    private JadwalKonsultasi jadwal;
    private AvailableState state;
    private JadwalStateContext context;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        state = new AvailableState();
        context = new JadwalStateContext(jadwal);
    }

    @Test
    void testHandleRequest() {
        String pasienId = "PAT-12345";
        String message = "Saya ingin konsultasi";

        state.handleRequest(context, pasienId, message);

        assertEquals("REQUESTED", jadwal.getStatusDokter());
        assertEquals(pasienId, jadwal.getIdPasien());
        assertEquals(message, jadwal.getMessage());
    }

    @Test
    void testHandleApprove() {
        assertThrows(IllegalStateException.class, () -> state.handleApprove(context));
    }

    @Test
    void testHandleReject() {
        assertThrows(IllegalStateException.class, () -> state.handleReject(context, null));
    }

    @Test
    void testHandleChangeSchedule() {
        assertThrows(IllegalStateException.class,
                () -> state.handleChangeSchedule(context, "MONDAY", "09:00", "10:00",
                        "ada urusan mendadak, mohon diganti"));
    }
}