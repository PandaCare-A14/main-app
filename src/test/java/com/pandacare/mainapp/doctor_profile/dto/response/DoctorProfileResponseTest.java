package com.pandacare.mainapp.doctor_profile.dto.response;

import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class DoctorProfileResponseTest {

    private DoctorProfileResponse response;
    private static final UUID ID = UUID.randomUUID();
    private static final String NAME = "Dr. Clara";
    private static final String EMAIL = "clara@pandacare.com";
    private static final String PHONE = "081122334455";
    private static final String WORK_ADDRESS = "RS Pandacare";
    private static final String SPECIALITY = "Neurology";
    private static final Double AVERAGE_RATING = 4.7;
    private static final int TOTAL_RATINGS = 25;
    private static final List<RatingResponse> RATINGS = List.of();

    @BeforeEach
    void setUp() {
        List<DoctorProfileResponse.CaregiverScheduleDTO> schedules = new ArrayList<>();
        response = new DoctorProfileResponse(
                ID,
                NAME,
                EMAIL,
                PHONE,
                WORK_ADDRESS,
                schedules,
                SPECIALITY,
                AVERAGE_RATING,
                RATINGS,
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
        assertEquals(RATINGS, response.getRatings());
        assertNotNull(response.getWorkSchedule());
    }

    @Test
    void testAllArgsConstructor() {
        DoctorProfileResponse newResponse = new DoctorProfileResponse(
                UUID.randomUUID(),
                "Dr. Smith",
                "smith@pandacare.com",
                "081133344455",
                "RS Medika",
                new ArrayList<>(),
                "Cardiology",
                4.2,
                RATINGS,
                15
        );

        assertEquals("Dr. Smith", newResponse.getName());
        assertEquals("Cardiology", newResponse.getSpeciality());
        assertEquals(4.2, newResponse.getAverageRating());
        assertEquals(RATINGS, newResponse.getRatings());
    }

    @Test
    void testEqualsAndHashCode() {
        List<DoctorProfileResponse.CaregiverScheduleDTO> schedules = new ArrayList<>();
        DoctorProfileResponse sameResponse = new DoctorProfileResponse(
                ID,
                NAME,
                EMAIL,
                PHONE,
                WORK_ADDRESS,
                schedules,
                SPECIALITY,
                AVERAGE_RATING,
                RATINGS,
                TOTAL_RATINGS
        );

        assertEquals(response, sameResponse);
        assertEquals(response.hashCode(), sameResponse.hashCode());
    }
}