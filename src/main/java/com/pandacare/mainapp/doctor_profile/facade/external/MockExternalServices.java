package com.pandacare.mainapp.doctor_profile.facade.external;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Profile("!prod")
public class MockExternalServices implements ExternalServices {

    @Override
    public void startChat(String doctorId, String patientId) {
        System.out.println("[MOCK] Chat initiated - Doctor: " + doctorId + ", Patient: " + patientId);
    }

    @Override
    public void createAppointment(String doctorId, String patientId, LocalDateTime time) {
        System.out.println("[MOCK] Appointment created - Doctor: " + doctorId +
                ", Patient: " + patientId + ", Time: " + time);
    }
}