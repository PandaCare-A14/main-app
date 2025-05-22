package com.pandacare.mainapp.rating.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Builder class for Rating entity
 */
public class RatingBuilder {
    private String id;
    private String idDokter;
    private String idPasien;
    private String idJadwalKonsultasi;
    private Integer ratingScore;
    private String ulasan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RatingBuilder() {
        // Default values
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public RatingBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public RatingBuilder withIdDokter(String idDokter) {
        this.idDokter = idDokter;
        return this;
    }

    public RatingBuilder withIdPasien(String idPasien) {
        this.idPasien = idPasien;
        return this;
    }

    public RatingBuilder withIdJadwalKonsultasi(String idJadwalKonsultasi) {
        this.idJadwalKonsultasi = idJadwalKonsultasi;
        return this;
    }

    public RatingBuilder withRatingScore(Integer ratingScore) {
        this.ratingScore = ratingScore;
        return this;
    }

    public RatingBuilder withUlasan(String ulasan) {
        this.ulasan = ulasan;
        return this;
    }

    public RatingBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public RatingBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Rating build() {
        // Validate required fields
        if (idDokter == null || idDokter.isEmpty()) {
            throw new IllegalArgumentException("idDokter cannot be null or empty");
        }

        if (idPasien == null || idPasien.isEmpty()) {
            throw new IllegalArgumentException("idPasien cannot be null or empty");
        }

        if (idJadwalKonsultasi == null || idJadwalKonsultasi.isEmpty()) {
            throw new IllegalArgumentException("idJadwalKonsultasi cannot be null or empty");
        }

        if (ratingScore == null) {
            throw new IllegalArgumentException("ratingScore cannot be null");
        }

        if (ratingScore < 1 || ratingScore > 5) {
            throw new IllegalArgumentException("ratingScore must be between 1 and 5");
        }

        Rating rating = new Rating();
        rating.setId(id);
        rating.setIdDokter(idDokter);
        rating.setIdPasien(idPasien);
        rating.setIdJadwalKonsultasi(idJadwalKonsultasi);
        rating.setRatingScore(ratingScore);
        rating.setUlasan(ulasan);
        rating.setCreatedAt(createdAt);
        rating.setUpdatedAt(updatedAt);

        return rating;
    }
}