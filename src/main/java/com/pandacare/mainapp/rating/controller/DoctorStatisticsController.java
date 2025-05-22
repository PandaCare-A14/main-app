package com.pandacare.mainapp.rating.controller;

import com.pandacare.mainapp.rating.model.DoctorStatistics;
import com.pandacare.mainapp.rating.service.DoctorStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for managing doctor statistics
 */
@RestController
@RequestMapping("/api")
public class DoctorStatisticsController {

    private static final Logger log = LoggerFactory.getLogger(DoctorStatisticsController.class);
    private final DoctorStatisticsService doctorStatisticsService;

    @Autowired
    public DoctorStatisticsController(DoctorStatisticsService doctorStatisticsService) {
        this.doctorStatisticsService = doctorStatisticsService;
    }

    /**
     * GET: Get statistics for a doctor
     */
    @GetMapping("/doctors/{idDokter}/statistics")
    public ResponseEntity<?> getDoctorStatistics(@PathVariable String idDokter) {
        log.info("Fetching statistics for doctor: {}", idDokter);

        Optional<DoctorStatistics> statistics = doctorStatisticsService.getStatisticsByDoctor(idDokter);

        if (statistics.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", Map.of(
                            "averageRating", statistics.get().getAverageRating(),
                            "totalRatings", statistics.get().getTotalRatings(),
                            "updatedAt", statistics.get().getUpdatedAt()
                    )
            ));
        } else {
            // If statistics don't exist yet, calculate them on-the-fly
            DoctorStatistics calculatedStats = doctorStatisticsService.calculateStatistics(idDokter);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", Map.of(
                            "averageRating", calculatedStats.getAverageRating(),
                            "totalRatings", calculatedStats.getTotalRatings(),
                            "updatedAt", calculatedStats.getUpdatedAt()
                    )
            ));
        }
    }

    /**
     * POST: Refresh statistics for a doctor
     */
    @PostMapping("/doctors/{idDokter}/statistics/refresh")
    public ResponseEntity<?> refreshDoctorStatistics(@PathVariable String idDokter) {
        log.info("Refreshing statistics for doctor: {}", idDokter);

        try {
            DoctorStatistics updatedStats = doctorStatisticsService.updateStatistics(idDokter);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Statistik dokter berhasil diperbarui",
                    "data", Map.of(
                            "averageRating", updatedStats.getAverageRating(),
                            "totalRatings", updatedStats.getTotalRatings(),
                            "updatedAt", updatedStats.getUpdatedAt()
                    )
            ));
        } catch (Exception e) {
            log.error("Error refreshing doctor statistics: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Gagal memperbarui statistik dokter: " + e.getMessage()
            ));
        }
    }
}