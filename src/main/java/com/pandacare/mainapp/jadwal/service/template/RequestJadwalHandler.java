package com.pandacare.mainapp.jadwal.service.template;

import com.pandacare.mainapp.jadwal.enums.StatusJadwalPacilian;
import com.pandacare.mainapp.jadwal.model.JadwalKonsultasi;
import com.pandacare.mainapp.jadwal.repository.JadwalPacilianRepository;

public class RequestJadwalHandler extends JadwalKonsultasiTemplate {

    private final String idDokter, day, startTime, endTime;
    private final JadwalPacilianRepository repository;

    public RequestJadwalHandler(String idDokter, String day, String startTime, String endTime, JadwalPacilianRepository repository) {
        this.idDokter = idDokter;
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
    protected JadwalKonsultasi prepare() {
        JadwalKonsultasi jadwal = new JadwalKonsultasi();
        jadwal.setIdDokter(idDokter);
        jadwal.setDay(day);
        jadwal.setStartTime(startTime);
        jadwal.setEndTime(endTime);
        jadwal.setStatusPacilian(StatusJadwalPacilian.WAITING);
        return jadwal;
    }

    @Override
    protected JadwalKonsultasi save(JadwalKonsultasi jadwal) {
        return repository.save(jadwal);
    }
}
