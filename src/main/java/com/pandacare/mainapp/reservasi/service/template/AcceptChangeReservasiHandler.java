package com.pandacare.mainapp.reservasi.service.template;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;

public class AcceptChangeReservasiHandler extends ReservasiKonsultasiTemplate {

    private final String id;
    private final ReservasiKonsultasiRepository repository;
    private ReservasiKonsultasi reservasi;

    public AcceptChangeReservasiHandler(String id, ReservasiKonsultasiRepository repository) {
        this.id = id;
        this.repository = repository;
    }

    @Override
    protected void validate() {
        reservasi = repository.findById(id).get();

        if (!reservasi.isChangeReservasi()) {
            throw new IllegalStateException("No change request exists for this schedule");
        }
    }

    @Override
    protected ReservasiKonsultasi prepare() {
        reservasi.setDay(reservasi.getNewDay());
        reservasi.setStartTime(reservasi.getNewStartTime());
        reservasi.setEndTime(reservasi.getNewEndTime());

        reservasi.setNewDay(null);
        reservasi.setNewStartTime(null);
        reservasi.setNewEndTime(null);
        reservasi.setChangeReservasi(false);

        return reservasi;
    }

    @Override
    protected ReservasiKonsultasi save(ReservasiKonsultasi reservasi) {
        return repository.save(reservasi);
    }
}
