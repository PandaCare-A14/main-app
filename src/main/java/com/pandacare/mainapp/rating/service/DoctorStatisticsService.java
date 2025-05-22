package com.pandacare.mainapp.rating.service;

import com.pandacare.mainapp.rating.model.DoctorStatistics;
import com.pandacare.mainapp.rating.model.DoctorStatisticsBuilder;
import com.pandacare.mainapp.rating.repository.DoctorStatisticsRepository;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for handling doctor statistics operations
 */
@Service
public class DoctorStatisticsService {

    private static final Logger log = LoggerFactory.getLogger(DoctorStatisticsService.class);
    private final DoctorStatisticsRepository doctorStatisticsRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public DoctorStatisticsService(
            DoctorStatisticsRepository doctorStatisticsRepository,
            RatingRepository ratingRepository) {
        this.doctorStatisticsRepository = doctorStatisticsRepository;
        this.ratingRepository = ratingRepository;
    }

    /**
     * Get doctor statistics by ID
     */
    @Transactional(readOnly = true)
    public Optional<DoctorStatistics> getStatisticsByDoctor(String idDokter) {
        return doctorStatisticsRepository.findByIdDokter(idDokter);
    }

    /**
     * Update statistics for a doctor
     */
    @Transactional
    public DoctorStatistics updateStatistics(String idDokter) {
        log.info("Updating statistics for doctor: {}", idDokter);

        // Calculate average rating for doctor
        Double averageRating = ratingRepository.calculateAverageRatingByDokter(idDokter);
        if (averageRating == null) {
            averageRating = 0.0;
        }

        // Count total ratings for doctor
        Integer totalRatings = ratingRepository.countRatingsByDokter(idDokter);
        if (totalRatings == null) {
            totalRatings = 0;
        }

        log.debug("Calculated statistics: average={}, total={}", averageRating, totalRatings);

        // Check if doctor already has statistics record
        Optional<DoctorStatistics> existingStats = doctorStatisticsRepository.findByIdDokter(idDokter);

        if (existingStats.isPresent()) {
            // Update existing record
            DoctorStatistics stats = existingStats.get();

            // Use builder to create updated stats
            DoctorStatistics updatedStats = new DoctorStatisticsBuilder()
                    .withIdDokter(idDokter)
                    .withAverageRating(averageRating)
                    .withTotalRatings(totalRatings)
                    .withCreatedAt(stats.getCreatedAt())
                    .build(); // Will set updatedAt to now

            // Update and save
            stats.updateFrom(updatedStats);
            return doctorStatisticsRepository.save(stats);
        } else {
            // Create new record
            DoctorStatistics newStats = new DoctorStatisticsBuilder()
                    .withIdDokter(idDokter)
                    .withAverageRating(averageRating)
                    .withTotalRatings(totalRatings)
                    .build();

            return doctorStatisticsRepository.save(newStats);
        }
    }

    /**
     * Calculate and retrieve fresh statistics for a doctor
     */
    @Transactional(readOnly = true)
    public DoctorStatistics calculateStatistics(String idDokter) {
        Double averageRating = ratingRepository.calculateAverageRatingByDokter(idDokter);
        Integer totalRatings = ratingRepository.countRatingsByDokter(idDokter);

        // Use builder to create statistics object without saving
        return new DoctorStatisticsBuilder()
                .withIdDokter(idDokter)
                .withAverageRating(averageRating)
                .withTotalRatings(totalRatings)
                .build();
    }
}