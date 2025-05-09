package com.pandacare.mainapp.reservasi.controller;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.ReservasiKonsultasiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservasi-konsultasi")
public class ReservasiKonsultasiController {

    @Autowired
    private ReservasiKonsultasiServiceImpl reservasiService;

    @PostMapping("/request")
    public ResponseEntity<?> requestReservasi(@RequestBody Map<String, String> body) {
        ReservasiKonsultasi result = reservasiService.requestReservasi(
                body.get("idDokter"),
                body.get("idPasien"),
                body.get("day"),
                body.get("startTime"),
                body.get("endTime")
        );

        return ResponseEntity.ok(Map.of(
                "message", "Jadwal konsultasi berhasil diajukan",
                "reservasi", result
        ));
    }

    @PostMapping("/{id}/edit")
    public ResponseEntity<?> editReservasi(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String day = body.get("day");
            String startTime = body.get("startTime");
            String endTime = body.get("endTime");

            ReservasiKonsultasi updated = reservasiService.editReservasi(id, day, startTime, endTime);

            return ResponseEntity.ok(Map.of(
                    "message", "Jadwal berhasil diperbarui",
                    "reservasi", updated
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{idPasien}")
    public ResponseEntity<?> getAllReservasiByPasien(@PathVariable String idPasien) {
        List<ReservasiKonsultasi> reservasiList = reservasiService.findAllByPasien(idPasien);
        return ResponseEntity.ok(reservasiList);
    }
}