package com.pandacare.mainapp.konsultasi_dokter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

class JadwalKonsultasiTest {
    private JadwalKonsultasi jadwal;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setId("JADWAL001");
        jadwal.setIdDokter(1L);
        jadwal.setDay("Rabu");
        jadwal.setStartTime("11:30");
        jadwal.setEndTime("12:15");

        assertEquals("JADWAL001", jadwal.getId());
        assertEquals(1L, jadwal.getIdDokter());
        assertEquals("Rabu", jadwal.getDay());
        assertEquals("11:30", jadwal.getStartTime());
        assertEquals("12:15", jadwal.getEndTime());
        assertEquals(StatusJadwalDokter.AVAILABLE, jadwal.getStatusDokter());
    }

    @Test
    void testRequestByPatient() {
        jadwal.setIdDokter(1L);
        jadwal.requestByPatient(2L, "Konsultasi sakit kepala");

        assertEquals(StatusJadwalDokter.REQUESTED, jadwal.getStatusDokter());
        assertEquals(2L, jadwal.getIdPasien());
        assertEquals("Konsultasi sakit kepala", jadwal.getMessage());
    }

    @Test
    void testApproveConsultation() {
        jadwal.setIdDokter(1L);
        jadwal.requestByPatient(2L, "Konsultasi sakit kepala");
        jadwal.approveConsultation();

        assertEquals(StatusJadwalDokter.APPROVED, jadwal.getStatusDokter());
    }

    @Test
    void testRejectConsultation() {
        jadwal.setIdDokter(1L);
        jadwal.requestByPatient(2L, null);

        String alasan = "Jadwal saat ini penuh, waktu reschedule belum bisa saya tentukan.";
        jadwal.rejectConsultation(alasan);

        assertEquals(StatusJadwalDokter.REJECTED, jadwal.getStatusDokter());
        assertEquals(alasan, jadwal.getMessage());
    }

    @Test
    void testProposeScheduleChange() {
        jadwal.setIdDokter(1L);
        jadwal.setDay("Senin");
        jadwal.setStartTime("10:00");
        jadwal.setEndTime("11:00");
        jadwal.requestByPatient(2L, "Konsultasi sakit kepala");

        jadwal.proposeScheduleChange("Selasa", "14:15", "15:00", "Ada operasi mendadak");

        assertEquals(StatusJadwalDokter.CHANGE_SCHEDULE, jadwal.getStatusDokter());
        assertEquals("Selasa", jadwal.getDay());
        assertEquals("14:15", jadwal.getStartTime());
        assertEquals("15:00", jadwal.getEndTime());
        assertEquals("Ada operasi mendadak", jadwal.getMessage());
        assertTrue(jadwal.isChangeSchedule());
    }

    @Test
    void testObserversAreNotifiedOnStatusChange() {
        TestObserver observer = new TestObserver();
        jadwal.addObserver(observer);
        jadwal.requestByPatient(2L, "Konsultasi sakit kepala");

        assertEquals(1, observer.getNotificationCount());
        assertEquals(StatusJadwalDokter.REQUESTED, observer.getLastNotification());

        jadwal.approveConsultation();
        assertEquals(2, observer.getNotificationCount());
        assertEquals(StatusJadwalDokter.APPROVED, observer.getLastNotification());
    }

    @Test
    void testRemovedObserversDoNotReceiveNotifications() {
        TestObserver observer = new TestObserver();
        jadwal.addObserver(observer);
        jadwal.removeObserver(observer);
        jadwal.setStatusDokter(StatusJadwalDokter.REQUESTED);
        assertEquals(0, observer.getNotificationCount());
    }

    static class TestObserver implements JadwalObserver {
        private List<StatusJadwalDokter> notifications = new ArrayList<>();

        @Override
        public void update(JadwalKonsultasi jadwal) {
            notifications.add(jadwal.getStatusDokter());
        }

        public int getNotificationCount() {
            return notifications.size();
        }

        public StatusJadwalDokter getLastNotification() {
            if (notifications.isEmpty()) {
                return null;
            }
            return notifications.get(notifications.size() - 1);
        }
    }
}