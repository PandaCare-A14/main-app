package com.pandacare.mainapp.doctor_profile.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DoctorResponseTest {

    private DoctorResponse response;

    private static final String ID = "doc-001";
    private static final String NAME = "Dr. Clara";
    private static final String EMAIL = "clara@pandacare.com";
    private static final String PHONE = "+6281122334455";
    private static final String ADDRESS = "RS Pandacare";
    private static final String SPECIALITY = "Neurology";
    private static final double RATING = 4.7;
    private static final LocalDateTime CREATED_AT = LocalDateTime.now().minusDays(2);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        response = new DoctorResponse();
        Map<String, String> schedule = new HashMap<>();
        schedule.put("Kamis", "10:00-13:00");

        response.setId(ID);
        response.setName(NAME);
        response.setEmail(EMAIL);
        response.setPhoneNumber(PHONE);
        response.setWorkAddress(ADDRESS);
        response.setWorkSchedule(schedule);
        response.setSpeciality(SPECIALITY);
        response.setRating(RATING);
        response.setCreatedAt(CREATED_AT);
        response.setUpdatedAt(UPDATED_AT);
    }

    @Test
    void testFieldValues() {
        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(EMAIL, response.getEmail());
        assertEquals(PHONE, response.getPhoneNumber());
        assertEquals(ADDRESS, response.getWorkAddress());
        assertEquals(SPECIALITY, response.getSpeciality());
        assertEquals(RATING, response.getRating());
        assertEquals(CREATED_AT, response.getCreatedAt());
        assertEquals(UPDATED_AT, response.getUpdatedAt());

        assertNotNull(response.getWorkSchedule());
        assertTrue(response.getWorkSchedule().containsKey("Kamis"));
        assertEquals("10:00-13:00", response.getWorkSchedule().get("Kamis"));
    }
}
