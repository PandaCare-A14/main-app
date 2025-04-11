package com.pandacare.rating.repository;

import com.pandacare.rating.model.Rating;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class RatingRepositoryImpl implements RatingRepository {

    private final Map<Long, Rating> ratingDatabase = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Optional<List<Rating>> findByOwnerId(String idPacillian) {
        List<Rating> result = ratingDatabase.values().stream()
                .filter(rating -> rating.getIdPacillian().equals(idPacillian))
                .collect(Collectors.toList());
        
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    @Override
    public Optional<List<Rating>> findByIdDokter(String idDokter) {
        List<Rating> result = ratingDatabase.values().stream()
                .filter(rating -> rating.getIdDokter().equals(idDokter))
                .collect(Collectors.toList());
        
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    @Override
    public Optional<Rating> deleteById(String idPacillian, String idDokter) {
        Optional<Rating> ratingToDelete = ratingDatabase.values().stream()
                .filter(rating -> rating.getIdPacillian().equals(idPacillian) 
                              && rating.getIdDokter().equals(idDokter))
                .findFirst();
        
        ratingToDelete.ifPresent(rating -> ratingDatabase.remove(rating.getId()));
        
        return ratingToDelete;
    }

    @Override
    public Rating save(Rating rating) {
        if (rating.getId() == null) {
            // Buat rating baru
            rating.setId(idCounter.getAndIncrement());
        }
        
        // Save or update
        ratingDatabase.put(rating.getId(), rating);
        
        return rating;
    }
}