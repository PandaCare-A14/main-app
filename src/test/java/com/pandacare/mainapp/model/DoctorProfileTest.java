package com.pandacare.mainapp.model;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DoctorProfileTest {
    private DoctorProfile doctorProfile;

    // Define the test data constants for reuse
    private static final String INITIAL_ID = "eb558e9f-1c39-460e-8860-71af6af63bd6";
    private static final String INITIAL_NAME = "Dr. Hafiz";
    private static final String INITIAL_EMAIL = "hafiz@pandacare.com";
    private static final String INITIAL_PHONE_NUMBER = "08123456789";
    private static final String INITIAL_WORK_ADDRESS = "RS Pandacare";
    private static final String INITIAL_SPECIALITY = "Cardiologist";
    private static final double INITIAL_RATING = 4.9;

    private static final String UPDATED_ID = "eb558e9f-1c39-460e-8860-71af6af63ds2";
    private static final String UPDATED_NAME = "Dr. Jonah";
    private static final String UPDATED_EMAIL = "jonah@pandacare.com";
    private static final String UPDATED_PHONE_NUMBER = "08192836789";
    private static final String UPDATED_WORK_ADDRESS = "RS Pondok Indah";
    private static final String UPDATED_SPECIALITY = "Orthopedic";
    private static final double UPDATED_RATING = 4.8;

    @BeforeEach
    void setUp() {
        Map<String, String> workSchedule = new HashMap<>();
        workSchedule.put("Senin", "09:00-12:00");

        doctorProfile = createDoctorProfile(INITIAL_ID, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PHONE_NUMBER, INITIAL_WORK_ADDRESS, workSchedule, INITIAL_SPECIALITY, INITIAL_RATING);
    }

    private DoctorProfile createDoctorProfile(String id, String name, String email, String phoneNumber, String workAddress, Map<String, String> workSchedule, String speciality, double rating) {
        DoctorProfile doctorProfile = new DoctorProfile(name, email, phoneNumber, workAddress, workSchedule, speciality, rating);
        doctorProfile.setId(id);
        return doctorProfile;
    }

    @Test
    void testGetDoctorProfileInfo() {
        assertEquals(INITIAL_ID, doctorProfile.getId());
        assertEquals(INITIAL_NAME, doctorProfile.getName());
        assertEquals(INITIAL_EMAIL, doctorProfile.getEmail());
        assertEquals(INITIAL_PHONE_NUMBER, doctorProfile.getPhoneNumber());
        assertEquals(INITIAL_WORK_ADDRESS, doctorProfile.getWorkAddress());
        assertTrue(doctorProfile.getWorkSchedule().containsKey("Senin"));
        assertEquals("09:00-12:00", doctorProfile.getWorkSchedule().get("Senin"));
        assertEquals(INITIAL_SPECIALITY, doctorProfile.getSpeciality());
        assertEquals(INITIAL_RATING, doctorProfile.getRating());
    }

    @Test
    void testSetDoctorProfileInfo() {
        Map<String, String> workSchedule = new HashMap<>();
        workSchedule.put("Selasa", "14:00-18:00");

        doctorProfile.setId(UPDATED_ID);
        doctorProfile.setName(UPDATED_NAME);
        doctorProfile.setEmail(UPDATED_EMAIL);
        doctorProfile.setPhoneNumber(UPDATED_PHONE_NUMBER);
        doctorProfile.setWorkAddress(UPDATED_WORK_ADDRESS);
        doctorProfile.setWorkSchedule(workSchedule);
        doctorProfile.setSpeciality(UPDATED_SPECIALITY);
        doctorProfile.setRating(UPDATED_RATING);

        assertEquals(UPDATED_ID, doctorProfile.getId());
        assertEquals(UPDATED_NAME, doctorProfile.getName());
        assertEquals(UPDATED_EMAIL, doctorProfile.getEmail());
        assertEquals(UPDATED_PHONE_NUMBER, doctorProfile.getPhoneNumber());
        assertEquals(UPDATED_WORK_ADDRESS, doctorProfile.getWorkAddress());
        assertTrue(doctorProfile.getWorkSchedule().containsKey("Selasa"));
        assertEquals("14:00-18:00", doctorProfile.getWorkSchedule().get("Selasa"));
        assertEquals(UPDATED_SPECIALITY, doctorProfile.getSpeciality());
        assertEquals(UPDATED_RATING, doctorProfile.getRating());
    }
}