package com.pandacare.mainapp.reservasi.repository;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservasiKonsultasiRepository extends JpaRepository<ReservasiKonsultasi, String> {
    List<ReservasiKonsultasi> findAllByIdPasien(String idPasien);
}
