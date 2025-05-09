package com.pandacare.mainapp.rating.dto;

/**
 * DTO for receiving rating data from client
 */
public class RatingRequest {

    private Integer ratingScore;
    private String ulasan;

    // Default constructor
    public RatingRequest() {}

    // Constructor with fields
    public RatingRequest(Integer ratingScore, String ulasan) {
        this.ratingScore = ratingScore;
        this.ulasan = ulasan;
    }

    // Getters and Setters
    public Integer getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(Integer ratingScore) {
        this.ratingScore = ratingScore;
    }

    public String getUlasan() {
        return ulasan;
    }

    public void setUlasan(String ulasan) {
        this.ulasan = ulasan;
    }

    /**
     * Validate the rating request
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (ratingScore == null) {
            throw new IllegalArgumentException("Rating score harus diisi");
        }

        if (ratingScore < 1 || ratingScore > 5) {
            throw new IllegalArgumentException("Rating score harus di antara 1 dan 5");
        }

        if (ulasan == null || ulasan.trim().isEmpty()) {
            throw new IllegalArgumentException("Ulasan tidak boleh kosong");
        }
    }
}