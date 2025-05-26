package com.pandacare.mainapp.doctor_profile.repository;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = "com.pandacare.mainapp")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DoctorProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    private Caregiver caregiver1;
    private Caregiver caregiver2;    @BeforeEach
    void setUp() {
        // Create caregivers
        caregiver1 = new Caregiver("Dr. Hafiz", "3704892357482376", "08123456789", "RS Pandacare", "Cardiologist");
        caregiver1.setId(UUID.randomUUID()); // Set ID manually since no auto-generation

        caregiver2 = new Caregiver("Dr. Jonah", "3704892357482377", "08192836789", "RS Pondok Indah", "Orthopedic");
        caregiver2.setId(UUID.randomUUID()); // Set ID manually since no auto-generation

        // Add working schedules
        CaregiverSchedule schedule1 = new CaregiverSchedule();
        schedule1.setDay(DayOfWeek.MONDAY);
        schedule1.setStartTime(LocalTime.of(9, 0));
        schedule1.setEndTime(LocalTime.of(12, 0));
        schedule1.setStatus(ScheduleStatus.AVAILABLE);
        caregiver1.addWorkingSchedule(schedule1);

        CaregiverSchedule schedule2 = new CaregiverSchedule();
        schedule2.setDay(DayOfWeek.WEDNESDAY);
        schedule2.setStartTime(LocalTime.of(10, 0));
        schedule2.setEndTime(LocalTime.of(13, 0));
        schedule2.setStatus(ScheduleStatus.AVAILABLE);
        caregiver1.addWorkingSchedule(schedule2);

        CaregiverSchedule schedule3 = new CaregiverSchedule();
        schedule3.setDay(DayOfWeek.MONDAY);
        schedule3.setStartTime(LocalTime.of(14, 0));
        schedule3.setEndTime(LocalTime.of(17, 0));
        schedule3.setStatus(ScheduleStatus.AVAILABLE);
        caregiver2.addWorkingSchedule(schedule3);
    }

    // Utility method
    private void assertCaregiversEqual(Caregiver expected, Caregiver actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getNik(), actual.getNik());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals(expected.getWorkAddress(), actual.getWorkAddress());
        assertEquals(expected.getSpeciality(), actual.getSpeciality());
    }

    @Test
    void testSaveCaregiver() {
        Caregiver saved = doctorProfileRepository.save(caregiver1);
        Caregiver found = entityManager.find(Caregiver.class, caregiver1.getId());

        assertNotNull(saved);
        assertCaregiversEqual(caregiver1, found);
    }

    @Test
    void testSaveUpdateCaregiver() {
        // First save
        doctorProfileRepository.save(caregiver1);

        // Update
        Caregiver updatedCaregiver = new Caregiver(caregiver1.getName(), "3704892357482399",
                "082723726789", "RS Premiere Bintaro", "Cardiologist");
        updatedCaregiver.setId(caregiver1.getId());

        Caregiver result = doctorProfileRepository.save(updatedCaregiver);
        Caregiver findResult = doctorProfileRepository.findById(caregiver1.getId()).orElse(null);

        assertNotNull(findResult);
        assertEquals(caregiver1.getId(), result.getId());
        assertCaregiversEqual(updatedCaregiver, findResult);
    }

    @Test
    void testDeleteCaregiverIfExists() {
        entityManager.persist(caregiver1);

        doctorProfileRepository.deleteById(caregiver1.getId());

        Caregiver deleted = entityManager.find(Caregiver.class, caregiver1.getId());
        assertNull(deleted);
    }

    @Test
    void testFindAllCaregivers() {
        entityManager.persist(caregiver1);
        entityManager.persist(caregiver2);

        List<Caregiver> result = doctorProfileRepository.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testFindCaregiverByIdIfIdExists() {
        entityManager.persist(caregiver1);

        Optional<Caregiver> result = doctorProfileRepository.findById(caregiver1.getId());
        assertTrue(result.isPresent());
        assertCaregiversEqual(caregiver1, result.get());
    }

    @Test
    void testFindCaregiverByIdIfIdNotExist() {
        Optional<Caregiver> result = doctorProfileRepository.findById(UUID.randomUUID());
        assertFalse(result.isPresent());
    }

    @Test
    void testFindCaregiverByNameIfFound() {
        entityManager.persist(caregiver1);
        entityManager.persist(caregiver2);

        List<Caregiver> result = doctorProfileRepository.findByNameContainingIgnoreCase("Hafiz");
        assertEquals(1, result.size());
        assertCaregiversEqual(caregiver1, result.get(0));
    }

    @Test
    void testFindCaregiverByNameIfNotFound() {
        List<Caregiver> result = doctorProfileRepository.findByNameContainingIgnoreCase("Nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindCaregiverBySpecialityIfFound() {
        entityManager.persist(caregiver1);
        entityManager.persist(caregiver2);

        List<Caregiver> result = doctorProfileRepository.findBySpecialityContainingIgnoreCase("Cardio");
        assertEquals(1, result.size());
        assertCaregiversEqual(caregiver1, result.get(0));
    }

    @Test
    void testFindCaregiverBySpecialityIfNotFound() {
        List<Caregiver> result = doctorProfileRepository.findBySpecialityContainingIgnoreCase("Nonexistent");
        assertTrue(result.isEmpty());
    }    @Test
    void testFindByWorkScheduleAvailable() {
        entityManager.persist(caregiver1);
        entityManager.persist(caregiver2);

        // Search for Monday 10:00-11:00 (should match caregiver1's 9:00-12:00 schedule)
        List<Caregiver> result = doctorProfileRepository.findByWorkingSchedulesAvailable(
                DayOfWeek.MONDAY,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0));

        assertEquals(1, result.size());
        assertEquals(caregiver1.getId(), result.get(0).getId());

        // Search for Monday 16:00-17:00 (should match caregiver2's 14:00-17:00 schedule)
        result = doctorProfileRepository.findByWorkingSchedulesAvailable(
                DayOfWeek.MONDAY,
                LocalTime.of(16, 0),
                LocalTime.of(17, 0));

        assertEquals(1, result.size());
        assertEquals(caregiver2.getId(), result.get(0).getId());

        // Search for Wednesday 11:00-12:00 (should match caregiver1's 10:00-13:00 schedule)
        result = doctorProfileRepository.findByWorkingSchedulesAvailable(
                DayOfWeek.WEDNESDAY,
                LocalTime.of(11, 0),
                LocalTime.of(12, 0));

        assertEquals(1, result.size());
        assertEquals(caregiver1.getId(), result.get(0).getId());

        // Search for time with no available caregivers
        result = doctorProfileRepository.findByWorkingSchedulesAvailable(
                DayOfWeek.FRIDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0));

        assertTrue(result.isEmpty());
    }
}