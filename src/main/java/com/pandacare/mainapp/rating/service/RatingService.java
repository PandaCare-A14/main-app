package com.pandacare.mainapp.rating.service;

import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;

import java.util.UUID;

/**
 * Service interface for handling consultation ratings
 */
public interface RatingService {

    /**
     * Add a new rating for a consultation
     */
    RatingResponse addRating(UUID idPasien, RatingRequest ratingRequest);

    /**
     * Update an existing rating
     */
    RatingResponse updateRating(UUID idPasien, RatingRequest ratingRequest);

    /**
     * Delete a rating for a consultation
     */
    void deleteRating(UUID idPasien, UUID idJadwalKonsultasi);

    /**
     * Get all ratings for a specific doctor
     */
    RatingListResponse getRatingsByDokter(UUID idDokter);

    /**
     * Get all ratings made by a specific patient
     */
    RatingListResponse getRatingsByPasien(UUID idPasien);

    /**
     * Get a specific rating by consultation ID
     */
    RatingResponse getRatingByKonsultasi(UUID idPasien, UUID idJadwalKonsultasi);

    /**
     * Check if a patient has already rated a specific consultation
     */
    boolean hasRatedKonsultasi(UUID idPasien, UUID idJadwalKonsultasi);
}