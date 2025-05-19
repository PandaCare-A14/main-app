package com.pandacare.mainapp.doctor_profile.facade;

import com.pandacare.mainapp.doctor_profile.facade.external.ExternalServices;
import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
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
        String doctorId = "doc123";
        String patientId = "patient456";
        DoctorProfile expectedProfile = new DoctorProfile();
        when(doctorProfileService.findById(doctorId)).thenReturn(expectedProfile);

        DoctorProfile result = doctorFacade.getDoctorProfileWithActions(doctorId, patientId);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(expectedProfile, result, "Should return expected profile"),
                () -> assertTrue(outputStream.toString().contains("=== Action buttons ready for UI ==="),
                        "Should print UI action message"),
                () -> verify(externalServices, times(1)).startChat(doctorId, patientId),
                () -> verify(externalServices, times(1)).createAppointment(doctorId, patientId, null)
        );
    }

    @Test
    void getDoctorProfileWithActions_ShouldHandleNullPatientId() {
        String doctorId = "doc123";
        DoctorProfile expectedProfile = new DoctorProfile();
        when(doctorProfileService.findById(doctorId)).thenReturn(expectedProfile);

        DoctorProfile result = doctorFacade.getDoctorProfileWithActions(doctorId, null);

        assertAll(
                () -> assertNotNull(result),
                () -> verify(externalServices).startChat(doctorId, null),
                () -> verify(externalServices).createAppointment(doctorId, null, null)
        );
    }
}