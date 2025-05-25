package com.pandacare.mainapp.rating.repository;

import com.pandacare.mainapp.rating.model.DoctorStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for doctor statistics data
 */
@Repository
public interface DoctorStatisticsRepository extends JpaRepository<DoctorStatistics, String> {

    /**
     * Find doctor statistics by doctor ID
     */
    Optional<DoctorStatistics> findByIdDokter(UUID idDokter);

    /**
     * Check if statistics exist for a doctor
     */
    boolean existsByIdDokter(UUID idDokter);
}