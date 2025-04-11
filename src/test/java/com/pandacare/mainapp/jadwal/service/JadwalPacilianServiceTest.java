package com.pandacare.mainapp.jadwal.service;

import com.pandacare.mainapp.jadwal.enums.StatusJadwalPacilian;
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
        assertEquals(StatusJadwalPacilian.WAITING, result.getStatusPacilian());
    }

    @Test
    void requestJadwal_shouldThrowException_whenStartTimeAfterEndTime() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.requestJadwal("dok123", "Senin", "14:00", "10:00")
        );
        assertEquals("Start time must be before end time", exception.getMessage());
    }

    @Test
    void editSchedule_shouldUpdateSchedule_whenStatusIsWaiting() {
        JadwalKonsultasi existing = new JadwalKonsultasi();
        existing.setId("jadwal123");
        existing.setDay("Senin");
        existing.setStartTime("09:00");
        existing.setEndTime("10:00");
        existing.setStatusPacilian(StatusJadwalPacilian.WAITING);

        JadwalPacilianServiceImpl service = new JadwalPacilianServiceImpl() {
            @Override
            public JadwalKonsultasi findById(String id) {
                return existing;
            }
        };

        JadwalKonsultasi updated = service.editSchedule("jadwal123", "Selasa", "10:00", "11:00");

        assertEquals("Selasa", updated.getDay());
        assertEquals("10:00", updated.getStartTime());
        assertEquals("11:00", updated.getEndTime());
        assertEquals(StatusJadwalPacilian.WAITING, updated.getStatusPacilian());
    }

    @Test
    void editSchedule_shouldThrowException_whenStatusIsNotWaiting() {
        JadwalKonsultasi jadwal = new JadwalKonsultasi();
        jadwal.setId("jadwal124");
        jadwal.setStatusPacilian(StatusJadwalPacilian.APPROVED);

        JadwalPacilianServiceImpl service = new JadwalPacilianServiceImpl() {
            @Override
            public JadwalKonsultasi findById(String id) {
                return jadwal;
            }
        };

        Exception ex = assertThrows(IllegalStateException.class, () ->
                service.editSchedule("jadwal124", "Senin", "09:00", "10:00")
        );

        assertEquals("Only schedules with status WAITING can be edited", ex.getMessage());
    }
}
