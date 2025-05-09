package com.pandacare.mainapp.rating.dto.response;

import java.time.LocalDateTime;

import com.pandacare.mainapp.rating.model.Rating;

/**
 * DTO for sending rating data to client
 */
public class RatingResponse {
    private String id;
    private String idDokter;
    private String idPasien;
    private Integer ratingScore;
    private String ulasan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public RatingResponse() {}

    // Constructor from Rating entity
    public RatingResponse(Rating rating) {
        this.id = rating.getId();
        this.idDokter = rating.getIdDokter();
        this.idPasien = rating.getIdPasien();
        this.ratingScore = rating.getRatingScore();
        this.ulasan = rating.getUlasan();
        this.createdAt = rating.getCreatedAt();
        this.updatedAt = rating.getUpdatedAt();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdDokter() {
        return idDokter;
    }

    public void setIdDokter(String idDokter) {
        this.idDokter = idDokter;
    }

    public String getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(String idPasien) {
        this.idPasien = idPasien;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}