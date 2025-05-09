package com.pandacare.mainapp.doctor_profile.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CreateDoctorRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreateDoctorRequest createValidRequest() {
        CreateDoctorRequest request = new CreateDoctorRequest();
        request.setName("Dr. Hafiz");
        request.setEmail("hafiz@pandacare.com");
        request.setPhoneNumber("+628123456789");
        request.setWorkAddress("RS Pandacare");

        Map<String, String> schedule = new HashMap<>();
        schedule.put("Senin", "08:00-12:00");
        request.setWorkSchedule(schedule);

        request.setSpeciality("Cardiology");
        return request;
    }

    @Test
    void testValidRequest() {
        CreateDoctorRequest request = createValidRequest();
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no constraint violations for valid input");
    }

    @Test
    void testMissingName() {
        CreateDoctorRequest request = createValidRequest();
        request.setName("");
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testInvalidEmail() {
        CreateDoctorRequest request = createValidRequest();
        request.setEmail("not-an-email");
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testInvalidPhoneNumber() {
        CreateDoctorRequest request = createValidRequest();
        request.setPhoneNumber("abc123");
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
    }

    @Test
    void testNullWorkSchedule() {
        CreateDoctorRequest request = createValidRequest();
        request.setWorkSchedule(null);
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("workSchedule")));
    }

    @Test
    void testBlankSpeciality() {
        CreateDoctorRequest request = createValidRequest();
        request.setSpeciality("  ");
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("speciality")));
    }
}
