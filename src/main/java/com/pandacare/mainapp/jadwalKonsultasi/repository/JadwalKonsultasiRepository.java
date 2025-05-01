package com.pandacare.mainapp.jadwalKonsultasi.repository;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;

import java.util.*;
import java.util.stream.Collectors;

public class JadwalKonsultasiRepository {
    private final Map<String, JadwalKonsultasi> data = new HashMap<>();

    public void save(JadwalKonsultasi jadwal) {
        data.put(jadwal.getId(), jadwal);
    }

    public JadwalKonsultasi findById(String id) {
        return data.get(id);
    }

    public List<JadwalKonsultasi> findByIdDokter(String idDokter) {
        return data.values().stream()
                .filter(j -> idDokter.equals(j.getIdDokter()))
                .collect(Collectors.toList());
    }

    public List<JadwalKonsultasi> findByIdPasien(String idPasien) {
        return data.values().stream()
                .filter(j -> idPasien.equals(j.getIdPasien()))
                .collect(Collectors.toList());
    }

    public List<JadwalKonsultasi> findByStatus(String statusDokter) {
        return data.values().stream()
                .filter(j -> statusDokter.equals(j.getStatusDokter()))
                .collect(Collectors.toList());
    }

    public List<JadwalKonsultasi> findAll() {
        return new ArrayList<>(data.values());
    }

    public void clear() {
        data.clear();
    }
}