package com.pandacare.mainapp.rating.controller;

import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.service.AsyncRatingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Async REST Controller for ratings with consistent routing
 */
@RestController
@RequestMapping("/api/async")
public class AsyncRatingController {

    private static final Logger log = LoggerFactory.getLogger(AsyncRatingController.class);
    private final AsyncRatingService asyncRatingService;

    @Autowired
    public AsyncRatingController(AsyncRatingService asyncRatingService) {
        this.asyncRatingService = asyncRatingService;
    }

    /**
     * POST: Add a new rating for a consultation (async)
     */
    @PostMapping("/consultations/{idJadwalKonsultasi}/ratings")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> addRating(
            @PathVariable UUID idJadwalKonsultasi,
            @RequestBody @Valid RatingRequest ratingRequest) {

        log.info("Async request add rating for consultation: {}", idJadwalKonsultasi);

        return asyncRatingService.addRatingAsync(idJadwalKonsultasi, ratingRequest)
                .thenApply(response -> {
                    log.info("Rating added successfully async for consultation: {}", idJadwalKonsultasi);
                    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                            "status", "success",
                            "data", Map.of("rating", response),
                            "message", "Rating berhasil ditambahkan"
                    ));
                })
                .exceptionally(throwable -> {
                    log.error("Failed to add rating async for consultation {}: {}", idJadwalKonsultasi, throwable.getMessage());
                    if (throwable.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                                "status", "error",
                                "message", throwable.getCause().getMessage()
                        ));
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                            "status", "error",
                            "message", "Terjadi kesalahan sistem: " + throwable.getMessage()
                    ));
                });
    }

    /**
     * PUT: Update an existing rating for a consultation (async)
     */
    @PutMapping("/consultations/{idJadwalKonsultasi}/ratings")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> updateRating(
            @PathVariable UUID idJadwalKonsultasi,
            @RequestBody @Valid RatingRequest ratingRequest) {

        log.info("Async request update rating for consultation: {}", idJadwalKonsultasi);

        return asyncRatingService.updateRatingAsync(idJadwalKonsultasi, ratingRequest)
                .thenApply(response -> {
                    log.info("Rating updated successfully async for consultation: {}", idJadwalKonsultasi);
                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "data", Map.of("rating", response),
                            "message", "Rating berhasil diperbarui"
                    ));
                })
                .exceptionally(throwable -> {
                    log.error("Failed to update rating async for consultation {}: {}", idJadwalKonsultasi, throwable.getMessage());
                    if (throwable.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                                "status", "error",
                                "message", throwable.getCause().getMessage()
                        ));
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                            "status", "error",
                            "message", "Terjadi kesalahan sistem: " + throwable.getMessage()
                    ));
                });
    }

    /**
     * DELETE: Delete a rating for a consultation (async)
     */
    @DeleteMapping("/consultations/{idJadwalKonsultasi}/ratings")
    public CompletableFuture<ResponseEntity<Map<String, String>>> deleteRating(@PathVariable UUID idJadwalKonsultasi) {

        log.info("Async request delete rating for consultation: {}", idJadwalKonsultasi);

        return asyncRatingService.deleteRatingAsync(idJadwalKonsultasi)
                .thenApply(result -> {
                    log.info("Rating deleted successfully async for consultation: {}", idJadwalKonsultasi);
                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "message", "Rating berhasil dihapus"
                    ));
                })
                .exceptionally(throwable -> {
                    log.error("Failed to delete rating async for consultation {}: {}", idJadwalKonsultasi, throwable.getMessage());
                    if (throwable.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                                "status", "error",
                                "message", throwable.getCause().getMessage()
                        ));
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                            "status", "error",
                            "message", "Terjadi kesalahan sistem: " + throwable.getMessage()
                    ));
                });
    }

    /**
     * GET: Get rating details for a specific consultation (async)
     */
    @GetMapping("/consultations/{idJadwalKonsultasi}/ratings")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getRatingByKonsultasi(@PathVariable UUID idJadwalKonsultasi) {

        log.info("Async request get rating for consultation: {}", idJadwalKonsultasi);

        return asyncRatingService.getRatingByKonsultasiAsync(idJadwalKonsultasi)
                .thenApply(response -> {
                    log.info("Rating fetched successfully async for consultation: {}", idJadwalKonsultasi);
                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "data", Map.of("rating", response)
                    ));
                })
                .exceptionally(throwable -> {
                    log.error("Failed to fetch rating async for consultation {}: {}", idJadwalKonsultasi, throwable.getMessage());
                    if (throwable.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                                "status", "error",
                                "message", throwable.getCause().getMessage()
                        ));
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                            "status", "error",
                            "message", "Terjadi kesalahan sistem: " + throwable.getMessage()
                    ));
                });
    }

    /**
     * GET: Check if a consultation has been rated (async)
     */
    @GetMapping("/consultations/{idJadwalKonsultasi}/rating/status")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> hasRatedKonsultasi(@PathVariable UUID idJadwalKonsultasi) {

        log.info("Async request check rating status for consultation: {}", idJadwalKonsultasi);

        return asyncRatingService.hasRatedKonsultasiAsync(idJadwalKonsultasi)
                .thenApply(hasRated -> {
                    log.info("Rating status checked successfully async for consultation: {}", idJadwalKonsultasi);
                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "data", Map.of("hasRated", hasRated)
                    ));
                })
                .exceptionally(throwable -> {
                    log.error("Failed to check rating status async for consultation {}: {}", idJadwalKonsultasi, throwable.getMessage());
                    if (throwable.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                                "status", "error",
                                "message", throwable.getCause().getMessage()
                        ));
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                            "status", "error",
                            "message", "Terjadi kesalahan sistem: " + throwable.getMessage()
                    ));
                });
    }

    /**
     * GET: Get all ratings for a caregiver (async) - kept from original
     */
    @GetMapping("/caregivers/{idCaregiver}/ratings")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getRatingsByDokter(@PathVariable UUID idCaregiver) {
        log.info("Async request get ratings for caregiver: {}", idCaregiver);

        return asyncRatingService.getRatingsByDokterAsync(idCaregiver)
                .thenApply(response -> {
                    log.info("Ratings fetched successfully async for caregiver: {}", idCaregiver);
                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "data", response
                    ));
                })
                .exceptionally(throwable -> {
                    log.error("Failed to fetch ratings async for caregiver {}: {}", idCaregiver, throwable.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                            "status", "error",
                            "message", "Terjadi kesalahan sistem: " + throwable.getMessage()
                    ));
                });
    }

    /**
     * GET: Get all ratings by a patient (async) - kept from original
     */
    @GetMapping("/pacillians/{idPacilian}/ratings")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getRatingsByPatient(@PathVariable UUID idPacilian) {
        log.info("Async request get ratings for pacilian: {}", idPacilian);

        return asyncRatingService.getRatingsByPasienAsync(idPacilian)
                .thenApply(response -> {
                    log.info("Ratings fetched successfully async for pacilian: {}", idPacilian);
                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "data", response
                    ));
                })
                .exceptionally(throwable -> {
                    log.error("Failed to fetch ratings async for pacilian {}: {}", idPacilian, throwable.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                            "status", "error",
                            "message", "Terjadi kesalahan sistem: " + throwable.getMessage()
                    ));
                });
    }
}