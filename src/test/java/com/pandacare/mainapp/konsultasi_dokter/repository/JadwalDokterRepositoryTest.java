package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.model.StatusJadwalDokter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JadwalDokterRepositoryTest {
    private JadwalKonsultasiRepository repository;
    private JadwalKonsultasi jadwal1;
    private JadwalKonsultasi jadwal2;

    @BeforeEach
    void setUp() {
        repository = new JadwalKonsultasiRepository();

        jadwal1 = new JadwalKonsultasi();
        jadwal1.setId("jdwl-001");
        jadwal1.setIdDokter(101L);
        jadwal1.setDay("Senin");
        jadwal1.setStartTime("09:00");
        jadwal1.setEndTime("10:00");
        jadwal1.setIdPasien(1L);
        jadwal1.setStatusDokter(StatusJadwalDokter.AVAILABLE);

        jadwal2 = new JadwalKonsultasi();
        jadwal2.setId("jdwl-002");
        jadwal2.setIdDokter(102L);
        jadwal2.setDay("Selasa");
        jadwal2.setStartTime("10:00");
        jadwal2.setEndTime("11:00");
        jadwal2.setStatusDokter(StatusJadwalDokter.REQUESTED);
        jadwal2.setIdPasien(2L);

        repository.save(jadwal1);
        repository.save(jadwal2);
    }

    @Test
    void testSavedJadwal() {
        JadwalKonsultasi retrieved = repository.findById("jdwl-001");

        assertNotNull(retrieved);
        assertEquals("Senin", retrieved.getDay());
        assertEquals("09:00", retrieved.getStartTime());
    }

    @Test
    void testFindByIdJadwal() {
        JadwalKonsultasi found = repository.findByIdJadwal("jdwl-001");
        assertNotNull(found);
        assertEquals("jdwl-001", found.getId());
    }

    @Test
    void testFindByStatus() {
        List<JadwalKonsultasi> result = repository.findByStatus(StatusJadwalDokter.REQUESTED);
        assertEquals(1, result.size());
        assertEquals("jdwl-002", result.get(0).getId());
    }

    @Test
    void testFindByIdPasien() {
        List<JadwalKonsultasi> result = repository.findByIdPasien(2L);
        assertEquals(1, result.size());
        assertEquals("jdwl-002", result.get(0).getId());
    }

    @Test
    void shouldFindJadwalByDokterId() {
        List<JadwalKonsultasi> jadwalFor101 = repository.findByIdDokter(101L);
        assertEquals(1, jadwalFor101.size());
        assertEquals("jdwl-001", jadwalFor101.get(0).getId());
    }

    @Test
    void shouldReturnAllJadwal() {
        List<JadwalKonsultasi> all = repository.findAll();
        assertEquals(2, all.size());
    }
}
