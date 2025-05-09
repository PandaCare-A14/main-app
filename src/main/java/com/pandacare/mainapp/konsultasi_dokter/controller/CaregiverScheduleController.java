package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.service.CaregiverScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/doctors/")
public class CaregiverScheduleController {
    private final CaregiverScheduleService service;

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "AVAILABLE", "APPROVED", "REJECTED", "REQUESTED", "CHANGE_SCHEDULE"
    );

    public CaregiverScheduleController(CaregiverScheduleService service) {
        this.service = service;
    }

    @PostMapping("/{idCaregiver}/schedules")
    public ResponseEntity<CaregiverSchedule> createSchedule(
            @PathVariable String idCaregiver,
            @RequestBody Map<String, String> body
    ) {
        try {
            LocalDate date = LocalDate.parse(body.get("date"));
            LocalTime startTime = LocalTime.parse(body.get("startTime"));
            LocalTime endTime = LocalTime.parse(body.get("endTime"));
            CaregiverSchedule schedule = service.createSchedule(idCaregiver, date, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{idCaregiver}/schedules/interval")
    public ResponseEntity<List<CaregiverSchedule>> createScheduleInterval(
            @PathVariable String idCaregiver,
            @RequestBody Map<String, String> body
    ) {
        try {
            LocalDate date = LocalDate.parse(body.get("date"));
            LocalTime startTime = LocalTime.parse(body.get("startTime"));
            LocalTime endTime = LocalTime.parse(body.get("endTime"));

            List<CaregiverSchedule> schedules = service.createScheduleInterval(idCaregiver, date, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{idCaregiver}/schedules")
    public ResponseEntity<List<CaregiverSchedule>> getScheduleByCaregiver(
            @PathVariable String idCaregiver,
            @RequestParam(required = false) String status
    ) {
        try {
            List<CaregiverSchedule> schedules;
            if (status != null && ALLOWED_STATUSES.contains(status.toUpperCase())) {
                schedules = service.findByIdCaregiverAndStatus(idCaregiver, status.toUpperCase());
            } else {
                schedules = service.findByIdCaregiver(idCaregiver);
            }
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/schedules/{id}")
    public ResponseEntity<CaregiverSchedule> findByScheduleId(@PathVariable String id) {
        try {
            CaregiverSchedule schedule = service.findById(id);
            if (schedule != null) {
                return ResponseEntity.ok(schedule);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/schedules/{id}/status")
    public ResponseEntity<CaregiverSchedule> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String status = body.get("statusCaregiver");
            if (status == null) {
                return ResponseEntity.badRequest().build();
            }

            boolean success;

            switch (status) {
                case "APPROVED":
                    success = service.approveSchedule(id);
                    if (success) {
                        return ResponseEntity.ok(service.findById(id));
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                case "REJECTED":
                    success = service.rejectSchedule(id);
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
                    success = service.changeSchedule(id, date, startTime, endTime, message);
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