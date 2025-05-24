package com.pandacare.mainapp.reservasi.controller;

import com.pandacare.mainapp.reservasi.dto.UpdateStatusDTO;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.caregiver.CaregiverReservationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/caregivers")
public class CaregiverReservationController {
    private final CaregiverReservationService reservationService;
    private static final Set<String> ALLOWED_STATUSES = Arrays.stream(StatusReservasiKonsultasi.values())
            .map(Enum::name).collect(Collectors.toSet());

    @Autowired
    public CaregiverReservationController(CaregiverReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{caregiverId}/reservations")
    public ResponseEntity<List<ReservasiKonsultasi>> getReservationsByCaregiver(
            @PathVariable UUID caregiverId,
            @RequestParam(required = false) String status) {
        try {
            List<ReservasiKonsultasi> reservations;
            if (status != null && ALLOWED_STATUSES.contains(status.toUpperCase())) {
                StatusReservasiKonsultasi statusEnum = StatusReservasiKonsultasi.valueOf(status.toUpperCase());
                if (statusEnum == StatusReservasiKonsultasi.WAITING) {
                    reservations = reservationService.getWaitingReservations(caregiverId);
                } else {
                    reservations = reservationService.getReservationsForCaregiver(caregiverId).stream()
                            .filter(r -> r.getStatusReservasi() == statusEnum)
                            .collect(Collectors.toList());
                }
            } else {
                reservations = reservationService.getReservationsForCaregiver(caregiverId);
            }
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/reservations/{reservationId}/status")
    public ResponseEntity<ReservasiKonsultasi> updateStatus(
            @PathVariable UUID reservationId,
            @RequestBody @Valid UpdateStatusDTO dto) {
        try {
            if (dto.getStatus() == StatusReservasiKonsultasi.ON_RESCHEDULE && dto.getNewScheduleId() == null) {
                return ResponseEntity.badRequest().build();
            }
            ReservasiKonsultasi reservation;
            switch (dto.getStatus()) {
                case APPROVED:
                    reservation = reservationService.approveReservation(reservationId);
                    break;
                case REJECTED:
                    reservation = reservationService.rejectReservation(reservationId);
                    break;
                case ON_RESCHEDULE:
                    reservation = reservationService.changeSchedule(reservationId, dto.getNewScheduleId());
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity<ReservasiKonsultasi> handleException(Exception e) {
        if (e instanceof EntityNotFoundException) {
            return ResponseEntity.notFound().build();
        } else if (e instanceof IllegalStateException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else if (e instanceof IllegalArgumentException) {
            return ResponseEntity.badRequest().build();
        } else {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservasiKonsultasi> getReservationById(@PathVariable UUID reservationId) {
        try {
            ReservasiKonsultasi reservation = reservationService.getReservationOrThrow(reservationId);
            return ResponseEntity.ok(reservation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}