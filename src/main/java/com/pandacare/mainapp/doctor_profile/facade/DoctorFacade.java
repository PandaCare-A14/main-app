package com.pandacare.mainapp.doctor_profile.facade;

import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.facade.external.ExternalServices;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

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

    @Async
    public CompletableFuture<DoctorProfileResponse> getDoctorProfileWithActions(String caregiverId, String patientId) {
        DoctorProfileResponse response = doctorProfileService.findById(caregiverId).join();

        if (response == null) {
            return CompletableFuture.completedFuture(null);
        }

        System.out.println("=== Action buttons ready for UI ===");
        externalServices.startChat(caregiverId, patientId);
        externalServices.createAppointment(caregiverId, patientId, null);

        return CompletableFuture.completedFuture(response);
    }
}