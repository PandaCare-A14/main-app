package com.pandacare.mainapp.rating.repository;

import com.pandacare.mainapp.rating.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {

    List<Rating> findByIdDokter(String idDokter);

    List<Rating> findByIdPasien(String idPasien);

    Optional<Rating> findByIdPasienAndIdJadwalKonsultasi(String idPasien, String idJadwalKonsultasi);

    @Query("SELECT AVG(r.ratingScore) FROM Rating r WHERE r.idDokter = :idDokter")
    Double calculateAverageRatingByDokter(@Param("idDokter") String idDokter);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.idDokter = :idDokter")
    Integer countRatingsByDokter(@Param("idDokter") String idDokter);

    boolean existsByIdPasienAndIdJadwalKonsultasi(String idPasien, String idJadwalKonsultasi);
}