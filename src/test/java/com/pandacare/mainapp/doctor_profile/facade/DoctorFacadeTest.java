package com.pandacare.mainapp.doctor_profile.facade;

import com.pandacare.mainapp.doctor_profile.facade.external.ExternalServices;
import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class DoctorFacadeTest {

    @Test
    void testGetDoctorProfileWithActions() {
        DoctorProfileService mockProfileService = Mockito.mock(DoctorProfileService.class);
        ExternalServices mockExternalServices = Mockito.mock(ExternalServices.class);

        DoctorFacade facade = new DoctorFacade(mockProfileService, mockExternalServices);
        String doctorId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        String patientId = "eb558e9f-1c39-460e-8860-71af6af63bd2";
        DoctorProfile expectedProfile = new DoctorProfile();

        Mockito.when(mockProfileService.findById(doctorId)).thenReturn(expectedProfile);

        DoctorProfile result = facade.getDoctorProfileWithActions(doctorId, patientId);

        assertNotNull(result);
        assertEquals(expectedProfile, result);
        Mockito.verify(mockExternalServices).startChat(doctorId, patientId);
        Mockito.verify(mockExternalServices).createAppointment(doctorId, patientId, null);
    }
}