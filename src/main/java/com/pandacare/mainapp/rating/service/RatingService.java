package com.pandacare.mainapp.rating.service;

import com.pandacare.mainapp.rating.dto.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;

/**
 * Service interface for handling consultation ratings
 */
public interface RatingService {

    /**
     * Add a new rating for a consultation
     */
    RatingResponse addRating(String idPasien, RatingRequest ratingRequest);

    /**
     * Update an existing rating
     */
    RatingResponse updateRating(String idPasien, RatingRequest ratingRequest);

    /**
     * Delete a rating for a consultation
     */
    void deleteRating(String idPasien, String idJadwalKonsultasi);

    /**
     * Get all ratings for a specific doctor
     */
    RatingListResponse getRatingsByDokter(String idDokter);

    /**
     * Get all ratings made by a specific patient
     */
    RatingListResponse getRatingsByPasien(String idPasien);

    /**
     * Get a specific rating by consultation ID
     */
    RatingResponse getRatingByKonsultasi(String idPasien, String idJadwalKonsultasi);

    /**
     * Check if a patient has already rated a specific consultation
     */
    boolean hasRatedKonsultasi(String idPasien, String idJadwalKonsultasi);
}