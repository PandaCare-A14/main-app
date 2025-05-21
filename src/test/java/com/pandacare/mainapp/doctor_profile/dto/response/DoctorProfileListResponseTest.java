package com.pandacare.mainapp.doctor_profile.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DoctorProfileListResponseTest {

    private DoctorProfileListResponse response;

    @BeforeEach
    void setUp() {
        DoctorProfileListResponse.DoctorProfileSummary doctor1 = new DoctorProfileListResponse.DoctorProfileSummary(
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Dr. Alice",
                "Dermatology",
                4.5,
                10
        );

        DoctorProfileListResponse.DoctorProfileSummary doctor2 = new DoctorProfileListResponse.DoctorProfileSummary(
                "eb558e9f-1c39-460e-8860-71af6af63bi4",
                "Dr. Bob",
                "Cardiology",
                4.8,
                15
        );

        List<DoctorProfileListResponse.DoctorProfileSummary> doctors = Arrays.asList(doctor1, doctor2);

        response = new DoctorProfileListResponse(doctors, 100);
    }

    @Test
    void testListResponseFields() {
        assertEquals(100, response.getTotalItems());
        assertNotNull(response.getDoctorProfiles());
        assertEquals(2, response.getDoctorProfiles().size());

        DoctorProfileListResponse.DoctorProfileSummary first = response.getDoctorProfiles().get(0);
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", first.getId());
        assertEquals("Dr. Alice", first.getName());
        assertEquals("Dermatology", first.getSpeciality());
        assertEquals(4.5, first.getAverageRating());
        assertEquals(10, first.getTotalRatings());
    }

    @Test
    void testDoctorProfileSummary() {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary(
                "eb558e9f-1c39-460e-8860-71af6af63op0",
                "Dr. Charlie",
                "Pediatrics",
                4.9,
                20
        );

        assertEquals("eb558e9f-1c39-460e-8860-71af6af63op0", summary.getId());
        assertEquals("Dr. Charlie", summary.getName());
        assertEquals(4.9, summary.getAverageRating());
    }
}