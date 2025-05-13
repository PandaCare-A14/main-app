package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.service.CaregiverScheduleService;
import com.pandacare.mainapp.konsultasi_dokter.dto.CreateScheduleDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class CaregiverScheduleController {
    private final CaregiverScheduleService scheduleService;
    private static final Set<String> VALID_STATUSES = Arrays.stream(ScheduleStatus.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    public CaregiverScheduleController(CaregiverScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/{idCaregiver}/schedules")
    public ResponseEntity<?> createSchedule(
            @PathVariable String idCaregiver,
            @Valid @RequestBody CreateScheduleDTO dto
    ) {
        try {
            DayOfWeek day = DayOfWeek.valueOf(dto.getDay().toUpperCase());
            LocalTime startTime = LocalTime.parse(dto.getStartTime());
            LocalTime endTime = LocalTime.parse(dto.getEndTime());

            if (dto.getWeeks() != null && dto.getWeeks() > 0) {
                List<CaregiverSchedule> schedules = scheduleService.createRepeatedSchedules(
                        idCaregiver, day, startTime, endTime, dto.getWeeks());
                return ResponseEntity.status(HttpStatus.CREATED).body(schedules);
            } else {
                CaregiverSchedule schedule = scheduleService.createSchedule(idCaregiver, day, startTime, endTime);
                return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{idCaregiver}/schedules/interval")
    public ResponseEntity<List<CaregiverSchedule>> createScheduleInterval(
            @PathVariable String idCaregiver,
            @Valid @RequestBody CreateScheduleDTO dto
    ) {
        try {
            DayOfWeek day = DayOfWeek.valueOf(dto.getDay().toUpperCase());
            LocalTime startTime = LocalTime.parse(dto.getStartTime());
            LocalTime endTime = LocalTime.parse(dto.getEndTime());

            if (dto.getWeeks() != null && dto.getWeeks() > 0) {
                List<CaregiverSchedule> schedules = scheduleService.createRepeatedMultipleSchedules(
                        idCaregiver, day, startTime, endTime, dto.getWeeks());
                return ResponseEntity.status(HttpStatus.CREATED).body(schedules);
            } else {
                List<CaregiverSchedule> schedules = scheduleService.createMultipleSchedules(
                        idCaregiver, day, startTime, endTime);
                return ResponseEntity.status(HttpStatus.CREATED).body(schedules);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{idCaregiver}/schedules/{idSchedule}")
    public ResponseEntity<CaregiverSchedule> deleteSchedule(
            @PathVariable String idCaregiver,
            @PathVariable String idSchedule
    ) {
        try {
            scheduleService.getSchedulesByCaregiverAndIdSchedule(idCaregiver, idSchedule);
            CaregiverSchedule inactiveSchedule = scheduleService.deleteSchedule(idSchedule);
            return ResponseEntity.ok(inactiveSchedule);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{idCaregiver}/schedules")
    public ResponseEntity<List<CaregiverSchedule>> getScheduleByCaregiver(
            @PathVariable String idCaregiver,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String idSchedule
    ) {
        try {
            if (idSchedule != null) {
                return getScheduleById(idCaregiver, idSchedule);
            }

            if (status != null && isValidStatus(status)) {
                return getSchedulesByStatus(idCaregiver, status);
            }

            if (day != null) {
                return getSchedulesByDay(idCaregiver, day);
            }

            List<CaregiverSchedule> schedules = scheduleService.getSchedulesByCaregiver(idCaregiver);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<List<CaregiverSchedule>> getScheduleById(String idCaregiver, String idSchedule) {
        try {
            CaregiverSchedule schedule = scheduleService.getSchedulesByCaregiverAndIdSchedule(
                    idCaregiver, idSchedule);
            return ResponseEntity.ok(Collections.singletonList(schedule));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private ResponseEntity<List<CaregiverSchedule>> getSchedulesByStatus(String idCaregiver, String status) {
        ScheduleStatus statusEnum = ScheduleStatus.valueOf(status.toUpperCase());
        List<CaregiverSchedule> schedules = scheduleService.getSchedulesByCaregiverAndStatus(
                idCaregiver, statusEnum);
        return ResponseEntity.ok(schedules);
    }

    private ResponseEntity<List<CaregiverSchedule>> getSchedulesByDay(String idCaregiver, String day) {
        try {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
            List<CaregiverSchedule> schedules = scheduleService.getSchedulesByCaregiverAndDay(
                    idCaregiver, dayOfWeek);
            return ResponseEntity.ok(schedules);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isValidStatus(String status) {
        return VALID_STATUSES.contains(status.toUpperCase());
    }
}