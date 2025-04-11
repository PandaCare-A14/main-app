package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.model.StatusJadwalDokter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class JadwalKonsultasiRepository {
    private final List<JadwalKonsultasi> data = new ArrayList<>();

    public void save(JadwalKonsultasi jadwal) {
        for (int i = 0; i < data.size(); i++) {
            if (Objects.equals(data.get(i).getId(), jadwal.getId())) {
                data.set(i, jadwal);
                return;
            }
        }
        data.add(jadwal);
    }

    public JadwalKonsultasi findById(String id) {
        return data.stream()
                .filter(j -> Objects.equals(j.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public JadwalKonsultasi findByIdJadwal(String id) {
        return findById(id);
    }

    public List<JadwalKonsultasi> findByStatus(StatusJadwalDokter status) {
        return data.stream()
                .filter(j -> j.getStatusDokter() == status)
                .toList();
    }

    public List<JadwalKonsultasi> findByIdPasien(Long idPasien) {
        return data.stream()
                .filter(j -> Objects.equals(j.getIdPasien(), idPasien))
                .toList();
    }

    public List<JadwalKonsultasi> findByIdDokter(Long idDokter) {
        return data.stream()
                .filter(j -> Objects.equals(j.getIdDokter(), idDokter))
                .toList();
    }

    public List<JadwalKonsultasi> findAll() {
        return data.stream().toList();
    }

    public void deleteById(String id) {
        JadwalKonsultasi target = null;
        for (JadwalKonsultasi j : data) {
            if (Objects.equals(j.getId(), id)) {
                target = j;
                break;
            }
        }
        if (target != null) {
            data.remove(target);
        }
    }
}
