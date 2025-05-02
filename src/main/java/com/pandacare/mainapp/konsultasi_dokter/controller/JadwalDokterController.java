package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.service.JadwalDokterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class JadwalDokterController {
    private final JadwalDokterService service;

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "AVAILABLE", "APPROVED", "REJECTED", "REQUESTED", "CHANGE_SCHEDULE"
    );

    public JadwalDokterController(JadwalDokterService service) {
        this.service = service;
    }

    @PostMapping("/doctors/{idDokter}/schedules")
    public ResponseEntity<JadwalKonsultasi> createJadwal(
            @PathVariable String idDokter,
            @RequestBody Map<String, String> body
    ) {
        String day = body.get("day");
        String startTime = body.get("startTime");
        String endTime = body.get("endTime");

        JadwalKonsultasi jadwal = service.createJadwal(idDokter, day, startTime, endTime);
        return ResponseEntity.ok(jadwal);
    }

    @GetMapping("/doctors/{idDokter}/schedules")
    public ResponseEntity<List<JadwalKonsultasi>> getJadwalByDokter(
            @PathVariable String idDokter,
            @RequestParam(required = false) String status
    ) {
        if (status != null) {
            if (!ALLOWED_STATUSES.contains(status.toUpperCase())) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }
            return ResponseEntity.ok(service.findByIdDokterAndStatus(idDokter, status.toUpperCase()));
        }
        return ResponseEntity.ok(service.findByIdDokter(idDokter));
    }

    @GetMapping("/doctor/schedules/{id}")
    public ResponseEntity<JadwalKonsultasi> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/patients/{idPasien}/schedules")
    public ResponseEntity<List<JadwalKonsultasi>> findByIdPasien(@PathVariable String idPasien) {
        return ResponseEntity.ok(service.findByIdPasien(idPasien));
    }

    @PatchMapping("/schedules/{id}/status")
    public ResponseEntity<JadwalKonsultasi> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ) {
        String status = body.get("statusDokter");
        String message = body.get("message");

        switch (status) {
            case "APPROVED":
                service.approveJadwal(id);
                break;
            case "REJECTED":
                service.rejectJadwal(id);
                break;
            case "CHANGE_SCHEDULE":
                String day = body.get("day");
                String startTime = body.get("startTime");
                String endTime = body.get("endTime");
                service.changeJadwal(id, day, startTime, endTime, message);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(service.findById(id));
    }
}