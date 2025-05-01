package com.pandacare.mainapp.jadwal.service;

import com.pandacare.mainapp.jadwal.enums.StatusJadwalPacilian;
import com.pandacare.mainapp.jadwal.model.JadwalKonsultasi;
import com.pandacare.mainapp.jadwal.repository.JadwalPacilianRepository;
import com.pandacare.mainapp.jadwal.service.template.AcceptChangeScheduleHandler;
import com.pandacare.mainapp.jadwal.service.template.EditScheduleHandler;
import com.pandacare.mainapp.jadwal.service.template.RejectChangeScheduleHandler;
import com.pandacare.mainapp.jadwal.service.template.RequestJadwalHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JadwalPacilianServiceImpl {

    @Autowired
    private JadwalPacilianRepository repository;

    public JadwalKonsultasi requestJadwal(String idDokter, String idPasien, String day, String startTime, String endTime) {
        RequestJadwalHandler handler = new RequestJadwalHandler(idDokter, idPasien, day, startTime, endTime, repository);
        return handler.handle();
    }

    public JadwalKonsultasi editSchedule(String id, String day, String startTime, String endTime) {
        EditScheduleHandler handler = new EditScheduleHandler(id, day, startTime, endTime, repository);
        return handler.handle();
    }

    public JadwalKonsultasi findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public JadwalKonsultasi acceptChangeSchedule(String id) {
        AcceptChangeScheduleHandler handler = new AcceptChangeScheduleHandler(id, repository);
        return handler.handle();
    }

    public void rejectChangeSchedule(String id) {
        new RejectChangeScheduleHandler(id, repository).handle();
    }
}
