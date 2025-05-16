package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorProfileServiceImplTest {

    @InjectMocks
    private DoctorProfileServiceImpl doctorProfileService;

    @Mock
    private DoctorProfileRepository doctorProfileRepository;

    private List<DoctorProfile> doctorProfileList;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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
    void createProfile_ShouldCreateNewProfile_WhenValidInput() {
        DoctorProfile newProfile = doctorProfileList.get(0);
        when(doctorProfileRepository.existsById(newProfile.getId())).thenReturn(false);
        when(doctorProfileRepository.save(newProfile)).thenReturn(newProfile);

        DoctorProfile result = doctorProfileService.createProfile(newProfile);

        assertNotNull(result);
        assertDoctorProfilesEqual(newProfile, result);
        verify(doctorProfileRepository).existsById(newProfile.getId());
        verify(doctorProfileRepository).save(newProfile);
    }

    @Test
    void createProfile_ShouldReturnNull_WhenProfileExists() {
        DoctorProfile existingProfile = doctorProfileList.get(0);
        when(doctorProfileRepository.existsById(existingProfile.getId())).thenReturn(true);

        DoctorProfile result = doctorProfileService.createProfile(existingProfile);

        assertNull(result);
        verify(doctorProfileRepository).existsById(existingProfile.getId());
        verify(doctorProfileRepository, never()).save(existingProfile);
    }

    @Test
    void updateProfile_ShouldUpdateExistingProfile() {
        DoctorProfile existing = doctorProfileList.get(0);
        DoctorProfile updated = new DoctorProfile(
                "Dr. Hafiz Updated", "updated@email.com", "08123456789",
                "New Address", existing.getWorkSchedule(), "Updated Speciality", 5.0);
        updated.setId(existing.getId());

        when(doctorProfileRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(doctorProfileRepository.save(updated)).thenReturn(updated);

        DoctorProfile result = doctorProfileService.updateProfile(updated);

        assertNotNull(result);
        assertEquals(updated.getName(), result.getName());
        verify(doctorProfileRepository).findById(existing.getId());
        verify(doctorProfileRepository).save(updated);
    }

    @Test
    void deleteProfile_ShouldReturnTrue_WhenProfileExists() {
        String id = doctorProfileList.get(0).getId();
        when(doctorProfileRepository.existsById(id)).thenReturn(true);

        boolean result = doctorProfileService.deleteProfile(id);

        assertTrue(result);
        verify(doctorProfileRepository).existsById(id);
        verify(doctorProfileRepository).deleteById(id);
    }

    @Test
    void findAll_ShouldReturnAllProfiles() {
        when(doctorProfileRepository.findAll()).thenReturn(doctorProfileList);

        List<DoctorProfile> result = doctorProfileService.findAll();

        assertEquals(2, result.size());
        verify(doctorProfileRepository).findAll();
    }

    @Test
    void findById_ShouldReturnProfile_WhenExists() {
        DoctorProfile expected = doctorProfileList.get(0);
        when(doctorProfileRepository.findById(expected.getId())).thenReturn(Optional.of(expected));

        DoctorProfile result = doctorProfileService.findById(expected.getId());

        assertNotNull(result);
        assertDoctorProfilesEqual(expected, result);
        verify(doctorProfileRepository).findById(expected.getId());
    }

    @Test
    void findByName_ShouldReturnMatchingProfiles() {
        String name = "Hafiz";
        List<DoctorProfile> expected = Collections.singletonList(doctorProfileList.get(0));
        when(doctorProfileRepository.findByNameContainingIgnoreCase(name)).thenReturn(expected);

        List<DoctorProfile> result = doctorProfileService.findByName(name);

        assertEquals(1, result.size());
        assertDoctorProfilesEqual(expected.get(0), result.get(0));
        verify(doctorProfileRepository).findByNameContainingIgnoreCase(name);
    }

    @Test
    void findBySpeciality_ShouldReturnMatchingProfiles() {
        String speciality = "Cardio";
        List<DoctorProfile> expected = Collections.singletonList(doctorProfileList.get(0));
        when(doctorProfileRepository.findBySpecialityContainingIgnoreCase(speciality)).thenReturn(expected);

        List<DoctorProfile> result = doctorProfileService.findBySpeciality(speciality);

        assertEquals(1, result.size());
        assertDoctorProfilesEqual(expected.get(0), result.get(0));
        verify(doctorProfileRepository).findBySpecialityContainingIgnoreCase(speciality);
    }

    @Test
    void findByWorkSchedule_ShouldReturnMatchingProfiles() {
        String schedule = "Senin 10:00-11:00";
        String day = "Senin";
        LocalTime start = LocalTime.parse("10:00", timeFormatter);
        LocalTime end = LocalTime.parse("11:00", timeFormatter);

        List<DoctorProfile> expected = Collections.singletonList(doctorProfileList.get(0));
        when(doctorProfileRepository.findByWorkScheduleAvailable(day, start, end)).thenReturn(expected);

        List<DoctorProfile> result = doctorProfileService.findByWorkSchedule(schedule);

        assertEquals(1, result.size());
        assertDoctorProfilesEqual(expected.get(0), result.get(0));
        verify(doctorProfileRepository).findByWorkScheduleAvailable(day, start, end);
    }

    @Test
    void findByWorkSchedule_ShouldThrowException_WhenInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByWorkSchedule("InvalidFormat");
        });
    }
}