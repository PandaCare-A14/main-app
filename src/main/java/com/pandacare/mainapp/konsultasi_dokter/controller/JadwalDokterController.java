package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.service.JadwalDokterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/doctors/")
public class JadwalDokterController {
    private final JadwalDokterService service;

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "AVAILABLE", "APPROVED", "REJECTED", "REQUESTED", "CHANGE_SCHEDULE"
    );

    public JadwalDokterController(JadwalDokterService service) {
        this.service = service;
    }

    @PostMapping("/{idDokter}/schedules")
    public ResponseEntity<JadwalKonsultasi> createJadwal(
            @PathVariable String idDokter,
            @RequestBody Map<String, String> body
    ) {
        try {
            LocalDate date = LocalDate.parse(body.get("date"));
            LocalTime startTime = LocalTime.parse(body.get("startTime"));
            LocalTime endTime = LocalTime.parse(body.get("endTime"));
            JadwalKonsultasi jadwal = service.createJadwal(idDokter, date, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(jadwal);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{idDokter}/schedules/interval")
    public ResponseEntity<List<JadwalKonsultasi>> createJadwalInterval(
            @PathVariable String idDokter,
            @RequestBody Map<String, String> body
    ) {
        try {
            LocalDate date = LocalDate.parse(body.get("date"));
            LocalTime startTime = LocalTime.parse(body.get("startTime"));
            LocalTime endTime = LocalTime.parse(body.get("endTime"));

            List<JadwalKonsultasi> jadwals = service.createJadwalInterval(idDokter, date, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(jadwals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{idDokter}/schedules")
    public ResponseEntity<List<JadwalKonsultasi>> getJadwalByDokter(
            @PathVariable String idDokter,
            @RequestParam(required = false) String status
    ) {
        try {
            List<JadwalKonsultasi> jadwals;
            if (status != null && ALLOWED_STATUSES.contains(status.toUpperCase())) {
                jadwals = service.findByIdDokterAndStatus(idDokter, status.toUpperCase());
            } else {
                jadwals = service.findByIdDokter(idDokter);
            }
            return ResponseEntity.ok(jadwals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/schedules/{id}")
    public ResponseEntity<JadwalKonsultasi> findById(@PathVariable String id) {
        try {
            JadwalKonsultasi jadwal = service.findById(id);
            if (jadwal != null) {
                return ResponseEntity.ok(jadwal);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/schedules/{id}/status")
    public ResponseEntity<JadwalKonsultasi> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String status = body.get("statusDokter");
            if (status == null) {
                return ResponseEntity.badRequest().build();
            }

            boolean success;

            switch (status) {
                case "APPROVED":
                    success = service.approveJadwal(id);
                    if (success) {
                        return ResponseEntity.ok(service.findById(id));
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                case "REJECTED":
                    success = service.rejectJadwal(id);
                    if (success) {
                        return ResponseEntity.ok(service.findById(id));
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                case "CHANGE_SCHEDULE":
                    String message = body.get("message");
                    LocalDate date = LocalDate.parse(body.get("date"));
                    LocalTime startTime = LocalTime.parse(body.get("startTime"));
                    LocalTime endTime = LocalTime.parse(body.get("endTime"));
                    success = service.changeJadwal(id, date, startTime, endTime, message);
                    if (success) {
                        return ResponseEntity.ok(service.findById(id));
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(null);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}