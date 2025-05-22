package com.pandacare.mainapp.rating.model;

import java.time.LocalDateTime;

/**
 * Builder class for DoctorStatistics entity
 */
public class DoctorStatisticsBuilder {
    private String idDokter;
    private Double averageRating = 0.0;
    private Integer totalRatings = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DoctorStatisticsBuilder() {
        // Default values
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public DoctorStatisticsBuilder withIdDokter(String idDokter) {
        this.idDokter = idDokter;
        return this;
    }

    public DoctorStatisticsBuilder withAverageRating(Double averageRating) {
        this.averageRating = averageRating != null ? averageRating : 0.0;
        return this;
    }

    public DoctorStatisticsBuilder withTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings != null ? totalRatings : 0;
        return this;
    }

    public DoctorStatisticsBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public DoctorStatisticsBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public DoctorStatistics build() {
        // Validate required fields
        if (idDokter == null || idDokter.isEmpty()) {
            throw new IllegalArgumentException("idDokter cannot be null or empty");
        }

        DoctorStatistics doctorStatistics = new DoctorStatistics();
        doctorStatistics.setIdDokter(idDokter);
        doctorStatistics.setAverageRating(averageRating);
        doctorStatistics.setTotalRatings(totalRatings);
        doctorStatistics.setCreatedAt(createdAt);
        doctorStatistics.setUpdatedAt(updatedAt);

        return doctorStatistics;
    }
}