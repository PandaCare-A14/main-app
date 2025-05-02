package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import com.pandacare.mainapp.jadwalKonsultasi.repository.JadwalKonsultasiRepository;

import java.util.List;
import java.util.stream.Collectors;

public class JadwalDokterRepository {
    private final JadwalKonsultasiRepository sharedRepo = new JadwalKonsultasiRepository();

    public void save(JadwalKonsultasi jadwal) {
        sharedRepo.save(jadwal);
    }

    public JadwalKonsultasi findByIdJadwal(String id) {
        return sharedRepo.findById(id);
    }

    public List<JadwalKonsultasi> findAll() {
        return sharedRepo.findAll();
    }

    public List<JadwalKonsultasi> findByIdDokter(String idDokter) {
        return sharedRepo.findAll().stream()
                .filter(j -> idDokter.equals(j.getIdDokter()))
                .collect(Collectors.toList());
    }

    public List<JadwalKonsultasi> findByStatus(String status) {
        return sharedRepo.findAll().stream()
                .filter(j -> status.equals(j.getStatusDokter()))
                .collect(Collectors.toList());
    }

    public List<JadwalKonsultasi> findByIdPasien(String idPasien) {
        return sharedRepo.findAll().stream()
                .filter(j -> idPasien.equals(j.getIdPasien()))
                .collect(Collectors.toList());
    }
}