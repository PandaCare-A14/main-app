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

        DoctorProfileListResponse.DoctorProfileSummary second = response.getDoctorProfiles().get(1);
        assertEquals(doctor2Id, second.getCaregiverId());
        assertEquals("Dr. Bob", second.getName());
        assertEquals("Cardiology", second.getSpeciality());
        assertEquals(4.8, second.getAverageRating());
        assertEquals(15, second.getTotalRatings());
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
        assertEquals(doctorId, response.getDoctorProfiles().get(0).getCaregiverId());
        assertEquals("Dr. David", response.getDoctorProfiles().get(0).getName());
        assertEquals("Neurology", response.getDoctorProfiles().get(0).getSpeciality());
        assertEquals(4.7, response.getDoctorProfiles().get(0).getAverageRating());
        assertEquals(25, response.getDoctorProfiles().get(0).getTotalRatings());
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
        assertEquals("Dr. A", response.getDoctorProfiles().get(0).getName());
        assertEquals("Dr. B", response.getDoctorProfiles().get(1).getName());
        assertEquals("Dr. C", response.getDoctorProfiles().get(2).getName());
    }

    @Test
    void testSpecialCharactersInName() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. José María", "Cardiología", 4.8, 50
        );

        assertEquals("Dr. José María", summary.getName());
        assertEquals("Cardiología", summary.getSpeciality());
        assertEquals(4.8, summary.getAverageRating());
        assertEquals(50, summary.getTotalRatings());
    }

    @Test
    void testEquality() {
        UUID sameId = UUID.randomUUID();
        DoctorProfileListResponse.DoctorProfileSummary summary1 = new DoctorProfileListResponse.DoctorProfileSummary(
                sameId, "Dr. Same", "Same Specialty", 4.5, 20
        );
        DoctorProfileListResponse.DoctorProfileSummary summary2 = new DoctorProfileListResponse.DoctorProfileSummary(
                sameId, "Dr. Same", "Same Specialty", 4.5, 20
        );

        assertEquals(summary1, summary2);
        assertEquals(summary1.hashCode(), summary2.hashCode());
    }

    @Test
    void testInequality() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        DoctorProfileListResponse.DoctorProfileSummary summary1 = new DoctorProfileListResponse.DoctorProfileSummary(
                id1, "Dr. One", "Specialty One", 4.5, 20
        );
        DoctorProfileListResponse.DoctorProfileSummary summary2 = new DoctorProfileListResponse.DoctorProfileSummary(
                id2, "Dr. Two", "Specialty Two", 4.8, 25
        );

        assertNotEquals(summary1, summary2);
        assertNotEquals(summary1.hashCode(), summary2.hashCode());
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
        assertTrue(result.contains("DoctorProfileSummary"));
    }

    @Test
    void testListResponseToString() {
        String result = response.toString();
        assertNotNull(result);
        assertTrue(result.contains("DoctorProfileListResponse"));
        assertTrue(result.contains("totalItems"));
        assertTrue(result.contains("doctorProfiles"));
    }

    @Test
    void testListResponseEquality() {
        List<DoctorProfileListResponse.DoctorProfileSummary> doctors = Arrays.asList(
                new DoctorProfileListResponse.DoctorProfileSummary(doctor1Id, "Dr. Alice", "Dermatology", 4.5, 10),
                new DoctorProfileListResponse.DoctorProfileSummary(doctor2Id, "Dr. Bob", "Cardiology", 4.8, 15)
        );

        DoctorProfileListResponse response1 = new DoctorProfileListResponse(doctors, 2);
        DoctorProfileListResponse response2 = new DoctorProfileListResponse(doctors, 2);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testListResponseInequality() {
        List<DoctorProfileListResponse.DoctorProfileSummary> doctors1 = Arrays.asList(
                new DoctorProfileListResponse.DoctorProfileSummary(doctor1Id, "Dr. Alice", "Dermatology", 4.5, 10)
        );
        List<DoctorProfileListResponse.DoctorProfileSummary> doctors2 = Arrays.asList(
                new DoctorProfileListResponse.DoctorProfileSummary(doctor2Id, "Dr. Bob", "Cardiology", 4.8, 15)
        );

        DoctorProfileListResponse response1 = new DoctorProfileListResponse(doctors1, 1);
        DoctorProfileListResponse response2 = new DoctorProfileListResponse(doctors2, 1);

        assertNotEquals(response1, response2);
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testCanEqual() {
        DoctorProfileListResponse other = new DoctorProfileListResponse();
        assertTrue(response.canEqual(other));
        assertFalse(response.canEqual(new Object()));
        assertFalse(response.canEqual(null));
    }

    @Test
    void testSummaryCanEqual() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary();
        DoctorProfileListResponse.DoctorProfileSummary other = new DoctorProfileListResponse.DoctorProfileSummary();

        assertTrue(summary.canEqual(other));
        assertFalse(summary.canEqual(new Object()));
        assertFalse(summary.canEqual(null));
    }

    @Test
    void testEqualsWithSameReference() {
        assertEquals(response, response);
        assertEquals(response.hashCode(), response.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        assertNotEquals(response, null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertNotEquals(response, new Object());
    }

    @Test
    void testSummaryEqualsWithSameReference() {
        DoctorProfileListResponse.DoctorProfileSummary summary = response.getDoctorProfiles().get(0);
        assertEquals(summary, summary);
        assertEquals(summary.hashCode(), summary.hashCode());
    }

    @Test
    void testSummaryEqualsWithNull() {
        DoctorProfileListResponse.DoctorProfileSummary summary = response.getDoctorProfiles().get(0);
        assertNotEquals(summary, null);
    }

    @Test
    void testSummaryEqualsWithDifferentClass() {
        DoctorProfileListResponse.DoctorProfileSummary summary = response.getDoctorProfiles().get(0);
        assertNotEquals(summary, new Object());
    }

    @Test
    void testNullList() {
        DoctorProfileListResponse nullResponse = new DoctorProfileListResponse(null, 0);

        assertNull(nullResponse.getDoctorProfiles());
        assertEquals(0, nullResponse.getTotalItems());
    }

    @Test
    void testTotalItemsMismatch() {
        List<DoctorProfileListResponse.DoctorProfileSummary> singleDoctor = Arrays.asList(
                new DoctorProfileListResponse.DoctorProfileSummary(UUID.randomUUID(), "Dr. Single", "Single", 4.0, 1)
        );

        DoctorProfileListResponse mismatchResponse = new DoctorProfileListResponse(singleDoctor, 100);

        assertEquals(100, mismatchResponse.getTotalItems());
        assertEquals(1, mismatchResponse.getDoctorProfiles().size());
    }

    @Test
    void testMinimumRating() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. Minimum", "Min Specialty", Double.MIN_VALUE, 1
        );

        assertEquals(Double.MIN_VALUE, summary.getAverageRating());
        assertEquals(1, summary.getTotalRatings());
    }

    @Test
    void testMaximumRating() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. Maximum", "Max Specialty", Double.MAX_VALUE, Integer.MAX_VALUE
        );

        assertEquals(Double.MAX_VALUE, summary.getAverageRating());
        assertEquals(Integer.MAX_VALUE, summary.getTotalRatings());
    }

    @Test
    void testNegativeRating() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. Negative", "Negative Specialty", -1.0, -1
        );

        assertEquals(-1.0, summary.getAverageRating());
        assertEquals(-1, summary.getTotalRatings());
    }

    @Test
    void testLongSpecialtyName() {
        String longSpecialty = "Very Long Specialty Name That Exceeds Normal Length Expectations For Medical Specialties";
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. Long", longSpecialty, 4.5, 25
        );

        assertEquals(longSpecialty, summary.getSpeciality());
        assertEquals("Dr. Long", summary.getName());
    }

    @Test
    void testEmptyStrings() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "", "", 4.0, 5
        );

        assertEquals("", summary.getName());
        assertEquals("", summary.getSpeciality());
        assertEquals(4.0, summary.getAverageRating());
        assertEquals(5, summary.getTotalRatings());
    }

    @Test
    void testWhitespaceStrings() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "   ", "   ", 4.0, 5
        );

        assertEquals("   ", summary.getName());
        assertEquals("   ", summary.getSpeciality());
    }

    @Test
    void testDecimalRating() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                UUID.randomUUID(), "Dr. Decimal", "Decimal Specialty", 3.14159, 314
        );

        assertEquals(3.14159, summary.getAverageRating());
        assertEquals(314, summary.getTotalRatings());
    }

    @Test
    void testLargeTotalItems() {
        DoctorProfileListResponse response = new DoctorProfileListResponse(
                Collections.emptyList(),
                Long.MAX_VALUE
        );

        assertEquals(Long.MAX_VALUE, response.getTotalItems());
        assertTrue(response.getDoctorProfiles().isEmpty());
    }

    @Test
    void testNegativeTotalItems() {
        DoctorProfileListResponse response = new DoctorProfileListResponse(
                Collections.emptyList(),
                -100L
        );

        assertEquals(-100L, response.getTotalItems());
        assertTrue(response.getDoctorProfiles().isEmpty());
    }
}