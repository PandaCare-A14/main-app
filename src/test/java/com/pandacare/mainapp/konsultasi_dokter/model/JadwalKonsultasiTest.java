package com.pandacare.mainapp.konsultasi_dokter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JadwalKonsultasiTest {

    private JadwalKonsultasi jadwal;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalKonsultasi();
        jadwal.setId("JDW001");
        jadwal.setIdDokter(1L);
        jadwal.setDay("Senin");
        jadwal.setStartTime("10:00");
        jadwal.setEndTime("11:00");
        jadwal.setNote("Konsultasi umum");

        assertEquals("JDW001", jadwal.getId());
        assertEquals(1L, jadwal.getIdDokter());
        assertEquals("Senin", jadwal.getDay());
        assertEquals("10:00", jadwal.getStartTime());
        assertEquals("11:00", jadwal.getEndTime());
        assertEquals("Konsultasi umum", jadwal.getNote());
        assertEquals(StatusJadwalDokter.AVAILABLE, jadwal.getStatusDokter());
    }

    @Test
    // Test pasien request konsultasi
    void testRequestByPatient() {
        jadwal.setIdDokter(1L);

        jadwal.requestByPatient(2L, "Konsultasi sakit kepala");

        assertEquals(StatusJadwalDokter.REQUESTED, jadwal.getStatusDokter());
        assertEquals(2L, jadwal.getIdPasien());
        assertEquals("Konsultasi sakit kepala", jadwal.getMessage());
    }

    @Test
    // Test dokter menyetujui konsultasi
    void testApproveConsultation() {
        jadwal.setIdDokter(1L);
        jadwal.requestByPatient(2L, "Konsultasi sakit kepala");
        jadwal.approveConsultation();

        assertEquals(StatusJadwalDokter.APPROVED, jadwal.getStatusDokter());
    }

    @Test
    // Test dokter menolak konsultasi
    void testRejectConsultation() {
        jadwal.setIdDokter(1L);
        jadwal.requestByPatient(2L, "Konsultasi sakit kepala");

        String alasan = "Jadwal penuh";
        jadwal.rejectConsultation(alasan);

        assertEquals(StatusJadwalDokter.REJECTED, jadwal.getStatusDokter());
        assertEquals(alasan, jadwal.getMessage());
    }

    @Test
    void testProposeScheduleChange() {
        // Setup jadwal dengan dokter dan permintaan dari pasien
        jadwal.setIdDokter(1L);
        jadwal.setDay("Senin");
        jadwal.setStartTime("10:00");
        jadwal.setEndTime("11:00");
        jadwal.requestByPatient(2L, "Konsultasi sakit kepala");

        // Memasukkan ajuan jadwal baru
        jadwal.proposeScheduleChange("Selasa", "14:00", "15:00", "Ada operasi mendadak");

        assertEquals(StatusJadwalDokter.CHANGE_SCHEDULE, jadwal.getStatusDokter());
        assertEquals("Selasa", jadwal.getDay());
        assertEquals("14:00", jadwal.getStartTime());
        assertEquals("15:00", jadwal.getEndTime());
        assertEquals("Ada operasi mendadak", jadwal.getMessage());
        assertTrue(jadwal.isChangeSchedule());
    }
}