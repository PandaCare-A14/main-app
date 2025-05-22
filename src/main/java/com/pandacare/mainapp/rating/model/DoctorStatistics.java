package com.pandacare.mainapp.rating.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class for storing doctor rating statistics
 */
@Entity
@Table(name = "doctor_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStatistics {

    @Id
    @Column(name = "id_dokter")
    private String idDokter;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating;

    @Column(name = "total_ratings", nullable = false)
    private Integer totalRatings;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new doctor statistics
     */
    public DoctorStatistics(String idDokter, Double averageRating, Integer totalRatings) {
        this.idDokter = idDokter;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update statistics from another object
     */
    public void updateFrom(DoctorStatistics other) {
        this.averageRating = other.getAverageRating();
        this.totalRatings = other.getTotalRatings();
        this.updatedAt = LocalDateTime.now();
    }
}