package com.pandacare.mainapp.rating.controller;

import com.pandacare.mainapp.rating.dto.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for managing consultation ratings
 */
@RestController
@RequestMapping("/api")
public class RatingController {

    private static final Logger log = LoggerFactory.getLogger(RatingController.class);
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * POST: Add a new rating for a consultation
     */
    @PostMapping("/consultations/{idJadwalKonsultasi}/ratings")
    public ResponseEntity<?> addRating(
            @PathVariable String idJadwalKonsultasi,
            @RequestBody @Valid RatingRequest ratingRequest,
            @RequestHeader("X-User-ID") String idPasien) {

        try {
            log.info("Adding rating for consultation: {}, by patient: {}", idJadwalKonsultasi, idPasien);

            // Set the consultation ID in the request
            ratingRequest.setIdJadwalKonsultasi(idJadwalKonsultasi);

            // Add rating using service (with Builder pattern internally)
            RatingResponse response = ratingService.addRating(idPasien, ratingRequest);

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
            @PathVariable String idJadwalKonsultasi,
            @RequestBody @Valid RatingRequest ratingRequest,
            @RequestHeader("X-User-ID") String idPasien) {

        try {
            log.info("Updating rating for consultation: {}, by patient: {}", idJadwalKonsultasi, idPasien);

            // Set the consultation ID in the request
            ratingRequest.setIdJadwalKonsultasi(idJadwalKonsultasi);

            // Update rating using service (with Builder pattern internally)
            RatingResponse response = ratingService.updateRating(idPasien, ratingRequest);

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
            @PathVariable String idJadwalKonsultasi,
            @RequestHeader("X-User-ID") String idPasien) {

        try {
            log.info("Deleting rating for consultation: {}, by patient: {}", idJadwalKonsultasi, idPasien);

            ratingService.deleteRating(idPasien, idJadwalKonsultasi);

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
    @GetMapping("/doctors/{idDokter}/ratings")
    public ResponseEntity<?> getRatingsByDokter(@PathVariable String idDokter) {
        log.info("Fetching ratings for doctor: {}", idDokter);

        RatingListResponse response = ratingService.getRatingsByDokter(idDokter);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }

    /**
     * GET: Get all ratings by a patient
     */
    @GetMapping("/patients/{idPasien}/ratings")
    public ResponseEntity<?> getRatingsByPasien(
            @PathVariable String idPasien,
            @RequestHeader("X-User-ID") String requesterId) {

        // Security check: Only allow patients to view their own ratings
        if (!idPasien.equals(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "error",
                    "message", "Anda tidak memiliki izin untuk melihat rating pasien lain"
            ));
        }

        log.info("Fetching ratings by patient: {}", idPasien);
        RatingListResponse response = ratingService.getRatingsByPasien(idPasien);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }

    /**
     * GET: Check if a patient has rated a specific consultation
     */
    @GetMapping("/consultations/{idJadwalKonsultasi}/rating/status")
    public ResponseEntity<?> hasRatedKonsultasi(
            @PathVariable String idJadwalKonsultasi,
            @RequestHeader("X-User-ID") String idPasien) {

        log.info("Checking rating status for consultation: {} by patient: {}", idJadwalKonsultasi, idPasien);

        boolean hasRated = ratingService.hasRatedKonsultasi(idPasien, idJadwalKonsultasi);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", Map.of("hasRated", hasRated)
        ));
    }

    /**
     * GET: Get rating details for a specific consultation
     */
    @GetMapping("/consultations/{idJadwalKonsultasi}/ratings")
    public ResponseEntity<?> getRatingByKonsultasi(
            @PathVariable String idJadwalKonsultasi,
            @RequestHeader("X-User-ID") String idPasien) {

        try {
            log.info("Fetching rating for consultation: {} by patient: {}", idJadwalKonsultasi, idPasien);

            RatingResponse response = ratingService.getRatingByKonsultasi(idPasien, idJadwalKonsultasi);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", Map.of("rating", response)
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Rating not found for consultation: {} by patient: {}", idJadwalKonsultasi, idPasien);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}