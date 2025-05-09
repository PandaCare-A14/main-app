package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import java.time.LocalDate;
import java.time.LocalTime;

public interface StatusCaregiver {
    String getStatusName();

    boolean isAvailable();

    void handleRequest(CaregiverSchedule context, String idPasien, String message);

    void handleApprove(CaregiverSchedule context);

    void handleReject(CaregiverSchedule context, String reason);

    void handleChangeSchedule(CaregiverSchedule context, LocalDate newDay, LocalTime newStartTime, LocalTime newEndTime,
                              String reason);
}