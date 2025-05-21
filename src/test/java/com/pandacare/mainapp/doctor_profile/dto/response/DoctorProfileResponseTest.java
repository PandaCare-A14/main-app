package com.pandacare.mainapp.doctor_profile.dto.response;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DoctorProfileResponseTest {

    private DoctorProfileResponse response;
    private static final String ID = "eb558e9f-1c39-460e-8860-71af6af63bd6";
    private static final String NAME = "Dr. Clara";
    private static final String EMAIL = "clara@pandacare.com";
    private static final String PHONE = "081122334455";
    private static final String WORK_ADDRESS = "RS Pandacare";
    private static final String SPECIALITY = "Neurology";
    private static final Double AVERAGE_RATING = 4.7;
    private static final int TOTAL_RATINGS = 25;

    @BeforeEach
    void setUp() {
        List<CaregiverSchedule> schedules = new ArrayList<>();
        response = new DoctorProfileResponse(
                ID,
                NAME,
                EMAIL,
                PHONE,
                WORK_ADDRESS,
                schedules,
                SPECIALITY,
                AVERAGE_RATING,
                TOTAL_RATINGS
        );
    }

    @Test
    void testFieldValues() {
        assertEquals(NAME, response.getName());
        assertEquals(EMAIL, response.getEmail());
        assertEquals(PHONE, response.getPhoneNumber());
        assertEquals(WORK_ADDRESS, response.getWorkAddress());
        assertEquals(SPECIALITY, response.getSpeciality());
        assertEquals(AVERAGE_RATING, response.getAverageRating());
        assertEquals(TOTAL_RATINGS, response.getTotalRatings());
        assertNotNull(response.getWorkSchedule());
    }

    @Test
    void testAllArgsConstructor() {
        DoctorProfileResponse newResponse = new DoctorProfileResponse(
                "eb558e9f-1c39-460e-8860-71af6af63bd2",
                "Dr. Smith",
                "smith@pandacare.com",
                "081133344455",
                "RS Medika",
                new ArrayList<>(),
                "Cardiology",
                4.2,
                15
        );

        assertEquals("Dr. Smith", newResponse.getName());
        assertEquals("Cardiology", newResponse.getSpeciality());
        assertEquals(4.2, newResponse.getAverageRating());
    }

    @Test
    void testEqualsAndHashCode() {
        List<CaregiverSchedule> schedules = new ArrayList<>();
        DoctorProfileResponse sameResponse = new DoctorProfileResponse(
                ID,
                NAME,
                EMAIL,
                PHONE,
                WORK_ADDRESS,
                schedules,
                SPECIALITY,
                AVERAGE_RATING,
                TOTAL_RATINGS
        );

        assertEquals(response, sameResponse);
        assertEquals(response.hashCode(), sameResponse.hashCode());
    }
}