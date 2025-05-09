package com.pandacare.mainapp.reservasi.service.template;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;

public class RejectChangeReservasiHandler extends ReservasiKonsultasiTemplate {

    private final String id;
    private final ReservasiKonsultasiRepository repository;
    private ReservasiKonsultasi reservasi;

    public RejectChangeReservasiHandler(String id, ReservasiKonsultasiRepository repository) {
        this.id = id;
        this.repository = repository;
    }

    @Override
    protected void validate() {
        reservasi = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    }

    @Override
    protected ReservasiKonsultasi prepare() {
        return reservasi;
    }

    @Override
    protected ReservasiKonsultasi save(ReservasiKonsultasi reservasi) {
        repository.deleteById(reservasi.getId());
        return null;
    }
}
