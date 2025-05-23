package com.pandacare.mainapp.doctor_profile.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class DoctorProfileListResponseTest {

    private DoctorProfileListResponse response;
    private UUID doctor1Id;
    private UUID doctor2Id;

    @BeforeEach
    void setUp() {
        doctor1Id = UUID.randomUUID();
        doctor2Id = UUID.randomUUID();

        DoctorProfileListResponse.DoctorProfileSummary doctor1 = new DoctorProfileListResponse.DoctorProfileSummary(
                doctor1Id,
                "Dr. Alice",
                "Dermatology",
                4.5,
                10
        );

        DoctorProfileListResponse.DoctorProfileSummary doctor2 = new DoctorProfileListResponse.DoctorProfileSummary(
                doctor2Id,
                "Dr. Bob",
                "Cardiology",
                4.8,
                15
        );

        List<DoctorProfileListResponse.DoctorProfileSummary> doctors = Arrays.asList(doctor1, doctor2);

        response = new DoctorProfileListResponse();
        response.setDoctorProfiles(doctors);
        response.setTotalItems(2);
    }

    @Test
    void testListResponseFields() {
        assertEquals(2, response.getTotalItems());
        assertNotNull(response.getDoctorProfiles());
        assertEquals(2, response.getDoctorProfiles().size());

        DoctorProfileListResponse.DoctorProfileSummary first = response.getDoctorProfiles().get(0);
        assertEquals(doctor1Id, first.getCaregiverId());
        assertEquals("Dr. Alice", first.getName());
        assertEquals("Dermatology", first.getSpeciality());
        assertEquals(4.5, first.getAverageRating());
        assertEquals(10, first.getTotalRatings());
    }

    @Test
    void testDoctorProfileSummary() {
        UUID doctorId = UUID.randomUUID();
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                doctorId,
                "Dr. Charlie",
                "Pediatrics",
                4.9,
                20
        );

        assertEquals(doctorId, summary.getCaregiverId());
        assertEquals("Dr. Charlie", summary.getName());
        assertEquals("Pediatrics", summary.getSpeciality());
        assertEquals(4.9, summary.getAverageRating());
        assertEquals(20, summary.getTotalRatings());
    }

    @Test
    void testEmptyConstructor() {
        DoctorProfileListResponse emptyResponse = new DoctorProfileListResponse();
        assertNull(emptyResponse.getDoctorProfiles());
        assertEquals(0, emptyResponse.getTotalItems());
    }

    @Test
    void testSettersAndGetters() {
        DoctorProfileListResponse testResponse = new DoctorProfileListResponse();

        List<DoctorProfileListResponse.DoctorProfileSummary> doctors = Arrays.asList(
                new DoctorProfileListResponse.DoctorProfileSummary(
                        UUID.randomUUID(), "Dr. Test", "Testing", 5.0, 1)
        );

        testResponse.setDoctorProfiles(doctors);
        testResponse.setTotalItems(42);

        assertEquals(doctors, testResponse.getDoctorProfiles());
        assertEquals(42, testResponse.getTotalItems());
    }
}