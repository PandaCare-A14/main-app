package com.pandacare.mainapp.jadwal.repository;

import com.pandacare.mainapp.jadwal.model.JadwalKonsultasi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JadwalPacilianRepository extends JpaRepository<JadwalKonsultasi, String> {
}
