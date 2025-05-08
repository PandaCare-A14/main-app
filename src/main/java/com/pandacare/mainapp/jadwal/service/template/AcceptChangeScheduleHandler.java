package com.pandacare.mainapp.jadwal.service.template;

import com.pandacare.mainapp.jadwal.model.ReservasiKonsultasi;
import com.pandacare.mainapp.jadwal.repository.JadwalPacilianRepository;

public class AcceptChangeScheduleHandler extends ReservasiKonsultasiTemplate {

    private final String id;
    private final JadwalPacilianRepository repository;
    private ReservasiKonsultasi jadwal;

    public AcceptChangeScheduleHandler(String id, JadwalPacilianRepository repository) {
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
        jadwal.setDay(jadwal.getNewDay());
        jadwal.setStartTime(jadwal.getNewStartTime());
        jadwal.setEndTime(jadwal.getNewEndTime());

        jadwal.setNewDay(null);
        jadwal.setNewStartTime(null);
        jadwal.setNewEndTime(null);
        jadwal.setChangeSchedule(false);

        return jadwal;
    }

    @Override
    protected ReservasiKonsultasi save(ReservasiKonsultasi jadwal) {
        return repository.save(jadwal);
    }
}
