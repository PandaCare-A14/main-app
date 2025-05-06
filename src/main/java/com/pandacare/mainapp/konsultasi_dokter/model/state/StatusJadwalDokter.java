package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import java.time.LocalDate;
import java.time.LocalTime;

public interface StatusJadwalDokter {
    String getStatusName();

    boolean isAvailable();

    void handleRequest(JadwalKonsultasi context, String idPasien, String message);

    void handleApprove(JadwalKonsultasi context);

    void handleReject(JadwalKonsultasi context, String reason);

    void handleChangeSchedule(JadwalKonsultasi context, LocalDate newDay, LocalTime newStartTime, LocalTime newEndTime,
                              String reason);
}