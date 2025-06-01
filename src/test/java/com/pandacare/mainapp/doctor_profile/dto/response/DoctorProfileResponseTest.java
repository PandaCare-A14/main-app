package com.pandacare.mainapp.doctor_profile.dto.response;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;
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

    @Test
    void testToString() {
        String toString = response.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Dr. Clara"));
        assertTrue(toString.contains("clara@pandacare.com"));
        assertTrue(toString.contains("Neurology"));
    }

    @Test
    void testNoArgsConstructor() {
        DoctorProfileResponse emptyResponse = new DoctorProfileResponse();
        assertNull(emptyResponse.getCaregiverId());
        assertNull(emptyResponse.getName());
        assertNull(emptyResponse.getEmail());
        assertNull(emptyResponse.getPhoneNumber());
        assertNull(emptyResponse.getWorkAddress());
        assertNull(emptyResponse.getWorkSchedule());
        assertNull(emptyResponse.getSpeciality());
        assertNull(emptyResponse.getAverageRating());
        assertNull(emptyResponse.getRatings());
        assertEquals(0, emptyResponse.getTotalRatings());
    }

    @Test
    void testCaregiverScheduleDTOConstructors() {
        // Test all args constructor
        UUID scheduleId = UUID.randomUUID();
        DoctorProfileResponse.CaregiverScheduleDTO schedule = new DoctorProfileResponse.CaregiverScheduleDTO(
                scheduleId,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                ScheduleStatus.AVAILABLE
        );

        assertEquals(scheduleId, schedule.getId());
        assertEquals(DayOfWeek.MONDAY, schedule.getDay());
        assertEquals(LocalTime.of(9, 0), schedule.getStartTime());
        assertEquals(LocalTime.of(17, 0), schedule.getEndTime());
        assertEquals(ScheduleStatus.AVAILABLE, schedule.getStatus());

        // Test no args constructor
        DoctorProfileResponse.CaregiverScheduleDTO emptySchedule = new DoctorProfileResponse.CaregiverScheduleDTO();
        assertNull(emptySchedule.getId());
        assertNull(emptySchedule.getDay());
        assertNull(emptySchedule.getStartTime());
        assertNull(emptySchedule.getEndTime());
        assertNull(emptySchedule.getStatus());
    }

    @Test
    void testCaregiverScheduleDTOSettersAndGetters() {
        DoctorProfileResponse.CaregiverScheduleDTO schedule = new DoctorProfileResponse.CaregiverScheduleDTO();
        UUID scheduleId = UUID.randomUUID();
        
        schedule.setId(scheduleId);
        schedule.setDay(DayOfWeek.TUESDAY);
        schedule.setStartTime(LocalTime.of(8, 30));
        schedule.setEndTime(LocalTime.of(16, 30));
        schedule.setStatus(ScheduleStatus.UNAVAILABLE);

        assertEquals(scheduleId, schedule.getId());
        assertEquals(DayOfWeek.TUESDAY, schedule.getDay());
        assertEquals(LocalTime.of(8, 30), schedule.getStartTime());
        assertEquals(LocalTime.of(16, 30), schedule.getEndTime());
        assertEquals(ScheduleStatus.UNAVAILABLE, schedule.getStatus());
    }

    @Test
    void testCaregiverScheduleDTOEqualsAndHashCode() {
        UUID scheduleId = UUID.randomUUID();
        DoctorProfileResponse.CaregiverScheduleDTO schedule1 = new DoctorProfileResponse.CaregiverScheduleDTO(
                scheduleId,
                DayOfWeek.WEDNESDAY,
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                ScheduleStatus.AVAILABLE
        );

        DoctorProfileResponse.CaregiverScheduleDTO schedule2 = new DoctorProfileResponse.CaregiverScheduleDTO(
                scheduleId,
                DayOfWeek.WEDNESDAY,
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                ScheduleStatus.AVAILABLE
        );

        assertEquals(schedule1, schedule2);
        assertEquals(schedule1.hashCode(), schedule2.hashCode());

        // Test inequality
        DoctorProfileResponse.CaregiverScheduleDTO schedule3 = new DoctorProfileResponse.CaregiverScheduleDTO(
                UUID.randomUUID(),
                DayOfWeek.THURSDAY,
                LocalTime.of(11, 0),
                LocalTime.of(19, 0),
                ScheduleStatus.UNAVAILABLE
        );

        assertNotEquals(schedule1, schedule3);
        assertNotEquals(schedule1.hashCode(), schedule3.hashCode());
    }

    @Test
    void testCaregiverScheduleDTOToString() {
        DoctorProfileResponse.CaregiverScheduleDTO schedule = new DoctorProfileResponse.CaregiverScheduleDTO(
                UUID.randomUUID(),
                DayOfWeek.FRIDAY,
                LocalTime.of(9, 30),
                LocalTime.of(17, 30),
                ScheduleStatus.AVAILABLE
        );

        String toString = schedule.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("FRIDAY"));
        assertTrue(toString.contains("09:30"));
        assertTrue(toString.contains("17:30"));
        assertTrue(toString.contains("AVAILABLE"));
    }

    @Test
    void testSettersAndGetters() {
        DoctorProfileResponse testResponse = new DoctorProfileResponse();
        UUID testId = UUID.randomUUID();
        List<DoctorProfileResponse.CaregiverScheduleDTO> testSchedules = new ArrayList<>();
        List<RatingResponse> testRatings = new ArrayList<>();

        testResponse.setCaregiverId(testId);
        testResponse.setName("Dr. Test");
        testResponse.setEmail("test@pandacare.com");
        testResponse.setPhoneNumber("081199887766");
        testResponse.setWorkAddress("Test Hospital");
        testResponse.setWorkSchedule(testSchedules);
        testResponse.setSpeciality("Test Specialty");
        testResponse.setAverageRating(4.5);
        testResponse.setRatings(testRatings);
        testResponse.setTotalRatings(30);

        assertEquals(testId, testResponse.getCaregiverId());
        assertEquals("Dr. Test", testResponse.getName());
        assertEquals("test@pandacare.com", testResponse.getEmail());
        assertEquals("081199887766", testResponse.getPhoneNumber());
        assertEquals("Test Hospital", testResponse.getWorkAddress());
        assertEquals(testSchedules, testResponse.getWorkSchedule());
        assertEquals("Test Specialty", testResponse.getSpeciality());
        assertEquals(4.5, testResponse.getAverageRating());
        assertEquals(testRatings, testResponse.getRatings());
        assertEquals(30, testResponse.getTotalRatings());
    }
}