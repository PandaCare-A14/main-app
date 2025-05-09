package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public class JadwalKonsultasiRepository {
    private final Map<String, JadwalKonsultasi> data = new HashMap<>();

    public JadwalKonsultasi save(JadwalKonsultasi jadwal) {
        if (jadwal.getId() == null) {
            jadwal.setId(UUID.randomUUID().toString());
        }
        data.put(jadwal.getId(), jadwal);
        return jadwal;
    }

    public JadwalKonsultasi findById(String id) {
        return data.get(id);
    }

    public List<JadwalKonsultasi> findByIdDokter(String idDokter) {
        return data.values().stream()
                .filter(j -> idDokter.equals(j.getIdDokter()))
                .collect(Collectors.toList());
    }

    public List<JadwalKonsultasi> findByIdDokterAndDate(String idDokter, LocalDate date) {
        return data.values().stream()
                .filter(j -> idDokter.equals(j.getIdDokter()) && date.equals(j.getDate()))
                .collect(Collectors.toList());
    }

    public List<JadwalKonsultasi> findOverlappingSchedule(String idDokter, LocalDate date,
                                                        LocalTime startTime, LocalTime endTime) {
        return data.values().stream()
                .filter(j -> idDokter.equals(j.getIdDokter())
                        && date.equals(j.getDate())
                        && j.getStartTime().isBefore(endTime)
                        && j.getEndTime().isAfter(startTime))
                .collect(Collectors.toList());
    }

    public List<JadwalKonsultasi> findByIdPasien(String idPasien) {
        return data.values().stream()
                .filter(j -> idPasien != null && idPasien.equals(j.getIdPasien()))
                .collect(Collectors.toList());
    }

    public List<JadwalKonsultasi> findByStatus(String statusDokter) {
        return data.values().stream()
                .filter(j -> statusDokter.equals(j.getStatusDokter()))
                .collect(Collectors.toList());
    }
}