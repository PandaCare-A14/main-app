package com.pandacare.mainapp.rating.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncRatingService {

    private static final Logger log = LoggerFactory.getLogger(AsyncRatingService.class);
    private final RatingService ratingService;
    private final ReservasiKonsultasiRepository reservasiKonsultasiRepository;
    private final CaregiverScheduleRepository caregiverScheduleRepository;

    @Autowired
    public AsyncRatingService(RatingService ratingService,
                              ReservasiKonsultasiRepository reservasiKonsultasiRepository,
                              CaregiverScheduleRepository caregiverScheduleRepository) {
        this.ratingService = ratingService;
        this.reservasiKonsultasiRepository = reservasiKonsultasiRepository;
        this.caregiverScheduleRepository = caregiverScheduleRepository;
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<RatingResponse> addRatingAsync(UUID idJadwalKonsultasi, RatingRequest ratingRequest) {
        log.info("Async add rating for consultation: {}", idJadwalKonsultasi);
        try {
            // Fetch the consultation data
            ReservasiKonsultasi reservasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

            // Get the schedule from reservasi
            CaregiverSchedule schedule = reservasi.getIdSchedule();
            if (schedule == null) {
                throw new IllegalArgumentException("Schedule not found for this consultation");
            }

            // Get the patient ID from the reservation (using correct field name)
            UUID idPacilian = reservasi.getIdPacilian();

            // Set the consultation ID in the request
            ratingRequest.setIdJadwalKonsultasi(idJadwalKonsultasi);

            // Add rating using service (with Builder pattern internally)
            RatingResponse response = ratingService.addRating(idPacilian, ratingRequest);
            log.info("Async add rating completed for consultation: {}", idJadwalKonsultasi);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            log.error("Async add rating failed for consultation {}: {}", idJadwalKonsultasi, e.getMessage());
            CompletableFuture<RatingResponse> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<RatingResponse> updateRatingAsync(UUID idJadwalKonsultasi, RatingRequest ratingRequest) {
        log.info("Async update rating for consultation: {}", idJadwalKonsultasi);
        try {
            // Get patient ID from consultation
            ReservasiKonsultasi reservasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

            UUID idPacilian = reservasi.getIdPacilian();

            // Set the consultation ID in the request
            ratingRequest.setIdJadwalKonsultasi(idJadwalKonsultasi);

            RatingResponse response = ratingService.updateRating(idPacilian, ratingRequest);
            log.info("Async update rating completed for consultation: {}", idJadwalKonsultasi);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            log.error("Async update rating failed for consultation {}: {}", idJadwalKonsultasi, e.getMessage());
            CompletableFuture<RatingResponse> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<Void> deleteRatingAsync(UUID idJadwalKonsultasi) {
        log.info("Async delete rating for consultation: {}", idJadwalKonsultasi);
        try {
            // Get patient ID from consultation
            ReservasiKonsultasi reservasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

            UUID idPacilian = reservasi.getIdPacilian();

            ratingService.deleteRating(idPacilian, idJadwalKonsultasi);
            log.info("Async delete rating completed for consultation: {}", idJadwalKonsultasi);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Async delete rating failed for consultation {}: {}", idJadwalKonsultasi, e.getMessage());
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<RatingResponse> getRatingByKonsultasiAsync(UUID idJadwalKonsultasi) {
        log.info("Async get rating for consultation: {}", idJadwalKonsultasi);
        try {
            // Get patient ID from consultation
            ReservasiKonsultasi reservasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

            UUID idPacilian = reservasi.getIdPacilian();

            RatingResponse response = ratingService.getRatingByKonsultasi(idPacilian, idJadwalKonsultasi);
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
    public CompletableFuture<Boolean> hasRatedKonsultasiAsync(UUID idJadwalKonsultasi) {
        log.info("Async check rating exists for consultation: {}", idJadwalKonsultasi);
        try {
            // Get patient ID from consultation
            ReservasiKonsultasi reservasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

            UUID idPacilian = reservasi.getIdPacilian();

            Boolean result = ratingService.hasRatedKonsultasi(idPacilian, idJadwalKonsultasi);
            log.info("Async check rating completed for consultation: {}", idJadwalKonsultasi);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async check rating failed for consultation {}: {}", idJadwalKonsultasi, e.getMessage());
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    // Keep the existing methods for doctor and patient queries as they're still useful
    @Async("ratingTaskExecutor")
    public CompletableFuture<RatingListResponse> getRatingsByDokterAsync(UUID idCaregiver) {
        log.info("Async get ratings for caregiver: {}", idCaregiver);
        try {
            RatingListResponse response = ratingService.getRatingsByDokter(idCaregiver);
            log.info("Async get ratings completed for caregiver: {}", idCaregiver);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            log.error("Async get ratings failed for caregiver {}: {}", idCaregiver, e.getMessage());
            CompletableFuture<RatingListResponse> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async("ratingTaskExecutor")
    public CompletableFuture<RatingListResponse> getRatingsByPasienAsync(UUID idPacilian) {
        log.info("Async get ratings for pacilian: {}", idPacilian);
        try {
            RatingListResponse response = ratingService.getRatingsByPasien(idPacilian);
            log.info("Async get ratings completed for pacilian: {}", idPacilian);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            log.error("Async get ratings failed for pacilian {}: {}", idPacilian, e.getMessage());
            CompletableFuture<RatingListResponse> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
}