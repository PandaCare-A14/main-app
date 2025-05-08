package com.pandacare.mainapp.reservasi.service.template;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;

public class RequestJadwalHandler extends ReservasiKonsultasiTemplate {

    private final String idDokter, idPasien, day, startTime, endTime;
    private final ReservasiKonsultasiRepository repository;

    public RequestJadwalHandler(String idDokter,  String idPasien, String day, String startTime, String endTime, ReservasiKonsultasiRepository repository) {
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
        jadwal.setStatusPacilian(StatusReservasiKonsultasi.WAITING);
        return jadwal;
    }

    @Override
    protected ReservasiKonsultasi save(ReservasiKonsultasi jadwal) {
        return repository.save(jadwal);
    }
}
