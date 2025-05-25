package com.pandacare.mainapp.rating.repository;

import com.pandacare.mainapp.rating.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    List<Rating> findByIdDokter(UUID idDokter);

    List<Rating> findByIdPasien(UUID idPasien);

    Optional<Rating> findByIdPasienAndIdJadwalKonsultasi(UUID idPasien, UUID idJadwalKonsultasi);

    @Query("SELECT AVG(r.ratingScore) FROM Rating r WHERE r.idDokter = :idDokter")
    Double calculateAverageRatingByDokter(@Param("idDokter") UUID idDokter);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.idDokter = :idDokter")
    Integer countRatingsByDokter(@Param("idDokter") UUID idDokter);

    boolean existsByIdPasienAndIdJadwalKonsultasi(UUID idPasien, UUID idJadwalKonsultasi);
}