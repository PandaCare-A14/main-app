package com.pandacare.mainapp.jadwalKonsultasi.repository;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JadwalKonsultasiRepositoryTest {

    private JadwalKonsultasiRepository repository;
    private JadwalKonsultasi jadwal1;
    private JadwalKonsultasi jadwal2;

    @BeforeEach
    void setUp() {
        repository = new JadwalKonsultasiRepository();

        jadwal1 = new JadwalKonsultasi();
        jadwal1.setId("jdwl-001");
        jadwal1.setIdDokter("DOC-001");
        jadwal1.setIdPasien("PAT-001");
        jadwal1.setStatusDokter("AVAILABLE");
        jadwal1.setDay("Senin");

        jadwal2 = new JadwalKonsultasi();
        jadwal2.setId("jdwl-002");
        jadwal2.setIdDokter("DOC-002");
        jadwal2.setIdPasien("PAT-002");
        jadwal2.setStatusDokter("REQUESTED");
        jadwal2.setDay("Selasa");

        repository.save(jadwal1);
        repository.save(jadwal2);
    }

    @Test
    void testFindById() {
        JadwalKonsultasi found = repository.findById("jdwl-001");

        assertNotNull(found);
        assertEquals("Senin", found.getDay());
    }

    @Test
    void testFindByIdDokter() {
        List<JadwalKonsultasi> result = repository.findByIdDokter("DOC-001");

        assertEquals(1, result.size());
        assertEquals("jdwl-001", result.get(0).getId());
    }

    @Test
    void testFindByIdPasien() {
        List<JadwalKonsultasi> result = repository.findByIdPasien("PAT-002");

        assertEquals(1, result.size());
        assertEquals("jdwl-002", result.get(0).getId());
    }

    @Test
    void testFindByStatus() {
        List<JadwalKonsultasi> result = repository.findByStatus("REQUESTED");

        assertEquals(1, result.size());
        assertEquals("jdwl-002", result.get(0).getId());
    }

    @Test
    void testFindAll() {
        List<JadwalKonsultasi> all = repository.findAll();

        assertEquals(2, all.size());
    }
}