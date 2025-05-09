package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.model.state.AvailableState;
import com.pandacare.mainapp.konsultasi_dokter.model.state.RequestedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CaregiverScheduleRepositoryTest {
    private CaregiverScheduleRepository repository;
    private CaregiverSchedule schedule1;
    private CaregiverSchedule schedule2;
    private CaregiverSchedule schedule3;

    @BeforeEach
    void setUp() {
        repository = new CaregiverScheduleRepository();

        schedule1 = new CaregiverSchedule();
        schedule1.setId("SCHED001");
        schedule1.setIdCaregiver("DOC001");
        schedule1.setIdPacilian("PAT001");
        schedule1.setState(new AvailableState());
        schedule1.setDate(LocalDate.parse("2025-05-06"));
        schedule1.setStartTime(LocalTime.of(9, 0));
        schedule1.setEndTime(LocalTime.of(9, 30));

        schedule2 = new CaregiverSchedule();
        schedule2.setId("SCHED002");
        schedule2.setIdCaregiver("DOC002");
        schedule2.setIdPacilian("PAT002");
        schedule2.setState(new RequestedState());
        schedule2.setDate(LocalDate.parse("2025-05-07"));
        schedule2.setStartTime(LocalTime.of(10, 0));
        schedule2.setEndTime(LocalTime.of(10, 30));

        schedule3 = new CaregiverSchedule();
        schedule3.setId("SCHED003");
        schedule3.setIdCaregiver("DOC001");
        schedule3.setIdPacilian("PAT003");
        schedule3.setState(new AvailableState());
        schedule3.setDate(LocalDate.parse("2025-05-06"));
        schedule3.setStartTime(LocalTime.of(10, 0));
        schedule3.setEndTime(LocalTime.of(10, 30));

        repository.save(schedule1);
        repository.save(schedule2);
        repository.save(schedule3);
    }

    @Test
    void testSave() {
        CaregiverSchedule newSchedule = new CaregiverSchedule();
        newSchedule.setId("SCHED004");
        newSchedule.setIdCaregiver("DOC003");
        newSchedule.setState(new AvailableState());

        CaregiverSchedule saved = repository.save(newSchedule);

        assertNotNull(saved);
        assertEquals("SCHED004", saved.getId());
        assertEquals("DOC003", saved.getIdCaregiver());

        CaregiverSchedule found = repository.findById("SCHED004");
        assertNotNull(found);
        assertEquals("DOC003", found.getIdCaregiver());
    }

    @Test
    void testSaveWithNullId() {
        CaregiverSchedule newSchedule = new CaregiverSchedule();
        newSchedule.setIdCaregiver("DOC003");
        newSchedule.setState(new AvailableState());

        CaregiverSchedule saved = repository.save(newSchedule);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("DOC003", saved.getIdCaregiver());
    }

    @Test
    void testFindById() {
        CaregiverSchedule found = repository.findById("SCHED001");

        assertNotNull(found);
        assertEquals(LocalDate.parse("2025-05-06"), found.getDate());
        assertEquals("DOC001", found.getIdCaregiver());
    }

    @Test
    void testFindByIdNotFound() {
        CaregiverSchedule found = repository.findById("non-existent");

        assertNull(found);
    }

    @Test
    void testFindByIdCaregiver() {
        List<CaregiverSchedule> result = repository.findByIdCaregiver("DOC001");

        assertEquals(2, result.size());
        assertTrue(result.contains(schedule1));
        assertTrue(result.contains(schedule3));
    }

    @Test
    void testFindByIdCaregiverNoResult() {
        List<CaregiverSchedule> result = repository.findByIdCaregiver("non-existent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByIdCaregiverAndDate() {
        List<CaregiverSchedule> result = repository.findByIdCaregiverAndDate("DOC001", LocalDate.parse("2025-05-06"));

        assertEquals(2, result.size());
        assertTrue(result.contains(schedule1));
        assertTrue(result.contains(schedule3));
    }

    @Test
    void testFindByIdCaregiverAndDateNoResult() {
        List<CaregiverSchedule> result = repository.findByIdCaregiverAndDate("DOC001", LocalDate.parse("2025-05-10"));

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindOverlappingSchedule() {
        List<CaregiverSchedule> result = repository.findOverlappingSchedule(
                "DOC001",
                LocalDate.parse("2025-05-06"),
                LocalTime.of(9, 15),
                LocalTime.of(10, 15));

        assertEquals(2, result.size());
        assertTrue(result.contains(schedule1));
        assertTrue(result.contains(schedule3));
    }

    @Test
    void testFindOverlappingScheduleNoOverlap() {
        List<CaregiverSchedule> result = repository.findOverlappingSchedule(
                "DOC001",
                LocalDate.parse("2025-05-06"),
                LocalTime.of(11, 0),
                LocalTime.of(11, 30));

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByStatus() {
        List<CaregiverSchedule> result = repository.findByStatus("REQUESTED");

        assertEquals(1, result.size());
        assertEquals("SCHED002", result.get(0).getId());
    }

    @Test
    void testFindByStatusNoResult() {
        List<CaregiverSchedule> result = repository.findByStatus("REJECTED");

        assertTrue(result.isEmpty());
    }
}