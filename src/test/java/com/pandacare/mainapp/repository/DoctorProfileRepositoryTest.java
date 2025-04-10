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

    @Test
    void testSaveDoctorProfile() {
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        DoctorProfile result = doctorProfileRepository.save(doctorProfile);

        DoctorProfile expected = doctorProfileRepository.findById(doctorProfile.getId());
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getEmail(), result.getEmail());
        assertEquals(expected.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(expected.getWorkAddress(), result.getWorkAddress());
        assertEquals(expected.getWorkSchedule(), result.getWorkSchedule());
        assertEquals(expected.getSpeciality(), result.getSpeciality());
        assertEquals(expected.getRating(), result.getRating());
    }

    @Test
    void testDeleteDoctorProfile() {
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        DoctorProfile expected = doctorProfileRepository.save(doctorProfileList.getFirst());
        DoctorProfile result = doctorProfileRepository.delete(doctorProfile);

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getEmail(), result.getEmail());
        assertEquals(expected.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(expected.getWorkAddress(), result.getWorkAddress());
        assertEquals(expected.getWorkSchedule(), result.getWorkSchedule());
        assertEquals(expected.getSpeciality(), result.getSpeciality());
        assertEquals(expected.getRating(), result.getRating());
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

        DoctorProfile doctorProfile = doctorProfileList.get(1);
        DoctorProfile result = doctorProfileRepository.findById(doctorProfile.getId());

        assertEquals(doctorProfile.getId(), result.getId());
        assertEquals(doctorProfile.getName(), result.getName());
        assertEquals(doctorProfile.getEmail(), result.getEmail());
        assertEquals(doctorProfile.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(doctorProfile.getWorkAddress(), result.getWorkAddress());
        assertEquals(doctorProfile.getWorkSchedule(), result.getWorkSchedule());
        assertEquals(doctorProfile.getSpeciality(), result.getSpeciality());
        assertEquals(doctorProfile.getRating(), result.getRating());
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

        DoctorProfile doctorProfile = doctorProfileList.get(1);
        List<DoctorProfile> result = doctorProfileRepository.findByName(doctorProfile.getName());

        assertEquals(1, result.size());
        assertEquals(doctorProfile.getId(), result.getFirst().getId());
        assertEquals(doctorProfile.getName(), result.getFirst().getName());
        assertEquals(doctorProfile.getEmail(), result.getFirst().getEmail());
        assertEquals(doctorProfile.getPhoneNumber(), result.getFirst().getPhoneNumber());
        assertEquals(doctorProfile.getWorkAddress(), result.getFirst().getWorkAddress());
        assertEquals(doctorProfile.getWorkSchedule(), result.getFirst().getWorkSchedule());
        assertEquals(doctorProfile.getSpeciality(), result.getFirst().getSpeciality());
        assertEquals(doctorProfile.getRating(), result.getFirst().getRating());
    }

    @Test
    void testFindDoctorProfileBySpeciality() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        DoctorProfile doctorProfile = doctorProfileList.get(1);
        List<DoctorProfile> result = doctorProfileRepository.findBySpeciality(doctorProfile.getSpeciality());

        assertEquals(1, result.size());
        assertEquals(doctorProfile.getId(), result.getFirst().getId());
        assertEquals(doctorProfile.getName(), result.getFirst().getName());
        assertEquals(doctorProfile.getEmail(), result.getFirst().getEmail());
        assertEquals(doctorProfile.getPhoneNumber(), result.getFirst().getPhoneNumber());
        assertEquals(doctorProfile.getWorkAddress(), result.getFirst().getWorkAddress());
        assertEquals(doctorProfile.getWorkSchedule(), result.getFirst().getWorkSchedule());
        assertEquals(doctorProfile.getSpeciality(), result.getFirst().getSpeciality());
        assertEquals(doctorProfile.getRating(), result.getFirst().getRating());
    }

    @Test
    void testFindDoctorProfileByWorkSchedule() {
        for (DoctorProfile doctorProfile : doctorProfileList) {
            doctorProfileRepository.save(doctorProfile);
        }

        DoctorProfile doctorProfile = doctorProfileList.get(1);
        List<DoctorProfile> result = doctorProfileRepository.findByWorkSchedule("Selasa", "12:00-15:00");

        assertEquals(1, result.size());
        assertEquals(doctorProfile.getId(), result.getFirst().getId());
        assertEquals(doctorProfile.getName(), result.getFirst().getName());
        assertEquals(doctorProfile.getEmail(), result.getFirst().getEmail());
        assertEquals(doctorProfile.getPhoneNumber(), result.getFirst().getPhoneNumber());
        assertEquals(doctorProfile.getWorkAddress(), result.getFirst().getWorkAddress());
        assertEquals(doctorProfile.getWorkSchedule(), result.getFirst().getWorkSchedule());
        assertEquals(doctorProfile.getSpeciality(), result.getFirst().getSpeciality());
        assertEquals(doctorProfile.getRating(), result.getFirst().getRating());
    }
}
