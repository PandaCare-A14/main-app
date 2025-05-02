package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.pandacare.mainapp.jadwalKonsultasi.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.service.JadwalDokterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class JadwalDokterController {

    private final JadwalDokterService service;

    @PostMapping("/doctors/{idDokter}/schedules")
    public ResponseEntity<?> createJadwal(@PathVariable String idDokter,
                                          @RequestBody Map<String, String> body) {
        JadwalKonsultasi created = service.createJadwal(
                idDokter,
                body.get("day"),
                body.get("startTime"),
                body.get("endTime")
        );
        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("schedule", created)));
    }

    @GetMapping("/doctors/{idDokter}/schedules")
    public ResponseEntity<?> getAllByDokter(@PathVariable String idDokter,
                                            @RequestParam(required = false) String status) {
        List<JadwalKonsultasi> list = status == null
                ? service.findByIdDokter(idDokter)
                : service.findByIdDokterAndStatus(idDokter, status);
        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("schedules", list)));
    }

    @GetMapping("/patients/{idPasien}/schedules")
    public ResponseEntity<?> getAllByPasien(@PathVariable String idPasien) {
        List<JadwalKonsultasi> list = service.findByIdPasien(idPasien);
        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("schedules", list)));
    }

    @GetMapping("/doctor/schedules/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        JadwalKonsultasi jadwal = service.findById(id);
        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("schedule", jadwal)));
    }

    @PatchMapping("/schedules/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id,
                                          @RequestBody Map<String, String> body) {
        String status = body.get("statusDokter");
        String message = body.get("message");
        boolean result = false;

        switch (status) {
            case "APPROVED" -> result = service.approveJadwal(id);
            case "REJECTED" -> result = service.rejectJadwal(id);
            case "CHANGE_SCHEDULE" -> result = service.changeJadwal(
                    id,
                    body.get("day"),
                    body.get("startTime"),
                    body.get("endTime"),
                    message
            );
        }

        if (!result) return ResponseEntity.badRequest().body(Map.of("status", "fail"));
        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("schedule", service.findById(id))));
    }
}