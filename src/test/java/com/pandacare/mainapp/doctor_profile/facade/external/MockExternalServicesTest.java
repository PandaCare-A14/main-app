package com.pandacare.mainapp.doctor_profile.facade.external;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
        // Redirect system out to capture console output
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    void startChat_ShouldPrintCorrectMessage() throws Exception {
        // Arrange
        UUID caregiverId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        String expectedOutput = "[MOCK] Chat initiated - Doctor: " + caregiverId + ", Patient: " + patientId;

        // Act
        CompletableFuture<Void> future = mockExternalServices.startChat(caregiverId, patientId);
        future.get(); // Wait for async operation to complete

        // Assert
        assertTrue(outputStream.toString().contains(expectedOutput));
    }

    @Test
    void createAppointment_WithValidTime_ShouldPrintCorrectMessage() throws Exception {
        // Arrange
        UUID caregiverId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        String expectedOutput = "[MOCK] Appointment created - Doctor: " + caregiverId +
                ", Patient: " + patientId + ", Time: " + time;

        // Act
        CompletableFuture<Void> future = mockExternalServices.createAppointment(caregiverId, patientId, time);
        future.get(); // Wait for async operation to complete

        // Assert
        assertTrue(outputStream.toString().contains(expectedOutput));
    }

    @Test
    void createAppointment_WithNullTime_ShouldNotFail() throws Exception {
        // Arrange
        UUID caregiverId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        String expectedOutput = "[MOCK] Appointment created - Doctor: " + caregiverId +
                ", Patient: " + patientId + ", Time: null";

        // Act
        CompletableFuture<Void> future = mockExternalServices.createAppointment(caregiverId, patientId, null);
        future.get(); // Wait for async operation to complete

        // Assert
        assertTrue(outputStream.toString().contains(expectedOutput));
    }
}