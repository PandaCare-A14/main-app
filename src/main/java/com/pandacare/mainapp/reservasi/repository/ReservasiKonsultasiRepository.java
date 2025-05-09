package com.pandacare.mainapp.reservasi.repository;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservasiKonsultasiRepository extends JpaRepository<ReservasiKonsultasi, String> {
    List<ReservasiKonsultasi> findAllByIdPasien(String idPasien);
}
