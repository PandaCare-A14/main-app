package com.pandacare.mainapp.rating.repository;

import java.util.List;
import java.util.Optional;

import com.pandacare.mainapp.rating.model.Rating;

/**
 * Repository interface for Rating entity
 */
public interface RatingRepository {

    /**
     * Find ratings by patient ID
     * @param idPasien patient ID
     * @return list of ratings
     */
    List<Rating> findByIdPasien(String idPasien);

    /**
     * Find ratings by doctor ID
     * @param idDokter doctor ID
     * @return list of ratings
     */
    List<Rating> findByIdDokter(String idDokter);

    /**
     * Find rating by patient ID and doctor ID
     * @param idPasien patient ID
     * @param idDokter doctor ID
     * @return optional rating
     */
    Optional<Rating> findByIdPasienAndIdDokter(String idPasien, String idDokter);

    /**
     * Calculate average rating score for a doctor
     * @param idDokter doctor ID
     * @return average rating score
     */
    Double calculateAverageRatingByDokter(String idDokter);

    /**
     * Count ratings for a doctor
     * @param idDokter doctor ID
     * @return number of ratings
     */
    long countByIdDokter(String idDokter);

    /**
     * Save or update a rating
     * @param rating the rating to save
     * @return the saved rating
     */
    Rating save(Rating rating);

    /**
     * Delete rating by patient ID and doctor ID
     * @param idPasien patient ID
     * @param idDokter doctor ID
     */
    void deleteByIdPasienAndIdDokter(String idPasien, String idDokter);
}