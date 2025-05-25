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
    private UUID id;

    @Column(name = "id_dokter", nullable = false)
    private UUID idDokter;

    @Column(name = "id_pasien", nullable = false)
    private UUID idPasien;

    @Column(name = "id_jadwal_konsultasi", nullable = false)
    private UUID idJadwalKonsultasi;

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
    public Rating(UUID idDokter, UUID idPasien, UUID idJadwalKonsultasi, Integer ratingScore, String ulasan) {
        this.idDokter = idDokter;
        this.idPasien = idPasien;
        this.idJadwalKonsultasi = idJadwalKonsultasi;
        this.setRatingScore(ratingScore);
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
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
}