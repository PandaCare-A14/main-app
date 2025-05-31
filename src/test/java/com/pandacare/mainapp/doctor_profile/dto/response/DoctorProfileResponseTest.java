package com.pandacare.mainapp.doctor_profile.dto.response;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        assertEquals(ID, response.getCaregiverId());
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
        UUID newId = UUID.randomUUID();
        List<RatingResponse> newRatings = Arrays.asList(new RatingResponse());
        DoctorProfileResponse newResponse = new DoctorProfileResponse(
                newId,
                "Dr. Smith",
                "smith@pandacare.com",
                "081133344455",
                "RS Medika",
                new ArrayList<>(),
                "Cardiology",
                4.2,
                newRatings,
                15
        );

        assertEquals(newId, newResponse.getCaregiverId());
        assertEquals("Dr. Smith", newResponse.getName());
        assertEquals("smith@pandacare.com", newResponse.getEmail());
        assertEquals("081133344455", newResponse.getPhoneNumber());
        assertEquals("RS Medika", newResponse.getWorkAddress());
        assertEquals("Cardiology", newResponse.getSpeciality());
        assertEquals(4.2, newResponse.getAverageRating());
        assertEquals(newRatings, newResponse.getRatings());
        assertEquals(15, newResponse.getTotalRatings());
        assertNotNull(newResponse.getWorkSchedule());
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

        DoctorProfileResponse differentResponse = new DoctorProfileResponse(
                UUID.randomUUID(),
                "Dr. Different",
                "different@pandacare.com",
                "081199887766",
                "Different Hospital",
                schedules,
                "Cardiology",
                3.5,
                RATINGS,
                10
        );

        assertNotEquals(response, differentResponse);
        assertNotEquals(response.hashCode(), differentResponse.hashCode());
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
    void testSettersAndGetters() {
        DoctorProfileResponse testResponse = new DoctorProfileResponse();
        UUID newId = UUID.randomUUID();
        List<RatingResponse> newRatings = Arrays.asList(new RatingResponse());
        List<DoctorProfileResponse.CaregiverScheduleDTO> newSchedule = new ArrayList<>();

        testResponse.setCaregiverId(newId);
        testResponse.setName("Dr. Test");
        testResponse.setEmail("test@example.com");
        testResponse.setPhoneNumber("+9876543210");
        testResponse.setWorkAddress("Test Hospital");
        testResponse.setWorkSchedule(newSchedule);
        testResponse.setSpeciality("Surgery");
        testResponse.setAverageRating(4.8);
        testResponse.setRatings(newRatings);
        testResponse.setTotalRatings(50);

        assertEquals(newId, testResponse.getCaregiverId());
        assertEquals("Dr. Test", testResponse.getName());
        assertEquals("test@example.com", testResponse.getEmail());
        assertEquals("+9876543210", testResponse.getPhoneNumber());
        assertEquals("Test Hospital", testResponse.getWorkAddress());
        assertEquals(newSchedule, testResponse.getWorkSchedule());
        assertEquals("Surgery", testResponse.getSpeciality());
        assertEquals(4.8, testResponse.getAverageRating());
        assertEquals(newRatings, testResponse.getRatings());
        assertEquals(50, testResponse.getTotalRatings());
    }

    @Test
    void testToString() {
        String result = response.toString();
        assertNotNull(result);
        assertTrue(result.contains("Dr. Clara"));
        assertTrue(result.contains("Neurology"));
        assertTrue(result.contains("clara@pandacare.com"));
        assertTrue(result.contains("DoctorProfileResponse"));
    }

    @Test
    void testCanEqual() {
        DoctorProfileResponse other = new DoctorProfileResponse();
        assertTrue(response.canEqual(other));
        assertFalse(response.canEqual(new Object()));
        assertFalse(response.canEqual(null));
    }

    @Test
    void testCaregiverScheduleDTONoArgsConstructor() {
        DoctorProfileResponse.CaregiverScheduleDTO schedule = new DoctorProfileResponse.CaregiverScheduleDTO();

        assertNull(schedule.getId());
        assertNull(schedule.getDay());
        assertNull(schedule.getStartTime());
        assertNull(schedule.getEndTime());
        assertNull(schedule.getStatus());
    }

    @Test
    void testCaregiverScheduleDTOAllArgsConstructor() {
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
    }

    @Test
    void testCaregiverScheduleDTOSettersAndGetters() {
        DoctorProfileResponse.CaregiverScheduleDTO schedule = new DoctorProfileResponse.CaregiverScheduleDTO();
        UUID scheduleId = UUID.randomUUID();

        schedule.setId(scheduleId);
        schedule.setDay(DayOfWeek.FRIDAY);
        schedule.setStartTime(LocalTime.of(8, 30));
        schedule.setEndTime(LocalTime.of(18, 30));
        schedule.setStatus(ScheduleStatus.UNAVAILABLE);

        assertEquals(scheduleId, schedule.getId());
        assertEquals(DayOfWeek.FRIDAY, schedule.getDay());
        assertEquals(LocalTime.of(8, 30), schedule.getStartTime());
        assertEquals(LocalTime.of(18, 30), schedule.getEndTime());
        assertEquals(ScheduleStatus.UNAVAILABLE, schedule.getStatus());
    }

    @Test
    void testCaregiverScheduleDTOEqualsAndHashCode() {
        UUID scheduleId = UUID.randomUUID();
        DoctorProfileResponse.CaregiverScheduleDTO schedule1 = new DoctorProfileResponse.CaregiverScheduleDTO(
                scheduleId,
                DayOfWeek.WEDNESDAY,
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                ScheduleStatus.AVAILABLE
        );

        DoctorProfileResponse.CaregiverScheduleDTO schedule2 = new DoctorProfileResponse.CaregiverScheduleDTO(
                scheduleId,
                DayOfWeek.WEDNESDAY,
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                ScheduleStatus.AVAILABLE
        );

        DoctorProfileResponse.CaregiverScheduleDTO schedule3 = new DoctorProfileResponse.CaregiverScheduleDTO(
                UUID.randomUUID(),
                DayOfWeek.THURSDAY,
                LocalTime.of(11, 0),
                LocalTime.of(17, 0),
                ScheduleStatus.UNAVAILABLE
        );

        assertEquals(schedule1, schedule2);
        assertEquals(schedule1.hashCode(), schedule2.hashCode());
        assertNotEquals(schedule1, schedule3);
        assertNotEquals(schedule1.hashCode(), schedule3.hashCode());
    }

    @Test
    void testCaregiverScheduleDTOToString() {
        DoctorProfileResponse.CaregiverScheduleDTO schedule = new DoctorProfileResponse.CaregiverScheduleDTO(
                UUID.randomUUID(),
                DayOfWeek.SATURDAY,
                LocalTime.of(9, 0),
                LocalTime.of(15, 0),
                ScheduleStatus.AVAILABLE
        );

        String result = schedule.toString();
        assertNotNull(result);
        assertTrue(result.contains("SATURDAY"));
        assertTrue(result.contains("AVAILABLE"));
        assertTrue(result.contains("CaregiverScheduleDTO"));
    }

    @Test
    void testCaregiverScheduleDTOCanEqual() {
        DoctorProfileResponse.CaregiverScheduleDTO schedule = new DoctorProfileResponse.CaregiverScheduleDTO();
        DoctorProfileResponse.CaregiverScheduleDTO other = new DoctorProfileResponse.CaregiverScheduleDTO();

        assertTrue(schedule.canEqual(other));
        assertFalse(schedule.canEqual(new Object()));
        assertFalse(schedule.canEqual(null));
    }

    @Test
    void testNullValues() {
        DoctorProfileResponse nullResponse = new DoctorProfileResponse(
                null, null, null, null, null, null, null, null, null, 0
        );

        assertNull(nullResponse.getCaregiverId());
        assertNull(nullResponse.getName());
        assertNull(nullResponse.getEmail());
        assertNull(nullResponse.getPhoneNumber());
        assertNull(nullResponse.getWorkAddress());
        assertNull(nullResponse.getWorkSchedule());
        assertNull(nullResponse.getSpeciality());
        assertNull(nullResponse.getAverageRating());
        assertNull(nullResponse.getRatings());
        assertEquals(0, nullResponse.getTotalRatings());
    }

    @Test
    void testEmptyCollections() {
        DoctorProfileResponse emptyResponse = new DoctorProfileResponse(
                UUID.randomUUID(),
                "Dr. Empty",
                "empty@test.com",
                "123456789",
                "Empty Hospital",
                Collections.emptyList(),
                "General",
                0.0,
                Collections.emptyList(),
                0
        );

        assertNotNull(emptyResponse.getWorkSchedule());
        assertTrue(emptyResponse.getWorkSchedule().isEmpty());
        assertNotNull(emptyResponse.getRatings());
        assertTrue(emptyResponse.getRatings().isEmpty());
        assertEquals(0.0, emptyResponse.getAverageRating());
        assertEquals(0, emptyResponse.getTotalRatings());
    }

    @Test
    void testMaxValues() {
        DoctorProfileResponse maxResponse = new DoctorProfileResponse(
                UUID.randomUUID(),
                "Dr. Maximum",
                "max@test.com",
                "999999999",
                "Max Hospital",
                Arrays.asList(new DoctorProfileResponse.CaregiverScheduleDTO()),
                "Super Specialty",
                5.0,
                Arrays.asList(new RatingResponse()),
                Integer.MAX_VALUE
        );

        assertEquals("Dr. Maximum", maxResponse.getName());
        assertEquals(5.0, maxResponse.getAverageRating());
        assertEquals(Integer.MAX_VALUE, maxResponse.getTotalRatings());
        assertEquals(1, maxResponse.getWorkSchedule().size());
        assertEquals(1, maxResponse.getRatings().size());
    }

    @Test
    void testMinValues() {
        DoctorProfileResponse minResponse = new DoctorProfileResponse(
                UUID.randomUUID(),
                "Dr. Minimum",
                "min@test.com",
                "000000000",
                "Min Hospital",
                Collections.emptyList(),
                "General",
                0.0,
                Collections.emptyList(),
                0
        );

        assertEquals("Dr. Minimum", minResponse.getName());
        assertEquals(0.0, minResponse.getAverageRating());
        assertEquals(0, minResponse.getTotalRatings());
        assertTrue(minResponse.getWorkSchedule().isEmpty());
        assertTrue(minResponse.getRatings().isEmpty());
    }

    @Test
    void testWorkScheduleWithMultipleDays() {
        List<DoctorProfileResponse.CaregiverScheduleDTO> schedules = Arrays.asList(
                new DoctorProfileResponse.CaregiverScheduleDTO(UUID.randomUUID(), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0), ScheduleStatus.AVAILABLE),
                new DoctorProfileResponse.CaregiverScheduleDTO(UUID.randomUUID(), DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0), ScheduleStatus.AVAILABLE),
                new DoctorProfileResponse.CaregiverScheduleDTO(UUID.randomUUID(), DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0), ScheduleStatus.UNAVAILABLE)
        );

        DoctorProfileResponse response = new DoctorProfileResponse(
                UUID.randomUUID(), "Dr. Multi", "multi@test.com", "123", "Hospital", schedules, "Multi", 4.0, Collections.emptyList(), 5
        );

        assertEquals(3, response.getWorkSchedule().size());
        assertEquals(DayOfWeek.MONDAY, response.getWorkSchedule().get(0).getDay());
        assertEquals(DayOfWeek.TUESDAY, response.getWorkSchedule().get(1).getDay());
        assertEquals(DayOfWeek.WEDNESDAY, response.getWorkSchedule().get(2).getDay());
        assertEquals(ScheduleStatus.AVAILABLE, response.getWorkSchedule().get(0).getStatus());
        assertEquals(ScheduleStatus.AVAILABLE, response.getWorkSchedule().get(1).getStatus());
        assertEquals(ScheduleStatus.UNAVAILABLE, response.getWorkSchedule().get(2).getStatus());
    }

    @Test
    void testWorkScheduleWithDifferentTimes() {
        List<DoctorProfileResponse.CaregiverScheduleDTO> schedules = Arrays.asList(
                new DoctorProfileResponse.CaregiverScheduleDTO(UUID.randomUUID(), DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), ScheduleStatus.AVAILABLE),
                new DoctorProfileResponse.CaregiverScheduleDTO(UUID.randomUUID(), DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(17, 0), ScheduleStatus.AVAILABLE),
                new DoctorProfileResponse.CaregiverScheduleDTO(UUID.randomUUID(), DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(15, 30), ScheduleStatus.AVAILABLE)
        );

        DoctorProfileResponse response = new DoctorProfileResponse(
                UUID.randomUUID(), "Dr. Flexible", "flexible@test.com", "123", "Hospital", schedules, "Flexible", 4.5, Collections.emptyList(), 20
        );

        assertEquals(3, response.getWorkSchedule().size());
        assertEquals(LocalTime.of(8, 0), response.getWorkSchedule().get(0).getStartTime());
        assertEquals(LocalTime.of(12, 0), response.getWorkSchedule().get(0).getEndTime());
        assertEquals(LocalTime.of(13, 0), response.getWorkSchedule().get(1).getStartTime());
        assertEquals(LocalTime.of(17, 0), response.getWorkSchedule().get(1).getEndTime());
        assertEquals(LocalTime.of(10, 30), response.getWorkSchedule().get(2).getStartTime());
        assertEquals(LocalTime.of(15, 30), response.getWorkSchedule().get(2).getEndTime());
    }

    @Test
    void testCaregiverScheduleDTOWithNullValues() {
        DoctorProfileResponse.CaregiverScheduleDTO schedule = new DoctorProfileResponse.CaregiverScheduleDTO(
                null, null, null, null, null
        );

        assertNull(schedule.getId());
        assertNull(schedule.getDay());
        assertNull(schedule.getStartTime());
        assertNull(schedule.getEndTime());
        assertNull(schedule.getStatus());
    }

    @Test
    void testCaregiverScheduleDTOAllDaysOfWeek() {
        for (DayOfWeek day : DayOfWeek.values()) {
            DoctorProfileResponse.CaregiverScheduleDTO schedule = new DoctorProfileResponse.CaregiverScheduleDTO(
                    UUID.randomUUID(),
                    day,
                    LocalTime.of(9, 0),
                    LocalTime.of(17, 0),
                    ScheduleStatus.AVAILABLE
            );

            assertEquals(day, schedule.getDay());
            assertNotNull(schedule.getId());
            assertEquals(LocalTime.of(9, 0), schedule.getStartTime());
            assertEquals(LocalTime.of(17, 0), schedule.getEndTime());
            assertEquals(ScheduleStatus.AVAILABLE, schedule.getStatus());
        }
    }

    @Test
    void testCaregiverScheduleDTOAllScheduleStatuses() {
        for (ScheduleStatus status : ScheduleStatus.values()) {
            DoctorProfileResponse.CaregiverScheduleDTO schedule = new DoctorProfileResponse.CaregiverScheduleDTO(
                    UUID.randomUUID(),
                    DayOfWeek.MONDAY,
                    LocalTime.of(9, 0),
                    LocalTime.of(17, 0),
                    status
            );

            assertEquals(status, schedule.getStatus());
            assertNotNull(schedule.getId());
            assertEquals(DayOfWeek.MONDAY, schedule.getDay());
            assertEquals(LocalTime.of(9, 0), schedule.getStartTime());
            assertEquals(LocalTime.of(17, 0), schedule.getEndTime());
        }
    }

    @Test
    void testRatingsWithMultipleEntries() {
        List<RatingResponse> multipleRatings = Arrays.asList(
                new RatingResponse(),
                new RatingResponse(),
                new RatingResponse()
        );

        DoctorProfileResponse response = new DoctorProfileResponse(
                UUID.randomUUID(),
                "Dr. Popular",
                "popular@test.com",
                "123456789",
                "Popular Hospital",
                Collections.emptyList(),
                "Popular Specialty",
                4.9,
                multipleRatings,
                100
        );

        assertEquals(3, response.getRatings().size());
        assertEquals(4.9, response.getAverageRating());
        assertEquals(100, response.getTotalRatings());
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
}