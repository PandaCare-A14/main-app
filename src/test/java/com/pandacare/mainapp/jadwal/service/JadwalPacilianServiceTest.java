package com.pandacare.mainapp.jadwal.service;

import com.pandacare.mainapp.jadwal.enums.StatusJadwalPacilian;
import com.pandacare.mainapp.jadwal.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JadwalPacilianServiceTest {

    private JadwalPacilianServiceImpl service;
    private JadwalKonsultasi waitingJadwal;
    private JadwalKonsultasi approvedJadwal;

    @BeforeEach
    void setUp() {
        service = new JadwalPacilianServiceImpl();

        waitingJadwal = new JadwalKonsultasi();
        waitingJadwal.setId("jadwal123");
        waitingJadwal.setIdDokter("dok123");
        waitingJadwal.setDay("Senin");
        waitingJadwal.setStartTime("09:00");
        waitingJadwal.setEndTime("10:00");
        waitingJadwal.setStatusPacilian(StatusJadwalPacilian.WAITING);

        approvedJadwal = new JadwalKonsultasi();
        approvedJadwal.setId("jadwal124");
        approvedJadwal.setIdDokter("dok123");
        approvedJadwal.setDay("Rabu");
        approvedJadwal.setStartTime("13:00");
        approvedJadwal.setEndTime("14:00");
        approvedJadwal.setStatusPacilian(StatusJadwalPacilian.APPROVED);
    }

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
        JadwalPacilianServiceImpl mockService = new JadwalPacilianServiceImpl() {
            @Override
            public JadwalKonsultasi findById(String id) {
                return waitingJadwal;
            }
        };

        JadwalKonsultasi updated = mockService.editSchedule("jadwal123", "Selasa", "10:00", "11:00");

        assertEquals("Selasa", updated.getDay());
        assertEquals("10:00", updated.getStartTime());
        assertEquals("11:00", updated.getEndTime());
        assertEquals(StatusJadwalPacilian.WAITING, updated.getStatusPacilian());
    }

    @Test
    void editSchedule_shouldThrowException_whenStatusIsNotWaiting() {
        JadwalPacilianServiceImpl mockService = new JadwalPacilianServiceImpl() {
            @Override
            public JadwalKonsultasi findById(String id) {
                return approvedJadwal;
            }
        };

        Exception ex = assertThrows(IllegalStateException.class, () ->
                mockService.editSchedule("jadwal124", "Senin", "09:00", "10:00")
        );

        assertEquals("Only schedules with status WAITING can be edited", ex.getMessage());
    }

    @Test
    void acceptChangeSchedule_shouldApplyRequestedChange() {
        JadwalKonsultasi jadwal = new JadwalKonsultasi();
        jadwal.setId("jadwal123");
        jadwal.setDay("Selasa");
        jadwal.setStartTime("10:00");
        jadwal.setEndTime("11:00");
        jadwal.setStatusPacilian(StatusJadwalPacilian.WAITING);
        jadwal.setChangeSchedule(true);

        JadwalPacilianServiceImpl mockService = new JadwalPacilianServiceImpl() {
            @Override
            public JadwalKonsultasi findById(String id) {
                return jadwal;
            }
        };

        JadwalKonsultasi result = mockService.acceptChangeSchedule("jadwal123");

        assertNotNull(result);
        assertFalse(result.isChangeSchedule(), "State changeSchedule set to false if accepted");
        assertEquals(StatusJadwalPacilian.WAITING, result.getStatusPacilian(), "Status should remain WAITING");
    }

    @Test
    void acceptChangeSchedule_shouldThrowException_whenChangeScheduleIsFalse() {
        JadwalKonsultasi noChangeRequestJadwal = new JadwalKonsultasi();
        noChangeRequestJadwal.setId("jadwal125");
        noChangeRequestJadwal.setDay("Rabu");
        noChangeRequestJadwal.setStartTime("13:00");
        noChangeRequestJadwal.setEndTime("14:00");
        noChangeRequestJadwal.setStatusPacilian(StatusJadwalPacilian.WAITING);
        noChangeRequestJadwal.setChangeSchedule(false);

        JadwalPacilianServiceImpl mockService = new JadwalPacilianServiceImpl() {
            @Override
            public JadwalKonsultasi findById(String id) {
                return noChangeRequestJadwal;
            }
        };

        Exception ex = assertThrows(IllegalStateException.class, () ->
                mockService.acceptChangeSchedule("jadwal125")
        );

        assertEquals("No change request exists for this schedule", ex.getMessage());
    }
}