package com.pandacare.mainapp.rating.service;

import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import com.pandacare.mainapp.rating.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public RatingServiceImpl(RatingRepository ratingRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Template method untuk proses pembuatan/pembaruan rating
     * Mendefinisikan struktur algoritma, dengan langkah-langkah tertentu yang dapat di-override
     */
    protected final Rating processRating(Rating rating, boolean isNew) {
        // Validasi data rating
        validateRating(rating);
        
        // Validasi pengguna
        if (isNew) {
            validateUsers(rating);
        }
        
        // Set tanggal sesuai status
        prepareRatingDates(rating, isNew);
        
        // Jika update, pastikan rating sudah ada
        if (!isNew) {
            Rating existingRating = findExistingRating(rating);
            // Copy ID dan created date dari yang sudah ada
            rating.setId(existingRating.getId());
            rating.setCreatedAt(existingRating.getCreatedAt());
        }
        
        // Simpan rating
        return saveRating(rating);
    }
    
    // Hook methods yang dapat dioverride oleh subclass
    protected void validateRating(Rating rating) {
        if (rating.getRatingScore() < 1 || rating.getRatingScore() > 5) {
            throw new IllegalArgumentException("Rating score harus di antara 1 dan 5");
        }
        
        if (rating.getUlasan() == null || rating.getUlasan().trim().isEmpty()) {
            throw new IllegalArgumentException("Ulasan tidak boleh kosong");
        }
    }
    
    protected void validateUsers(Rating rating) {
        // Validasi dokter ada
        if (!userRepository.existsById(rating.getIdDokter())) {
            throw new IllegalArgumentException("Dokter dengan ID " + rating.getIdDokter() + " tidak ditemukan");
        }
        
        // Validasi pacillian ada
        if (!userRepository.existsById(rating.getIdPacillian())) {
            throw new IllegalArgumentException("Pacillian dengan ID " + rating.getIdPacillian() + " tidak ditemukan");
        }
    }
    
    protected void prepareRatingDates(Rating rating, boolean isNew) {
        LocalDateTime now = LocalDateTime.now();
        if (isNew) {
            rating.setCreatedAt(now);
        }
        rating.setUpdatedAt(now);
    }
    
    protected Rating findExistingRating(Rating rating) {
        Optional<List<Rating>> ratingsByOwner = ratingRepository.findByOwnerId(rating.getIdPacillian());
        
        if (ratingsByOwner.isPresent()) {
            return ratingsByOwner.get().stream()
                .filter(r -> r.getIdDokter().equals(rating.getIdDokter()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Rating untuk dokter dengan ID " + rating.getIdDokter() + " tidak ditemukan"));
        } else {
            throw new IllegalArgumentException("Tidak ada rating yang dimiliki oleh pacillian dengan ID " 
                + rating.getIdPacillian());
        }
    }
    
    protected Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public Rating addRating(Rating rating) {
        return processRating(rating, true);
    }

    @Override
    public Rating updateRating(Rating rating) {
        return processRating(rating, false);
    }

    @Override
    public Rating deleteRating(String idPacillian, String idDokter) {
        return ratingRepository.deleteById(idPacillian, idDokter)
            .orElseThrow(() -> new IllegalArgumentException("Rating tidak ditemukan"));
    }

    @Override
    public List<Rating> getRatingsByOwnerId(String idPacillian) {
        return ratingRepository.findByOwnerId(idPacillian).orElse(new ArrayList<>());
    }

    @Override
    public List<Rating> getRatingsByIdDokter(String idDokter) {
        return ratingRepository.findByIdDokter(idDokter).orElse(new ArrayList<>());
    }
}