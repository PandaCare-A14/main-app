package com.pandacare.mainapp.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for receiving rating data from client
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {

    private Integer ratingScore;
    private String ulasan;
    
    public void validate() {
        if (ratingScore == null) {
            throw new IllegalArgumentException("Rating score harus diisi");
        }

        if (ratingScore < 1 || ratingScore > 5) {
            throw new IllegalArgumentException("Rating score harus di antara 1 dan 5");
        }

        if (ulasan == null || ulasan.trim().isEmpty()) {
            throw new IllegalArgumentException("Ulasan tidak boleh kosong");
        }
    }
}