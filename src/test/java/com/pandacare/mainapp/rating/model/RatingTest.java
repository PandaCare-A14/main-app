package com.pandacare.mainapp.rating.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    @Test
    void givenValidValues_whenCreateRating_thenSuccess() {
        // Arrange & Act
        Rating rating = new Rating(
                "DOC12345",
                "PAT7890",
                "JDW12345",
                4,
                "Great doctor!"
        );

        // Assert
        assertEquals("DOC12345", rating.getIdDokter());
        assertEquals("PAT7890", rating.getIdPasien());
        assertEquals("JDW12345", rating.getIdJadwalKonsultasi());
        assertEquals(4, rating.getRatingScore());
        assertEquals("Great doctor!", rating.getUlasan());
        assertNotNull(rating.getCreatedAt());
        assertNotNull(rating.getUpdatedAt());
    }

    @Test
    void givenInvalidRatingScore_whenSetRatingScore_thenThrowsException() {
        // Arrange
        Rating rating = new Rating();

        // Act & Assert - Test score below minimum
        Exception exceptionLow = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(0);
        });
        assertTrue(exceptionLow.getMessage().contains("Rating score harus di antara 1 dan 5"));

        // Act & Assert - Test score above maximum
        Exception exceptionHigh = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(6);
        });
        assertTrue(exceptionHigh.getMessage().contains("Rating score harus di antara 1 dan 5"));

        // Act & Assert - Test null score
        Exception exceptionNull = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(null);
        });
        assertTrue(exceptionNull.getMessage().contains("Rating score cannot be null"));
    }

    @Test
    void givenExistingRating_whenUpdateFrom_thenCorrectlyUpdated() {
        // Arrange
        Rating original = new Rating(
                "DOC12345",
                "PAT7890",
                "JDW12345",
                4,
                "Original review"
        );
        original.setId("RTG12345");
        original.setCreatedAt(LocalDateTime.now().minusDays(1));
        original.setUpdatedAt(LocalDateTime.now().minusDays(1));

        Rating updated = new Rating(
                "DOC12345",
                "PAT7890",
                "JDW12345",
                5,
                "Updated review"
        );

        LocalDateTime beforeUpdate = LocalDateTime.now();

        // Act
        original.updateFrom(updated);

        // Assert
        assertEquals("RTG12345", original.getId()); // ID should not change
        assertEquals("DOC12345", original.getIdDokter()); // Doctor ID should not change
        assertEquals("PAT7890", original.getIdPasien()); // Patient ID should not change
        assertEquals("JDW12345", original.getIdJadwalKonsultasi()); // Consultation ID should not change
        assertEquals(5, original.getRatingScore()); // Score should be updated
        assertEquals("Updated review", original.getUlasan()); // Review should be updated
        assertTrue(original.getUpdatedAt().isAfter(beforeUpdate) || original.getUpdatedAt().isEqual(beforeUpdate)); // UpdatedAt should be updated
    }

    @Test
    void givenNewRating_whenPrePersist_thenIdIsGenerated() {
        // Arrange
        Rating rating = new Rating(
                "DOC12345",
                "PAT7890",
                "JDW12345",
                4,
                "Great doctor!"
        );

        // Act
        rating.onCreate();

        // Assert
        assertNotNull(rating.getId());
        assertTrue(rating.getId().length() > 0);
    }
}