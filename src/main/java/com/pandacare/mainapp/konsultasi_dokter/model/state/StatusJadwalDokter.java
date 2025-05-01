package com.pandacare.mainapp.konsultasi_dokter.model.state;

public interface StatusJadwalDokter {
    String getStatusName();

    boolean isAvailable();

    void handleRequest(JadwalStateContext context, String idPasien, String message);

    void handleApprove(JadwalStateContext context);

    void handleReject(JadwalStateContext context, String reason);

    void handleChangeSchedule(JadwalStateContext context, String newDay, String newStartTime, String newEndTime,
            String reason);
}