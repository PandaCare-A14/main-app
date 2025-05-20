package com.pandacare.mainapp.reservasi.controller;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.ReservasiKonsultasiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservasi-konsultasi")
public class ReservasiKonsultasiController {

    @Autowired
    private ReservasiKonsultasiServiceImpl reservasiService;

    @PostMapping("/request")
    public ResponseEntity<?> requestReservasi(@RequestBody Map<String, String> body) {
        try {
            UUID idSchedule = UUID.fromString(body.get("idSchedule")); // Ambil ID jadwal langsung
            String idPacilian = body.get("idPacilian"); // Ambil ID pasien

            ReservasiKonsultasi result = reservasiService.requestReservasi(idSchedule, idPacilian);

            return ResponseEntity.ok(Map.of(
                    "message", "Jadwal konsultasi berhasil diajukan",
                    "reservasi", result
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
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
                    "reservasi", null
            ));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{idPasien}")
    public ResponseEntity<?> getAllReservasiByPasien(@PathVariable String idPasien) {
        List<ReservasiKonsultasi> reservasiList = reservasiService.findAllByPasien(idPasien);
        return ResponseEntity.ok(reservasiList);
    }

    @PostMapping("/{id}/accept-change")
    public ResponseEntity<?> acceptChangeReservasi(@PathVariable String id) {
        try {
            ReservasiKonsultasi updated = reservasiService.acceptChangeReservasi(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Perubahan reservasi diterima",
                    "reservasi", null
            ));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject-change")
    public ResponseEntity<?> rejectChangeReservasi(@PathVariable String id) {
        try {
            reservasiService.rejectChangeReservasi(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Perubahan jadwal ditolak"
            ));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}