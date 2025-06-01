package com.pandacare.mainapp.doctor_profile.service.strategy;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class ParsedWorkScheduleTest {

    @Test
    void testRecordCreation() {
        DayOfWeek day = DayOfWeek.MONDAY;
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        
        ParsedWorkSchedule schedule = new ParsedWorkSchedule(day, startTime, endTime);
        
        assertEquals(day, schedule.day());
        assertEquals(startTime, schedule.startTime());
        assertEquals(endTime, schedule.endTime());
    }

    @Test
    void testRecordEquality() {
        DayOfWeek day = DayOfWeek.TUESDAY;
        LocalTime startTime = LocalTime.of(10, 30);
        LocalTime endTime = LocalTime.of(16, 45);
        
        ParsedWorkSchedule schedule1 = new ParsedWorkSchedule(day, startTime, endTime);
        ParsedWorkSchedule schedule2 = new ParsedWorkSchedule(day, startTime, endTime);
        ParsedWorkSchedule schedule3 = new ParsedWorkSchedule(DayOfWeek.WEDNESDAY, startTime, endTime);
        
        assertEquals(schedule1, schedule2);
        assertNotEquals(schedule1, schedule3);
        assertEquals(schedule1.hashCode(), schedule2.hashCode());
        assertNotEquals(schedule1.hashCode(), schedule3.hashCode());
    }

    @Test
    void testRecordToString() {
        DayOfWeek day = DayOfWeek.FRIDAY;
        LocalTime startTime = LocalTime.of(8, 15);
        LocalTime endTime = LocalTime.of(18, 30);
        
        ParsedWorkSchedule schedule = new ParsedWorkSchedule(day, startTime, endTime);
        String result = schedule.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("FRIDAY"));
        assertTrue(result.contains("08:15"));
        assertTrue(result.contains("18:30"));
        assertTrue(result.contains("ParsedWorkSchedule"));
    }

    @Test
    void testRecordWithNullValues() {
        // Test record behavior with null values
        ParsedWorkSchedule scheduleWithNulls = new ParsedWorkSchedule(null, null, null);
        
        assertNull(scheduleWithNulls.day());
        assertNull(scheduleWithNulls.startTime());
        assertNull(scheduleWithNulls.endTime());
    }

    @Test
    void testRecordWithAllDaysOfWeek() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        
        for (DayOfWeek day : DayOfWeek.values()) {
            ParsedWorkSchedule schedule = new ParsedWorkSchedule(day, startTime, endTime);
            assertEquals(day, schedule.day());
            assertEquals(startTime, schedule.startTime());
            assertEquals(endTime, schedule.endTime());
        }
    }

    @Test
    void testRecordWithVariousTimeValues() {
        DayOfWeek day = DayOfWeek.SATURDAY;
        
        // Test with different time combinations
        LocalTime[] times = {
            LocalTime.of(0, 0),
            LocalTime.of(12, 0),
            LocalTime.of(23, 59),
            LocalTime.of(8, 30),
            LocalTime.of(17, 45)
        };
        
        for (int i = 0; i < times.length - 1; i++) {
            ParsedWorkSchedule schedule = new ParsedWorkSchedule(day, times[i], times[i + 1]);
            assertEquals(day, schedule.day());
            assertEquals(times[i], schedule.startTime());
            assertEquals(times[i + 1], schedule.endTime());
        }
    }

    @Test
    void testRecordEqualsWithDifferentTypes() {
        ParsedWorkSchedule schedule = new ParsedWorkSchedule(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        
        assertNotEquals(schedule, new Object());
        assertNotEquals(schedule, null);
        assertNotEquals(schedule, "string");
        assertEquals(schedule, schedule); // self-equality
    }

    @Test
    void testRecordFieldIndependence() {
        // Test that changing one field doesn't affect others
        DayOfWeek day1 = DayOfWeek.MONDAY;
        DayOfWeek day2 = DayOfWeek.TUESDAY;
        LocalTime time1 = LocalTime.of(9, 0);
        LocalTime time2 = LocalTime.of(10, 0);
        LocalTime time3 = LocalTime.of(17, 0);
        LocalTime time4 = LocalTime.of(18, 0);
        
        ParsedWorkSchedule schedule1 = new ParsedWorkSchedule(day1, time1, time3);
        ParsedWorkSchedule schedule2 = new ParsedWorkSchedule(day2, time1, time3);
        ParsedWorkSchedule schedule3 = new ParsedWorkSchedule(day1, time2, time3);
        ParsedWorkSchedule schedule4 = new ParsedWorkSchedule(day1, time1, time4);
        
        assertNotEquals(schedule1, schedule2);
        assertNotEquals(schedule1, schedule3);
        assertNotEquals(schedule1, schedule4);
    }
}
