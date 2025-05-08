package com.pandacare.mainapp.jadwal.service.template;

import com.pandacare.mainapp.jadwal.enums.StatusJadwalPacilian;
import com.pandacare.mainapp.jadwal.model.ReservasiKonsultasi;
import com.pandacare.mainapp.jadwal.repository.JadwalPacilianRepository;

public class RequestJadwalHandler extends ReservasiKonsultasiTemplate {

    private final String idDokter, idPasien, day, startTime, endTime;
    private final JadwalPacilianRepository repository;

    public RequestJadwalHandler(String idDokter,  String idPasien, String day, String startTime, String endTime, JadwalPacilianRepository repository) {
        this.idDokter = idDokter;
        this.idPasien = idPasien;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repository = repository;
    }

    @Override
    protected void validate() {
        if (startTime.compareTo(endTime) >= 0) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (idDokter == null || day == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("All fields are required");
        }
    }

    @Override
    protected ReservasiKonsultasi prepare() {
        ReservasiKonsultasi jadwal = new ReservasiKonsultasi();
        jadwal.setIdDokter(idDokter);
        jadwal.setIdPasien(idPasien);
        jadwal.setDay(day);
        jadwal.setStartTime(startTime);
        jadwal.setEndTime(endTime);
        jadwal.setStatusPacilian(StatusJadwalPacilian.WAITING);
        return jadwal;
    }

    @Override
    protected ReservasiKonsultasi save(ReservasiKonsultasi jadwal) {
        return repository.save(jadwal);
    }
}
