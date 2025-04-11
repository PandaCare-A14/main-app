package com.pandacare.mainapp.jadwal.service;

import com.pandacare.mainapp.jadwal.model.JadwalKonsultasi;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JadwalPacilianServiceTest {

    private JadwalPacilianServiceImpl service = new JadwalPacilianServiceImpl();

    @Test
    void requestJadwal_shouldReturnWaitingStatus() {
        JadwalKonsultasi result = service.requestJadwal("dok123", "Senin", "09:00", "10:00");

        assertNotNull(result);
        assertEquals("dok123", result.getIdDokter());
        assertEquals("Senin", result.getDay());
        assertEquals("09:00", result.getStartTime());
        assertEquals("10:00", result.getEndTime());
        assertEquals("WAITING", result.getStatusPacilian());
    }

    @Test
    void requestJadwal_shouldThrowException_whenStartTimeAfterEndTime() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.requestJadwal("dok123", "Senin", "14:00", "10:00")
        );
        assertEquals("Start time must be before end time", exception.getMessage());
    }
}
