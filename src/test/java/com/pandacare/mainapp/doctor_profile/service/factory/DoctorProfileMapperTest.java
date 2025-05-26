package com.pandacare.mainapp.doctor_profile.service.factory;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DoctorProfileMapperTest {

    @Test
    void mapToDoctorProfileResponse_WithValidInput_ReturnsCorrectResponse() {
        // Arrange
        UUID id = UUID.randomUUID();
        Caregiver caregiver = new Caregiver();
        caregiver.setId(id);
        caregiver.setName("Dr. Smith");
        caregiver.setEmail("dr.smith@example.com");
        caregiver.setPhoneNumber("1234567890");
        caregiver.setWorkAddress("123 Clinic St");
        caregiver.setSpeciality("Cardiology");

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(UUID.randomUUID());
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));        schedule.setEndTime(LocalTime.of(17, 0));
        caregiver.setWorkingSchedules(List.of(schedule));

        RatingListResponse ratingResponse = new RatingListResponse(4.5, 10, List.of());

        // Act
        DoctorProfileResponse response =
                DoctorProfileMapper.mapToDoctorProfileResponse(caregiver, ratingResponse);

        // Assert
        assertEquals(id, response.getCaregiverId());
        assertEquals("Dr. Smith", response.getName());
        assertEquals("Cardiology", response.getSpeciality());
        assertEquals(4.5, response.getAverageRating());
        assertEquals(10, response.getTotalRatings());
        assertNotNull(response.getRatings());
        assertEquals(1, response.getWorkSchedule().size());
    }

    @Test
    void mapToDoctorProfileResponse_WithNullRatings_ReturnsDefaultValues() {
        // Arrange
        Caregiver caregiver = new Caregiver();
        caregiver.setId(UUID.randomUUID());
        caregiver.setWorkingSchedules(List.of());

        // Act
        DoctorProfileResponse response =
                DoctorProfileMapper.mapToDoctorProfileResponse(caregiver, null);

        // Assert
        assertEquals(0.0, response.getAverageRating());
        assertEquals(0, response.getTotalRatings());
    }

    @Test
    void mapToDoctorProfileSummary_WithValidInput_ReturnsCorrectSummary() {
        // Arrange
        Caregiver caregiver = new Caregiver();
        caregiver.setId(UUID.randomUUID());        caregiver.setName("Dr. Jones");
        caregiver.setSpeciality("Neurology");

        RatingListResponse ratings = new RatingListResponse(4.8, 15, List.of());

        // Act
        DoctorProfileListResponse.DoctorProfileSummary summary =
                DoctorProfileMapper.mapToDoctorProfileSummary(caregiver, ratings);

        // Assert
        assertEquals("Dr. Jones", summary.getName());
        assertEquals("Neurology", summary.getSpeciality());
        assertEquals(4.8, summary.getAverageRating());
        assertEquals(15, summary.getTotalRatings());
    }
}