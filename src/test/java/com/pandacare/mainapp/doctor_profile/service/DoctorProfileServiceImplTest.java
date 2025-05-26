package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import com.pandacare.mainapp.doctor_profile.service.strategy.ParsedWorkSchedule;
import com.pandacare.mainapp.doctor_profile.service.strategy.WorkScheduleParser;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@EnableAsync
class DoctorProfileServiceImplTest {

    @InjectMocks
    private DoctorProfileServiceImpl doctorProfileService;

    @Mock
    private DoctorProfileRepository doctorProfileRepository;

    @Mock
    private RatingService ratingService;

    @Mock
    private WorkScheduleParser workScheduleParser;

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
    void findAll_ShouldReturnAllDoctorsWithRatings() throws ExecutionException, InterruptedException {
        when(doctorProfileRepository.findAll()).thenReturn(caregivers);
        when(ratingService.getRatingsByDokter(any(UUID.class)))
                .thenReturn(new RatingListResponse(4.5, 10, Collections.emptyList()));

        CompletableFuture<DoctorProfileListResponse> future = doctorProfileService.findAll();
        DoctorProfileListResponse response = future.get();

        assertNotNull(response);
        assertEquals(2, response.getDoctorProfiles().size());
        assertEquals(2, response.getTotalItems());
        assertEquals("Dr. Hafiz", response.getDoctorProfiles().get(0).getName());
        assertEquals("Cardiologist", response.getDoctorProfiles().get(0).getSpeciality());
        assertEquals(4.5, response.getDoctorProfiles().get(0).getAverageRating());
        assertEquals(10, response.getDoctorProfiles().get(0).getTotalRatings());
        verify(doctorProfileRepository).findAll();
        verify(ratingService, times(2)).getRatingsByDokter(any(UUID.class));
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoDoctors() throws ExecutionException, InterruptedException {
        when(doctorProfileRepository.findAll()).thenReturn(Collections.emptyList());

        CompletableFuture<DoctorProfileListResponse> future = doctorProfileService.findAll();
        DoctorProfileListResponse response = future.get();

        assertNotNull(response);
        assertEquals(0, response.getDoctorProfiles().size());
        assertEquals(0, response.getTotalItems());
        verify(doctorProfileRepository).findAll();
        verify(ratingService, never()).getRatingsByDokter(any(UUID.class));
    }

    @Test
    void findById_ShouldReturnDoctorWithDetails() throws ExecutionException, InterruptedException {
        UUID doctorId = caregivers.get(0).getId();
        when(doctorProfileRepository.findById(doctorId)).thenReturn(Optional.of(caregivers.get(0)));
        when(ratingService.getRatingsByDokter(doctorId))
                .thenReturn(new RatingListResponse(4.9, 15, Collections.emptyList()));

        CompletableFuture<DoctorProfileResponse> future = doctorProfileService.findById(doctorId);
        DoctorProfileResponse response = future.get();

        assertNotNull(response);
        assertEquals(doctorId, response.getCaregiverId());
        assertEquals("Dr. Hafiz", response.getName());
        assertEquals("hafiz@pandacare.com", response.getEmail());
        assertEquals("08123456789", response.getPhoneNumber());
        assertEquals("RS Pandacare", response.getWorkAddress());
        assertEquals("Cardiologist", response.getSpeciality());
        assertEquals(4.9, response.getAverageRating());
        assertEquals(15, response.getTotalRatings());
        verify(doctorProfileRepository).findById(doctorId);
        verify(ratingService).getRatingsByDokter(doctorId);
    }

    @Test
    void findById_ShouldReturnNullWhenNotFound() throws ExecutionException, InterruptedException {
        UUID nonExistentId = UUID.randomUUID();
        when(doctorProfileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        CompletableFuture<DoctorProfileResponse> future = doctorProfileService.findById(nonExistentId);
        DoctorProfileResponse response = future.get();

        assertNull(response);
        verify(doctorProfileRepository).findById(nonExistentId);
        verify(ratingService, never()).getRatingsByDokter(any(UUID.class));
    }

    @Test
    void findById_ShouldThrowExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            doctorProfileService.findById(null).join();
        });
    }

    @Test
    void searchByCriteria_ShouldReturnDoctorsByNameAndSpeciality() throws ExecutionException, InterruptedException {
        String name = "Hafiz";
        String speciality = "Cardio";

        when(doctorProfileRepository.findByNameContainingIgnoreCaseAndSpecialityContainingIgnoreCase(name, speciality))
                .thenReturn(Collections.singletonList(caregivers.get(0)));
        when(ratingService.getRatingsByDokter(any(UUID.class)))
                .thenReturn(new RatingListResponse(4.5, 10, Collections.emptyList()));

        CompletableFuture<DoctorProfileListResponse> future = doctorProfileService.searchByCriteria(name, speciality, null, null, null);
        DoctorProfileListResponse response = future.get();

        assertNotNull(response);
        assertEquals(1, response.getDoctorProfiles().size());
        assertEquals("Dr. Hafiz", response.getDoctorProfiles().get(0).getName());
        assertEquals("Cardiologist", response.getDoctorProfiles().get(0).getSpeciality());
        verify(doctorProfileRepository).findByNameContainingIgnoreCaseAndSpecialityContainingIgnoreCase(name, speciality);
    }

    @Test
    void searchByCriteria_ShouldReturnDoctorsByNameOnly() throws ExecutionException, InterruptedException {
        String name = "Hafiz";

        when(doctorProfileRepository.findByNameContainingIgnoreCase(name))
                .thenReturn(Collections.singletonList(caregivers.get(0)));
        when(ratingService.getRatingsByDokter(any(UUID.class)))
                .thenReturn(new RatingListResponse(4.5, 10, Collections.emptyList()));

        CompletableFuture<DoctorProfileListResponse> future = doctorProfileService.searchByCriteria(name, null, null, null, null);
        DoctorProfileListResponse response = future.get();

        assertNotNull(response);
        assertEquals(1, response.getDoctorProfiles().size());
        assertEquals("Dr. Hafiz", response.getDoctorProfiles().get(0).getName());
        verify(doctorProfileRepository).findByNameContainingIgnoreCase(name);
    }

    @Test
    void searchByCriteria_ShouldReturnDoctorsBySpecialityOnly() throws ExecutionException, InterruptedException {
        String speciality = "Ortho";

        when(doctorProfileRepository.findBySpecialityContainingIgnoreCase(speciality))
                .thenReturn(Collections.singletonList(caregivers.get(1)));
        when(ratingService.getRatingsByDokter(any(UUID.class)))
                .thenReturn(new RatingListResponse(4.5, 10, Collections.emptyList()));

        CompletableFuture<DoctorProfileListResponse> future = doctorProfileService.searchByCriteria(null, speciality, null, null, null);
        DoctorProfileListResponse response = future.get();

        assertNotNull(response);
        assertEquals(1, response.getDoctorProfiles().size());
        assertEquals("Orthopedic", response.getDoctorProfiles().get(0).getSpeciality());
        verify(doctorProfileRepository).findBySpecialityContainingIgnoreCase(speciality);
    }

    @Test
    void searchByCriteria_ShouldReturnDoctorsByScheduleOnly() throws ExecutionException, InterruptedException {
        String day = "MONDAY";
        String startTime = "09:00";
        String endTime = "12:00";

        when(workScheduleParser.parse(day + " " + startTime + "-" + endTime))
                .thenReturn(new ParsedWorkSchedule(DayOfWeek.MONDAY, LocalTime.parse(startTime), LocalTime.parse(endTime)));
        when(doctorProfileRepository.findByWorkingSchedulesAvailable(DayOfWeek.MONDAY, LocalTime.parse(startTime), LocalTime.parse(endTime)))
                .thenReturn(caregivers);
        when(ratingService.getRatingsByDokter(any(UUID.class)))
                .thenReturn(new RatingListResponse(4.5, 10, Collections.emptyList()));

        CompletableFuture<DoctorProfileListResponse> future = doctorProfileService.searchByCriteria(null, null, day, startTime, endTime);
        DoctorProfileListResponse response = future.get();

        assertNotNull(response);
        assertEquals(2, response.getDoctorProfiles().size());
        verify(doctorProfileRepository).findByWorkingSchedulesAvailable(DayOfWeek.MONDAY, LocalTime.parse(startTime), LocalTime.parse(endTime));
    }

    @Test
    void searchByCriteria_ShouldReturnDoctorsByCombinedFilters() throws ExecutionException, InterruptedException {
        String name = "Hafiz";
        String speciality = "Cardio";
        String day = "MONDAY";
        String startTime = "09:00";
        String endTime = "12:00";

        when(workScheduleParser.parse(day + " " + startTime + "-" + endTime))
                .thenReturn(new ParsedWorkSchedule(DayOfWeek.MONDAY, LocalTime.parse(startTime), LocalTime.parse(endTime)));
        when(doctorProfileRepository.findByWorkingSchedulesAvailable(DayOfWeek.MONDAY, LocalTime.parse(startTime), LocalTime.parse(endTime)))
                .thenReturn(caregivers);
        when(ratingService.getRatingsByDokter(any(UUID.class)))
                .thenReturn(new RatingListResponse(4.5, 10, Collections.emptyList()));

        CompletableFuture<DoctorProfileListResponse> future = doctorProfileService.searchByCriteria(name, speciality, day, startTime, endTime);
        DoctorProfileListResponse response = future.get();

        assertNotNull(response);
        assertEquals(1, response.getDoctorProfiles().size()); // After filtering
        assertEquals("Dr. Hafiz", response.getDoctorProfiles().get(0).getName());
        verify(doctorProfileRepository).findByWorkingSchedulesAvailable(DayOfWeek.MONDAY, LocalTime.parse(startTime), LocalTime.parse(endTime));
    }    @Test
    void searchByCriteria_ShouldThrowExceptionWhenNoParameters() {
        CompletionException exception = assertThrows(CompletionException.class, () -> {
            doctorProfileService.searchByCriteria(null, null, null, null, null).join();
        });
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("At least one search parameter must be provided", exception.getCause().getMessage());
    }

    @Test
    void getDoctorProfileListResponse_ShouldHandleNullRatings() {
        when(ratingService.getRatingsByDokter(any(UUID.class))).thenReturn(null);

        DoctorProfileListResponse response = doctorProfileService.getDoctorProfileListResponse(caregivers);

        assertNotNull(response);
        assertEquals(2, response.getDoctorProfiles().size());
        assertEquals(0.0, response.getDoctorProfiles().get(0).getAverageRating());
        assertEquals(0, response.getDoctorProfiles().get(0).getTotalRatings());
        verify(ratingService, times(2)).getRatingsByDokter(any(UUID.class));
    }

    @Test
    void getDoctorProfileListResponse_ShouldHandleEmptyList() {
        DoctorProfileListResponse response = doctorProfileService.getDoctorProfileListResponse(Collections.emptyList());

        assertNotNull(response);
        assertEquals(0, response.getDoctorProfiles().size());
        assertEquals(0, response.getTotalItems());
        verify(ratingService, never()).getRatingsByDokter(any(UUID.class));
    }

    @Test
    void getDoctorProfileListResponse_ShouldHandleRatingServiceException() {
        when(ratingService.getRatingsByDokter(any(UUID.class))).thenThrow(new RuntimeException("Rating service error"));

        DoctorProfileListResponse response = doctorProfileService.getDoctorProfileListResponse(caregivers);

        assertNotNull(response);
        assertEquals(2, response.getDoctorProfiles().size());
        assertEquals(0.0, response.getDoctorProfiles().get(0).getAverageRating());
        assertEquals(0, response.getDoctorProfiles().get(0).getTotalRatings());
        verify(ratingService, times(2)).getRatingsByDokter(any(UUID.class));
    }
}