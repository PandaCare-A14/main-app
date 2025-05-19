package com.pandacare.mainapp.doctor_profile.facade.external;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MockExternalServicesTest {

    @Test
    void testStartChat() {
        // Arrange
        MockExternalServices mock = new MockExternalServices();
        String doctorId = "doc123";
        String patientId = "patient456";

        // Act & Assert
        assertDoesNotThrow(() -> mock.startChat(doctorId, patientId));
    }

    @Test
    void testCreateAppointment() {
        // Arrange
        MockExternalServices mock = new MockExternalServices();
        String doctorId = "doc123";
        String patientId = "patient456";
        LocalDateTime time = LocalDateTime.now();

        // Act & Assert
        assertDoesNotThrow(() -> mock.createAppointment(doctorId, patientId, time));
    }

    @Test
    void testCreateAppointmentWithNullTime() {
        // Arrange
        MockExternalServices mock = new MockExternalServices();
        String doctorId = "doc123";
        String patientId = "patient456";

        // Act & Assert
        assertDoesNotThrow(() -> mock.createAppointment(doctorId, patientId, null));
    }
}