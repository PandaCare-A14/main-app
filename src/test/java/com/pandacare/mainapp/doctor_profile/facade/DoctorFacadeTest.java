package com.pandacare.mainapp.doctor_profile.facade;

import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.facade.external.ExternalServices;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorFacadeTest {

    @Mock
    private DoctorProfileService doctorProfileService;

    @Mock
    private ExternalServices externalServices;

    @InjectMocks
    private DoctorFacade doctorFacade;

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
    void getDoctorProfileWithActions_ShouldReturnProfileAndTriggerActions() {
        String caregiverId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        String patientId = "patient456";
        DoctorProfileResponse expectedResponse = new DoctorProfileResponse(
                caregiverId,
                "Dr. Hafiz",
                "hafiz@example.com",
                "08123456789",
                "RS Pandacare",
                null,
                "Cardiologist",
                4.8,
                10
        );

        when(doctorProfileService.findById(caregiverId)).thenReturn(expectedResponse);

        DoctorProfileResponse result = doctorFacade.getDoctorProfileWithActions(caregiverId, patientId);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(expectedResponse, result, "Should return expected profile"),
                () -> assertTrue(outputStream.toString().contains("=== Action buttons ready for UI ==="),
                        "Should print UI action message"),
                () -> verify(externalServices, times(1)).startChat(caregiverId, patientId),
                () -> verify(externalServices, times(1)).createAppointment(caregiverId, patientId, null)
        );
    }

    @Test
    void getDoctorProfileWithActions_ShouldHandleNullPatientId() {
        // Given
        String caregiverId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        DoctorProfileResponse expectedResponse = new DoctorProfileResponse(
                caregiverId,
                "Dr. Hafiz",
                "hafiz@example.com",
                "08123456789",
                "RS Pandacare",
                null,
                "Cardiologist",
                4.8,
                10
        );

        when(doctorProfileService.findById(caregiverId)).thenReturn(expectedResponse);

        // When
        DoctorProfileResponse result = doctorFacade.getDoctorProfileWithActions(caregiverId, null);

        // Then
        assertAll(
                () -> assertNotNull(result),
                () -> verify(externalServices).startChat(caregiverId, null),
                () -> verify(externalServices).createAppointment(caregiverId, null, null)
        );
    }

    @Test
    void getDoctorProfileWithActions_ShouldHandleNullResponseFromService() {
        // Given
        String caregiverId = "non-existent-id";
        String patientId = "patient456";

        when(doctorProfileService.findById(caregiverId)).thenReturn(null);

        // When
        DoctorProfileResponse result = doctorFacade.getDoctorProfileWithActions(caregiverId, patientId);

        // Then
        assertNull(result);
        verify(externalServices, never()).startChat(any(), any());
        verify(externalServices, never()).createAppointment(any(), any(), any());
    }
}