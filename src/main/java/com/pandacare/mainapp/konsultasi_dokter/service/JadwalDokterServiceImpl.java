package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.model.strategy.CreateJadwalStrategy;
import com.pandacare.mainapp.konsultasi_dokter.model.strategy.CreateManualStrategy;
import com.pandacare.mainapp.konsultasi_dokter.repository.JadwalKonsultasiRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JadwalDokterServiceImpl implements JadwalDokterService {
    private final JadwalKonsultasiRepository repository;

    public JadwalDokterServiceImpl(JadwalKonsultasiRepository repository) {
        this.repository = repository;
    }

    @Override
    public JadwalKonsultasi createJadwal(String idDokter, LocalDate date, LocalTime startTime, LocalTime endTime) {
        CreateJadwalStrategy strategy = new CreateManualStrategy();
        JadwalKonsultasi jadwal = strategy.create(idDokter, date, startTime, endTime);
        jadwal.setId(UUID.randomUUID().toString());
        return repository.save(jadwal);
    }

    @Override
    public boolean changeJadwal(String idJadwal, String newDay, String newStartTime, String newEndTime, String message) {
        JadwalKonsultasi jadwal = repository.findById(idJadwal);
        if (jadwal == null) return false;
        jadwal.changeSchedule(LocalDate.parse(newDay), LocalTime.parse(newStartTime), LocalTime.parse(newEndTime), message);
        repository.save(jadwal);
        return true;
    }

    @Override
    public boolean approveJadwal(String idJadwal) {
        JadwalKonsultasi jadwal = repository.findById(idJadwal);
        if (jadwal == null) return false;
        jadwal.approve();
        repository.save(jadwal);
        return true;
    }

    @Override
    public boolean rejectJadwal(String idJadwal) {
        JadwalKonsultasi jadwal = repository.findById(idJadwal);
        if (jadwal == null) return false;
        jadwal.reject("Jadwal tidak sesuai");
        repository.save(jadwal);
        return true;
    }

    @Override
    public List<JadwalKonsultasi> findByIdDokter(String idDokter) {
        return repository.findByIdDokter(idDokter);
    }

    @Override
    public List<JadwalKonsultasi> findByIdDokterAndStatus(String idDokter, String status) {
        return repository.findByIdDokter(idDokter).stream()
                .filter(j -> status.equals(j.getStatusDokter()))
                .collect(Collectors.toList());
    }

    @Override
    public List<JadwalKonsultasi> findByIdPasien(String idPasien) {
        return repository.findByIdPasien(idPasien);
    }

    @Override
    public JadwalKonsultasi findById(String id) {
        return repository.findById(id);
    }
}