package com.pandacare.mainapp.doctor_profile.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DoctorListResponseTest {

    private DoctorListResponse response;

    @BeforeEach
    void setUp() {
        DoctorListResponse.DoctorSummary doctor1 = new DoctorListResponse.DoctorSummary();
        doctor1.setId("doc-001");
        doctor1.setName("Dr. Alice");
        doctor1.setSpeciality("Dermatology");
        doctor1.setRating(4.5);

        DoctorListResponse.DoctorSummary doctor2 = new DoctorListResponse.DoctorSummary();
        doctor2.setId("doc-002");
        doctor2.setName("Dr. Bob");
        doctor2.setSpeciality("Cardiology");
        doctor2.setRating(4.8);

        List<DoctorListResponse.DoctorSummary> doctors = Arrays.asList(doctor1, doctor2);

        response = new DoctorListResponse();
        response.setDoctors(doctors);
        response.setCurrentPage(1);
        response.setTotalPages(5);
        response.setTotalItems(100);
    }

    @Test
    void testListResponseFields() {
        assertEquals(1, response.getCurrentPage());
        assertEquals(5, response.getTotalPages());
        assertEquals(100, response.getTotalItems());
        assertNotNull(response.getDoctors());
        assertEquals(2, response.getDoctors().size());

        DoctorListResponse.DoctorSummary first = response.getDoctors().get(0);
        assertEquals("doc-001", first.getId());
        assertEquals("Dr. Alice", first.getName());
        assertEquals("Dermatology", first.getSpeciality());
        assertEquals(4.5, first.getRating());

        DoctorListResponse.DoctorSummary second = response.getDoctors().get(1);
        assertEquals("doc-002", second.getId());
        assertEquals("Dr. Bob", second.getName());
        assertEquals("Cardiology", second.getSpeciality());
        assertEquals(4.8, second.getRating());
    }
}
