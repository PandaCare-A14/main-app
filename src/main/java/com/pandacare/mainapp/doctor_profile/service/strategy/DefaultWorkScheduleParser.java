package com.pandacare.mainapp.doctor_profile.service.strategy;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class DefaultWorkScheduleParser implements WorkScheduleParser {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public ParsedWorkSchedule parse(String workSchedule) throws IllegalArgumentException {
        try {
            String[] parts = workSchedule.split(" ");
            if (parts.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid format. Expected: 'Day HH:mm-HH:mm'"
                );
            }

            DayOfWeek day = DayOfWeek.valueOf(parts[0].toUpperCase());
            String[] timeRange = parts[1].split("-");
            if (timeRange.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid time range. Expected: 'HH:mm-HH:mm'"
                );
            }

            LocalTime start = LocalTime.parse(timeRange[0], TIME_FORMATTER);
            LocalTime end = LocalTime.parse(timeRange[1], TIME_FORMATTER);

            if (start.isAfter(end)) {
                throw new IllegalArgumentException("Start time must be before end time");
            }

            return new ParsedWorkSchedule(day, start, end);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Use HH:mm");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid day. Use full names (e.g., Monday)");
        }
    }
}
