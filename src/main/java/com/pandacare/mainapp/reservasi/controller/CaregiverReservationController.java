package com.pandacare.mainapp.reservasi.controller;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/doctors")
public class CaregiverReservationController {
    private final CaregiverReservationService reservationService;
    @Autowired
    private DoctorProfileRepository doctorProfileRepository;
    private static final Set<String> ALLOWED_STATUSES = Arrays.stream(StatusReservasiKonsultasi.values())
            .map(Enum::name).collect(Collectors.toSet());

    @Autowired
    public CaregiverReservationController(CaregiverReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{caregiverId}/profile")
    public ResponseEntity<?> getCaregiverProfile(@PathVariable String caregiverId) {
        try {
            UUID caregiverUUID = UUID.fromString(caregiverId);

            Optional<Caregiver> caregiverOpt = doctorProfileRepository.findById(caregiverUUID);

            if (caregiverOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Caregiver caregiver = caregiverOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("caregiverId", caregiverId);
            response.put("name", caregiver.getName());
            response.put("email", caregiver.getEmail());
            response.put("speciality", caregiver.getSpeciality());
            response.put("phoneNumber", caregiver.getPhoneNumber());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch profile"));
        }
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