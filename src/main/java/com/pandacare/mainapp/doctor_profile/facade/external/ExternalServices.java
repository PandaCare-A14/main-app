package com.pandacare.mainapp.doctor_profile.facade.external;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface ExternalServices {
    CompletableFuture<Void> startChat(String doctorId, String patientId);
    CompletableFuture<Void> createAppointment(String doctorId, String patientId, LocalDateTime time);
}
