package com.pandacare.mainapp.jadwal.service.template;

import com.pandacare.mainapp.jadwal.model.ReservasiKonsultasi;

public abstract class ReservasiKonsultasiTemplate {

    public final ReservasiKonsultasi handle() {
        validate();
        ReservasiKonsultasi jadwal = prepare();
        return save(jadwal);
    }

    protected abstract void validate();
    protected abstract ReservasiKonsultasi prepare();
    protected abstract ReservasiKonsultasi save(ReservasiKonsultasi jadwal);
}
