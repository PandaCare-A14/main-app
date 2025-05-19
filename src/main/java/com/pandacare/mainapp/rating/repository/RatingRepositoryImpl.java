package com.pandacare.mainapp.rating.repository;

import org.springframework.stereotype.Repository;

import com.pandacare.mainapp.rating.model.Rating;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory implementation of RatingRepository
 */
@Repository
public class RatingRepositoryImpl implements RatingRepository {

    private final Map<String, Rating> ratingDatabase = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Rating> findByIdPasien(String idPasien) {
        return ratingDatabase.values().stream()
                .filter(rating -> rating.getIdPasien().equals(idPasien))
                .collect(Collectors.toList());
    }

    @Override
    public List<Rating> findByIdDokter(String idDokter) {
        return ratingDatabase.values().stream()
                .filter(rating -> rating.getIdDokter().equals(idDokter))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Rating> findByIdPasienAndIdDokter(String idPasien, String idDokter) {
        return ratingDatabase.values().stream()
                .filter(rating -> rating.getIdPasien().equals(idPasien)
                        && rating.getIdDokter().equals(idDokter))
                .findFirst();
    }

    @Override
    public Double calculateAverageRatingByDokter(String idDokter) {
        List<Rating> doctorRatings = findByIdDokter(idDokter);
        if (doctorRatings.isEmpty()) {
            return null;
        }

        return doctorRatings.stream()
                .mapToInt(Rating::getRatingScore)
                .average()
                .orElse(0.0);
    }

    @Override
    public long countByIdDokter(String idDokter) {
        return findByIdDokter(idDokter).size();
    }

    @Override
    public Rating save(Rating rating) {
        if (rating.getId() == null) {
            // Create a new unique ID for new ratings
            String newId = "RTG" + idCounter.getAndIncrement();
            rating.setId(newId);
        }

        // Save or update
        ratingDatabase.put(rating.getId(), rating);

        return rating;
    }

    @Override
    public void deleteByIdPasienAndIdDokter(String idPasien, String idDokter) {
        Optional<Rating> ratingToDelete = findByIdPasienAndIdDokter(idPasien, idDokter);

        ratingToDelete.ifPresent(rating -> ratingDatabase.remove(rating.getId()));
    }
}