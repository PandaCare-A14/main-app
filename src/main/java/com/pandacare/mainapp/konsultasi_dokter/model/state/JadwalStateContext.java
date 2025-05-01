package com.pandacare.mainapp.konsultasi_dokter.model.state;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;

public class JadwalStateContext {
    private final JadwalKonsultasi jadwal;

    public JadwalStateContext(JadwalKonsultasi jadwal) {
        this.jadwal = jadwal;
    }

    public JadwalKonsultasi getJadwal() {
        return jadwal;
    }

    public void setStatus(String status) {
        jadwal.setStatusDokter(status);
    }

    public void setPasien(String idPasien) {
        jadwal.setIdPasien(idPasien);
    }

    public void setMessage(String message) {
        jadwal.setMessage(message);
    }
}