package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.model.state.AvailableState;
import com.pandacare.mainapp.konsultasi_dokter.model.state.RequestedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JadwalKonsultasiRepositoryTest {
    private JadwalKonsultasiRepository repository;
    private JadwalKonsultasi jadwal1;
    private JadwalKonsultasi jadwal2;
    private JadwalKonsultasi jadwal3;

    @BeforeEach
    void setUp() {
        repository = new JadwalKonsultasiRepository();

        jadwal1 = new JadwalKonsultasi();
        jadwal1.setId("SCHED001");
        jadwal1.setIdDokter("DOC001");
        jadwal1.setIdPasien("PAT001");
        jadwal1.setState(new AvailableState());
        jadwal1.setDate(LocalDate.parse("2025-05-06"));
        jadwal1.setStartTime(LocalTime.of(9, 0));
        jadwal1.setEndTime(LocalTime.of(9, 30));

        jadwal2 = new JadwalKonsultasi();
        jadwal2.setId("SCHED002");
        jadwal2.setIdDokter("DOC002");
        jadwal2.setIdPasien("PAT002");
        jadwal2.setState(new RequestedState());
        jadwal2.setDate(LocalDate.parse("2025-05-07"));
        jadwal2.setStartTime(LocalTime.of(10, 0));
        jadwal2.setEndTime(LocalTime.of(10, 30));

        jadwal3 = new JadwalKonsultasi();
        jadwal3.setId("SCHED003");
        jadwal3.setIdDokter("DOC001");
        jadwal3.setIdPasien("PAT003");
        jadwal3.setState(new AvailableState());
        jadwal3.setDate(LocalDate.parse("2025-05-06"));
        jadwal3.setStartTime(LocalTime.of(10, 0));
        jadwal3.setEndTime(LocalTime.of(10, 30));

        repository.save(jadwal1);
        repository.save(jadwal2);
        repository.save(jadwal3);
    }

    @Test
    void testSave() {
        JadwalKonsultasi newJadwal = new JadwalKonsultasi();
        newJadwal.setId("SCHED004");
        newJadwal.setIdDokter("DOC003");
        newJadwal.setState(new AvailableState());

        JadwalKonsultasi saved = repository.save(newJadwal);

        assertNotNull(saved);
        assertEquals("SCHED004", saved.getId());
        assertEquals("DOC003", saved.getIdDokter());

        JadwalKonsultasi found = repository.findById("SCHED004");
        assertNotNull(found);
        assertEquals("DOC003", found.getIdDokter());
    }

    @Test
    void testSaveWithNullId() {
        JadwalKonsultasi newJadwal = new JadwalKonsultasi();
        newJadwal.setIdDokter("DOC003");
        newJadwal.setState(new AvailableState());

        JadwalKonsultasi saved = repository.save(newJadwal);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("DOC003", saved.getIdDokter());
    }

    @Test
    void testFindById() {
        JadwalKonsultasi found = repository.findById("SCHED001");

        assertNotNull(found);
        assertEquals(LocalDate.parse("2025-05-06"), found.getDate());
        assertEquals("DOC001", found.getIdDokter());
    }

    @Test
    void testFindByIdNotFound() {
        JadwalKonsultasi found = repository.findById("non-existent");

        assertNull(found);
    }

    @Test
    void testFindByIdDokter() {
        List<JadwalKonsultasi> result = repository.findByIdDokter("DOC001");

        assertEquals(2, result.size());
        assertTrue(result.contains(jadwal1));
        assertTrue(result.contains(jadwal3));
    }

    @Test
    void testFindByIdDokterNoResult() {
        List<JadwalKonsultasi> result = repository.findByIdDokter("non-existent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByIdDokterAndDate() {
        List<JadwalKonsultasi> result = repository.findByIdDokterAndDate("DOC001", LocalDate.parse("2025-05-06"));

        assertEquals(2, result.size());
        assertTrue(result.contains(jadwal1));
        assertTrue(result.contains(jadwal3));
    }

    @Test
    void testFindByIdDokterAndDateNoResult() {
        List<JadwalKonsultasi> result = repository.findByIdDokterAndDate("DOC001", LocalDate.parse("2025-05-10"));

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindOverlappingJadwal() {
        List<JadwalKonsultasi> result = repository.findOverlappingSchedule(
                "DOC001",
                LocalDate.parse("2025-05-06"),
                LocalTime.of(9, 15),
                LocalTime.of(10, 15));

        assertEquals(2, result.size());
        assertTrue(result.contains(jadwal1));
        assertTrue(result.contains(jadwal3));
    }

    @Test
    void testFindOverlappingJadwalNoOverlap() {
        List<JadwalKonsultasi> result = repository.findOverlappingSchedule(
                "DOC001",
                LocalDate.parse("2025-05-06"),
                LocalTime.of(11, 0),
                LocalTime.of(11, 30));

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByIdPasien() {
        List<JadwalKonsultasi> result = repository.findByIdPasien("PAT001");

        assertEquals(1, result.size());
        assertEquals("SCHED001", result.get(0).getId());
    }

    @Test
    void testFindByIdPasienNoResult() {
        List<JadwalKonsultasi> result = repository.findByIdPasien("non-existent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByStatus() {
        List<JadwalKonsultasi> result = repository.findByStatus("REQUESTED");

        assertEquals(1, result.size());
        assertEquals("SCHED002", result.get(0).getId());
    }

    @Test
    void testFindByStatusNoResult() {
        List<JadwalKonsultasi> result = repository.findByStatus("REJECTED");

        assertTrue(result.isEmpty());
    }
}