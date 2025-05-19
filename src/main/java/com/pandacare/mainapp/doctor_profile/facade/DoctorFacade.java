package com.pandacare.mainapp.doctor_profile.facade;

import com.pandacare.mainapp.doctor_profile.facade.external.ExternalServices;
import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
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

    public DoctorProfile getDoctorProfileWithActions(String doctorId, String patientId) {
        DoctorProfile doctor = doctorProfileService.findById(doctorId);

        System.out.println("=== Action buttons ready for UI ===");
        externalServices.startChat(doctorId, patientId);      // Mock
        externalServices.createAppointment(doctorId, patientId, null); // Mock

        return doctor;
    }
}