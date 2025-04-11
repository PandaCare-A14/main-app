package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.model.StatusJadwalDokter;
import com.pandacare.mainapp.konsultasi_dokter.repository.JadwalKonsultasiRepository;
import com.pandacare.mainapp.konsultasi_dokter.service.impl.JadwalDokterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JadwalDokterServiceImplTest {
    private JadwalKonsultasiRepository repository;
    private JadwalDokterServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = new JadwalKonsultasiRepository();
        service = new JadwalDokterServiceImpl(repository);
    }

    @Test
    void testCreateJadwal() {
        service.createJadwal(10L, "Senin", "09:00", "10:00");

        JadwalKonsultasi created = repository.findAll().get(0);
        assertNotNull(created);
        assertEquals(10L, created.getIdDokter());
        assertEquals("Senin", created.getDay());
        assertEquals("09:00", created.getStartTime());
        assertEquals(StatusJadwalDokter.AVAILABLE, created.getStatusDokter());
    }

    @Test
    void testChangeJadwal() {
        service.createJadwal(11L, "Selasa", "10:00", "11:00");
        JadwalKonsultasi original = repository.findAll().get(0);
        String id = original.getId();

        boolean updated = service.changeJadwal(id, "Rabu", "11:00", "12:00");

        JadwalKonsultasi changed = repository.findById(id);
        assertTrue(updated);
        assertEquals("Rabu", changed.getDay());
        assertEquals("11:00", changed.getStartTime());
        assertEquals("12:00", changed.getEndTime());
    }
}
