package com.pandacare.mainapp.rating.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving rating data from client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {

    @NotNull(message = "Rating score harus diisi")
    @Min(value = 1, message = "Rating score harus di antara 1 dan 5")
    @Max(value = 5, message = "Rating score harus di antara 1 dan 5")
    private Integer ratingScore;

    @NotBlank(message = "Ulasan tidak boleh kosong")
    private String ulasan;

    private String idJadwalKonsultasi;
//
//    public void validate() {
//        if (ratingScore == null) {
//            throw new IllegalArgumentException("Rating score harus diisi");
//        }
//
//        if (ratingScore < 1 || ratingScore > 5) {
//            throw new IllegalArgumentException("Rating score harus di antara 1 dan 5");
//        }
//
//        if (ulasan == null || ulasan.trim().isEmpty()) {
//            throw new IllegalArgumentException("Ulasan tidak boleh kosong");
//        }
//
//        if (idJadwalKonsultasi == null || idJadwalKonsultasi.trim().isEmpty()) {
//            throw new IllegalArgumentException("ID jadwal konsultasi harus diisi");
//        }
//    }
}