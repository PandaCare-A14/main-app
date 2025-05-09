package com.pandacare.mainapp.rating.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for storing rating data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    private String id;
    private String idDokter;
    private String idPasien;
    private Integer ratingScore;
    private String ulasan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor without id and dates (for creating new rating)
     */
    public Rating(String idDokter, String idPasien, Integer ratingScore, String ulasan) {
        this.idDokter = idDokter;
        this.idPasien = idPasien;
        this.ratingScore = ratingScore;
        this.ulasan = ulasan;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Validation method for rating score
     */
    public void setRatingScore(Integer ratingScore) {
        if (ratingScore < 1 || ratingScore > 5) {
            throw new IllegalArgumentException("Rating score harus di antara 1 dan 5");
        }
        this.ratingScore = ratingScore;
    }

    /**
     * Update rating from another rating
     */
    public void updateFrom(Rating other) {
        this.ratingScore = other.getRatingScore();
        this.ulasan = other.getUlasan();
        this.updatedAt = LocalDateTime.now();
    }
}