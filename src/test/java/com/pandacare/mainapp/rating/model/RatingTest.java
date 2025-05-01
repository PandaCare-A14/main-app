package com.pandacare.mainapp.rating.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class RatingTest {

    @Test
    void testRatingCreation() {
        // Arrange
        Long id = 1L;
        String idDokter = "DOK001";
        String idPacillian = "PAC001";
        int ratingScore = 4;
        String ulasan = "Pelayanan sangat baik";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        Rating rating = new Rating();
        rating.setId(id);
        rating.setIdDokter(idDokter);
        rating.setIdPacillian(idPacillian);
        rating.setRatingScore(ratingScore);
        rating.setUlasan(ulasan);
        rating.setCreatedAt(createdAt);
        rating.setUpdatedAt(updatedAt);

        // Assert
        assertEquals(id, rating.getId());
        assertEquals(idDokter, rating.getIdDokter());
        assertEquals(idPacillian, rating.getIdPacillian());
        assertEquals(ratingScore, rating.getRatingScore());
        assertEquals(ulasan, rating.getUlasan());
        assertEquals(createdAt, rating.getCreatedAt());
        assertEquals(updatedAt, rating.getUpdatedAt());
    }

    @Test
    void testRatingValidation() {
        // Arrange
        Rating rating = new Rating();
        rating.setIdDokter("DOK001");
        rating.setIdPacillian("PAC001");
        
        // Act & Assert
        // Rating harus di antara 1 dan 5
        assertThrows(IllegalArgumentException.class, () -> rating.setRatingScore(0));
        assertThrows(IllegalArgumentException.class, () -> rating.setRatingScore(6));
        
        // Test valid ratings
        assertDoesNotThrow(() -> rating.setRatingScore(1));
        assertDoesNotThrow(() -> rating.setRatingScore(5));
    }

    @Test
    void testConstructorWithAllParameters() {
        // Arrange
        Long id = 1L;
        String idDokter = "DOK001";
        String idPacillian = "PAC001";
        int ratingScore = 4;
        String ulasan = "Pelayanan sangat baik";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        Rating rating = new Rating(id, idDokter, idPacillian, ratingScore, ulasan, createdAt, updatedAt);

        // Assert
        assertEquals(id, rating.getId());
        assertEquals(idDokter, rating.getIdDokter());
        assertEquals(idPacillian, rating.getIdPacillian());
        assertEquals(ratingScore, rating.getRatingScore());
        assertEquals(ulasan, rating.getUlasan());
        assertEquals(createdAt, rating.getCreatedAt());
        assertEquals(updatedAt, rating.getUpdatedAt());
    }
}