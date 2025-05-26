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
}