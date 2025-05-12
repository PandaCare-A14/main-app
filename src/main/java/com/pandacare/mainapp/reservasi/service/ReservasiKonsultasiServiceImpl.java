package com.pandacare.mainapp.reservasi.service;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import com.pandacare.mainapp.reservasi.service.template.AcceptChangeReservasiHandler;
import com.pandacare.mainapp.reservasi.service.template.EditReservasiHandler;
import com.pandacare.mainapp.reservasi.service.template.RejectChangeReservasiHandler;
import com.pandacare.mainapp.reservasi.service.template.RequestReservasiHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservasiKonsultasiServiceImpl {

    @Autowired
    private ReservasiKonsultasiRepository repository;

    public ReservasiKonsultasi requestReservasi(String idDokter, String idPasien, String day, String startTime, String endTime) {
        RequestReservasiHandler handler = new RequestReservasiHandler(idDokter, idPasien, day, startTime, endTime, repository);
        return handler.handle();
    }

    public ReservasiKonsultasi editReservasi(String id, String day, String startTime, String endTime) {
        EditReservasiHandler handler = new EditReservasiHandler(id, day, startTime, endTime, repository);
        return handler.handle();
    }

    public ReservasiKonsultasi acceptChangeReservasi(String id) {
        AcceptChangeReservasiHandler handler = new AcceptChangeReservasiHandler(id, repository);
        return handler.handle();
    }

    public void rejectChangeReservasi(String id) {
        new RejectChangeReservasiHandler(id, repository).handle();
    }

    public ReservasiKonsultasi findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public List<ReservasiKonsultasi> findAllByPasien(String idPasien) {
        return repository.findAllByIdPasien(idPasien);
    }
}
