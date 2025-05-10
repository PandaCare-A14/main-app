package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.service.CaregiverScheduleService;
import com.pandacare.mainapp.konsultasi_dokter.dto.CreateScheduleDTO;
import com.pandacare.mainapp.konsultasi_dokter.dto.UpdateScheduleStatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import jakarta.validation.Valid;

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
            @RequestBody @Valid CreateScheduleDTO dto
    ) {
        try {
            DayOfWeek day = DayOfWeek.valueOf(dto.getDay().toUpperCase());
            LocalTime startTime = LocalTime.parse(dto.getStartTime());
            LocalTime endTime = LocalTime.parse(dto.getEndTime());

            CaregiverSchedule schedule = service.createSchedule(idCaregiver, day, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{idCaregiver}/schedules/interval")
    public ResponseEntity<List<CaregiverSchedule>> createScheduleInterval(
            @PathVariable String idCaregiver,
            @RequestBody @Valid CreateScheduleDTO dto
    ) {
        try {
            DayOfWeek day = DayOfWeek.valueOf(dto.getDay().toUpperCase());
            LocalTime startTime = LocalTime.parse(dto.getStartTime());
            LocalTime endTime = LocalTime.parse(dto.getEndTime());

            List<CaregiverSchedule> schedules = service.createScheduleInterval(idCaregiver, day, startTime, endTime);
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
            @RequestBody @Valid UpdateScheduleStatusDTO dto
    ) {
        try {
            String status = dto.getStatusCaregiver();
            boolean success;

            switch (status) {
                case "APPROVED":
                    success = service.approveSchedule(id);
                    break;
                case "REJECTED":
                    success = service.rejectSchedule(id);
                    break;
                case "CHANGE_SCHEDULE":
                    DayOfWeek day = DayOfWeek.valueOf(dto.getDay().toUpperCase());
                    LocalTime startTime = LocalTime.parse(dto.getStartTime());
                    LocalTime endTime = LocalTime.parse(dto.getEndTime());
                    success = service.changeSchedule(id, day, startTime, endTime, dto.getMessage());
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }

            if (success) {
                return ResponseEntity.ok(service.findById(id));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}