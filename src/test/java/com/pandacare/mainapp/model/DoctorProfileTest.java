package com.pandacare.mainapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DoctorProfileTest {
    private DoctorProfile doctorProfile;

    @BeforeEach
    void setUp() {
        Map<String, String> workSchedule = new HashMap<>();
        workSchedule.put("Senin", "09:00-12:00");

        doctorProfile = new DoctorProfile();
        doctorProfile.setId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        doctorProfile.setName("Dr. Hafiz");
        doctorProfile.setEmail("hafiz@pandacare.com");
        doctorProfile.setPhoneNumber("08192836789");
        doctorProfile.setWorkAddress("RS Pandacare");
        doctorProfile.setWorkSchedule(workSchedule);
        doctorProfile.setSpeciality("Cardiologist");
        doctorProfile.setRating(4.9);
    }

    @Test
    void testGetDoctorProfileInfo() {
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", doctorProfile.getId());
        assertEquals("Dr. Hafiz", doctorProfile.getName());
        assertEquals("hafiz@pandacare.com", doctorProfile.getEmail());
        assertEquals("08192836789", doctorProfile.getPhoneNumber());
        assertEquals("RS Pandacare", doctorProfile.getWorkAddress());
        assertTrue(doctorProfile.getWorkSchedule().containsKey("Senin"));
        assertEquals("09:00-12:00", doctorProfile.getWorkSchedule().get("Senin"));
        assertEquals("Cardiologist", doctorProfile.getSpeciality());
        assertEquals(4.9, doctorProfile.getRating());
    }

    @Test
    void testSetDoctorProfileInfo() {
        Map<String, String> workSchedule = new HashMap<>();
        workSchedule.put("Selasa", "14:00-18:00");

        doctorProfile.setId("eb558e9f-1c39-460e-8860-71af6af63ds2");
        doctorProfile.setName("Dr. Jonah");
        doctorProfile.setEmail("jonah@pandacare.com");
        doctorProfile.setPhoneNumber("08123456789");
        doctorProfile.setWorkAddress("RS Pondok Indah");
        doctorProfile.setWorkSchedule(workSchedule);
        doctorProfile.setSpeciality("Orthopedic");
        doctorProfile.setRating(4.8);

        assertEquals("eb558e9f-1c39-460e-8860-71af6af63ds2", doctorProfile.getId());
        assertEquals("Dr. Jonah", doctorProfile.getName());
        assertEquals("jonah@pandacare.com", doctorProfile.getEmail());
        assertEquals("08123456789", doctorProfile.getPhoneNumber());
        assertEquals("RS Pondok Indah", doctorProfile.getWorkAddress());
        assertTrue(doctorProfile.getWorkSchedule().containsKey("Selasa"));
        assertEquals("14:00-18:00", doctorProfile.getWorkSchedule().get("Selasa"));
        assertEquals("Orthopedic", doctorProfile.getSpeciality());
        assertEquals(4.8, doctorProfile.getRating());
    }
}