package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.service.CaregiverScheduleService;
import com.pandacare.mainapp.konsultasi_dokter.dto.CreateScheduleDTO;
import com.pandacare.mainapp.konsultasi_dokter.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/caregivers")
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
    public ResponseEntity<ApiResponse<?>> createSchedule(
            @PathVariable UUID idCaregiver,
            @Valid @RequestBody CreateScheduleDTO dto
    ) {
        try {
            DayOfWeek day = DayOfWeek.valueOf(dto.getDay().toUpperCase());
            LocalTime startTime = LocalTime.parse(dto.getStartTime());
            LocalTime endTime = LocalTime.parse(dto.getEndTime());

            if (dto.getWeeks() != null && dto.getWeeks() > 0) {
                List<CaregiverSchedule> schedules = scheduleService.createRepeatedSchedules(
                        idCaregiver, day, startTime, endTime, dto.getWeeks());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.created("Schedules created successfully", schedules));
            } else {
                CaregiverSchedule schedule = scheduleService.createSchedule(idCaregiver, day, startTime, endTime);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.created("Schedule created successfully", schedule));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("An internal error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/{idCaregiver}/schedules/interval")
    public ResponseEntity<ApiResponse<?>> createScheduleInterval(
            @PathVariable UUID idCaregiver,
            @Valid @RequestBody CreateScheduleDTO dto
    ) {
        try {
            DayOfWeek day = DayOfWeek.valueOf(dto.getDay().toUpperCase());
            LocalTime startTime = LocalTime.parse(dto.getStartTime());
            LocalTime endTime = LocalTime.parse(dto.getEndTime());

            if (dto.getWeeks() != null && dto.getWeeks() > 0) {
                List<CaregiverSchedule> schedules = scheduleService.createRepeatedMultipleSchedules(
                        idCaregiver, day, startTime, endTime, dto.getWeeks());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.created("Multiple schedules created successfully", schedules));
            } else {
                List<CaregiverSchedule> schedules = scheduleService.createMultipleSchedules(
                        idCaregiver, day, startTime, endTime);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.created("Interval schedules created successfully", schedules));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("An internal error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{idCaregiver}/schedules/{idSchedule}")
    public ResponseEntity<ApiResponse<CaregiverSchedule>> deleteSchedule(
            @PathVariable UUID idCaregiver,
            @PathVariable UUID idSchedule
    ) {
        try {
            scheduleService.getSchedulesByCaregiverAndIdSchedule(idCaregiver, idSchedule);
            CaregiverSchedule inactiveSchedule = scheduleService.deleteSchedule(idSchedule);
            return ResponseEntity.ok(
                    ApiResponse.success("Schedule deleted successfully", inactiveSchedule));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Schedule not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.conflict("Cannot delete schedule: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("An internal error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/{idCaregiver}/schedules")
    public ResponseEntity<ApiResponse<?>> getScheduleByCaregiver(
            @PathVariable UUID idCaregiver,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) UUID idSchedule
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
            return ResponseEntity.ok(ApiResponse.success("Schedules retrieved successfully", schedules));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("An internal error occurred: " + e.getMessage()));
        }
    }

    private ResponseEntity<ApiResponse<?>> getScheduleById(UUID idCaregiver, UUID idSchedule) {
        try {
            CaregiverSchedule schedule = scheduleService.getSchedulesByCaregiverAndIdSchedule(idCaregiver, idSchedule);
            return ResponseEntity.ok(
                    ApiResponse.success("Schedule retrieved successfully", schedule));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Schedule not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("Invalid input: " + e.getMessage()));
        }
    }

    private ResponseEntity<ApiResponse<?>> getSchedulesByStatus(UUID idCaregiver, String status) {
        try {
            ScheduleStatus statusEnum = ScheduleStatus.valueOf(status.toUpperCase());
            List<CaregiverSchedule> schedules = scheduleService.getSchedulesByCaregiverAndStatus(idCaregiver, statusEnum);
            return ResponseEntity.ok(
                    ApiResponse.success("Schedules with status " + status + " retrieved successfully", schedules));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("Invalid status: " + e.getMessage()));
        }
    }

    private ResponseEntity<ApiResponse<?>> getSchedulesByDay(UUID idCaregiver, String day) {
        try {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
            List<CaregiverSchedule> schedules = scheduleService.getSchedulesByCaregiverAndDay(idCaregiver, dayOfWeek);
            return ResponseEntity.ok(ApiResponse.success("Schedules for " + day + " retrieved successfully", schedules));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("Invalid day: " + e.getMessage()));
        }
    }

    @GetMapping("/{idCaregiver}/schedules/available")
    public ResponseEntity<ApiResponse<?>> getAvailableSchedules(
            @PathVariable UUID idCaregiver,
            @RequestParam(required = false) String date
    ) {
        try {
            List<CaregiverSchedule> schedules = scheduleService.getSchedulesByCaregiver(idCaregiver)
                    .stream()
                    .filter(s -> s.getStatus() == ScheduleStatus.AVAILABLE)
                    .filter(s -> date == null || date.equals(s.getDate().toString()))
                    .sorted(Comparator.comparing(CaregiverSchedule::getDate)
                            .thenComparing(CaregiverSchedule::getStartTime))
                    .toList();

            return ResponseEntity.ok(ApiResponse.success("Available schedules retrieved", schedules));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("Failed to fetch available schedules"));
        }
    }

    private boolean isValidStatus(String status) {
        return VALID_STATUSES.contains(status.toUpperCase());
    }
}