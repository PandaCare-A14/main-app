package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JadwalDokterRepositoryTest {

    private JadwalDokterRepository repository;
    private JadwalKonsultasi jadwal1;
    private JadwalKonsultasi jadwal2;

    @BeforeEach
    void setUp() {
        repository = new JadwalDokterRepository();

        jadwal1 = new JadwalKonsultasi();
        jadwal1.setId("JD-001");
        jadwal1.setIdDokter("DOK-1");
        jadwal1.setDay("Senin");
        jadwal1.setStartTime("08:00");
        jadwal1.setEndTime("09:00");
        jadwal1.setStatusDokter("AVAILABLE");

        jadwal2 = new JadwalKonsultasi();
        jadwal2.setId("JD-002");
        jadwal2.setIdDokter("DOK-2");
        jadwal2.setDay("Selasa");
        jadwal2.setStartTime("10:00");
        jadwal2.setEndTime("11:00");
        jadwal2.setStatusDokter("REQUESTED");
        jadwal2.setIdPasien("PAT-1");

        repository.save(jadwal1);
        repository.save(jadwal2);
    }

    @Test
    void testFindByIdJadwal() {
        JadwalKonsultasi result = repository.findByIdJadwal("JD-001");
        assertNotNull(result);
        assertEquals("DOK-1", result.getIdDokter());
    }

    @Test
    void testFindByStatus() {
        List<JadwalKonsultasi> results = repository.findByStatus("REQUESTED");
        assertEquals(1, results.size());
        assertEquals("JD-002", results.get(0).getId());
    }

    @Test
    void testFindByIdDokter() {
        List<JadwalKonsultasi> results = repository.findByIdDokter("DOK-1");
        assertEquals(1, results.size());
        assertEquals("JD-001", results.get(0).getId());
    }

    @Test
    void testFindByIdPasien() {
        List<JadwalKonsultasi> results = repository.findByIdPasien("PAT-1");
        assertEquals(1, results.size());
        assertEquals("JD-002", results.get(0).getId());
    }

    @Test
    void testFindAll() {
        List<JadwalKonsultasi> results = repository.findAll();
        assertEquals(2, results.size());
    }
}