package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.repository.JadwalDokterRepository;
import com.pandacare.mainapp.konsultasi_dokter.model.strategy.CreateJadwalStrategy;
import com.pandacare.mainapp.konsultasi_dokter.model.strategy.CreateManualStrategy;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JadwalDokterServiceImpl implements JadwalDokterService {
    private final JadwalDokterRepository repository;
    private final JadwalKonsultasiStateHandler stateHandler;

    public JadwalDokterServiceImpl(JadwalDokterRepository repository, JadwalKonsultasiStateHandler stateHandler) {
        this.repository = repository;
        this.stateHandler = stateHandler;
    }

    @Override
    public JadwalKonsultasi createJadwal(String idDokter, String day, String startTime, String endTime) {
        CreateJadwalStrategy strategy = new CreateManualStrategy();
        JadwalKonsultasi jadwal = strategy.create(idDokter, day, startTime, endTime);
        jadwal.setId(UUID.randomUUID().toString());
        return repository.save(jadwal);
    }

    @Override
    public boolean changeJadwal(String idJadwal, String newDay, String newStartTime, String newEndTime, String message) {
        JadwalKonsultasi jadwal = repository.findByIdJadwal(idJadwal);
        if (jadwal == null) return false;
        stateHandler.changeSchedule(jadwal, newDay, newStartTime, newEndTime, message);
        repository.save(jadwal);
        return true;
    }

    @Override
    public boolean approveJadwal(String idJadwal) {
        JadwalKonsultasi jadwal = repository.findByIdJadwal(idJadwal);
        if (jadwal == null) return false;
        stateHandler.approve(jadwal);
        repository.save(jadwal);
        return true;
    }

    @Override
    public boolean rejectJadwal(String idJadwal) {
        JadwalKonsultasi jadwal = repository.findByIdJadwal(idJadwal);
        if (jadwal == null) return false;
        stateHandler.reject(jadwal, "Jadwal tidak sesuai");
        repository.save(jadwal);
        return true;
    }
}