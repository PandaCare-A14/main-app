package com.pandacare.mainapp.konsultasi_dokter.model;

public class PatientObserver implements JadwalObserver {
    private Long patientId;

    public PatientObserver(Long patientId) {
        this.patientId = patientId;
    }

    @Override
    public void update(JadwalKonsultasi jadwal) {
        if (jadwal == null) {
            return;
        }

        StatusJadwalDokter status = jadwal.getStatusDokter();
        String message;

        switch (status) {
            case APPROVED:
                message = "Disetujui";
                break;

            case REJECTED:
                message = "Ditolak";
                break;

            case CHANGE_SCHEDULE:
                message = "Perubahan jadwal";
                break;

            default:
                return;
        }

        System.out.println("Notifikasi untuk Pasien " + patientId + ": " + message);
    }
}