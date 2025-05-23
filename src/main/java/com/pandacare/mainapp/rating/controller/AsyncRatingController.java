package com.pandacare.mainapp.rating.controller;

import com.pandacare.mainapp.rating.dto.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.AsyncRatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

/**
 * Simple async REST Controller for ratings
 */
@RestController
@RequestMapping("/api/ratings/async")
public class AsyncRatingController {

    private static final Logger log = LoggerFactory.getLogger(AsyncRatingController.class);
    private final AsyncRatingService asyncRatingService;

    @Autowired
    public AsyncRatingController(AsyncRatingService asyncRatingService) {
        this.asyncRatingService = asyncRatingService;
    }

    @PostMapping("/patients/{idPasien}")
    public CompletableFuture<ResponseEntity<RatingResponse>> addRating(
            @PathVariable String idPasien,
            @Valid @RequestBody RatingRequest ratingRequest) {

        log.info("Request add rating async for patient: {}", idPasien);

        return asyncRatingService.addRatingAsync(idPasien, ratingRequest)
                .thenApply(response -> {
                    log.info("Rating added successfully async for patient: {}", idPasien);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .exceptionally(throwable -> {
                    log.error("Failed to add rating async for patient {}: {}", idPasien, throwable.getMessage());
                    return ResponseEntity.badRequest().build();
                });
    }

    @PutMapping("/patients/{idPasien}")
    public CompletableFuture<ResponseEntity<RatingResponse>> updateRating(
            @PathVariable String idPasien,
            @Valid @RequestBody RatingRequest ratingRequest) {

        log.info("Request update rating async for patient: {}", idPasien);

        return asyncRatingService.updateRatingAsync(idPasien, ratingRequest)
                .thenApply(response -> {
                    log.info("Rating updated successfully async for patient: {}", idPasien);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    log.error("Failed to update rating async for patient {}: {}", idPasien, throwable.getMessage());
                    return ResponseEntity.badRequest().build();
                });
    }

    @DeleteMapping("/patients/{idPasien}/consultations/{idJadwalKonsultasi}")
    public CompletableFuture<ResponseEntity<Void>> deleteRating(
            @PathVariable String idPasien,
            @PathVariable String idJadwalKonsultasi) {

        log.info("Request delete rating async for patient: {} consultation: {}", idPasien, idJadwalKonsultasi);

        return asyncRatingService.deleteRatingAsync(idPasien, idJadwalKonsultasi)
                .thenApply(result -> {
                    log.info("Rating deleted successfully async for patient: {}", idPasien);
                    return ResponseEntity.noContent().<Void>build();
                })
                .exceptionally(throwable -> {
                    log.error("Failed to delete rating async for patient {}: {}", idPasien, throwable.getMessage());
                    return ResponseEntity.badRequest().build();
                });
    }

    @GetMapping("/doctors/{idDokter}")
    public CompletableFuture<ResponseEntity<RatingListResponse>> getRatingsByDokter(@PathVariable String idDokter) {
        log.info("Request get ratings async for doctor: {}", idDokter);

        return asyncRatingService.getRatingsByDokterAsync(idDokter)
                .thenApply(response -> {
                    log.info("Ratings fetched successfully async for doctor: {}", idDokter);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    log.error("Failed to fetch ratings async for doctor {}: {}", idDokter, throwable.getMessage());
                    return ResponseEntity.internalServerError().build();
                });
    }

    @GetMapping("/patients/{idPasien}")
    public CompletableFuture<ResponseEntity<RatingListResponse>> getRatingsByPasien(@PathVariable String idPasien) {
        log.info("Request get ratings async for patient: {}", idPasien);

        return asyncRatingService.getRatingsByPasienAsync(idPasien)
                .thenApply(response -> {
                    log.info("Ratings fetched successfully async for patient: {}", idPasien);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    log.error("Failed to fetch ratings async for patient {}: {}", idPasien, throwable.getMessage());
                    return ResponseEntity.internalServerError().build();
                });
    }

    @GetMapping("/patients/{idPasien}/consultations/{idJadwalKonsultasi}")
    public CompletableFuture<ResponseEntity<RatingResponse>> getRatingByKonsultasi(
            @PathVariable String idPasien,
            @PathVariable String idJadwalKonsultasi) {

        log.info("Request get rating async for patient: {} consultation: {}", idPasien, idJadwalKonsultasi);

        return asyncRatingService.getRatingByKonsultasiAsync(idPasien, idJadwalKonsultasi)
                .thenApply(response -> {
                    log.info("Rating fetched successfully async for consultation: {}", idJadwalKonsultasi);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    log.error("Failed to fetch rating async for consultation {}: {}", idJadwalKonsultasi, throwable.getMessage());
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/patients/{idPasien}/consultations/{idJadwalKonsultasi}/check")
    public CompletableFuture<ResponseEntity<Boolean>> hasRatedKonsultasi(
            @PathVariable String idPasien,
            @PathVariable String idJadwalKonsultasi) {

        log.info("Request check rating async for patient: {} consultation: {}", idPasien, idJadwalKonsultasi);

        return asyncRatingService.hasRatedKonsultasiAsync(idPasien, idJadwalKonsultasi)
                .thenApply(hasRated -> {
                    log.info("Rating check completed async for consultation: {}", idJadwalKonsultasi);
                    return ResponseEntity.ok(hasRated);
                })
                .exceptionally(throwable -> {
                    log.error("Failed to check rating async for consultation {}: {}", idJadwalKonsultasi, throwable.getMessage());
                    return ResponseEntity.internalServerError().build();
                });
    }
}