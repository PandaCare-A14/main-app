package com.pandacare.mainapp.doctor_profile.facade.external;

import java.time.LocalDateTime;

public interface ExternalServices {
    void startChat(String doctorId, String patientId);
    void createAppointment(String doctorId, String patientId, LocalDateTime time);
}
