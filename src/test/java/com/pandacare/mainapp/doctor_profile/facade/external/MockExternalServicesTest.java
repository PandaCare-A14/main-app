package com.pandacare.mainapp.doctor_profile.facade.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MockExternalServicesTest {

    @InjectMocks
    private MockExternalServices mockExternalServices;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
    }

    @BeforeEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void startChat_ShouldPrintCorrectMessage() {
        // Arrange
        String doctorId = "doc123";
        String patientId = "patient456";
        String expectedOutput = "[MOCK] Chat initiated - Doctor: doc123, Patient: patient456";

        // Act
        mockExternalServices.startChat(doctorId, patientId);

        // Assert
        assertTrue(outputStream.toString().contains(expectedOutput));
    }

    @Test
    void createAppointment_WithValidTime_ShouldPrintCorrectMessage() {
        // Arrange
        String doctorId = "doc123";
        String patientId = "patient456";
        LocalDateTime time = LocalDateTime.of(2023, 12, 25, 14, 30);
        String expectedOutput = "[MOCK] Appointment created - Doctor: doc123, Patient: patient456, Time: 2023-12-25T14:30";

        // Act
        mockExternalServices.createAppointment(doctorId, patientId, time);

        // Assert
        assertTrue(outputStream.toString().contains(expectedOutput));
    }

    @Test
    void createAppointment_WithNullTime_ShouldPrintCorrectMessage() {
        // Arrange
        String doctorId = "doc123";
        String patientId = "patient456";
        String expectedOutput = "[MOCK] Appointment created - Doctor: doc123, Patient: patient456, Time: null";

        // Act
        mockExternalServices.createAppointment(doctorId, patientId, null);

        // Assert
        assertTrue(outputStream.toString().contains(expectedOutput));
    }
}