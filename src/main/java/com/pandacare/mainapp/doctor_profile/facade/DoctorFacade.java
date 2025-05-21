package com.pandacare.mainapp.doctor_profile.facade;

import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.facade.external.ExternalServices;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorFacade {
    private final DoctorProfileService doctorProfileService;
    private final ExternalServices externalServices;

    @Autowired
    public DoctorFacade(
            DoctorProfileService doctorProfileService,
            ExternalServices externalServices
    ) {
        this.doctorProfileService = doctorProfileService;
        this.externalServices = externalServices;
    }

    public DoctorProfileResponse getDoctorProfileWithActions(String caregiverId, String patientId) {
        DoctorProfileResponse response = doctorProfileService.findById(caregiverId);

        if (response == null) {
            return null;
        }

        System.out.println("=== Action buttons ready for UI ===");
        externalServices.startChat(caregiverId, patientId); // Mock
        externalServices.createAppointment(caregiverId, patientId, null); // Mock

        return response;
    }
}