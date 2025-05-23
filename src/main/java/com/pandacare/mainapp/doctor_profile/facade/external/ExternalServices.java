package com.pandacare.mainapp.doctor_profile.facade.external;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ExternalServices {
    CompletableFuture<Void> startChat(UUID caregiverId, UUID patientId);
    CompletableFuture<Void> createAppointment(UUID caregiverId, UUID patientId, LocalDateTime time);
}
