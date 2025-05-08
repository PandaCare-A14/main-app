package com.pandacare.mainapp.doctor_profile.repository;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DoctorProfileRepositoryTest {
    DoctorProfileRepository doctorProfileRepository;
    List<DoctorProfile> doctorProfileList;

    @BeforeEach
    void setUp() {
        doctorProfileRepository = new DoctorProfileRepository();
        doctorProfileList = new ArrayList<>();

        Map<String, String> workSchedule1 = new HashMap<>();
        workSchedule1.put("Senin", "09:00-12:00");
        workSchedule1.put("Rabu", "10:00-13:00");

        DoctorProfile doctorProfile1 = new DoctorProfile("Dr. Hafiz", "hafiz@pandacare.com", "08123456789", "RS Pandacare",
                workSchedule1, "Cardiologist", 4.9);
        doctorProfile1.setId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        doctorProfileList.add(doctorProfile1);

        Map<String, String> workSchedule2 = new HashMap<>();
        workSchedule2.put("Selasa", "14:00-18:00");
        workSchedule2.put("Kamis", "09:00-12:00");

        DoctorProfile doctorProfile2 = new DoctorProfile("Dr. Jonah", "jonah@pandacare.com", "08192836789", "RS Pondok Indah",
                workSchedule2, "Orthopedic", 4.8);
        doctorProfile2.setId("eb558e9f-1c39-460e-8860-71af6af63ds2");
        doctorProfileList.add(doctorProfile2);
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
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        DoctorProfile result = doctorProfileRepository.save(doctorProfile);

        DoctorProfile expected = doctorProfileRepository.findById(doctorProfile.getId());
        assertDoctorProfilesEqual(expected, result);
    }

    @Test
    void testSaveNullDoctorProfile() {
        DoctorProfile doctorProfile = null;
        DoctorProfile result = doctorProfileRepository.save(doctorProfile);

        assertNull(result);
    }

    @Test
    void testSaveDoctorProfileWithNullId() {
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        doctorProfile.setId(null);
        DoctorProfile result = doctorProfileRepository.save(doctorProfile);

        assertNull(result);
    }

    @Test
    void testSaveUpdateDoctorProfile() {
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        doctorProfileRepository.save(doctorProfile);

        Map<String, String> workSchedule = new HashMap<>();
        workSchedule.put("Selasa", "15:00-18:00");
        workSchedule.put("Jumat", "19:00-21:00");

        DoctorProfile newDoctorProfile = new DoctorProfile(doctorProfile.getName(), "hafiz@premierebintaro.com", "082723726789", "RS Premiere Bintaro",
                workSchedule, doctorProfile.getSpeciality(), 4.95);
        newDoctorProfile.setId(doctorProfile.getId());

        DoctorProfile result = doctorProfileRepository.save(newDoctorProfile);
        DoctorProfile findResult = doctorProfileRepository.findById(doctorProfile.getId());

        assertEquals(doctorProfile.getId(), result.getId());
        assertEquals(doctorProfile.getId(), findResult.getId());
        assertDoctorProfilesEqual(newDoctorProfile, findResult);
    }

    @Test
    void testDeleteDoctorProfileIfExists() {
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        DoctorProfile expected = doctorProfileRepository.save(doctorProfileList.getFirst());
        DoctorProfile result = doctorProfileRepository.delete(doctorProfile);

        assertDoctorProfilesEqual(expected, result);
    }

    @Test
    void testDeleteDoctorProfileIfNotExist() {
        DoctorProfile unsavedDoctorProfile = doctorProfileList.getFirst();
        DoctorProfile result = doctorProfileRepository.delete(unsavedDoctorProfile);

        assertNull(result);
    }

    @Test
    void testFindAllDoctorProfile() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        List<DoctorProfile> result= doctorProfileRepository.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testFindDoctorProfileByIdIfIdExists() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        DoctorProfile expected = doctorProfileList.get(1);
        DoctorProfile result = doctorProfileRepository.findById(expected.getId());

        assertDoctorProfilesEqual(expected, result);
    }

    @Test
    void testFindDoctorProfileByIdIfIdNotExist() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        DoctorProfile result = doctorProfileRepository.findById("NonExistentId");

        assertNull(result);
    }

    @Test
    void testFindDoctorProfileByNameIfFound() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        DoctorProfile expected = doctorProfileList.get(1);
        List<DoctorProfile> result = doctorProfileRepository.findByName(expected.getName());

        assertEquals(1, result.size());
        assertDoctorProfilesEqual(expected, result.getFirst());
    }

    @Test
    void testFindDoctorProfileByNameIfNotFound() {
        DoctorProfile expected = doctorProfileList.get(1);
        List<DoctorProfile> result = doctorProfileRepository.findByName(expected.getName());

        assertEquals(0, result.size());
    }

    @Test
    void testFindDoctorProfileBySpecialityIfFound() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        DoctorProfile expected = doctorProfileList.get(1);
        List<DoctorProfile> result = doctorProfileRepository.findBySpeciality(expected.getSpeciality());

        assertEquals(1, result.size());
        assertDoctorProfilesEqual(expected, result.getFirst());
    }

    @Test
    void testFindDoctorProfileBySpecialityIfNotFound() {
        DoctorProfile expected = doctorProfileList.get(1);
        List<DoctorProfile> result = doctorProfileRepository.findBySpeciality(expected.getSpeciality());

        assertEquals(0, result.size());
    }

    @Test
    void testFindDoctorProfileByWorkSchedule() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        DoctorProfile expected1 = doctorProfileList.get(0);
        DoctorProfile expected2 = doctorProfileList.get(1);
        List<DoctorProfile> result1 = doctorProfileRepository.findByWorkSchedule("Senin 11:00-13:00");
        List<DoctorProfile> result2 = doctorProfileRepository.findByWorkSchedule("Selasa 12:00-15:00");


        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertDoctorProfilesEqual(expected1, result1.getFirst());
        assertDoctorProfilesEqual(expected2, result2.getFirst());
    }

    @Test
    void testFindByWorkScheduleSkipDoctorTooShortOverlap() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        List<DoctorProfile> result1 = doctorProfileRepository.findByWorkSchedule("Senin 11:45-13:00");
        List<DoctorProfile> result2 = doctorProfileRepository.findByWorkSchedule("Selasa 12:00-14:05");
        List<DoctorProfile> result3 = doctorProfileRepository.findByWorkSchedule("Selasa 12:00-13:00");
        List<DoctorProfile> result4 = doctorProfileRepository.findByWorkSchedule("Senin 12:30-13:00");

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertTrue(result3.isEmpty());
        assertTrue(result4.isEmpty());
    }

}
