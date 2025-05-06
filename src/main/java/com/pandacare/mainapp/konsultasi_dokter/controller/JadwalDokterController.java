package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.service.JadwalDokterService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
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
    public JadwalKonsultasi createJadwal(
            @PathVariable String idDokter,
            @RequestBody Map<String, String> body
    ) {
        LocalDate date = LocalDate.parse(body.get("date"));
        LocalTime startTime = LocalTime.parse(body.get("startTime"));
        LocalTime endTime = LocalTime.parse(body.get("endTime"));
        return service.createJadwal(idDokter, date, startTime, endTime);
    }

    @GetMapping("/doctors/{idDokter}/schedules")
    public List<JadwalKonsultasi> getJadwalByDokter(
            @PathVariable String idDokter,
            @RequestParam(required = false) String status
    ) {
        if (status != null && ALLOWED_STATUSES.contains(status.toUpperCase())) {
            return service.findByIdDokterAndStatus(idDokter, status.toUpperCase());
        }
        return service.findByIdDokter(idDokter);
    }

    @GetMapping("/doctor/schedules/{id}")
    public JadwalKonsultasi findById(@PathVariable String id) {
        return service.findById(id);
    }

    @GetMapping("/patients/{idPasien}/schedules")
    public List<JadwalKonsultasi> findByIdPasien(@PathVariable String idPasien) {
        return service.findByIdPasien(idPasien);
    }

    @PatchMapping("/schedules/{id}/status")
    public JadwalKonsultasi updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ) {
        String status = body.get("statusDokter");
        String message = body.get("message");

        switch (status) {
            case "APPROVED" -> service.approveJadwal(id);
            case "REJECTED" -> service.rejectJadwal(id);
            case "CHANGE_SCHEDULE" -> {
                String date = body.get("date");
                String startTime = body.get("startTime");
                String endTime = body.get("endTime");
                service.changeJadwal(id, date, startTime, endTime, message);
            }
            default -> throw new IllegalArgumentException("Status tidak valid: " + status);
        }

        return service.findById(id);
    }
}