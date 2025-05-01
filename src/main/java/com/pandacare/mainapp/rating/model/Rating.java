package com.pandacare.mainapp.rating.model;

import java.time.LocalDateTime;

public class Rating {
    private Long id;
    private String idDokter;
    private String idPacillian;
    private int ratingScore;
    private String ulasan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Rating() {
    }

    // Constructor with all parameters
    public Rating(Long id, String idDokter, String idPacillian, int ratingScore, String ulasan,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.idDokter = idDokter;
        this.idPacillian = idPacillian;
        setRatingScore(ratingScore); // Use setter for validation
        this.ulasan = ulasan;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdDokter() {
        return idDokter;
    }

    public void setIdDokter(String idDokter) {
        this.idDokter = idDokter;
    }

    public String getIdPacillian() {
        return idPacillian;
    }

    public void setIdPacillian(String idPacillian) {
        this.idPacillian = idPacillian;
    }

    public int getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(int ratingScore) {
        if (ratingScore < 1 || ratingScore > 5) {
            throw new IllegalArgumentException("Rating score harus di antara 1 dan 5");
        }
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