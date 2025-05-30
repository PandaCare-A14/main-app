package com.pandacare.mainapp.doctor_profile.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
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

    @Test
    void testAllArgsConstructor() {
        UUID doctorId = UUID.randomUUID();
        List<DoctorProfileListResponse.DoctorProfileSummary> doctors = Arrays.asList(
                new DoctorProfileListResponse.DoctorProfileSummary(
                        doctorId, "Dr. David", "Neurology", 4.7, 25)
        );

        DoctorProfileListResponse response = new DoctorProfileListResponse(doctors, 1);

        assertEquals(1, response.getTotalItems());
        assertEquals(doctors, response.getDoctorProfiles());
        assertEquals(1, response.getDoctorProfiles().size());
    }

    @Test
    void testDoctorProfileSummaryEmptyConstructor() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary();

        assertNull(summary.getCaregiverId());
        assertNull(summary.getName());
        assertNull(summary.getSpeciality());
        assertNull(summary.getAverageRating());
        assertNull(summary.getTotalRatings());
    }

    @Test
    void testDoctorProfileSummarySettersAndGetters() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary();
        UUID doctorId = UUID.randomUUID();

        summary.setCaregiverId(doctorId);
        summary.setName("Dr. Eve");
        summary.setSpeciality("Orthopedics");
        summary.setAverageRating(4.6);
        summary.setTotalRatings(30);

        assertEquals(doctorId, summary.getCaregiverId());
        assertEquals("Dr. Eve", summary.getName());
        assertEquals("Orthopedics", summary.getSpeciality());
        assertEquals(4.6, summary.getAverageRating());
        assertEquals(30, summary.getTotalRatings());
    }

    @Test
    void testEmptyList() {
        DoctorProfileListResponse response = new DoctorProfileListResponse(Collections.emptyList(), 0);

        assertEquals(0, response.getTotalItems());
        assertNotNull(response.getDoctorProfiles());
        assertTrue(response.getDoctorProfiles().isEmpty());
    }

    @Test
    void testNullValues() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                null, null, null, null, null
        );

        assertNull(summary.getCaregiverId());
        assertNull(summary.getName());
        assertNull(summary.getSpeciality());
        assertNull(summary.getAverageRating());
        assertNull(summary.getTotalRatings());
    }

    @Test
    void testZeroRating() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. Frank", "Psychiatry", 0.0, 0
        );

        assertEquals(0.0, summary.getAverageRating());
        assertEquals(0, summary.getTotalRatings());
    }

    @Test
    void testMaxRating() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. Grace", "Surgery", 5.0, 100
        );

        assertEquals(5.0, summary.getAverageRating());
        assertEquals(100, summary.getTotalRatings());
    }

    @Test
    void testLargeDataSet() {
        List<DoctorProfileListResponse.DoctorProfileSummary> largeDoctorList = Arrays.asList(
                new DoctorProfileListResponse.DoctorProfileSummary(UUID.randomUUID(), "Dr. A", "Specialty A", 4.1, 1),
                new DoctorProfileListResponse.DoctorProfileSummary(UUID.randomUUID(), "Dr. B", "Specialty B", 4.2, 2),
                new DoctorProfileListResponse.DoctorProfileSummary(UUID.randomUUID(), "Dr. C", "Specialty C", 4.3, 3)
        );

        DoctorProfileListResponse response = new DoctorProfileListResponse(largeDoctorList, 1000);

        assertEquals(1000, response.getTotalItems());
        assertEquals(3, response.getDoctorProfiles().size());
    }

    @Test
    void testSpecialCharactersInName() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. José María", "Cardiología", 4.8, 50
        );

        assertEquals("Dr. José María", summary.getName());
        assertEquals("Cardiología", summary.getSpeciality());
    }

    @Test
    void testEquality() {
        UUID sameId = UUID.randomUUID();
        DoctorProfileListResponse.DoctorProfileSummary summary1 = new DoctorProfileListResponse.DoctorProfileSummary();
        DoctorProfileListResponse.DoctorProfileSummary summary2 = new DoctorProfileListResponse.DoctorProfileSummary();

        summary1.setCaregiverId(sameId);
        summary2.setCaregiverId(sameId);

        assertEquals(summary1, summary2);
        assertEquals(summary1.hashCode(), summary2.hashCode());
    }

    @Test
    void testToString() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. Henry", "Radiology", 4.4, 35
        );

        String result = summary.toString();
        assertNotNull(result);
        assertTrue(result.contains("Dr. Henry"));
        assertTrue(result.contains("Radiology"));
    }
}