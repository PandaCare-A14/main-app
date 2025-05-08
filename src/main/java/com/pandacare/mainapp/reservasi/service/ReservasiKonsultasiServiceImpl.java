package com.pandacare.mainapp.reservasi.service;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import com.pandacare.mainapp.reservasi.service.template.AcceptChangeReservasiHandler;
import com.pandacare.mainapp.reservasi.service.template.EditReservasiHandler;
import com.pandacare.mainapp.reservasi.service.template.RejectChangeReservasiHandler;
import com.pandacare.mainapp.reservasi.service.template.RequestReservasiHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservasiKonsultasiServiceImpl {

    @Autowired
    private ReservasiKonsultasiRepository repository;

    public ReservasiKonsultasi requestJadwal(String idDokter, String idPasien, String day, String startTime, String endTime) {
        RequestReservasiHandler handler = new RequestReservasiHandler(idDokter, idPasien, day, startTime, endTime, repository);
        return handler.handle();
    }

    public ReservasiKonsultasi editSchedule(String id, String day, String startTime, String endTime) {
        EditReservasiHandler handler = new EditReservasiHandler(id, day, startTime, endTime, repository);
        return handler.handle();
    }

    public ReservasiKonsultasi findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public ReservasiKonsultasi acceptChangeSchedule(String id) {
        AcceptChangeReservasiHandler handler = new AcceptChangeReservasiHandler(id, repository);
        return handler.handle();
    }

    public void rejectChangeSchedule(String id) {
        new RejectChangeReservasiHandler(id, repository).handle();
    }
}
