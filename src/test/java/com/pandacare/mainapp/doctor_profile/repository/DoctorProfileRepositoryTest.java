package com.pandacare.mainapp.doctor_profile.repository;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DoctorProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    private DoctorProfile doctorProfile1;
    private DoctorProfile doctorProfile2;

    @BeforeEach
    void setUp() {
        Map<String, String> workSchedule1 = new HashMap<>();
        workSchedule1.put("Senin", "09:00-12:00");
        workSchedule1.put("Rabu", "10:00-13:00");

        doctorProfile1 = new DoctorProfile("Dr. Hafiz", "hafiz@pandacare.com", "08123456789", "RS Pandacare",
                workSchedule1, "Cardiologist", 4.9);
        doctorProfile1.setId("eb558e9f-1c39-460e-8860-71af6af63bd6");

        Map<String, String> workSchedule2 = new HashMap<>();
        workSchedule2.put("Selasa", "14:00-18:00");
        workSchedule2.put("Kamis", "09:00-12:00");

        doctorProfile2 = new DoctorProfile("Dr. Jonah", "jonah@pandacare.com", "08192836789", "RS Pondok Indah",
                workSchedule2, "Orthopedic", 4.8);
        doctorProfile2.setId("eb558e9f-1c39-460e-8860-71af6af63ds2");
    }

    // Utility method
    private void assertDoctorProfilesEqual(DoctorProfile expected, DoctorProfile actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals(expected.getWorkAddress(), actual.getWorkAddress());
        assertEquals(expected.getWorkSchedule(), actual.getWorkSchedule());
        assertEquals(expected.getSpeciality(), actual.getSpeciality());
        assertEquals(expected.getRating(), actual.getRating());
    }

    @Test
    void testSaveDoctorProfile() {
        DoctorProfile saved = doctorProfileRepository.save(doctorProfile1);
        DoctorProfile found = entityManager.find(DoctorProfile.class, doctorProfile1.getId());

        assertNotNull(saved);
        assertDoctorProfilesEqual(doctorProfile1, found);
    }

    @Test
    void testSaveUpdateDoctorProfile() {
        // First save
        doctorProfileRepository.save(doctorProfile1);

        // Update
        Map<String, String> workSchedule = new HashMap<>();
        workSchedule.put("Selasa", "15:00-18:00");
        workSchedule.put("Jumat", "19:00-21:00");

        DoctorProfile updatedProfile = new DoctorProfile(doctorProfile1.getName(), "hafiz@premierebintaro.com",
                "082723726789", "RS Premiere Bintaro", workSchedule, doctorProfile1.getSpeciality(), 4.95);
        updatedProfile.setId(doctorProfile1.getId());

        DoctorProfile result = doctorProfileRepository.save(updatedProfile);
        DoctorProfile findResult = doctorProfileRepository.findById(doctorProfile1.getId()).orElse(null);

        assertNotNull(findResult);
        assertEquals(doctorProfile1.getId(), result.getId());
        assertDoctorProfilesEqual(updatedProfile, findResult);
    }

    @Test
    void testDeleteDoctorProfileIfExists() {
        entityManager.persist(doctorProfile1);

        doctorProfileRepository.deleteById(doctorProfile1.getId());

        DoctorProfile deleted = entityManager.find(DoctorProfile.class, doctorProfile1.getId());
        assertNull(deleted);
    }

    @Test
    void testFindAllDoctorProfile() {
        entityManager.persist(doctorProfile1);
        entityManager.persist(doctorProfile2);

        List<DoctorProfile> result = doctorProfileRepository.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testFindDoctorProfileByIdIfIdExists() {
        entityManager.persist(doctorProfile1);

        Optional<DoctorProfile> result = doctorProfileRepository.findById(doctorProfile1.getId());
        assertTrue(result.isPresent());
        assertDoctorProfilesEqual(doctorProfile1, result.get());
    }

    @Test
    void testFindDoctorProfileByIdIfIdNotExist() {
        Optional<DoctorProfile> result = doctorProfileRepository.findById("NonExistentId");
        assertFalse(result.isPresent());
    }

    @Test
    void testFindDoctorProfileByNameIfFound() {
        entityManager.persist(doctorProfile1);
        entityManager.persist(doctorProfile2);

        List<DoctorProfile> result = doctorProfileRepository.findByNameContainingIgnoreCase("Hafiz");
        assertEquals(1, result.size());
        assertDoctorProfilesEqual(doctorProfile1, result.get(0));
    }

    @Test
    void testFindDoctorProfileByNameIfNotFound() {
        List<DoctorProfile> result = doctorProfileRepository.findByNameContainingIgnoreCase("Nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindDoctorProfileBySpecialityIfFound() {
        entityManager.persist(doctorProfile1);
        entityManager.persist(doctorProfile2);

        List<DoctorProfile> result = doctorProfileRepository.findBySpecialityContainingIgnoreCase("Cardio");
        assertEquals(1, result.size());
        assertDoctorProfilesEqual(doctorProfile1, result.get(0));
    }

    @Test
    void testFindDoctorProfileBySpecialityIfNotFound() {
        List<DoctorProfile> result = doctorProfileRepository.findBySpecialityContainingIgnoreCase("Nonexistent");
        assertTrue(result.isEmpty());
    }

    // Note: The work schedule tests would need to be adjusted to use the new findByWorkScheduleAvailable method
    // which takes separate day, start time, and end time parameters rather than a combined string
}