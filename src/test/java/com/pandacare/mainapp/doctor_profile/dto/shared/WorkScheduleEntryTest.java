package com.pandacare.mainapp.doctor_profile.dto.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WorkScheduleEntryTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Set<ConstraintViolation<WorkScheduleEntry>> validateTimeSlot(String timeSlot) {
        WorkScheduleEntry entry = new WorkScheduleEntry();
        entry.setTimeSlot(timeSlot);
        return validator.validate(entry);
    }

    @Test
    void testValidTimeSlot() {
        Set<ConstraintViolation<WorkScheduleEntry>> violations = validateTimeSlot("09:00-17:30");
        assertTrue(violations.isEmpty(), "Expected valid time format to pass validation");
    }

    @Test
    void testInvalidTimeSlot_NoDash() {
        Set<ConstraintViolation<WorkScheduleEntry>> violations = validateTimeSlot("09:0017:30");
        assertFalse(violations.isEmpty(), "Expected time format without dash to fail validation");
    }

    @Test
    void testInvalidTimeSlot_WrongHour() {
        Set<ConstraintViolation<WorkScheduleEntry>> violations = validateTimeSlot("25:00-12:00");
        assertFalse(violations.isEmpty(), "Expected invalid hour (25) to fail validation");
    }

    @Test
    void testInvalidTimeSlot_MissingMinutes() {
        Set<ConstraintViolation<WorkScheduleEntry>> violations = validateTimeSlot("09-17:00");
        assertFalse(violations.isEmpty(), "Expected time format missing minutes to fail validation");
    }

    @Test
    void testInvalidTimeSlot_Empty() {
        Set<ConstraintViolation<WorkScheduleEntry>> violations = validateTimeSlot("");
        assertFalse(violations.isEmpty(), "Expected empty time slot to fail validation");
    }
}
