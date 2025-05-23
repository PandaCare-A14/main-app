package com.pandacare.mainapp.rating.service;

import com.pandacare.mainapp.rating.dto.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Simple async wrapper for RatingService
 */
@Service
public class AsyncRatingService {

    private static final Logger log = LoggerFactory.getLogger(AsyncRatingService.class);
    private final RatingService ratingService;

    @Autowired
    public AsyncRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<RatingResponse> addRatingAsync(String idPasien, RatingRequest ratingRequest) {
        log.info("Async add rating for patient: {}", idPasien);
        try {
            RatingResponse response = ratingService.addRating(idPasien, ratingRequest);
            log.info("Async add rating completed for patient: {}", idPasien);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            log.error("Async add rating failed for patient {}: {}", idPasien, e.getMessage());
            CompletableFuture<RatingResponse> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<RatingResponse> updateRatingAsync(String idPasien, RatingRequest ratingRequest) {
        log.info("Async update rating for patient: {}", idPasien);
        try {
            RatingResponse response = ratingService.updateRating(idPasien, ratingRequest);
            log.info("Async update rating completed for patient: {}", idPasien);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            log.error("Async update rating failed for patient {}: {}", idPasien, e.getMessage());
            CompletableFuture<RatingResponse> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<Void> deleteRatingAsync(String idPasien, String idJadwalKonsultasi) {
        log.info("Async delete rating for patient: {} consultation: {}", idPasien, idJadwalKonsultasi);
        try {
            ratingService.deleteRating(idPasien, idJadwalKonsultasi);
            log.info("Async delete rating completed for patient: {}", idPasien);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Async delete rating failed for patient {}: {}", idPasien, e.getMessage());
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<RatingListResponse> getRatingsByDokterAsync(String idDokter) {
        log.info("Async get ratings for doctor: {}", idDokter);
        try {
            RatingListResponse response = ratingService.getRatingsByDokter(idDokter);
            log.info("Async get ratings completed for doctor: {}", idDokter);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            log.error("Async get ratings failed for doctor {}: {}", idDokter, e.getMessage());
            CompletableFuture<RatingListResponse> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<RatingListResponse> getRatingsByPasienAsync(String idPasien) {
        log.info("Async get ratings for patient: {}", idPasien);
        try {
            RatingListResponse response = ratingService.getRatingsByPasien(idPasien);
            log.info("Async get ratings completed for patient: {}", idPasien);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            log.error("Async get ratings failed for patient {}: {}", idPasien, e.getMessage());
            CompletableFuture<RatingListResponse> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<RatingResponse> getRatingByKonsultasiAsync(String idPasien, String idJadwalKonsultasi) {
        log.info("Async get rating for patient: {} consultation: {}", idPasien, idJadwalKonsultasi);
        try {
            RatingResponse response = ratingService.getRatingByKonsultasi(idPasien, idJadwalKonsultasi);
            log.info("Async get rating completed for consultation: {}", idJadwalKonsultasi);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            log.error("Async get rating failed for consultation {}: {}", idJadwalKonsultasi, e.getMessage());
            CompletableFuture<RatingResponse> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<Boolean> hasRatedKonsultasiAsync(String idPasien, String idJadwalKonsultasi) {
        log.info("Async check rating exists for patient: {} consultation: {}", idPasien, idJadwalKonsultasi);
        try {
            Boolean result = ratingService.hasRatedKonsultasi(idPasien, idJadwalKonsultasi);
            log.info("Async check rating completed for consultation: {}", idJadwalKonsultasi);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async check rating failed for consultation {}: {}", idJadwalKonsultasi, e.getMessage());
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
}