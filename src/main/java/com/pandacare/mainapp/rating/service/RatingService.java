package com.pandacare.mainapp.rating.service;

import java.util.List;

import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;

/**
 * Service interface for rating operations
 */
public interface RatingService {

    /**
     * Get all ratings for a doctor
     * @param idDokter doctor ID
     * @return list of ratings with average and total count
     */
    RatingListResponse getRatingsByDokter(String idDokter);

    /**
     * Get all ratings given by a patient
     * @param idPasien patient ID
     * @return list of ratings
     */
    List<RatingResponse> getRatingsByPasien(String idPasien);

    /**
     * Get a specific rating given by a patient to a doctor
     * @param idPasien patient ID
     * @param idDokter doctor ID
     * @return rating response
     */
    RatingResponse getRatingByPasienAndDokter(String idPasien, String idDokter);

    /**
     * Add a new rating
     * @param idPasien patient ID
     * @param idDokter doctor ID
     * @param request rating data
     * @return created rating
     */
    RatingResponse addRating(String idPasien, String idDokter, RatingRequest request);

    /**
     * Update an existing rating
     * @param idPasien patient ID
     * @param idDokter doctor ID
     * @param request rating data
     * @return updated rating
     */
    RatingResponse updateRating(String idPasien, String idDokter, RatingRequest request);

    /**
     * Delete a rating
     * @param idPasien patient ID
     * @param idDokter doctor ID
     */
    void deleteRating(String idPasien, String idDokter);
}