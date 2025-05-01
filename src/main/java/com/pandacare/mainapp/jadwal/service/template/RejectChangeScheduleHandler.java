package com.pandacare.mainapp.jadwal.service.template;

import com.pandacare.mainapp.jadwal.model.JadwalKonsultasi;
import com.pandacare.mainapp.jadwal.repository.JadwalPacilianRepository;

public class RejectChangeScheduleHandler extends JadwalKonsultasiTemplate {

    private final String id;
    private final JadwalPacilianRepository repository;
    private JadwalKonsultasi jadwal;

    public RejectChangeScheduleHandler(String id, JadwalPacilianRepository repository) {
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
    protected JadwalKonsultasi prepare() {
        return jadwal;
    }

    @Override
    protected JadwalKonsultasi save(JadwalKonsultasi jadwal) {
        repository.deleteById(jadwal.getId());
        return null;
    }
}
