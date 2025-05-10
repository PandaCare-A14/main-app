package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateIntervalStrategyTest {
    private CreateIntervalStrategy strategy;
    private final String DOCTOR_ID = "DOC12345";
    private final DayOfWeek TEST_DAY = DayOfWeek.MONDAY;

    @BeforeEach
    void setUp() {
        strategy = new CreateIntervalStrategy();
    }

    @Test
    void testCreateSuccessfulCreation() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        CaregiverSchedule schedule = strategy.create(DOCTOR_ID, TEST_DAY, startTime, endTime);

        assertNotNull(schedule);
        assertEquals(DOCTOR_ID, schedule.getIdCaregiver());
        assertEquals(TEST_DAY, schedule.getDay());
        assertEquals(startTime, schedule.getStartTime());
        assertEquals(LocalTime.of(9, 30), schedule.getEndTime());
        assertEquals("AVAILABLE", schedule.getStatusCaregiver());
    }

    @Test
    void testCreateInvalidDuration() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(9, 15);

        CaregiverSchedule schedule = strategy.create(DOCTOR_ID, TEST_DAY, startTime, endTime);

        assertNotNull(schedule);
        assertEquals(startTime, schedule.getStartTime());
        assertEquals(endTime, schedule.getEndTime());
    }

    @Test
    void testCreateMultipleSlotsSuccess() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        List<CaregiverSchedule> schedules = strategy.createMultipleSlots(DOCTOR_ID, TEST_DAY, startTime, endTime);
        assertEquals(4, schedules.size());

        assertEquals(LocalTime.of(9, 0), schedules.get(0).getStartTime());
        assertEquals(LocalTime.of(9, 30), schedules.get(0).getEndTime());

        assertEquals(LocalTime.of(9, 30), schedules.get(1).getStartTime());
        assertEquals(LocalTime.of(10, 0), schedules.get(1).getEndTime());

        assertEquals(LocalTime.of(10, 0), schedules.get(2).getStartTime());
        assertEquals(LocalTime.of(10, 30), schedules.get(2).getEndTime());

        assertEquals(LocalTime.of(10, 30), schedules.get(3).getStartTime());
        assertEquals(LocalTime.of(11, 0), schedules.get(3).getEndTime());

        for (CaregiverSchedule schedule : schedules) {
            assertEquals(DOCTOR_ID, schedule.getIdCaregiver());
            assertEquals(TEST_DAY, schedule.getDay());
            assertEquals("AVAILABLE", schedule.getStatusCaregiver());
        }
    }

    @Test
    void testCreateMultipleSlotsInvalidDuration() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 45);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.createMultipleSlots(DOCTOR_ID, TEST_DAY, startTime, endTime));

        assertEquals("Time is not valid.", exception.getMessage());
    }

    @Test
    void testCreateMultipleSlotsEqualStartAndEndTime() {
        LocalTime time = LocalTime.of(9, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.createMultipleSlots(DOCTOR_ID, TEST_DAY, time, time));

        assertEquals("Start time can't be equal to end time.", exception.getMessage());
    }

    @Test
    void testCreateMultipleSlotsStartTimeAfterEndTime() {
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.createMultipleSlots(DOCTOR_ID, TEST_DAY, startTime, endTime));

        assertEquals("Start time can't be set after end time.", exception.getMessage());
    }

    @Test
    void testCreateScheduleWithStartTimeEqualStartAndEndTime() {
        LocalTime time = LocalTime.of(9, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.create(DOCTOR_ID, TEST_DAY, time, time));

        assertEquals("Start time can't be equal to end time.", exception.getMessage());
    }
}