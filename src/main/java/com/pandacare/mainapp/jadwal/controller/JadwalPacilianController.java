package com.pandacare.mainapp.jadwal.controller;

import com.pandacare.mainapp.jadwal.model.JadwalKonsultasi;
import com.pandacare.mainapp.jadwal.service.JadwalPacilianServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jadwal-konsultasi")
public class JadwalPacilianController {

    @Autowired
    private JadwalPacilianServiceImpl jadwalService;

    @PostMapping("/request")
    public ResponseEntity<?> requestJadwal(@RequestBody Map<String, String> body) {
        JadwalKonsultasi result = jadwalService.requestJadwal(
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

