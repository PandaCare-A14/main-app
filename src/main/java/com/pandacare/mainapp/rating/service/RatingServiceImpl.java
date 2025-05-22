package com.pandacare.mainapp.rating.service;

import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.model.RatingBuilder;
import com.pandacare.mainapp.rating.observer.RatingSubject;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the RatingService interface using JPA and Builder pattern
 */
@Service
public class RatingServiceImpl implements RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingServiceImpl.class);
    private final RatingRepository ratingRepository;
    private final ReservasiKonsultasiRepository reservasiKonsultasiRepository;
    private final RatingSubject ratingSubject;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository,
                             ReservasiKonsultasiRepository reservasiKonsultasiRepository) {
        this.ratingRepository = ratingRepository;
        this.reservasiKonsultasiRepository = reservasiKonsultasiRepository;
        this.ratingSubject = RatingSubject.getInstance();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public RatingResponse addRating(String idPasien, RatingRequest ratingRequest) {
        log.info("Starting addRating for patient: {} and consultation: {}",
                idPasien, ratingRequest.getIdJadwalKonsultasi());

        try {
            // Verify the consultation exists and is completed
            String idJadwalKonsultasi = ratingRequest.getIdJadwalKonsultasi();
            ReservasiKonsultasi konsultasi = reservasiKonsultasiRepository.findById(idJadwalKonsultasi)
                    .orElseThrow(() -> new IllegalArgumentException("Jadwal konsultasi tidak ditemukan"));

            log.debug("Found consultation with status: {}", konsultasi.getStatusReservasi());

            if (!konsultasi.getIdPasien().equals(idPasien)) {
                log.warn("Patient mismatch: expected={}, actual={}",
                        konsultasi.getIdPasien(), idPasien);
                throw new IllegalArgumentException("Jadwal konsultasi ini bukan milik pasien ini");
            }

            String idDokter = konsultasi.getIdDokter();

            if (konsultasi.getStatusReservasi() != StatusReservasiKonsultasi.APPROVED) {
                log.warn("Consultation not approved: status={}", konsultasi.getStatusReservasi());
                throw new IllegalArgumentException("Rating hanya dapat diberikan untuk konsultasi yang telah disetujui");
            }

            // Check if rating already exists for this consultation
            boolean exists = ratingRepository.existsByIdPasienAndIdJadwalKonsultasi(idPasien, idJadwalKonsultasi);
            log.debug("Rating exists check: {}", exists);

            if (exists) {
                throw new IllegalArgumentException("Rating untuk jadwal konsultasi ini sudah diberikan");
            }

            // Build the rating using Builder pattern
            Rating rating = new RatingBuilder()
                    .withIdDokter(idDokter)
                    .withIdPasien(idPasien)
                    .withIdJadwalKonsultasi(idJadwalKonsultasi)
                    .withRatingScore(ratingRequest.getRatingScore())
                    .withUlasan(ratingRequest.getUlasan())
                    .build();

            log.debug("Saving rating to database with ID: {}", rating.getId());

            // Save using JPA repository
            Rating savedRating = ratingRepository.save(rating);
            log.info("Rating saved successfully with ID: {}", savedRating.getId());

            // Ensure the transaction is committed before notifying observers
            log.debug("Rating saved, about to flush and sync");
            ratingRepository.flush();

            // Notify observers
            try {
                log.debug("Notifying observers about new rating");
                ratingSubject.notifyRatingCreated(savedRating);
                log.debug("Observers notified successfully");
            } catch (Exception e) {
                log.error("Error notifying observers: {}", e.getMessage(), e);
                // Continue anyway - this should not affect the main operation
            }

            return new RatingResponse(savedRating);
        } catch (Exception e) {
            log.error("Error in addRating: {}", e.getMessage(), e);
            throw e; // Will trigger transaction rollback
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public RatingResponse updateRating(String idPasien, RatingRequest ratingRequest) {
        String idJadwalKonsultasi = ratingRequest.getIdJadwalKonsultasi();
        log.info("Updating rating for patient: {} and consultation: {}", idPasien, idJadwalKonsultasi);

        // Find the existing rating
        Rating existingRating = ratingRepository.findByIdPasienAndIdJadwalKonsultasi(
                idPasien,
                idJadwalKonsultasi
        ).orElseThrow(() -> new IllegalArgumentException("Rating tidak ditemukan"));

        // Update rating using builder for consistency
        Rating updatedRating = new RatingBuilder()
                .withId(existingRating.getId())
                .withIdDokter(existingRating.getIdDokter())
                .withIdPasien(existingRating.getIdPasien())
                .withIdJadwalKonsultasi(existingRating.getIdJadwalKonsultasi())
                .withRatingScore(ratingRequest.getRatingScore())
                .withUlasan(ratingRequest.getUlasan())
                .withCreatedAt(existingRating.getCreatedAt())
                .build(); // This will set updatedAt to current time

        // Save to database
        Rating savedRating = ratingRepository.save(updatedRating);
        log.info("Rating updated successfully with ID: {}", savedRating.getId());

        // Notify observers
        try {
            ratingSubject.notifyRatingUpdated(savedRating);
        } catch (Exception e) {
            log.error("Error notifying observers for update: {}", e.getMessage(), e);
            // Continue anyway
        }

        return new RatingResponse(savedRating);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public void deleteRating(String idPasien, String idJadwalKonsultasi) {
        log.info("Deleting rating for patient: {} and consultation: {}", idPasien, idJadwalKonsultasi);

        // Find the existing rating
        Rating existingRating = ratingRepository.findByIdPasienAndIdJadwalKonsultasi(
                idPasien,
                idJadwalKonsultasi
        ).orElseThrow(() -> new IllegalArgumentException("Rating tidak ditemukan"));

        // Keep a copy for observers
        Rating ratingCopy = new Rating(
                existingRating.getIdDokter(),
                existingRating.getIdPasien(),
                existingRating.getIdJadwalKonsultasi(),
                existingRating.getRatingScore(),
                existingRating.getUlasan()
        );
        ratingCopy.setId(existingRating.getId());
        ratingCopy.setCreatedAt(existingRating.getCreatedAt());
        ratingCopy.setUpdatedAt(existingRating.getUpdatedAt());

        // Delete from database
        ratingRepository.delete(existingRating);
        log.info("Rating deleted successfully with ID: {}", existingRating.getId());

        // Notify observers
        try {
            ratingSubject.notifyRatingDeleted(ratingCopy);
        } catch (Exception e) {
            log.error("Error notifying observers for deletion: {}", e.getMessage(), e);
            // Continue anyway
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RatingListResponse getRatingsByDokter(String idDokter) {
        log.info("Fetching ratings for doctor: {}", idDokter);

        List<Rating> ratings = ratingRepository.findByIdDokter(idDokter);

        Double averageRating = ratingRepository.calculateAverageRatingByDokter(idDokter);
        if (averageRating == null) {
            averageRating = 0.0;
        }

        Integer totalRatings = ratingRepository.countRatingsByDokter(idDokter);

        List<RatingResponse> ratingResponses = ratings.stream()
                .map(RatingResponse::new)
                .collect(Collectors.toList());

        log.info("Found {} ratings for doctor: {}", totalRatings, idDokter);
        return new RatingListResponse(averageRating, totalRatings, ratingResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingListResponse getRatingsByPasien(String idPasien) {
        log.info("Fetching ratings by patient: {}", idPasien);

        List<Rating> ratings = ratingRepository.findByIdPasien(idPasien);

        List<RatingResponse> ratingResponses = ratings.stream()
                .map(RatingResponse::new)
                .collect(Collectors.toList());

        // Here we don't calculate average for patient's ratings since they are for different doctors
        Double dummyAverage = 0.0;

        log.info("Found {} ratings by patient: {}", ratings.size(), idPasien);
        return new RatingListResponse(dummyAverage, ratings.size(), ratingResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingResponse getRatingByKonsultasi(String idPasien, String idJadwalKonsultasi) {
        log.info("Fetching rating for consultation: {} by patient: {}", idJadwalKonsultasi, idPasien);

        Rating rating = ratingRepository.findByIdPasienAndIdJadwalKonsultasi(idPasien, idJadwalKonsultasi)
                .orElseThrow(() -> new IllegalArgumentException("Rating tidak ditemukan"));

        log.info("Found rating with ID: {}", rating.getId());
        return new RatingResponse(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRatedKonsultasi(String idPasien, String idJadwalKonsultasi) {
        boolean exists = ratingRepository.existsByIdPasienAndIdJadwalKonsultasi(idPasien, idJadwalKonsultasi);
        log.info("Check if patient: {} has rated consultation: {}: {}", idPasien, idJadwalKonsultasi, exists);
        return exists;
    }
}