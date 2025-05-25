package com.pandacare.mainapp.doctor_profile.facade.external;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Profile("!prod")
public class MockExternalServices implements ExternalServices {

    @Async
    @Override
    public CompletableFuture<Void> startChat(UUID caregiverId, UUID patientId) {
        System.out.println("[MOCK] Chat initiated - Doctor: " + caregiverId + ", Patient: " + patientId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Override
    public CompletableFuture<Void> createAppointment(UUID caregiverId, UUID patientId, LocalDateTime time) {
        System.out.println("[MOCK] Appointment created - Doctor: " + caregiverId +
                ", Patient: " + patientId + ", Time: " + time);
        return CompletableFuture.completedFuture(null);
    }
}