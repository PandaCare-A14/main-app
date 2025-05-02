package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.repository.JadwalDokterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JadwalDokterServiceImplTest {

    private JadwalDokterRepository repository;
    private JadwalDokterServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = new JadwalDokterRepository();
        service = new JadwalDokterServiceImpl(repository, new JadwalKonsultasiStateHandler());
    }

    @Test
    void testCreateJadwal() {
        JadwalKonsultasi created = service.createJadwal("DOK-010", "Senin", "09:00", "10:00");

        assertNotNull(created);
        assertEquals("DOK-010", created.getIdDokter());
        assertEquals("Senin", created.getDay());
        assertEquals("09:00", created.getStartTime());
        assertEquals("10:00", created.getEndTime());
        assertEquals("AVAILABLE", created.getStatusDokter());
    }

    @Test
    void testChangeJadwal() {
        JadwalKonsultasi original = service.createJadwal("DOK-011", "Selasa", "10:00", "11:00");
        original.setStatusDokter("REQUESTED");
        repository.save(original);

        String id = original.getId();

        boolean updated = service.changeJadwal(id, "Rabu", "11:00", "12:00", "Reschedule oleh dokter");

        JadwalKonsultasi changed = repository.findByIdJadwal(id);
        assertTrue(updated);
        assertEquals("Rabu", changed.getDay());
        assertEquals("11:00", changed.getStartTime());
        assertEquals("12:00", changed.getEndTime());
        assertEquals("CHANGE_SCHEDULE", changed.getStatusDokter());
    }

    @Test
    void testApproveJadwalSuccess() {
        JadwalKonsultasi jadwal = service.createJadwal("DOK-012", "Kamis", "08:00", "09:00");
        jadwal.setStatusDokter("REQUESTED");
        repository.save(jadwal);

        boolean result = service.approveJadwal(jadwal.getId());

        assertTrue(result);
        assertEquals("APPROVED", repository.findByIdJadwal(jadwal.getId()).getStatusDokter());
    }

    @Test
    void testRejectJadwalSuccess() {
        JadwalKonsultasi jadwal = service.createJadwal("DOK-013", "Jumat", "13:00", "14:00");
        jadwal.setStatusDokter("REQUESTED");
        repository.save(jadwal);

        boolean result = service.rejectJadwal(jadwal.getId());

        assertTrue(result);
        assertEquals("REJECTED", repository.findByIdJadwal(jadwal.getId()).getStatusDokter());
    }

    @Test
    void testApproveJadwalInvalidId() {
        boolean result = service.approveJadwal("INVALID-ID");
        assertFalse(result);
    }

    @Test
    void testRejectJadwalInvalidId() {
        boolean result = service.rejectJadwal("INVALID-ID");
        assertFalse(result);
    }
}