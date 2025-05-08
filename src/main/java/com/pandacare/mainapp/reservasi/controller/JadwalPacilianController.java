package com.pandacare.mainapp.reservasi.controller;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.ReservasiKonsultasiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jadwal-konsultasi")
public class JadwalPacilianController {

    @Autowired
    private ReservasiKonsultasiServiceImpl jadwalService;

    @PostMapping("/request")
    public ResponseEntity<?> requestJadwal(@RequestBody Map<String, String> body) {
        ReservasiKonsultasi result = jadwalService.requestJadwal(
                body.get("idDokter"),
                body.get("idPasien"),
                body.get("day"),
                body.get("startTime"),
                body.get("endTime")
        );

        return ResponseEntity.ok(Map.of(
                "message", "Jadwal konsultasi berhasil diajukan",
                "jadwal", result
        ));
    }
}

