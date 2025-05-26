package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class CreateIntervalStrategyTest {
    private CreateIntervalStrategy strategy;
    private final UUID DOCTOR_ID = UUID.randomUUID();
    private final DayOfWeek TEST_DAY = DayOfWeek.MONDAY;
    private LocalDate nextMonday;

    @BeforeEach
    void setUp() {
        strategy = new CreateIntervalStrategy();
        nextMonday = LocalDate.now().with(TemporalAdjusters.nextOrSame(TEST_DAY));
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
        assertEquals(LocalTime.of(10, 00), schedule.getEndTime());
        assertEquals(ScheduleStatus.AVAILABLE, schedule.getStatus());
    }

    @Test
    void testCreateWithDateSuccessfulCreation() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        CaregiverSchedule schedule = strategy.createWithDate(DOCTOR_ID, TEST_DAY, nextMonday, startTime, endTime);

        assertNotNull(schedule);
        assertEquals(DOCTOR_ID, schedule.getIdCaregiver());
        assertEquals(TEST_DAY, schedule.getDay());
        assertEquals(nextMonday, schedule.getDate());
        assertEquals(startTime, schedule.getStartTime());
        assertEquals(LocalTime.of(10, 00), schedule.getEndTime());
        assertEquals(ScheduleStatus.AVAILABLE, schedule.getStatus());
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
            assertEquals(ScheduleStatus.AVAILABLE, schedule.getStatus());
        }
    }

    @Test
    void testCreateMultipleSlotsWithDateSuccess() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        List<CaregiverSchedule> schedules = strategy.createMultipleSlotsWithDate(DOCTOR_ID, TEST_DAY, nextMonday, startTime, endTime);
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
            assertEquals(nextMonday, schedule.getDate());
            assertEquals(ScheduleStatus.AVAILABLE, schedule.getStatus());
        }
    }

    @Test
    void testCreateMultipleSlotsInvalidDuration() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 45);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.createMultipleSlots(DOCTOR_ID, TEST_DAY, startTime, endTime));

        assertTrue(exception.getMessage().contains("Time is not valid"));
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

    @Test
    void testCreateRepeatedSuccess() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        int recurrenceCount = 3;

        List<CaregiverSchedule> schedules = strategy.createRepeated(DOCTOR_ID, TEST_DAY, startTime, endTime, recurrenceCount);

        assertEquals(12, schedules.size());

        CaregiverSchedule firstSlot = schedules.get(0);
        assertEquals(DOCTOR_ID, firstSlot.getIdCaregiver());
        assertEquals(TEST_DAY, firstSlot.getDay());
        assertEquals(nextMonday, firstSlot.getDate());
        assertEquals(LocalTime.of(9, 0), firstSlot.getStartTime());
        assertEquals(LocalTime.of(9, 30), firstSlot.getEndTime());

        CaregiverSchedule firstSlotSecondWeek = schedules.get(4);
        assertEquals(DOCTOR_ID, firstSlotSecondWeek.getIdCaregiver());
        assertEquals(TEST_DAY, firstSlotSecondWeek.getDay());
        assertEquals(nextMonday.plusWeeks(1), firstSlotSecondWeek.getDate());
        assertEquals(LocalTime.of(9, 0), firstSlotSecondWeek.getStartTime());
        assertEquals(LocalTime.of(9, 30), firstSlotSecondWeek.getEndTime());

        CaregiverSchedule lastSlot = schedules.get(11);
        assertEquals(DOCTOR_ID, lastSlot.getIdCaregiver());
        assertEquals(TEST_DAY, lastSlot.getDay());
        assertEquals(nextMonday.plusWeeks(2), lastSlot.getDate());
        assertEquals(LocalTime.of(10, 30), lastSlot.getStartTime());
        assertEquals(LocalTime.of(11, 0), lastSlot.getEndTime());
    }

    @Test
    void testCreateRepeatedInvalidRecurrenceCount() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        int invalidRecurrenceCount = 0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.createRepeated(DOCTOR_ID, TEST_DAY, startTime, endTime, invalidRecurrenceCount));

        assertEquals("Minimum week(s) added is 1.", exception.getMessage());
    }
}