package com.pandacare.mainapp.service;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorProfileServiceImplTest {

    @InjectMocks
    DoctorProfileServiceImpl doctorProfileService;

    @Mock
    DoctorProfileRepository doctorProfileRepository;

    List<DoctorProfile> doctorProfileList;

    @BeforeEach
    void setUp() {
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
    void createDoctorProfile() {
        DoctorProfile expected = doctorProfileList.getFirst();
        doReturn(expected).when(doctorProfileRepository).save(expected);

        DoctorProfile result = doctorProfileService.createProfile(expected);

        assertDoctorProfilesEqual(expected, result);
        verify(doctorProfileRepository, times(1)).save(expected);
    }

    @Test
    void testCreateDoctorProfileIfAlreadyExists() {
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        doReturn(doctorProfile).when(doctorProfileRepository).findById(doctorProfile.getId());

        assertNull(doctorProfileService.createProfile(doctorProfile));
        verify(doctorProfileRepository, times(0)).save(doctorProfile);
    }

    @Test
    void testUpdateDoctorProfileIfExists() {
        DoctorProfile doctorProfile = doctorProfileList.get(1);

        Map<String, String> workSchedule = new HashMap<>();
        workSchedule.put("Selasa", "15:00-18:00");
        workSchedule.put("Jumat", "19:00-21:00");

        DoctorProfile newDoctorProfile = new DoctorProfile(doctorProfile.getName(), "hafiz@premierebintaro.com", "082723726789", "RS Premiere Bintaro",
                workSchedule, doctorProfile.getSpeciality(), 4.95);
        newDoctorProfile.setId(doctorProfile.getId());

        doReturn(doctorProfile).when(doctorProfileRepository).findById(doctorProfile.getId());
        doReturn(newDoctorProfile).when(doctorProfileRepository).save(any(DoctorProfile.class));

        DoctorProfile result = doctorProfileService.updateProfile(newDoctorProfile);

        assertEquals(doctorProfile.getId(), result.getId());
        assertEquals(doctorProfile.getName(), result.getName());
        assertEquals(doctorProfile.getSpeciality(), result.getSpeciality());
        assertEquals(newDoctorProfile.getEmail(), result.getEmail());
        assertEquals(newDoctorProfile.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(newDoctorProfile.getWorkAddress(), result.getWorkAddress());
        assertEquals(newDoctorProfile.getWorkSchedule(), result.getWorkSchedule());
        assertEquals(newDoctorProfile.getRating(), result.getRating());
        verify(doctorProfileRepository, times(1)).save(any(DoctorProfile.class));
    }

    @Test
    void testUpdateDoctorProfileIfNotExist() {
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        doReturn(null).when(doctorProfileRepository).findById(doctorProfile.getId());

        assertNull(doctorProfileService.updateProfile(doctorProfile));
        verify(doctorProfileRepository, times(0)).save(doctorProfile);
    }

    @Test
    void testDeleteDoctorProfileIfExists() {
        DoctorProfile expected = doctorProfileList.getFirst();
        doReturn(expected).when(doctorProfileRepository).delete(expected);

        DoctorProfile result = doctorProfileService.deleteProfile(expected);

        assertDoctorProfilesEqual(expected, result);
        verify(doctorProfileRepository, times(1)).delete(expected);
    }

    @Test
    void testDeleteDoctorProfileIfNotExist() {
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        doReturn(null).when(doctorProfileRepository).delete(doctorProfile);

        assertNull(doctorProfileService.deleteProfile(doctorProfile));
        verify(doctorProfileRepository, times(1)).delete(doctorProfile);
    }

    @Test
    void testFindAllDoctorProfiles() {
        doReturn(doctorProfileList).when(doctorProfileRepository).findAll();

        List<DoctorProfile> result = doctorProfileService.findAll();
        assertEquals(doctorProfileList.size(), result.size());
    }

    @Test
    void testFindByIdIfFound() {
        DoctorProfile expected = doctorProfileList.get(1);
        doReturn(expected).when(doctorProfileRepository).findById(expected.getId());

        DoctorProfile result = doctorProfileService.findById(expected.getId());
        assertDoctorProfilesEqual(expected, result);
    }

    @Test
    void testFindByIdIfIdNotFound() {
        doReturn(null).when(doctorProfileRepository).findById("nonExistentId");
        DoctorProfile result = doctorProfileService.findById("nonExistentId");
        assertNull(result);
    }

    @Test
    void testFindByNameIfFound() {
        DoctorProfile expected = doctorProfileList.get(1);
        List<DoctorProfile> matchingNames = new ArrayList<>();
        matchingNames.add(expected);
        doReturn(matchingNames).when(doctorProfileRepository).findByName(expected.getName());

        List<DoctorProfile> result = doctorProfileService.searchDoctorProfile("NAME", expected.getName());
        assertEquals(matchingNames.size(), result.size());
        assertDoctorProfilesEqual(expected, result.getFirst());
    }

    @Test
    void testFindByNameIfNotFound() {
        List<DoctorProfile> matchingNames = new ArrayList<>();
        doReturn(matchingNames).when(doctorProfileRepository).findByName("Random Name");

        List<DoctorProfile> result = doctorProfileService.searchDoctorProfile("NAME", "Random Name");
        assertEquals(0, result.size());
    }

    @Test
    void testFindBySpecialityIfFound() {
        DoctorProfile expected = doctorProfileList.get(1);
        List<DoctorProfile> matchingNames = new ArrayList<>();
        matchingNames.add(expected);
        doReturn(matchingNames).when(doctorProfileRepository).findBySpeciality(expected.getSpeciality());

        List<DoctorProfile> result = doctorProfileService.searchDoctorProfile("SPECIALITY", expected.getSpeciality());
        assertEquals(matchingNames.size(), result.size());
        assertDoctorProfilesEqual(expected, result.getFirst());
    }

    @Test
    void testFindBySpecialityIfNotFound() {
        List<DoctorProfile> matchingNames = new ArrayList<>();
        doReturn(matchingNames).when(doctorProfileRepository).findBySpeciality("Random Speciality");

        List<DoctorProfile> result = doctorProfileService.searchDoctorProfile("SPECIALITY", "Random Speciality");
        assertEquals(0, result.size());
    }

    @Test
    void testFindByWorkScheduleIfFound() {
        DoctorProfile expected = doctorProfileList.getFirst();
        List<DoctorProfile> matchingNames = new ArrayList<>();
        matchingNames.add(expected);
        doReturn(matchingNames).when(doctorProfileRepository).findByWorkSchedule("Senin 10:30-12:30");

        List<DoctorProfile> result = doctorProfileService.searchDoctorProfile("WORK_SCHEDULE", "Senin 10:30-12:30");
        assertEquals(matchingNames.size(), result.size());
        assertDoctorProfilesEqual(expected, result.getFirst());
    }

    @Test
    void testFindByWorkScheduleIfNotFound() {
        List<DoctorProfile> matchingNames = new ArrayList<>();
        doReturn(matchingNames).when(doctorProfileRepository).findByWorkSchedule("Sabtu 13:00-16:00");

        List<DoctorProfile> result = doctorProfileService.searchDoctorProfile("WORK_SCHEDULE", "Sabtu 13:00-16:00");
        assertEquals(0, result.size());
    }
}
