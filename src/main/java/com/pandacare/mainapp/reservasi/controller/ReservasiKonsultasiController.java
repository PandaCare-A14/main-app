package com.pandacare.mainapp.reservasi.controller;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.ReservasiKonsultasiService;
import com.pandacare.mainapp.reservasi.service.ReservasiKonsultasiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservasi-konsultasi")
public class ReservasiKonsultasiController {

    @Autowired
    private ReservasiKonsultasiService reservasiService;

    @PostMapping("/request")
    public ResponseEntity<?> requestReservasi(@RequestBody Map<String, String> body) {
        try {
            UUID idSchedule = UUID.fromString(body.get("idSchedule"));
            UUID idPacilian = UUID.fromString(body.get("idPacilian")); // Convert to UUID

            ReservasiKonsultasi result = reservasiService.requestReservasi(idSchedule, idPacilian);

            Map<String, Object> reservasiMap = new HashMap<>();
            reservasiMap.put("idReservasi", result.getId());
            reservasiMap.put("idSchedule", result.getIdSchedule());
            reservasiMap.put("idPacilian", result.getIdPacilian());
            reservasiMap.put("statusReservasi", result.getStatusReservasi());
            reservasiMap.put("pacilianNote", result.getPacilianNote());

            return ResponseEntity.ok(Map.of(
                    "message", "Jadwal konsultasi berhasil diajukan",
                    "reservasi", reservasiMap
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }    @PostMapping("/{id}/edit")
    public ResponseEntity<?> editReservasi(@PathVariable("id") UUID id, @RequestBody Map<String, String> request) {
        try {
            UUID newScheduleId = UUID.fromString(request.get("idSchedule"));
            ReservasiKonsultasi updated = reservasiService.editReservasi(id, newScheduleId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reservasi updated successfully");
            response.put("reservasi", updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }    @GetMapping("/{idPacilian}")
    public ResponseEntity<?> getAllReservasiByPacilian(@PathVariable("idPacilian") UUID idPacilian) {
        try {
            List<?> reservations = reservasiService.findAllByPacilian(idPacilian).get();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    @PostMapping("/{id}/accept-change")
    public ResponseEntity<?> acceptChangeReservasi(@PathVariable("id") UUID id) {
        try {
            ReservasiKonsultasi updated = reservasiService.acceptChangeReservasi(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Perubahan reservasi diterima");
            response.put("reservasi", updated); // Can handle null values
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }    @PostMapping("/{id}/reject-change")
    public ResponseEntity<?> rejectChangeReservasi(@PathVariable("id") UUID id) {
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