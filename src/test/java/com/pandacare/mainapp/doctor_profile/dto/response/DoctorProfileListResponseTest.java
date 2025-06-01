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

    @Test
    void testToString() {
        String toString = response.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("2"));
        assertTrue(toString.contains("DoctorProfileListResponse"));
    }

    @Test
    void testEqualsAndHashCode() {
        // Test same object
        assertEquals(response, response);
        assertEquals(response.hashCode(), response.hashCode());

        // Test equal objects
        DoctorProfileListResponse sameResponse = new DoctorProfileListResponse();
        sameResponse.setDoctorProfiles(response.getDoctorProfiles());
        sameResponse.setTotalItems(response.getTotalItems());

        assertEquals(response, sameResponse);
        assertEquals(response.hashCode(), sameResponse.hashCode());

        // Test different objects
        DoctorProfileListResponse differentResponse = new DoctorProfileListResponse();
        differentResponse.setTotalItems(5);

        assertNotEquals(response, differentResponse);
        assertNotEquals(response.hashCode(), differentResponse.hashCode());

        // Test null
        assertNotEquals(response, null);

        // Test different class
        assertNotEquals(response, "not a response");
    }

    @Test
    void testAllArgsConstructor() {
        List<DoctorProfileListResponse.DoctorProfileSummary> doctors = Arrays.asList(
                new DoctorProfileListResponse.DoctorProfileSummary(
                        UUID.randomUUID(), "Dr. Test", "Testing", 5.0, 1)
        );

        DoctorProfileListResponse constructedResponse = new DoctorProfileListResponse(doctors, 1);

        assertEquals(doctors, constructedResponse.getDoctorProfiles());
        assertEquals(1, constructedResponse.getTotalItems());
    }

    @Test
    void testDoctorProfileSummaryNoArgsConstructor() {
        DoctorProfileListResponse.DoctorProfileSummary emptySummary = new DoctorProfileListResponse.DoctorProfileSummary();
        
        assertNull(emptySummary.getCaregiverId());
        assertNull(emptySummary.getName());
        assertNull(emptySummary.getSpeciality());
        assertNull(emptySummary.getAverageRating());
        assertNull(emptySummary.getTotalRatings());
    }

    @Test
    void testDoctorProfileSummarySettersAndGetters() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary();
        UUID testId = UUID.randomUUID();

        summary.setCaregiverId(testId);
        summary.setName("Dr. Setter");
        summary.setSpeciality("Setter Specialty");
        summary.setAverageRating(4.2);
        summary.setTotalRatings(42);

        assertEquals(testId, summary.getCaregiverId());
        assertEquals("Dr. Setter", summary.getName());
        assertEquals("Setter Specialty", summary.getSpeciality());
        assertEquals(4.2, summary.getAverageRating());
        assertEquals(42, summary.getTotalRatings());
    }

    @Test
    void testDoctorProfileSummaryEqualsAndHashCode() {
        UUID doctorId = UUID.randomUUID();
        
        DoctorProfileListResponse.DoctorProfileSummary summary1 = new DoctorProfileListResponse.DoctorProfileSummary(
                doctorId, "Dr. Equal", "Equality", 4.0, 20);
        
        DoctorProfileListResponse.DoctorProfileSummary summary2 = new DoctorProfileListResponse.DoctorProfileSummary(
                doctorId, "Dr. Equal", "Equality", 4.0, 20);

        // Test equality
        assertEquals(summary1, summary2);
        assertEquals(summary1.hashCode(), summary2.hashCode());

        // Test same object
        assertEquals(summary1, summary1);

        // Test inequality
        DoctorProfileListResponse.DoctorProfileSummary summary3 = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. Different", "Different", 3.0, 10);

        assertNotEquals(summary1, summary3);
        assertNotEquals(summary1.hashCode(), summary3.hashCode());

        // Test null
        assertNotEquals(summary1, null);

        // Test different class
        assertNotEquals(summary1, "not a summary");
    }

    @Test
    void testDoctorProfileSummaryToString() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. ToString", "ToString Specialty", 4.7, 35);

        String toString = summary.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Dr. ToString"));
        assertTrue(toString.contains("ToString Specialty"));
        assertTrue(toString.contains("4.7"));
        assertTrue(toString.contains("35"));
    }
}