package com.pandacare.mainapp.reservasi.service;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.JadwalPacilianRepository;
import com.pandacare.mainapp.reservasi.service.template.AcceptChangeScheduleHandler;
import com.pandacare.mainapp.reservasi.service.template.EditScheduleHandler;
import com.pandacare.mainapp.reservasi.service.template.RejectChangeScheduleHandler;
import com.pandacare.mainapp.reservasi.service.template.RequestJadwalHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservasiKonsultasiServiceImpl {

    @Autowired
    private JadwalPacilianRepository repository;

    public ReservasiKonsultasi requestJadwal(String idDokter, String idPasien, String day, String startTime, String endTime) {
        RequestJadwalHandler handler = new RequestJadwalHandler(idDokter, idPasien, day, startTime, endTime, repository);
        return handler.handle();
    }

    public ReservasiKonsultasi editSchedule(String id, String day, String startTime, String endTime) {
        EditScheduleHandler handler = new EditScheduleHandler(id, day, startTime, endTime, repository);
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
        AcceptChangeScheduleHandler handler = new AcceptChangeScheduleHandler(id, repository);
        return handler.handle();
    }

    public void rejectChangeSchedule(String id) {
        new RejectChangeScheduleHandler(id, repository).handle();
    }
}
