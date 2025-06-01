package com.pandacare.mainapp.doctor_profile.service.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DefaultWorkScheduleParserTest {

    private final DefaultWorkScheduleParser parser = new DefaultWorkScheduleParser();

    @Test
    void parse_ValidSchedule_ReturnsParsedWorkSchedule() {
        // Arrange
        String input = "Monday 09:00-17:00";

        // Act
        ParsedWorkSchedule result = parser.parse(input);

        // Assert
        assertEquals(DayOfWeek.MONDAY, result.day());
        assertEquals(LocalTime.of(9, 0), result.startTime());
        assertEquals(LocalTime.of(17, 0), result.endTime());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Monday",                   // Missing time range
            "Monday 09:00",             // Missing end time
            "Monday 09:00-17:00-18:00", // Extra time component
            "InvalidDay 09:00-17:00",   // Invalid day
            "Monday 25:00-17:00",       // Invalid start time
            "Monday 09:00-24:00"        // Invalid end time
    })
    void parse_InvalidSchedule_ThrowsException(String input) {
        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(input)
        );
    }

    @Test
    void parse_StartTimeAfterEndTime_ThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("Monday 17:00-09:00")
        );
    }

    @Test
    void parse_AllDaysOfWeek_WorksCorrectly() {
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        DayOfWeek[] expectedDays = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};

        for (int i = 0; i < days.length; i++) {
            String input = days[i] + " 10:00-16:00";
            ParsedWorkSchedule result = parser.parse(input);

            assertEquals(expectedDays[i], result.day());
            assertEquals(LocalTime.of(10, 0), result.startTime());
            assertEquals(LocalTime.of(16, 0), result.endTime());
        }
    }

    @Test
    void parse_CaseInsensitiveDays_WorksCorrectly() {
        String[] inputs = {
            "monday 09:00-17:00",
            "TUESDAY 09:00-17:00",
            "Wednesday 09:00-17:00",
            "tHuRsDay 09:00-17:00"
        };

        DayOfWeek[] expectedDays = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY};

        for (int i = 0; i < inputs.length; i++) {
            ParsedWorkSchedule result = parser.parse(inputs[i]);
            assertEquals(expectedDays[i], result.day());
        }
    }

    @Test
    void parse_VariousTimeFormats_WorksCorrectly() {
        String[] inputs = {
            "Monday 00:00-23:59",
            "Tuesday 01:30-14:45",
            "Wednesday 08:15-20:30"
        };

        LocalTime[][] expectedTimes = {
            {LocalTime.of(0, 0), LocalTime.of(23, 59)},
            {LocalTime.of(1, 30), LocalTime.of(14, 45)},
            {LocalTime.of(8, 15), LocalTime.of(20, 30)}
        };

        for (int i = 0; i < inputs.length; i++) {
            ParsedWorkSchedule result = parser.parse(inputs[i]);
            assertEquals(expectedTimes[i][0], result.startTime());
            assertEquals(expectedTimes[i][1], result.endTime());
        }
    }

    @Test
    void parse_InvalidTimeFormat_ThrowsCorrectException() {
        String[] invalidTimeInputs = {
            "Monday 9:00-17:00",    // Single digit hour
            "Monday 09:0-17:00",    // Single digit minute
            "Monday 09:00-17:0",    // Single digit minute
            "Monday 09.00-17.00",   // Wrong separator
            "Monday 09-17",         // Missing minutes
            "Monday 25:00-17:00",   // Invalid hour
            "Monday 09:60-17:00"    // Invalid minute
        };

        for (String input : invalidTimeInputs) {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(input));
            assertTrue(exception.getMessage().contains("Invalid time format") ||
                      exception.getMessage().contains("Invalid day"));
        }
    }    @Test
    void parse_NoTimeRange_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> parser.parse("Monday 09:00"));
        assertTrue(exception.getMessage().contains("Invalid time range"));
    }    @Test
    void parse_MultipleTimeRanges_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> parser.parse("Monday 09:00-12:00-17:00"));
        assertTrue(exception.getMessage().contains("Invalid time range"));
    }    @Test
    void parse_NoSpace_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> parser.parse("Monday09:00-17:00"));
        assertTrue(exception.getMessage().contains("Invalid format"));
    }    @Test
    void parse_MultipleSpaces_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> parser.parse("Monday extra 09:00-17:00"));
        assertTrue(exception.getMessage().contains("Invalid format"));
    }    @Test
    void parse_EmptyString_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> parser.parse(""));
        assertTrue(exception.getMessage().contains("Invalid format"));
    }    @Test
    void parse_OnlySpaces_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> parser.parse("   "));
        assertTrue(exception.getMessage().contains("Invalid format"));
    }

    @Test
    void parse_EqualStartAndEndTime_WorksCorrectly() {
        // Edge case: start and end time are the same (valid but unusual)
        ParsedWorkSchedule result = parser.parse("Monday 12:00-12:00");

        assertEquals(DayOfWeek.MONDAY, result.day());
        assertEquals(LocalTime.of(12, 0), result.startTime());
        assertEquals(LocalTime.of(12, 0), result.endTime());
    }

    @Test
    void parse_MidnightTimes_WorksCorrectly() {
        ParsedWorkSchedule result = parser.parse("Saturday 00:00-00:01");

        assertEquals(DayOfWeek.SATURDAY, result.day());
        assertEquals(LocalTime.of(0, 0), result.startTime());
        assertEquals(LocalTime.of(0, 1), result.endTime());
    }

    @Test
    void parse_AbbreviatedDayName_ThrowsException() {
        // Test that abbreviated day names don't work
        String[] abbreviatedDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        for (String day : abbreviatedDays) {
            Exception exception = assertThrows(IllegalArgumentException.class,
                () -> parser.parse(day + " 09:00-17:00"));
            assertTrue(exception.getMessage().contains("Invalid day"));
        }
    }
}