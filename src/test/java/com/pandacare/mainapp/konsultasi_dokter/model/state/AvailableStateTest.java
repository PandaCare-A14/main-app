package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class AvailableStateTest {
    private CaregiverSchedule jadwal;
    private AvailableState state;

    @BeforeEach
    void setUp() {
        jadwal = new CaregiverSchedule();
        state = new AvailableState();
    }

    @Test
    void testHandleRequest() {
        String pasienId = "PAT-12345";
        String message = "Saya ingin konsultasi";

        state.handleRequest(jadwal, pasienId, message);

        assertEquals("REQUESTED", jadwal.getStatusCaregiver());
        assertEquals(pasienId, jadwal.getIdPacilian());
        assertEquals(message, jadwal.getMessage());
    }

    @Test
    void testHandleApprove() {
        assertThrows(IllegalStateException.class, () -> state.handleApprove(jadwal));
    }

    @Test
    void testHandleReject() {
        assertThrows(IllegalStateException.class, () -> state.handleReject(jadwal, null));
    }

    @Test
    void testHandleChangeSchedule() {
        assertThrows(IllegalStateException.class,
                () -> state.handleChangeSchedule(jadwal, LocalDate.parse("2025-05-06"), LocalTime.parse("09:00"), LocalTime.parse("10:00"),
                        "ada urusan mendadak, mohon diganti"));
    }
}