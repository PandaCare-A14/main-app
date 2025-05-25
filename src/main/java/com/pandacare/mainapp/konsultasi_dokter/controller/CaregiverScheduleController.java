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
            @Valid @RequestBody CreateScheduleDTO dto) {

        return handleApiRequest(() -> {
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
        });
    }

    @PostMapping("/{idCaregiver}/schedules/interval")
    public ResponseEntity<ApiResponse<?>> createScheduleInterval(
            @PathVariable UUID idCaregiver,
            @Valid @RequestBody CreateScheduleDTO dto) {

        return handleApiRequest(() -> {
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
        });
    }

    @DeleteMapping("/{idCaregiver}/schedules/{idSchedule}")
    public ResponseEntity<ApiResponse<?>> deleteSchedule(
            @PathVariable UUID idCaregiver,
            @PathVariable UUID idSchedule) {

        return handleApiRequest(() -> {
            scheduleService.getSchedulesByCaregiverAndIdSchedule(idCaregiver, idSchedule);
            CaregiverSchedule inactiveSchedule = scheduleService.deleteSchedule(idSchedule);
            return ResponseEntity.ok(
                    ApiResponse.success("Schedule deleted successfully", inactiveSchedule));
        });
    }

    @GetMapping("/{idCaregiver}/schedules")
    public ResponseEntity<ApiResponse<?>> getScheduleByCaregiver(
            @PathVariable UUID idCaregiver,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) UUID idSchedule) {

        return handleApiRequest(() -> {
            if (idSchedule != null) {
                CaregiverSchedule schedule = scheduleService.getSchedulesByCaregiverAndIdSchedule(idCaregiver, idSchedule);
                return ResponseEntity.ok(ApiResponse.success("Schedule retrieved successfully", schedule));
            }

            List<CaregiverSchedule> schedules = getSchedulesWithFilters(idCaregiver, status, day);
            String message = buildSuccessMessage(status, day);

            return ResponseEntity.ok(ApiResponse.success(message, schedules));
        });
    }

    private List<CaregiverSchedule> getSchedulesWithFilters(UUID idCaregiver, String status, String day) {
        if (status != null && day != null) {
            ScheduleStatus statusEnum = ScheduleStatus.valueOf(status.toUpperCase());
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
            return scheduleService.getSchedulesByCaregiverStatusAndDay(idCaregiver, statusEnum, dayOfWeek);

        } else if (status != null && isValidStatus(status)) {
            ScheduleStatus statusEnum = ScheduleStatus.valueOf(status.toUpperCase());
            return scheduleService.getSchedulesByCaregiverAndStatus(idCaregiver, statusEnum);

        } else if (day != null) {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
            return scheduleService.getSchedulesByCaregiverAndDay(idCaregiver, dayOfWeek);

        } else {
            return scheduleService.getSchedulesByCaregiver(idCaregiver);
        }
    }

    private String buildSuccessMessage(String status, String day) {
        if (status != null && day != null) {
            return "Schedules with status " + status + " for " + day + " retrieved successfully";
        } else if (status != null) {
            return "Schedules with status " + status + " retrieved successfully";
        } else if (day != null) {
            return "Schedules for " + day + " retrieved successfully";
        } else {
            return "Schedules retrieved successfully";
        }
    }

    private boolean isValidStatus(String status) {
        return VALID_STATUSES.contains(status.toUpperCase());
    }

    private ResponseEntity<ApiResponse<?>> handleApiRequest(ApiRequestHandler handler) {
        try {
            return handler.handle();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Schedule not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.conflict("Cannot perform operation: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("Invalid input: " + e.getMessage()));
        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("Invalid time format: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("An internal error occurred"));
        }
    }

    @FunctionalInterface
    private interface ApiRequestHandler {
        ResponseEntity<ApiResponse<?>> handle() throws Exception;
    }
}