package com.pandacare.mainapp.doctor_profile.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateDoctorRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UpdateDoctorRequest createValidRequest() {
        UpdateDoctorRequest request = new UpdateDoctorRequest();
        request.setId("abc123");
        request.setName("Dr. Jonah");
        request.setEmail("jonah@pandacare.com");
        request.setPhoneNumber("+628123456789");
        request.setWorkAddress("RS Pondok Indah");

        Map<String, String> schedule = new HashMap<>();
        schedule.put("Rabu", "08:00-12:00");
        request.setWorkSchedule(schedule);

        request.setSpeciality("Orthopedic");
        return request;
    }

    @Test
    void testValidRequest() {
        UpdateDoctorRequest request = createValidRequest();
        Set<ConstraintViolation<UpdateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no constraint violations for valid input");
    }

    @Test
    void testMissingId() {
        UpdateDoctorRequest request = createValidRequest();
        request.setId("");
        Set<ConstraintViolation<UpdateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    void testInvalidEmail() {
        UpdateDoctorRequest request = createValidRequest();
        request.setEmail("not-an-email");
        Set<ConstraintViolation<UpdateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testInvalidPhoneNumber() {
        UpdateDoctorRequest request = createValidRequest();
        request.setPhoneNumber("abc123");
        Set<ConstraintViolation<UpdateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
    }

    @Test
    void testNullWorkSchedule() {
        UpdateDoctorRequest request = createValidRequest();
        request.setWorkSchedule(null);
        Set<ConstraintViolation<UpdateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("workSchedule")));
    }

    @Test
    void testBlankSpeciality() {
        UpdateDoctorRequest request = createValidRequest();
        request.setSpeciality(" ");
        Set<ConstraintViolation<UpdateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("speciality")));
    }
}
