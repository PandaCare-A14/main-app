package com.pandacare.mainapp.doctor_profile.facade.external;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Profile("!prod")
public class MockExternalServices implements ExternalServices {

    @Async
    @Override
    public CompletableFuture<Void> startChat(String doctorId, String patientId) {
        System.out.println("[MOCK] Chat initiated - Doctor: " + doctorId + ", Patient: " + patientId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Override
    public CompletableFuture<Void> createAppointment(String doctorId, String patientId, LocalDateTime time) {
        System.out.println("[MOCK] Appointment created - Doctor: " + doctorId +
                ", Patient: " + patientId + ", Time: " + time);
        return CompletableFuture.completedFuture(null);
    }
}