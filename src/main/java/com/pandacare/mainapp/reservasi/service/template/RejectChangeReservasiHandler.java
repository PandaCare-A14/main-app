package com.pandacare.mainapp.reservasi.service.template;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;

public class RejectChangeReservasiHandler extends ReservasiKonsultasiTemplate {

    private final String id;
    private final ReservasiKonsultasiRepository repository;
    private ReservasiKonsultasi jadwal;

    public RejectChangeReservasiHandler(String id, ReservasiKonsultasiRepository repository) {
        this.id = id;
        this.repository = repository;
    }

    @Override
    protected void validate() {
        jadwal = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (!jadwal.isChangeSchedule()) {
            throw new IllegalStateException("No change request exists for this schedule");
        }
    }

    @Override
    protected ReservasiKonsultasi prepare() {
        return jadwal;
    }

    @Override
    protected ReservasiKonsultasi save(ReservasiKonsultasi jadwal) {
        repository.deleteById(jadwal.getId());
        return null;
    }
}
