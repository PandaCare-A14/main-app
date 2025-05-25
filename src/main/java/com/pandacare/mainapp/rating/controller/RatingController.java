package com.pandacare.mainapp.rating.controller;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for managing consultation ratings
 */
@RestController
@RequestMapping("/api")
public class RatingController {

    private static final Logger log = LoggerFactory.getLogger(RatingController.class);
    private final RatingService ratingService;
    private final ReservasiKonsultasiRepository reservasiKonsultasiRepository;
    private final CaregiverScheduleRepository caregiverScheduleRepository;

    @Autowired
    public RatingController(RatingService ratingService, ReservasiKonsultasiRepository reservasiKonsultasiRepository, CaregiverScheduleRepository caregiverScheduleRepository) {
        this.ratingService = ratingService;
        this.reservasiKonsultasiRepository = reservasiKonsultasiRepository;
        this.caregiverScheduleRepository = caregiverScheduleRepository;
    }

    /**
     * POST: Add a new rating for a consultation
     */
//
    @PostMapping("/consultations/{idJadwalKonsultasi}/ratings")
    public ResponseEntity<?> addRating(
            @PathVariable UUID idJadwalKonsultasi,
            @RequestBody @Valid RatingRequest ratingRequest){

        try {
            log.info("Adding rating for consultation: {}", idJadwalKonsultasi);

            // Fetch the consultation data
            ReservasiKonsultasi reservasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

            // Get the schedule from reservasi
            CaregiverSchedule schedule = reservasi.getIdSchedule();
            if (schedule == null) {
                throw new IllegalArgumentException("Schedule not found for this consultation");
            }

            // Get the consultation date and time from the schedule
            LocalDate consultationDate = schedule.getDate();
            LocalTime consultationEndTime = schedule.getEndTime();

            // Validate consultation timing
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();

            if (consultationDate != null) {
                // If date is available, use it for validation
                if (consultationDate.isAfter(today)) {
                    throw new IllegalArgumentException("Cannot rate future consultation");
                }

                // If consultation is today, check if the consultation time has ended
                if (consultationDate.isEqual(today) && consultationEndTime != null) {
                    if (consultationEndTime.isAfter(now)) {
                        throw new IllegalArgumentException("Cannot rate consultation that hasn't ended yet");
                    }
                }
            } else {
                // Fallback: if date is null, only check time (assuming today's consultation)
                if (consultationEndTime != null && consultationEndTime.isAfter(now)) {
                    throw new IllegalArgumentException("Tidak dapat menilai konsultasi yang belum berakhir");
                }
                // If both date and endTime are null, allow rating (legacy data support)
                log.warn("Consultation {} has no date/time information, allowing rating", idJadwalKonsultasi);
            }

            // Get the patient ID from the reservation
            UUID idPacilian = reservasi.getIdPacilian();

            // Set the consultation ID in the request
            ratingRequest.setIdJadwalKonsultasi(idJadwalKonsultasi);

            // Add rating using service (with Builder pattern internally)
            RatingResponse response = ratingService.addRating(idPacilian, ratingRequest);

            log.info("Successfully added rating for consultation: {}", idJadwalKonsultasi);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "data", Map.of("rating", response),
                    "message", "Rating berhasil ditambahkan"
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Bad request when adding rating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error when adding rating: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Terjadi kesalahan sistem: " + e.getMessage()
            ));
        }
    }
    /**
     * PUT: Update an existing rating for a consultation
     */
    @PutMapping("/consultations/{idJadwalKonsultasi}/ratings")
    public ResponseEntity<?> updateRating(
            @PathVariable UUID idJadwalKonsultasi,
            @RequestBody @Valid RatingRequest ratingRequest) {

        try {
            log.info("Updating rating for consultation: {}", idJadwalKonsultasi);

            ReservasiKonsultasi reservasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

            // Get the patient ID from the reservation
            UUID idPacilian = reservasi.getIdPacilian();

            ratingRequest.setIdJadwalKonsultasi(idJadwalKonsultasi);

            // Update rating using service (with Builder pattern internally)
            RatingResponse response = ratingService.updateRating(idPacilian, ratingRequest);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", Map.of("rating", response),
                    "message", "Rating berhasil diperbarui"
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Bad request when updating rating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error when updating rating: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Terjadi kesalahan sistem: " + e.getMessage()
            ));
        }
    }

    /**
     * DELETE: Delete a rating for a consultation
     */
    @DeleteMapping("/consultations/{idJadwalKonsultasi}/ratings")
    public ResponseEntity<?> deleteRating(
            @PathVariable UUID idJadwalKonsultasi) {

        try {
            log.info("Deleting rating for consultation: {}", idJadwalKonsultasi);

            ReservasiKonsultasi reservasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

            // Get the patient ID from the reservation
            UUID idPacilian = reservasi.getIdPacilian();

            ratingService.deleteRating(idPacilian, idJadwalKonsultasi);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Rating berhasil dihapus"
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Bad request when deleting rating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error when deleting rating: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Terjadi kesalahan sistem: " + e.getMessage()
            ));
        }
    }


    /**
     * GET: Get all ratings for a doctor
     */
    @GetMapping("/caregivers/{idCaregiver}/ratings")
    public ResponseEntity<?> getRatingsByDokter(@PathVariable UUID idCaregiver) {
        log.info("Fetching ratings for caregiver: {}", idCaregiver);

        RatingListResponse response = ratingService.getRatingsByDokter(idCaregiver);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }

    /**
     * GET: Get all ratings by a patient
     */
    @GetMapping("/pacillians/{idPacilian}/ratings")
    public ResponseEntity<?> getRatingsByPatient(@PathVariable UUID idPacilian) {
        log.info("Fetching ratings by pacilian: {}", idPacilian);

        RatingListResponse response = ratingService.getRatingsByPasien(idPacilian);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }
    /**
     * GET: Check if a patient has rated a specific consultation
     */
    @GetMapping("/consultations/{idJadwalKonsultasi}/rating/status")
    public ResponseEntity<?> hasRatedKonsultasi(@PathVariable UUID idJadwalKonsultasi) {
        try {
            log.info("Checking rating status for consultation: {}", idJadwalKonsultasi);

            ReservasiKonsultasi reservasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

            UUID idPacilian = reservasi.getIdPacilian();
            boolean hasRated = ratingService.hasRatedKonsultasi(idPacilian, idJadwalKonsultasi);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", Map.of("hasRated", hasRated)
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Bad request when checking rating status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * GET: Get rating details for a specific consultation
     */
    @GetMapping("/consultations/{idJadwalKonsultasi}/ratings")
    public ResponseEntity<?> getRatingByKonsultasi(@PathVariable UUID idJadwalKonsultasi) {
        try {
            log.info("Fetching rating for consultation: {}", idJadwalKonsultasi);

            ReservasiKonsultasi reservasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

            // Get the patient ID from the reservation
            UUID idPacilian = reservasi.getIdPacilian();

            RatingResponse response = ratingService.getRatingByKonsultasi(idPacilian, idJadwalKonsultasi);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", Map.of("rating", response)
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Rating not found for consultation: {}", idJadwalKonsultasi);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}