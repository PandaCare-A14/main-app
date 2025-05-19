package com.pandacare.mainapp.rating.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pandacare.mainapp.common.exception.BusinessException;
import com.pandacare.mainapp.common.exception.ResourceNotFoundException;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.ReservasiKonsultasiServiceImpl;

/**
 * Implementation of RatingService
 */
@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ReservasiKonsultasiServiceImpl reservasiService;

    @Override
    public RatingListResponse getRatingsByDokter(String idDokter) {
        List<Rating> ratings = ratingRepository.findByIdDokter(idDokter);
        List<RatingResponse> ratingResponses = ratings.stream()
                .map(RatingResponse::new)
                .collect(Collectors.toList());

        Double averageRating = ratingRepository.calculateAverageRatingByDokter(idDokter);
        if (averageRating == null) {
            averageRating = 0.0;
        }

        long totalRatings = ratingRepository.countByIdDokter(idDokter);

        return new RatingListResponse(averageRating, (int) totalRatings, ratingResponses);
    }

    @Override
    public List<RatingResponse> getRatingsByPasien(String idPasien) {
        List<Rating> ratings = ratingRepository.findByIdPasien(idPasien);
        return ratings.stream()
                .map(RatingResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public RatingResponse getRatingByPasienAndDokter(String idPasien, String idDokter) {
        Rating rating = ratingRepository.findByIdPasienAndIdDokter(idPasien, idDokter)
                .orElseThrow(() -> new ResourceNotFoundException("Rating tidak ditemukan"));

        return new RatingResponse(rating);
    }

    @Override
    public RatingResponse addRating(String idPasien, String idDokter, RatingRequest request) {
        // Verify request data (handled by DTO validate method in controller)

        // Verify patient has consulted with this doctor
        boolean hasConsulted = verifyConsultation(idPasien, idDokter);
        if (!hasConsulted) {
            throw new BusinessException("Pasien belum pernah melakukan konsultasi dengan dokter ini");
        }

        // Check if rating already exists
        if (ratingRepository.findByIdPasienAndIdDokter(idPasien, idDokter).isPresent()) {
            throw new BusinessException("Rating untuk dokter ini sudah diberikan. Silakan gunakan endpoint update untuk mengubah rating");
        }

        // Create and save new rating
        Rating rating = new Rating();
        rating.setIdDokter(idDokter);
        rating.setIdPasien(idPasien);
        rating.setRatingScore(request.getRatingScore());
        rating.setUlasan(request.getUlasan());
        rating.setCreatedAt(LocalDateTime.now());
        rating.setUpdatedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(rating);

        return new RatingResponse(savedRating);
    }

    @Override
    public RatingResponse updateRating(String idPasien, String idDokter, RatingRequest request) {
        // Verify request data (handled by DTO validate method in controller)

        // Find existing rating
        Rating rating = ratingRepository.findByIdPasienAndIdDokter(idPasien, idDokter)
                .orElseThrow(() -> new ResourceNotFoundException("Rating tidak ditemukan"));

        // Update rating
        rating.setRatingScore(request.getRatingScore());
        rating.setUlasan(request.getUlasan());
        rating.setUpdatedAt(LocalDateTime.now());

        Rating updatedRating = ratingRepository.save(rating);

        return new RatingResponse(updatedRating);
    }

    @Override
    public void deleteRating(String idPasien, String idDokter) {
        // Check if rating exists
        if (!ratingRepository.findByIdPasienAndIdDokter(idPasien, idDokter).isPresent()) {
            throw new ResourceNotFoundException("Rating tidak ditemukan");
        }

        // Delete rating
        ratingRepository.deleteByIdPasienAndIdDokter(idPasien, idDokter);
    }

    /**
     * Verify that the patient has completed a consultation with the doctor
     * @param idPasien patient ID
     * @param idDokter doctor ID
     * @return true if consultation exists
     */
    private boolean verifyConsultation(String idPasien, String idDokter) {
        List<ReservasiKonsultasi> consultations = reservasiService.findAllByPasien(idPasien);

        // Check if any consultation exists with this doctor
        return consultations.stream()
                .anyMatch(c -> c.getIdDokter().equals(idDokter));
    }
}