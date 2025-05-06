package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.model.state.AvailableState;
import com.pandacare.mainapp.konsultasi_dokter.model.state.RequestedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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
        jadwal1.setId("SCHED001");
        jadwal1.setIdDokter("DOC001");
        jadwal1.setIdPasien("PAT001");
        jadwal1.setState(new AvailableState());
        jadwal1.setDate(LocalDate.parse("2025-05-06"));

        jadwal2 = new JadwalKonsultasi();
        jadwal2.setId("SCHED002");
        jadwal2.setIdDokter("DOC002");
        jadwal2.setIdPasien("PAT002");
        jadwal2.setState(new RequestedState());
        jadwal2.setDate(LocalDate.parse("2025-05-07"));

        repository.save(jadwal1);
        repository.save(jadwal2);
    }

    @Test
    void testFindById() {
        JadwalKonsultasi found = repository.findById("jdwl-001");

        assertNotNull(found);
        assertEquals(LocalDate.parse("2025-05-06"), found.getDate());
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