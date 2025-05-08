package com.pandacare.mainapp.reservasi.service.template;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

public abstract class ReservasiKonsultasiTemplate {

    public final ReservasiKonsultasi handle() {
        validate();
        ReservasiKonsultasi reservasi = prepare();
        return save(reservasi);
    }

    protected abstract void validate();
    protected abstract ReservasiKonsultasi prepare();
    protected abstract ReservasiKonsultasi save(ReservasiKonsultasi jadwal);
}
