package com.pandacare.mainapp.rating.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity class for storing doctor ratings
 */
@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "id_dokter", nullable = false)
    private String idDokter;

    @Column(name = "id_pasien", nullable = false)
    private String idPasien;

    @Column(name = "id_jadwal_konsultasi", nullable = false)
    private String idJadwalKonsultasi;

    @Column(name = "rating_score", nullable = false)
    private Integer ratingScore;

    @Column(name = "ulasan")
    private String ulasan;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor without id and dates (for creating new rating)
     */
    public Rating(String idDokter, String idPasien, String idJadwalKonsultasi, Integer ratingScore, String ulasan) {
        this.idDokter = idDokter;
        this.idPasien = idPasien;
        this.idJadwalKonsultasi = idJadwalKonsultasi;
        setRatingScore(ratingScore);
        this.ulasan = ulasan;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Validation method for rating score
     */
    public void setRatingScore(Integer ratingScore) {
        if (ratingScore == null) {
            throw new IllegalArgumentException("Rating score cannot be null");
        }
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

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
}