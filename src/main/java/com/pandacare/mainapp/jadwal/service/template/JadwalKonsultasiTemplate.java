package com.pandacare.mainapp.jadwal.service.template;

import com.pandacare.mainapp.jadwal.model.JadwalKonsultasi;

public abstract class JadwalKonsultasiTemplate {

    public final JadwalKonsultasi handle() {
        validate();
        JadwalKonsultasi jadwal = prepare();
        return save(jadwal);
    }

    protected abstract void validate();
    protected abstract JadwalKonsultasi prepare();
    protected abstract JadwalKonsultasi save(JadwalKonsultasi jadwal);
}
