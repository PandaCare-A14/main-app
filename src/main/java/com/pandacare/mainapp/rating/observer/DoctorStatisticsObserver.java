package com.pandacare.mainapp.rating.observer;

import com.pandacare.mainapp.rating.model.DoctorStatistics;
import com.pandacare.mainapp.rating.model.DoctorStatisticsBuilder;
import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.repository.DoctorStatisticsRepository;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Observer for updating doctor statistics when ratings change using JPA
 * Uses synchronous processing for better reliability
 */
@Component
@Slf4j
public class DoctorStatisticsObserver implements RatingObserver {

    private final DoctorStatisticsRepository doctorStatisticsRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public DoctorStatisticsObserver(
            DoctorStatisticsRepository doctorStatisticsRepository,
            RatingRepository ratingRepository) {
        this.doctorStatisticsRepository = doctorStatisticsRepository;
        this.ratingRepository = ratingRepository;
        RatingSubject.getInstance().attach(this);
        log.info("DoctorStatisticsObserver registered successfully");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onRatingCreated(Rating rating) {
        try {
            log.info("Observer: Rating created event for doctor: {}", rating.getIdDokter());
            updateDoctorStatistics(rating.getIdDokter());
        } catch (Exception e) {
            log.error("Error in onRatingCreated observer: {}", e.getMessage(), e);
            // Swallow exception to prevent affecting main transaction
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onRatingUpdated(Rating rating) {
        try {
            log.info("Observer: Rating updated event for doctor: {}", rating.getIdDokter());
            updateDoctorStatistics(rating.getIdDokter());
        } catch (Exception e) {
            log.error("Error in onRatingUpdated observer: {}", e.getMessage(), e);
            // Swallow exception to prevent affecting main transaction
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onRatingDeleted(Rating rating) {
        try {
            log.info("Observer: Rating deleted event for doctor: {}", rating.getIdDokter());
            updateDoctorStatistics(rating.getIdDokter());
        } catch (Exception e) {
            log.error("Error in onRatingDeleted observer: {}", e.getMessage(), e);
            // Swallow exception to prevent affecting main transaction
        }
    }

    /**
     * Update doctor statistics in the database using JPA and Builder pattern
     */
    private void updateDoctorStatistics(String idDokter) {
        try {
            log.debug("Starting statistics update for doctor: {}", idDokter);

            // Calculate average rating for doctor using Repository query
            Double averageRating = ratingRepository.calculateAverageRatingByDokter(idDokter);
            if (averageRating == null) {
                averageRating = 0.0;
            }

            // Count total ratings for doctor using Repository query
            Integer totalRatings = ratingRepository.countRatingsByDokter(idDokter);
            if (totalRatings == null) {
                totalRatings = 0;
            }

            log.debug("Calculated statistics for doctor {}: average={}, total={}",
                    idDokter, averageRating, totalRatings);

            // Check if doctor already has statistics record
            Optional<DoctorStatistics> existingStats = doctorStatisticsRepository.findByIdDokter(idDokter);

            if (existingStats.isPresent()) {
                // Update existing statistics using Builder pattern
                DoctorStatistics stats = existingStats.get();

                DoctorStatistics updatedStats = new DoctorStatisticsBuilder()
                        .withIdDokter(idDokter)
                        .withAverageRating(averageRating)
                        .withTotalRatings(totalRatings)
                        .withCreatedAt(stats.getCreatedAt())
                        .withUpdatedAt(LocalDateTime.now())
                        .build();

                // Update existing entity
                stats.updateFrom(updatedStats);
                doctorStatisticsRepository.save(stats);

                log.info("Updated existing statistics for doctor {}: avg={}, total={}",
                        idDokter, averageRating, totalRatings);
            } else {
                // Create new statistics entry using Builder pattern
                DoctorStatistics newStats = new DoctorStatisticsBuilder()
                        .withIdDokter(idDokter)
                        .withAverageRating(averageRating)
                        .withTotalRatings(totalRatings)
                        .build();

                doctorStatisticsRepository.save(newStats);

                log.info("Created new statistics for doctor {}: avg={}, total={}",
                        idDokter, averageRating, totalRatings);
            }

        } catch (Exception e) {
            log.error("Error updating doctor statistics for doctor {}: {}", idDokter, e.getMessage(), e);
            // Don't re-throw to prevent affecting main transaction
        }
    }
}