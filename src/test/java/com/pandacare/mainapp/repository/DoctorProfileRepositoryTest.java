package com.pandacare.mainapp.repository;

import com.pandacare.mainapp.model.DoctorProfile;
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

        DoctorProfile doctorProfile1 = new DoctorProfile();
        doctorProfile1.setId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        doctorProfile1.setName("Dr. Hafiz");
        doctorProfile1.setEmail("hafiz@pandacare.com");
        doctorProfile1.setPhoneNumber("08123456789");
        doctorProfile1.setWorkAddress("RS Pandacare");
        doctorProfile1.setWorkSchedule(workSchedule1);
        doctorProfile1.setSpeciality("Cardiologist");
        doctorProfile1.setRating(4.9);
        doctorProfileList.add(doctorProfile1);

        Map<String, String> workSchedule2 = new HashMap<>();
        workSchedule2.put("Selasa", "14:00-18:00");
        workSchedule2.put("Kamis", "09:00-12:00");

        DoctorProfile doctorProfile2 = new DoctorProfile();
        doctorProfile2.setId("eb558e9f-1c39-460e-8860-71af6af63ds2");
        doctorProfile2.setName("Dr. Jonah");
        doctorProfile2.setEmail("jonah@pandacare.com");
        doctorProfile2.setPhoneNumber("08192836789");
        doctorProfile2.setWorkAddress("RS Pondok Indah");
        doctorProfile2.setWorkSchedule(workSchedule2);
        doctorProfile2.setSpeciality("Orthopedic");
        doctorProfile2.setRating(4.8);
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
    void testDeleteDoctorProfile() {
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        DoctorProfile expected = doctorProfileRepository.save(doctorProfileList.getFirst());
        DoctorProfile result = doctorProfileRepository.delete(doctorProfile);

        assertDoctorProfilesEqual(expected, result);
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
    void testFindDoctorProfileByName() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        DoctorProfile expected = doctorProfileList.get(1);
        List<DoctorProfile> result = doctorProfileRepository.findByName(expected.getName());

        assertEquals(1, result.size());
        assertDoctorProfilesEqual(expected, result.getFirst());
    }

    @Test
    void testFindDoctorProfileBySpeciality() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        DoctorProfile expected = doctorProfileList.get(1);
        List<DoctorProfile> result = doctorProfileRepository.findBySpeciality(expected.getSpeciality());

        assertEquals(1, result.size());
        assertDoctorProfilesEqual(expected, result.getFirst());
    }

    @Test
    void testFindDoctorProfileByWorkSchedule() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        DoctorProfile expected = doctorProfileList.get(1);
        List<DoctorProfile> result = doctorProfileRepository.findByWorkSchedule("Selasa", "12:00-15:00");

        assertEquals(1, result.size());
        assertDoctorProfilesEqual(expected, result.getFirst());
    }
}
