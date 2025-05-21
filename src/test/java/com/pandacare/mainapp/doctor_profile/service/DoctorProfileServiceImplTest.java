package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
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

    @Mock
    private RatingService ratingService;

    private List<Caregiver> caregivers;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @BeforeEach
    void setUp() {
        caregivers = new ArrayList<>();

        Caregiver doctor1 = new Caregiver(
                "Dr. Hafiz",
                "3748275928347596",
                "08123456789",
                "RS Pandacare",
                "Cardiologist"
        );
        doctor1.setId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"));
        doctor1.setEmail("hafiz@pandacare.com");
        caregivers.add(doctor1);

        Caregiver doctor2 = new Caregiver(
                "Dr. Jonah",
                "3748275928347592",
                "08192836789",
                "RS Pondok Indah",
                "Orthopedic"
        );
        doctor2.setId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd7"));
        doctor2.setEmail("jonah@pandacare.com");
        caregivers.add(doctor2);
    }

    @Test
    void findAll_ShouldReturnDoctorProfileListResponse() {
        when(doctorProfileRepository.findAll()).thenReturn(caregivers);
        when(ratingService.getRatingsByDokter(anyString()))
                .thenReturn(new RatingListResponse(4.5, 10, Collections.emptyList()));

        DoctorProfileListResponse response = doctorProfileService.findAll();

        assertNotNull(response);
        assertEquals(2, response.getDoctorProfiles().size());
        assertEquals(2, response.getTotalItems());
        verify(doctorProfileRepository).findAll();
        verify(ratingService, times(2)).getRatingsByDokter(anyString());
    }

    @Test
    void findById_ShouldReturnDoctorProfileResponse() {
        UUID doctorId = caregivers.get(0).getId();
        when(doctorProfileRepository.findById(doctorId)).thenReturn(Optional.of(caregivers.get(0)));
        when(ratingService.getRatingsByDokter(doctorId.toString()))
                .thenReturn(new RatingListResponse(4.9, 15, Collections.emptyList()));

        DoctorProfileResponse response = doctorProfileService.findById(doctorId.toString());

        assertNotNull(response);
        assertEquals("Dr. Hafiz", response.getName());
        assertEquals("hafiz@pandacare.com", response.getEmail());
        assertEquals("Cardiologist", response.getSpeciality());
        assertEquals(4.9, response.getAverageRating());
        assertEquals(15, response.getTotalRatings());
    }

    @Test
    void findById_ShouldThrowException_WhenInvalidIdFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findById("invalid-uuid");
        });
    }

    @Test
    void findById_ShouldReturnNull_WhenNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(doctorProfileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        DoctorProfileResponse response = doctorProfileService.findById(nonExistentId.toString());

        assertNull(response);
    }

    @Test
    void findByName_ShouldReturnFilteredDoctors() {
        String searchName = "Hafiz";
        when(doctorProfileRepository.findByNameContainingIgnoreCase(searchName))
                .thenReturn(Collections.singletonList(caregivers.get(0)));
        when(ratingService.getRatingsByDokter(anyString()))
                .thenReturn(new RatingListResponse(4.9, 15, Collections.emptyList()));

        DoctorProfileListResponse response = doctorProfileService.findByName(searchName);

        assertNotNull(response);
        assertEquals(1, response.getDoctorProfiles().size());
        assertEquals("Dr. Hafiz", response.getDoctorProfiles().get(0).getName());
    }

    @Test
    void findByName_ShouldThrowException_WhenNameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByName("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByName(null);
        });
    }

    @Test
    void findBySpeciality_ShouldReturnFilteredDoctors() {
        String speciality = "Cardio";
        when(doctorProfileRepository.findBySpecialityContainingIgnoreCase(speciality))
                .thenReturn(Collections.singletonList(caregivers.get(0)));
        when(ratingService.getRatingsByDokter(anyString()))
                .thenReturn(new RatingListResponse(4.9, 15, Collections.emptyList()));

        DoctorProfileListResponse response = doctorProfileService.findBySpeciality(speciality);

        assertNotNull(response);
        assertEquals(1, response.getDoctorProfiles().size());
        assertEquals("Cardiologist", response.getDoctorProfiles().get(0).getSpeciality());
    }

    @Test
    void findBySpeciality_ShouldThrowException_WhenSpecialityEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findBySpeciality("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findBySpeciality(null);
        });
    }

    @Test
    void findByWorkSchedule_ShouldReturnAvailableDoctors() {
        String schedule = "MONDAY 10:00-11:00";
        when(doctorProfileRepository.findByWorkScheduleAvailable(
                DayOfWeek.MONDAY,
                LocalTime.parse("10:00", timeFormatter),
                LocalTime.parse("11:00", timeFormatter)))
                .thenReturn(Collections.singletonList(caregivers.get(0)));
        when(ratingService.getRatingsByDokter(anyString()))
                .thenReturn(new RatingListResponse(4.9, 15, Collections.emptyList()));

        DoctorProfileListResponse response = doctorProfileService.findByWorkSchedule(schedule);

        assertNotNull(response);
        assertEquals(1, response.getDoctorProfiles().size());
    }

    @Test
    void findByWorkSchedule_ShouldThrowException_WhenInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByWorkSchedule("InvalidFormat");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByWorkSchedule("MondayWithoutTime");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByWorkSchedule("Monday 10:00-");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByWorkSchedule("Monday 25:00-12:00");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByWorkSchedule("InvalidDay 10:00-11:00");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByWorkSchedule("MONDAY 11:00-10:00");
        });
    }

    @Test
    void findByWorkSchedule_ShouldThrowException_WhenEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByWorkSchedule("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findByWorkSchedule(null);
        });
    }

    @Test
    void getDoctorProfileListResponse_ShouldHandleNullRatings() {
        when(ratingService.getRatingsByDokter(anyString())).thenReturn(null);

        DoctorProfileListResponse response = doctorProfileService.getDoctorProfileListResponse(caregivers);

        assertNotNull(response);
        assertEquals(2, response.getDoctorProfiles().size());
        assertEquals(0.0, response.getDoctorProfiles().get(0).getAverageRating());
        assertEquals(0, response.getDoctorProfiles().get(0).getTotalRatings());
    }

    @Test
    void getDoctorProfileListResponse_ShouldHandleEmptyList() {
        DoctorProfileListResponse response = doctorProfileService.getDoctorProfileListResponse(Collections.emptyList());

        assertNotNull(response);
        assertEquals(0, response.getDoctorProfiles().size());
        assertEquals(0, response.getTotalItems());
    }
}