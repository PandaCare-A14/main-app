package com.pandacare.mainapp.konsultasi_dokter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class JadwalObserverTest {

    private JadwalKonsultasi jadwal;
    private TestObserver testObserver;

    static class TestObserver implements JadwalObserver {
        private List<StatusJadwalDokter> notifications = new ArrayList<>();

        @Override
        public void update(JadwalKonsultasi jadwal) {
            notifications.add(jadwal.getStatusDokter());
        }

        public List<StatusJadwalDokter> getNotifications() {
            return notifications;
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

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setIdDokter(1L);
        testObserver = new TestObserver();
        jadwal.addObserver(testObserver);
    }

    @Test
    void testInitialStateNoNotification() {
        assertEquals(0, testObserver.getNotificationCount());
    }

    @Test
    void testStatusChangeTriggersNotification() {
        jadwal.setStatusDokter(StatusJadwalDokter.REQUESTED);

        assertEquals(1, testObserver.getNotificationCount());
        assertEquals(StatusJadwalDokter.REQUESTED, testObserver.getLastNotification());
    }

    @Test
    void testBusinessMethodsTriggersNotification() {
        jadwal.requestByPatient(2L, "Konsultasi");
        assertEquals(1, testObserver.getNotificationCount());
        assertEquals(StatusJadwalDokter.REQUESTED, testObserver.getLastNotification());

        jadwal.approveConsultation();
        assertEquals(2, testObserver.getNotificationCount());
        assertEquals(StatusJadwalDokter.APPROVED, testObserver.getLastNotification());
    }

    @Test
    void testRemoveObserver() {
        jadwal.removeObserver(testObserver);
        jadwal.setStatusDokter(StatusJadwalDokter.REQUESTED);
        assertEquals(0, testObserver.getNotificationCount());
    }
}