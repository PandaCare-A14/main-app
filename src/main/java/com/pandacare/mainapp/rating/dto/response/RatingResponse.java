package com.pandacare.mainapp.rating.dto.response;

import com.pandacare.mainapp.rating.model.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for sending rating data to client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private String id;
    private String idDokter;
    private String idPasien;
    private String idJadwalKonsultasi;
    private Integer ratingScore;
    private String ulasan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RatingResponse(Rating rating) {
        this.id = rating.getId();
        this.idDokter = rating.getIdDokter();
        this.idPasien = rating.getIdPasien();
        this.idJadwalKonsultasi = rating.getIdJadwalKonsultasi();
        this.ratingScore = rating.getRatingScore();
        this.ulasan = rating.getUlasan();
        this.createdAt = rating.getCreatedAt();
        this.updatedAt = rating.getUpdatedAt();
    }
}